package com.imooc.passbook.merchants.constant;

/**
 * Constants Definition
 */
public class Constants {

    /**
     * Kafka Topic of Merchants Dropping Coupon
     */
    public static final String TEMPLATE_TOPIC = "merchants-template";

    /**
     * Coupon Token, the form of which is key-value pair in HTTP Header
     * This TOEKN_STRING is the key
     */
    public static final String TOKEN_STRING = "token";

    /**
     * The token info to give to the Merchants to pass the validation
     * This is the value
     */
    public static final String TOKEN = "imooc-passbook-merchants";
}






























