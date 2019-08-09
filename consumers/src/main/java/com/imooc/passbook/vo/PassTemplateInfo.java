package com.imooc.passbook.vo;

import com.imooc.passbook.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 优惠券模版信息
 * 商户在平台上投放、但用户没有领取的那些优惠券
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassTemplateInfo{

    /**
     * 优惠券模版
     */
    private PassTemplate passTemplate;

    /**
     * 优惠券对应商户
     */
    private Merchants merchants;
}
