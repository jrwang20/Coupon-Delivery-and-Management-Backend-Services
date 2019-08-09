package com.imooc.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.Util.RowKeyGenUtil;
import com.imooc.passbook.constant.Constants;
import com.imooc.passbook.mapper.PassTemplateRowMapper;
import com.imooc.passbook.service.IGainPassTemplateService;
import com.imooc.passbook.vo.GainPassTemplateRequest;
import com.imooc.passbook.vo.PassTemplate;
import com.imooc.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户领取优惠券功能实现
 */
@Slf4j
@Service
public class GainPassTemplateServiceImpl implements IGainPassTemplateService {

    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    public Response gainPassTemplate(GainPassTemplateRequest request) throws Exception {

        PassTemplate passTemplate;
        //0. 根据请求信息，获取行键
        String passTemplateId = RowKeyGenUtil.genPassTemplateRowKey(request.getPassTemplate());
        System.out.println(passTemplateId);

        //1. 根据从请求信息中的行键，去hbase中查找是否存在对应优惠券
        try {
            passTemplate = hbaseTemplate.get(
                    Constants.PassTemplateTable.TABLE_NAME,
                    passTemplateId,
                    new PassTemplateRowMapper()
            );
        } catch(Exception e) {
            log.error("No such Pass: {}", JSON.toJSONString(request.getPassTemplate()));
            return Response.error("Gain PassTemplate Error");
        }

        //2. 验证优惠券是否有剩余库存，没有则代表不能领取
        if(passTemplate.getLimit() < 1 && passTemplate.getLimit() != -1) {
            log.error("PassTemplate Limit Max: {}", JSON.toJSONString(request.getPassTemplate()));
            return Response.error("PassTemplate Limit Max!");
        }

        //3. 验证优惠券时间是否过期或是否生效
        Date cur = new Date();

        if(!(cur.getTime() >= passTemplate.getStart().getTime()
//        && cur.getTime() < passTemplate.getEnd().getTime()
        )
        ) {
            log.error("PassTemplate ValidTime Error: {}", JSON.toJSONString(request.getPassTemplate()));
            return Response.error("PassTemplate ValidTime Error");
        }

        //4. 通过验证，可以获取优惠券，需要减去库存中优惠券Limit
        if(passTemplate.getLimit() != -1) {
            List<Mutation> data = new ArrayList<>();
            byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
            byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();

            Put put = new Put(Bytes.toBytes(passTemplateId));
            put.addColumn(FAMILY_C, LIMIT, Bytes.toBytes(passTemplate.getLimit() - 1));

            hbaseTemplate.saveOrUpdates(Constants.PassTemplateTable.TABLE_NAME, data);
        }

        //5. 用户获取优惠券后，将Pass保存到相应的用户优惠券hbase表
        if(!addPassForUser(request, passTemplate.getId(), passTemplateId)) {
            return Response.error("Gain PassTemplate Error");
        }

        return Response.success();
    }

    /**
     * 给用户添加优惠券
     * @param request {@link GainPassTemplateRequest} 给用户添加优惠券的请求VO
     * @param merchantsId 商户Id
     * @param passTemplateId 优惠券Id
     * @return true/false 添加优惠券是否成功
     * @throws Exception
     */
    private boolean addPassForUser(GainPassTemplateRequest request,
                                   Integer merchantsId,
                                   String passTemplateId) throws Exception{

        //0. 准备好字节数组
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
        byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
        byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
        byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        //1. 构建put，将传入的用户领取优惠券请求VO的数据进行匹配和收集
        List<Mutation> data = new ArrayList<>();
        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genPassRowKey(request)));
        put.addColumn(FAMILY_I, USER_ID, Bytes.toBytes(request.getUserId()));
        put.addColumn(FAMILY_I, TEMPLATE_ID, Bytes.toBytes(passTemplateId));

        //1.1. 在put收集token的时候，需要去从redis缓存中token，然后记录下来该token已经使用，然后再put收集
        if(request.getPassTemplate().getHasToken()) {
            String token = redisTemplate.opsForSet().pop(passTemplateId);
            if(null == token) {
                log.error("Token not exist: {}", passTemplateId);
                return false;
            } else {
                recordTokenToFile(merchantsId, passTemplateId, token);
                put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes(token));
            }
        } else {
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes("-1"));
        }

        put.addColumn(FAMILY_I, ASSIGNED_DATE, Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(new Date())));
        put.addColumn(FAMILY_I, CON_DATE, Bytes.toBytes("-1"));

        data.add(put);

        //2. 在hbase中存储构建好的data(put)数据
        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, data);

        return true;
    }

    /**
     * 将已使用的token记录到文件中
     * @param merchantsId 商户Id
     * @param passTemplateId 优惠券Id
     * @param token 分配的优惠券token
     */
    private void recordTokenToFile(Integer merchantsId, String passTemplateId, String token) throws Exception {

        //首先获取文件路径，存储token的字节数组，并以追加的方式写入
        Files.write(
                Paths.get(Constants.TOKEN_DIR,
                        String.valueOf(merchantsId),
                        passTemplateId + Constants.USED_TOKEN_SUFFIX
                ),
                (token + "\n").getBytes(),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }



}
