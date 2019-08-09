package com.imooc.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户领取优惠券的请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GainPassTemplateRequest {

    /** 用户id */
    private Long userId;

    /** 用户领取到的某一个优惠券 */
    private PassTemplate passTemplate;

}
