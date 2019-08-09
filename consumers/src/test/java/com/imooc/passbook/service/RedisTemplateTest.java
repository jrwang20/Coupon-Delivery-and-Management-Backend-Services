package com.imooc.passbook.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 样例: Redis客户端测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void RedisTemplateTest() {

        //1. redis flush all，清空redis缓存
        redisTemplate.execute((RedisCallback<Object>) connection ->{
            connection.flushAll();
            return null;
        });

        assert redisTemplate.opsForValue().get("name") == null;

        //2. 重新赋值，断言判断
        redisTemplate.opsForValue().set("name", "imooc");

        assert redisTemplate.opsForValue().get("name") != null;
        System.out.println(redisTemplate.opsForValue().get("name"));

        // 输出 imooc

    }

}
