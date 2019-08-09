package com.imooc.passbook.merchants.constant;

/**
 * 普通(通用)常量定义
 * 如果对常量定义不是区分的很严格但很常用，就可以把常量定义在这里
 */
public class Constants {

    /**
     * 商户优惠券投放的Kafka Topic
     */
    public static final String TEMPLATE_TOPIC = "merchants-template";

    /**
     * 商户通用的token，在HTTP request的header里是key-value存在的
     * 这里的token string就是key
     */
    public static final String TOKEN_STRING = "token";

    /**
     * 实际颁发给商户进行使用的token info，用来通过平台验证
     * 这里的token就是value
     */
    public static final String TOKEN = "imooc-passbook-merchants";
}






























