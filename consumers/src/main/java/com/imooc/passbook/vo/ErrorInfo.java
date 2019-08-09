package com.imooc.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一的错误信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo<T> {

    /**
     *  错误码
     */
    public static final Integer ERROR = -1;

    /**
     * 根据不同的异常，给定特定对应的错误码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 异常发生时的请求url
     */
    private String url;

    /**
     * 请求返回的数据
     */
    private T data;
}
