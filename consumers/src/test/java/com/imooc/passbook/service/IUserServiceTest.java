package com.imooc.passbook.service;

import com.alibaba.fastjson.JSON;
import com.imooc.passbook.vo.Response;
import com.imooc.passbook.vo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * 用户服务测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class IUserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void createUser() throws Exception{

        User user = new User();

        user.setBaseInfo(
                new User.BaseInfo(
                        "student",
                        18,
                        "male"
                )
        );
        user.setOtherInfo(
                new User.OtherInfo(
                        "180808800",
                        "Beijing"
                )
        );

        Response res = userService.createUser(user);

        System.out.println(JSON.toJSONString(res));
        /**
         * 输出：
         * {
         *      "data":
         *          {
         *              "baseInfo":{"age":18,"name":"student","sex":"male"},
         *              "id":298335,
         *              "otherInfo":{"address":"Beijing","phone":"180808800"}
         *           },
         *      "errorCode":0,
         *      "errorMsg":""
         *  }
         */
    }
}