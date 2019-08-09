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
 */
@Slf4j
@Service
public class MerchantsServImpl implements IMerchantsServ {

    /**
     * Merchants数据库接口
     * 这里以构造函数的方法进行注入
     */
    private final MerchantsDao merchantsDao;

    /**
     * Kafka客户端注入，构造函数注入
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

        ErrorCode errorCode = request.validate(merchantsDao);
        if(errorCode != ErrorCode.SUCCESS) {
            merchantsResponse.setId(-1);
            response.setErrorCode(errorCode.getCode());
            response.setErrorMsg(errorCode.getDesc());
        } else {
            merchantsResponse.setId(merchantsDao.save(request.toMerChants()).getId());
        }

        response.setData(merchantsResponse);

        return response;
    }

    @Override
    public Response buildMerchantsInfoById(Integer id) {

        Response response = new Response();

        Merchants merchants = merchantsDao.findById(id);

        if(merchants == null) {
            response.setErrorCode(ErrorCode.MERCHANTS_NOT_EXIST.getCode());
            response.setErrorMsg(ErrorCode.MERCHANTS_NOT_EXIST.getDesc());
        }

        response.setData(merchants);

        return response;
    }

    @Override
    public Response dropPassTemplate(PassTemplate template) {

        Response response = new Response();
        ErrorCode errorCode = template.validate(merchantsDao);

        if(errorCode != ErrorCode.SUCCESS) {
            response.setErrorCode(errorCode.getCode());
            response.setErrorMsg(errorCode.getDesc());
        } else {
            String passTemplate = JSON.toJSONString(template);
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
