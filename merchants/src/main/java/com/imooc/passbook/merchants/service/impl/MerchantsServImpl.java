package com.imooc.passbook.merchants.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.merchants.constant.Constants;
import com.imooc.passbook.merchants.constant.ErrorCode;
import com.imooc.passbook.merchants.dao.MerchantsDao;
import com.imooc.passbook.merchants.entity.Merchants;
import com.imooc.passbook.merchants.service.IMerchantsServ;
import com.imooc.passbook.merchants.vo.CreateMerchantsRequest;
import com.imooc.passbook.merchants.vo.CreateMerchantsResponse;
import com.imooc.passbook.merchants.vo.PassTemplate;
import com.imooc.passbook.merchants.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商户服务接口实现
 * The Implementation of Merchants Service
 */
@Slf4j
@Service
public class MerchantsServImpl implements IMerchantsServ {

    /**
     * Merchants数据库接口
     * 这里以构造函数的方法进行注入
     * Autowired the MerchantsRepository
     */
    private final MerchantsDao merchantsDao;

    /**
     * Kafka客户端注入，构造函数注入
     * Autowired the Kafka Template so that we can call the methods to send the message
     */
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public MerchantsServImpl(MerchantsDao merchantsDao, KafkaTemplate<String, String> kafkaTemplate) {
        this.merchantsDao = merchantsDao;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Transactional
    public Response createMerchants(CreateMerchantsRequest request) {
        Response response = new Response();
        CreateMerchantsResponse merchantsResponse = new CreateMerchantsResponse();

        //1. validate the request, find that if the merchant already exist
        ErrorCode errorCode = request.validate(merchantsDao);
        if(errorCode != ErrorCode.SUCCESS) {
            merchantsResponse.setId(-1);
            response.setErrorCode(errorCode.getCode());
            response.setErrorMsg(errorCode.getDesc());
        } else {
            //2. call the repository to save the Merchants Object to Database, and set the ID to response
            merchantsResponse.setId(merchantsDao.save(request.toMerChants()).getId());
        }

        response.setData(merchantsResponse);

        //3. build the response VO and return
        return response;
    }

    @Override
    public Response buildMerchantsInfoById(Integer id) {

        Response response = new Response();

        //1. call the repository methods to query the database
        Merchants merchants = merchantsDao.findById(id);

        if(merchants == null) {
            response.setErrorCode(ErrorCode.MERCHANTS_NOT_EXIST.getCode());
            response.setErrorMsg(ErrorCode.MERCHANTS_NOT_EXIST.getDesc());
        }

        response.setData(merchants);

        //2. build the response VO and return
        return response;
    }

    @Override
    public Response dropPassTemplate(PassTemplate template) {

        Response response = new Response();
        //1. validate the current merchant, find that if it's a valid merchant
        ErrorCode errorCode = template.validate(merchantsDao);

        if(errorCode != ErrorCode.SUCCESS) {
            response.setErrorCode(errorCode.getCode());
            response.setErrorMsg(errorCode.getDesc());
        } else {
            //2.1. parse the coupon template to String
            String passTemplate = JSON.toJSONString(template);
            //2.2. using Kafka client to call the send method, with topic, key, and value, sending message to MQ
            kafkaTemplate.send(
                    Constants.TEMPLATE_TOPIC,
                    Constants.TEMPLATE_TOPIC,
                    passTemplate
            );
            log.info("DropPassTemplate: {}", passTemplate);
        }

        return response;
    }
}
