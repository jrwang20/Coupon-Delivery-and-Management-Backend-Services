package com.imooc.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.constant.Constants;
import com.imooc.passbook.constant.PassStatus;
import com.imooc.passbook.dao.MerchantsDao;
import com.imooc.passbook.entity.Merchants;
import com.imooc.passbook.mapper.PassRowMapper;
import com.imooc.passbook.service.IUserPassService;
import com.imooc.passbook.vo.Pass;
import com.imooc.passbook.vo.PassInfo;
import com.imooc.passbook.vo.PassTemplate;
import com.imooc.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户优惠券相关功能实现
 */
@Service
@Slf4j
public class UserPassServiceImpl implements IUserPassService {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private MerchantsDao merchantsDao;

    @Override
    public Response getUserPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.UNUSED);
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.USED);
    }

    @Override
    public Response getUserAllPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.ALL);
    }

    @Override
    public Response userUsePass(Pass pass) {

        //1. 根据pass中包含的userId获取前缀
        byte[] rowPrefix = Bytes.toBytes(
                new StringBuilder(String.valueOf(pass.getUserId()))
                        .reverse()
                        .toString()
        );

        //2. 设置Scan扫描器
        Scan scan = new Scan();

        //2.1. 设置过滤list，往里面添加各种过滤器
        List<Filter> filterList = new ArrayList<Filter>();

        //2.2. 配置前缀过滤器，过滤userId，添加进list
        filterList.add(new PrefixFilter(rowPrefix));

        //2.3. 配置单列过滤器，过滤优惠券模版Id(查看是否相同)，添加进list
        filterList.add(
                new SingleColumnValueFilter(
                        Constants.PassTable.FAMILY_I.getBytes(),
                        Constants.PassTable.TEMPLATE_ID.getBytes(),
                        CompareFilter.CompareOp.EQUAL,
                        Bytes.toBytes(pass.getTemplateId())
                )
        );

        //2.4. 配置单列过滤器，过滤优惠券消费日期是否为未消费，添加进list
        filterList.add(
                new SingleColumnValueFilter(
                        Constants.PassTable.FAMILY_I.getBytes(),
                        Constants.PassTable.CON_DATE.getBytes(),
                        CompareFilter.CompareOp.EQUAL,
                        Bytes.toBytes("-1")
                )
        );

        //2.5. 完成Scan过滤器
        scan.setFilter(new FilterList(filterList));

        //3. 根据扫描器，利用hbase客户端去hbase中查询数据，并进行ORM，查询到Pass用户优惠券信息List
        List<Pass> passList = hbaseTemplate.find(Constants.PassTable.TABLE_NAME, scan, new PassRowMapper());

//        if(passList == null || passList.size() != 1) {
//            log.error("UserUsePass Error: {}", JSON.toJSONString(pass));
//            return  Response.error("UserUsePass Error");
//        }

        //4. 用户使用优惠券，修改hbase中相关数据，更改消费日期为当前时间
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        List<Mutation> data = new ArrayList<Mutation>();

        Put put = new Put(passList.get(0).getRowKey().getBytes());
        put.addColumn(FAMILY_I, CON_DATE, Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(new Date())));

        data.add(put);

        //5. 将修改好的数据重新保存到hbase中，完成用户使用优惠券动作
        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, data);

        return Response.success();
    }

    /**
     * 根据优惠券状态获取优惠券信息
     * @param userId 用户Id
     * @param status {@link PassStatus}
     * @return {@link Response}
     */
    private Response getPassInfoByStatus(Long userId, PassStatus status) throws Exception{

        //1. 根据userId构造行键前缀
        byte[] rowPrefix = Bytes.toBytes(new StringBuilder(String.valueOf(userId)).reverse().toString());

        //2. 设置比较器: 如果优惠券未使用，那么设置比较器为相等；否则，那么比较器为不相等
        CompareFilter.CompareOp compareOp =
                status == PassStatus.UNUSED ?
                        CompareFilter.CompareOp.EQUAL :
                        CompareFilter.CompareOp.NOT_EQUAL;

        //3. 设置Scan扫描器，里面可以设置不同种类的过滤器
        Scan scan = new Scan();

        //3.1 创建存储多个过滤器的filterList
        List<Filter> filterList = new ArrayList<Filter>();

        //3.2. 设置行键前缀过滤器，找到特定用户的优惠券，放入list中
        filterList.add(new PrefixFilter(rowPrefix));

        //3.3. 设置基于列单元值的过滤器，找到未使用的优惠券，放入list中
        if(status != PassStatus.ALL) {
            filterList.add(
                    new SingleColumnValueFilter(
                        Constants.PassTable.FAMILY_I.getBytes(),
                        Constants.PassTable.CON_DATE.getBytes(),
                        compareOp,
                        Bytes.toBytes(-1)
                    )
            );
        }

        //3.4. 将整个filerList作为一个对象，配置进扫描器Scan中
        scan.setFilter(new FilterList(filterList));

        //4. 根据设置好过滤器的扫描器，通过hbase客户端，对相应的表调用find方法进行查询，并把查询结果进行Map成一列PassVO对象
        List<Pass> passList = hbaseTemplate.find(Constants.PassTable.TABLE_NAME, scan, new PassRowMapper());

        //5. 根据PassVO，调用Map生成方法，生成关于PassTemplate和Merchants的Map，包括了PassTemplate和Merchants的对象
        Map<String, PassTemplate> passTemplateMap = buildPassTemplateMap(passList);
        Map<Integer, Merchants> merchantsMap = buildMerchantsMap(
                new ArrayList<>(passTemplateMap.values())
        );

        //6.根据获取好的Pass对象、PassTemplate对象、Merchants对象，构建一列PassInfo对象
        List<PassInfo> res = new ArrayList<>();
        for(Pass pass : passList) {
            PassTemplate passTemplate = passTemplateMap.getOrDefault(
                    pass.getTemplateId(), null
            );
            if(null == passTemplate) {
                log.error("PassTemplate Null: {}", pass.getTemplateId());
                continue;
            }

            Merchants merchants = merchantsMap.getOrDefault(
                    passTemplate.getId(), null
            );
            if(null == merchants) {
                log.error("Merchants Null: {}", passTemplate.getId());
                continue;
            }

            res.add(new PassInfo(pass, passTemplate, merchants));
        }

        return new Response(res);
    }


    /**
     * 通过获取的Pass对象，构造Map
     * @param passList {@link Pass}
     * @return Map {@link PassTemplate} key是PassTemplate的rowKey，value是PassTemplate
     * @throws Exception
     */
    private Map<String, PassTemplate> buildPassTemplateMap(List<Pass> passList) throws Exception {

        String[] patterns = new String[] {"yyyy-MM-dd"};

        //这些字节数组是为了和从hbase中获取的结果对应的，辅助转化乘Java对象
        byte[] FAMILY_B = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B);
        byte[] ID = Bytes.toBytes(Constants.PassTemplateTable.ID);
        byte[] TITLE = Bytes.toBytes(Constants.PassTemplateTable.TITLE);
        byte[] SUMMARY = Bytes.toBytes(Constants.PassTemplateTable.SUMMARY);
        byte[] DESC = Bytes.toBytes(Constants.PassTemplateTable.DESC);
        byte[] HAS_TOKEN = Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN);
        byte[] BACKGROUND = Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND);

        byte[] FAMILY_C = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C);
        byte[] LIMIT = Bytes.toBytes(Constants.PassTemplateTable.LIMIT);
        byte[] START = Bytes.toBytes(Constants.PassTemplateTable.START);
        byte[] END = Bytes.toBytes(Constants.PassTemplateTable.END);

        //1. 根据这一组优惠券，提取他们各自的优惠券模版的Id
//        List<String> templateIdList = new ArrayList<String>();
//        for(Pass pass : passList) {
//            templateIdList.add(pass.getTemplateId());
//        }
        List<String> templateIdList = passList.stream().map(
                Pass::getTemplateId
        ).collect(Collectors.toList());

        //2. 根据这一组优惠券模版Id，创建他们对应的Get对象
//        List<Get> templateGetList = new ArrayList<Get>(templateIdList.size());
//        for(String templateId : templateIdList) {
//            templateGetList.add(new Get(Bytes.toBytes(templateId)));
//        }
        List<Get> templateGetList = new ArrayList<Get>(templateIdList.size());
        templateIdList.forEach(t -> templateGetList.add(new Get(Bytes.toBytes(t))));

        //3. 根据这一组Get对象，连接hbase获取查询到的优惠券模版结果数据列表(Result类型的数据)
        Result[] templateResultList = hbaseTemplate
                .getConnection()
                .getTable(TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME))
                .get(templateGetList);

        //4. 根据查询获取到的结果数据列表，构建对应的优惠券模版VO数据对象，并获取对应优惠券模版的行键作为key，存入Map中
        //构造PassTemplateId -> PassTemplate Object的Map，用于构造PassInfo
        Map<String, PassTemplate> templateId2Object = new HashMap<>();
        for(Result templateResult : templateResultList) {
            PassTemplate passTemplate = new PassTemplate();

            passTemplate.setId(Bytes.toInt(templateResult.getValue(FAMILY_B, ID)));
            passTemplate.setTitle(Bytes.toString(templateResult.getValue(FAMILY_B, TITLE)));
            passTemplate.setSummary(Bytes.toString(templateResult.getValue(FAMILY_B, SUMMARY)));
            passTemplate.setDesc(Bytes.toString(templateResult.getValue(FAMILY_B, DESC)));
            passTemplate.setHasToken(Bytes.toBoolean(templateResult.getValue(FAMILY_B, HAS_TOKEN)));
            passTemplate.setBackground(Bytes.toInt(templateResult.getValue(FAMILY_B, BACKGROUND)));

            passTemplate.setLimit(Bytes.toLong(templateResult.getValue(FAMILY_C, LIMIT)));
            passTemplate.setStart(DateUtils.parseDate(Bytes.toString(templateResult.getValue(FAMILY_C, START)), patterns));
            passTemplate.setEnd(DateUtils.parseDate(Bytes.toString(templateResult.getValue(FAMILY_C, END)), patterns));

            templateId2Object.put(Bytes.toString(templateResult.getRow()), passTemplate);
        }

        return templateId2Object;
    }

    /**
     * 通过获取的PassTemplate对象，构造MerchantsMap
     * @param passTemplateList {@link PassTemplate}
     * @return
     */
    private Map<Integer, Merchants> buildMerchantsMap(List<PassTemplate> passTemplateList) {

        Map<Integer, Merchants> merchantsMap = new HashMap<Integer, Merchants>();

        //1. 根据PassTemplateList，获取每一个优惠券模版所属的商户Id
//        List<Integer> merchantsIdList = new ArrayList<Integer>();
//        for(PassTemplate passTemplate : passTemplateList) {
//            merchantsIdList.add(passTemplate.getId());
//        }
        List<Integer> merchantsIdList = passTemplateList.stream().map(
                PassTemplate::getId
        ).collect(Collectors.toList());

        //2. 根据这一组商户Id，去数据库中检索对应的Merchants信息，同时组成Map
//        for(Integer merchantsId : merchantsIdList) {
//            Merchants merchants = merchantsDao.findById(merchantsId);
//            merchantsMap.put(merchantsId, merchants);
//        }
        List<Merchants> merchantsList = merchantsDao.findByIdIn(merchantsIdList);
        merchantsList.forEach(m -> merchantsMap.put(m.getId(), m));

        return merchantsMap;
    }
}
