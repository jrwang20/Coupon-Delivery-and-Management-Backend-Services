package com.imooc.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户领取的优惠券
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pass {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * pass在HBase中的rowKey
     */
    private String rowKey;

    /**
     * PassTemplate在HBase中的rowKey
     */
    private String templateId;

    /**
     * 优惠券token，有可能是null，则填充-1
     */
    private String token;

    /**
     * 领取日期
     */
    private Date assignedDate;

    /**
     * 消费日期，不为空代表已经被消费了
     */
    private Date conDate;
}
