package com.imooc.passbook.log;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志生成器
 */
@Slf4j
public class LogGenerator {

    /**
     * 日志生成方法
     * @param request {@link HttpServletRequest} 利用HTTP request，就可以获取到用户的IP，请求的header，cookie等信息
     * @param userId 用户id
     * @param action 日志类型
     * @param info 日志信息，可以是null
     */
    public static void genLog(HttpServletRequest request, Long userId, String action, Object info) {
        log.info(
                JSON.toJSONString(
                        new LogObject(
                                action,
                                userId,
                                System.currentTimeMillis(),
                                request.getRemoteAddr(),
                                info
                        )
                )
        );
    }
}
