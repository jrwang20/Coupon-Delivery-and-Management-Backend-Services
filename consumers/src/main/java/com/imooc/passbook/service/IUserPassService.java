package com.imooc.passbook.service;

import com.imooc.passbook.vo.Pass;
import com.imooc.passbook.vo.Response;

/**
 * 获取各类用户个人的优惠券信息列表
 */
public interface IUserPassService {

    /**
     * 获取用户个人优惠券信息，即"我的优惠券"功能实现
     * @param userId 用户Id
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserPassInfo(Long userId) throws Exception;

    /**
     * 获取用户已消费优惠券，即"已使用的优惠券"功能实现
     * @param userId 用户Id
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserUsedPassInfo(Long userId) throws Exception;

    /**
     * 获取用户所有的优惠券，即前两个功能汇总起来
     * @param userId
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserAllPassInfo(Long userId) throws Exception;

    /**
     * 用户使用优惠券
     * @param pass {@link Pass}
     * @return {@link Response}
     */
    Response userUsePass(Pass pass);
}
