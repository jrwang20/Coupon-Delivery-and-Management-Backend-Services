package com.imooc.passbook.merchants.constant;

/**
 * 描述优惠券的背景颜色的Enum
 * The Coupon background Color Enum
 */
public enum TemplateColor {

    RED(1, "红色"),
    GREEN(2, "绿色"),
    BLUD(3, "蓝色")
    ;

    /**
     * 标识背景颜色的代码 Color Code
     */
    private Integer code;

    /**
     * 颜色的描述 Color Description
     */
    private String color;

    TemplateColor(Integer code, String color) {
        this.code = code;
        this.color = color;
    }

    public Integer getCode() {
        return code;
    }

    public String getColor() {
        return color;
    }
}
