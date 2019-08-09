package com.imooc.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Controller统一响应
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    /**
     * 错误码，正确返回0
     */
    private Integer errorCode = 0;

    /**
     * 错误信息，正确返回空字符串
     */
    private String errorMsg = "";

    /**
     * 返回值对象
     */
    private Object data;

    /**
     * 正确响应时的构造函数
     * 只返回数据，不返回错误信息
     * @param data
     */
    public Response(Object data) {
        this.data = data;
    }

    /**
     * 空响应，有时正确响应了但是不需要返回任何值
     * @return
     */
    public static Response success() {
        return new Response();
    }

    /**
     * 错误响应，data为空，错误码为-1，错误信息自定义
     * @param errorMsg
     * @return
     */
    public static Response error(String errorMsg) {
        return new Response(-1, errorMsg, null);
    }
}
