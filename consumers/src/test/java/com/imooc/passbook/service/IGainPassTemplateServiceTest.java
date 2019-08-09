package com.imooc.passbook.service;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.vo.GainPassTemplateRequest;
import com.imooc.passbook.vo.PassTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * 用户领取优惠券功能测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IGainPassTemplateServiceTest extends AbstractServiceTest{

    @Autowired
    private IGainPassTemplateService gainPassTemplateService;

    @Test
    public void testGainPassTemplate() throws Exception{
        PassTemplate template = new PassTemplate();
        template.setId(19);
        template.setTitle("title: imooc-1");
        template.setHasToken(true);

        System.out.println(JSON.toJSONString(
                gainPassTemplateService.gainPassTemplate(
                        new GainPassTemplateRequest(userId, template)
                )
        ));

    }

}