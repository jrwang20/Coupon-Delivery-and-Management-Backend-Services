package com.imooc.passbook.merchants.security;

/**
 * 用ThreadLocal去单独存储每一个线程携带的toekn信息
 */
public class AccessContext {

    private static final ThreadLocal<String> token = new ThreadLocal<>();

    /**
     * 获取当前thread当中保存的String类型的token
     * @return
     */
    public static String getToken() {
        return token.get();
    }

    /**
     * 给当前的thread中的token进行重新设置
     * @param tokenStr
     */
    public static void setToken(String tokenStr) {
        token.set(tokenStr);
    }

    /**
     * 清除线程当中的token信息
     */
    public static void clearAccessKey() {
        token.remove();
    }
}
