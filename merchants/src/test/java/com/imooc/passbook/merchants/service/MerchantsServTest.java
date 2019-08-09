package com.imooc.passbook.merchants.service;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.merchants.vo.CreateMerchantsRequest;
import com.imooc.passbook.merchants.vo.PassTemplate;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 商户服务测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class MerchantsServTest {

    @Autowired
    private IMerchantsServ merchantsServ;

    /**
     * 测试返回结果：{"data":{"id":17},"errorCode":0,"errorMsg":""}
     */
    @Test
//    @Transactional
    public void testCreateMerchantServ() {

        CreateMerchantsRequest request = new CreateMerchantsRequest();
        request.setName("imooc");
        request.setLogoUrl("www.imooc.com");
        request.setBusinessLicenseUrl("www.imooc.com");
        request.setPhone("1234567890");
        request.setAddress("Beijing");

        System.out.println(JSON.toJSONString(merchantsServ.createMerchants(request)));
    }

    /**
     * 测试返回结果
     * {
     *  "data":
     *  {
     *      "address":"Beijing","businessLicenseUrl":"www.imooc.com","id":19,
     *      "isAudit":false,"logoUrl":"www.imooc.com",
     *      "name":"imooc","phone":"1234567890"
     *  },
     *  "errorCode":0,
     *  "errorMsg":""
     *  }
     */
    @Test
    public void testBuildMerchantsInfoById() {

        Integer id = 19;
        System.out.println(JSON.toJSONString(merchantsServ.buildMerchantsInfoById(id)));
    }

    /**
     * 测试结果
     *
     * 打日志
     * DropPassTemplate: {"background":2,"desc":"description:
     * imooc","end":1565549262137,"hasToken":false,"id":19,
     * "limit":10000,"start":1564685262137,"summary":"summary: imooc",
     * "title":"title: imooc"}
     *
     * 控制台打印ResponseVO
     * {"errorCode":0,"errorMsg":""}
     */
    @Test
    public void testDropPassTemplate() {

        PassTemplate passTemplate = new PassTemplate();

        passTemplate.setId(20);
        passTemplate.setTitle("title: imooc-3");
        passTemplate.setSummary("summary: KafkaServiceTest-3");
        passTemplate.setDesc("description: Test the message between two modules-3");
        passTemplate.setLimit(10000L);
        passTemplate.setHasToken(true);
        passTemplate.setBackground(1);
        passTemplate.setStart(DateUtils.addDays(new Date(), -10));
        passTemplate.setEnd(DateUtils.addDays(new Date(), 10));

        System.out.println(
                JSON.toJSONString(
                        merchantsServ.dropPassTemplate(passTemplate)
                )
        );
    }
}
