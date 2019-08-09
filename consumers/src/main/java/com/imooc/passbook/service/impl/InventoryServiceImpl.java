package com.imooc.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.Util.RowKeyGenUtil;
import com.imooc.passbook.constant.Constants;
import com.imooc.passbook.dao.MerchantsDao;
import com.imooc.passbook.entity.Merchants;
import com.imooc.passbook.mapper.PassTemplateRowMapper;
import com.imooc.passbook.service.IInventoryService;
import com.imooc.passbook.service.IUserPassService;
import com.imooc.passbook.vo.*;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.LongComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 获取库存信息，只返回用户没有领取的优惠券
 */
@Slf4j
@Service
public class InventoryServiceImpl implements IInventoryService {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private MerchantsDao merchantsDao;

    @Autowired
    private IUserPassService userPassService;


    @Override
    @SuppressWarnings("unchecked")
    public Response getInventoryInfo(Long userId) throws Exception {

        //1. 根据userId，调用userPassService方法，获取当前用户拥有的全部优惠券信息
        Response allUserPass = userPassService.getUserAllPassInfo(userId);

        //2. 从返回的Response中，获取封装的Data，即passInfoList
        List<PassInfo> passInfoList = (List<PassInfo>) allUserPass.getData();

        //3. 获取passInfoList中每一个passInfo的PassTemplate信息
        //   并基于此生成rowKey，作为excludeId，也就是需要被过滤的用户已拥有的优惠券id
        List<String> excludeIdList = new ArrayList<>();
        for(PassInfo passInfo : passInfoList) {
            PassTemplate passTemplate = passInfo.getPassTemplate();
            excludeIdList.add(RowKeyGenUtil.genPassTemplateRowKey(passTemplate));
        }

        //4. 根据需要排除的用户已使用优惠券IdList，调用方法，获取系统中仍然可以使用的优惠券List
        List<PassTemplate> passTemplateList = getAvailablePassTemplate(excludeIdList);

        //5. 根据优惠券List，创建优惠券+商户信息List
        List<PassTemplateInfo> passTemplateInfoList = buildPassTemplateInfo(passTemplateList);

        //6. 创建库存相应VO对象，封装进Response并返回
        return new Response(new InventoryResponse(userId, passTemplateInfoList));
    }

    /**
     * 获取系统中可用的优惠券
     * @param excludeIdList 需要排除的优惠券IdList
     * @return {@link PassTemplate}
     */
    private List<PassTemplate> getAvailablePassTemplate(List<String> excludeIdList) {

        //1. 设置过滤器列表，只需要满足列表中的一个过滤条件即可通过
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);

        //1.1. 设置单列过滤器，对limit列进行过滤条件设置，必须大于0
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.GREATER,
                        new LongComparator(0L)
                )
        );

        //1.2. 设置单列过滤器，对limit列进行过滤，可以等于1
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.EQUAL,
                        Bytes.toBytes(-1)
                )
        );

        //2. 创建扫描器，将过滤器配置进去
        Scan scan = new Scan();
        scan.setFilter(filterList);

        //3. 根据构建好的scan，去hbase中查找全部可用的数据，并进行ORM
        List<PassTemplate> validTemplateList = hbaseTemplate.find(Constants.PassTemplateTable.TABLE_NAME, scan, new PassTemplateRowMapper());

        System.out.println(JSON.toJSONString(validTemplateList));

        //4. 创建返回结果List，准备对所有获取到的数据进行用户层面上的过滤
        List<PassTemplate> availablePassTemplateList = new ArrayList<>();

        Date cur = new Date();

        for (PassTemplate template : validTemplateList) {

            //4.1. 如果当前优惠券是需要被过滤的(根据传入的用户已领取优惠券Id)，那么过滤
            if(excludeIdList.contains(RowKeyGenUtil.genPassTemplateRowKey(template))) {
                continue;
            }

//            System.out.println(template.getEnd());

            //4.2. 如果当前优惠券的时间是valid的，那么通过
            if(cur.getTime() >= template.getStart().getTime()
//            && cur.getTime() <= template.getEnd().getTime()
            ) {
                availablePassTemplateList.add(template);
            }

        }

        return availablePassTemplateList;
    }

    /**
     * 构造优惠券商户信息
     * @param passTemplateList {@link PassTemplate}
     * @return {@link PassTemplateInfo}
     */
    private List<PassTemplateInfo> buildPassTemplateInfo(List<PassTemplate> passTemplateList) {

//        List<PassTemplateInfo> result = new ArrayList<PassTemplateInfo>();
//
//        for (PassTemplate passTemplate : passTemplateList) {
//            Merchants merchants = merchantsDao.findById(passTemplate.getId());
//            result.add(new PassTemplateInfo(passTemplate, merchants));
//        }

        Map<Integer, Merchants> merchantsMap = new HashMap<Integer, Merchants>();

        List<Integer> merchantsIdList = new ArrayList<Integer>();

        //1. 根据传入的优惠券模版，构建所属商户的IdList
        for (PassTemplate passTemplate : passTemplateList) {
            merchantsIdList.add(passTemplate.getId());
        }

        //2. 调用merchantsDao的数据查询方法，根据IdList查找MerchantsList
        List<Merchants> merchantsList = merchantsDao.findByIdIn(merchantsIdList);

        //3. 创建Merchants的Map
        for (Merchants merchants : merchantsList) {
            merchantsMap.put(merchants.getId(), merchants);
        }

        List<PassTemplateInfo> res = new ArrayList<PassTemplateInfo>();

        //4. 利用passTemplate和merchants对象，创建一列passTemplateInfo对象，返回
        for(PassTemplate passTemplate : passTemplateList) {
            if(!merchantsMap.containsKey(passTemplate.getId())) {
                log.error("Merchants Error: {}", passTemplate.getId());
                continue;
            }

            Merchants merchants = merchantsMap.get(passTemplate.getId());

            res.add(new PassTemplateInfo(passTemplate, merchants));
        }

        return res;
    }
}
