package com.imooc.passbook.merchants.security;

/**
 * 用ThreadLocal去单独存储每一个线程携带的token信息
 * Using ThreadLocal to store the Token info in each Thread
 */
public class AccessContext {

    private static final ThreadLocal<String> token = new ThreadLocal<>();

    /**
     * 获取当前thread当中保存的String类型的token
     * Get the Token String from current Thread
     * @return
     */
    public static String getToken() {
        return token.get();
    }

    /**
     * 给当前的thread中的token进行重新设置
     * Reset the Token String in current Thread
     * @param tokenStr
     */
    public static void setToken(String tokenStr) {
        token.set(tokenStr);
    }

    /**
     * 清除线程当中的token信息
     * Remove the Token String in current Thread
     */
    public static void clearAccessKey() {
        token.remove();
    }
}
