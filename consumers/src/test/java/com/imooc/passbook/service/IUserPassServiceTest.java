package com.imooc.passbook.service;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.vo.Pass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * 用户优惠券服务测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IUserPassServiceTest extends AbstractServiceTest{

    @Autowired
    IUserPassService userPassService;

    @Test
    public void getUserPassInfo() throws Exception {
        System.out.println(JSON.toJSONString(userPassService.getUserPassInfo(userId)));
    }

    @Test
    public void getUserUsedPassInfo() throws Exception{
        System.out.println(JSON.toJSONString(userPassService.getUserUsedPassInfo(userId)));
    }

    @Test
    public void getUserAllPassInfo() throws Exception{
        System.out.println(JSON.toJSONString(userPassService.getUserAllPassInfo(userId)));
    }

    @Test
    public void userUsePass()throws Exception{

        Pass pass = new Pass();
        pass.setUserId(userId);
        pass.setTemplateId("99a8183d069aadfb629077d843912ceb");

        System.out.println(JSON.toJSONString(userPassService.userUsePass(pass)));
    }
}