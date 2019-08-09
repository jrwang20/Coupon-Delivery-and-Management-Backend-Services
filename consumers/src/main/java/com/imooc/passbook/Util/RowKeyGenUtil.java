package com.imooc.passbook.Util;

import com.imooc.passbook.vo.FeedBack;
import com.imooc.passbook.vo.GainPassTemplateRequest;
import com.imooc.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * RowKey生成器工具类
 */
@Slf4j
public class RowKeyGenUtil {

    /**
     * 根据提供的PassTemplate对象，生成PassTemplate的RowKey
     * @param passTemplate {@link PassTemplate}
     * @return String RowKey
     */
    public static String genPassTemplateRowKey(PassTemplate passTemplate) {

        /**
         * 使用passTemplate的id和title进行拼接，生成一个可以唯一标识当前passTemplate的字段
         * 并根据该字段生成一个md5的值，作为RowKey
         */
        String passInfo = String.valueOf(passTemplate.getId()) + "_" + String.valueOf(passTemplate.getTitle());
        String rowKey = DigestUtils.md5Hex(passInfo);

        log.info("GenPassTemplateRowKey: {}, {}", passInfo, rowKey);

        return rowKey;
    }

    /**
     * 根据提供的领取优惠券请求，生成rowKey，只可以在领取优惠券时使用
     * Pass_RowKey = reversed(userId) + inverse(timestamp) + PassTemplate_RowKey
     * @param request {@link GainPassTemplateRequest}
     * @return String RowKey
     */
    public static String genPassRowKey(GainPassTemplateRequest request) {
        return new StringBuilder(String.valueOf(request.getUserId())).reverse().toString()
                + (Long.MAX_VALUE - System.currentTimeMillis())
                + genPassTemplateRowKey(request.getPassTemplate());
    }

    /**
     * 根据feedback对象参数，构造rowKey
     * @param feedBack {@link FeedBack}
     * @return String RowKey
     */
    public static String genFeedbackRowKey(FeedBack feedBack) {

        /**
         * rowKey分为两个部分，用户Id和
         */
        return new StringBuilder(String.valueOf(feedBack.getUserId())).reverse().toString()
                + (Long.MAX_VALUE - System.currentTimeMillis());
    }
}
