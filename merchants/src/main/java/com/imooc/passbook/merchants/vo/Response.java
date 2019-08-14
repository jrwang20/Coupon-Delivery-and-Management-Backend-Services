package com.imooc.passbook.merchants.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应对象 the Response VO to store all the response data
 * 在HTTP响应时，需要有一个同一个对象进行承载封装，才能传给客户端
 * 所有HTTP response都是这个
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    /**
     * 错误码，正确返回0
     * Error Code, return 0 if success
     */
    private Integer errorCode = 0;

    /**
     * 错误信息，正确返回空字符串
     * Error Message, return empty if success
     */
    private String errorMsg = "";

    /**
     * 返回值对象
     * Response Data Object, return null if error
     */
    private Object data;

    /**
     * 正确的响应构造函数
     * 当响应正确时，code和errorMsg为空，只返回data
     * @param data 返回值对象
     */
    public Response(Object data) {
        this.data = data;
    }
}
