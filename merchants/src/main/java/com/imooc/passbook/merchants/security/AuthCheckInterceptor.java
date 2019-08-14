package com.imooc.passbook.merchants.security;

import com.imooc.passbook.merchants.constant.Constants;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限拦截器
 * 使用该拦截器拦截所有HTTP请求，进行检查，再决定是否放行
 * The Authentication Check Interceptor
 * To Intercept and Check All the HTTP Request
 */
@Component
public class AuthCheckInterceptor implements HandlerInterceptor {
    /**
     * 如果HTTP请求没有token信息，就不能够通过校验
     * If the HTTP has no Token Info in its header, then deny
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String token = httpServletRequest.getHeader(Constants.TOKEN_STRING);

        if(StringUtils.isEmpty(token)) {
            throw new Exception("Header中缺少 " + Constants.TOKEN_STRING);
        }

        if(!token.equals(Constants.TOKEN)) {
            throw new Exception("Header中 " + Constants.TOKEN_STRING + "错误");
        }

        AccessContext.setToken(token);

        return true;
    }

    /**
     * 抛出异常时，postHandle不会生效
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 这里是真正完成请求后执行的清理工作
     * 将线程的token给clear掉
     * clear ther token info in current Thread
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        AccessContext.clearAccessKey();
    }
}
