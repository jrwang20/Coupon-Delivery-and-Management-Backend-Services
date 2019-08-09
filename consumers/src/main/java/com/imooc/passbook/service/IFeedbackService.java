package com.imooc.passbook.service;

import com.imooc.passbook.vo.FeedBack;
import com.imooc.passbook.vo.Response;

/**
 * 评论功能：用户评论相关功能实现
 */
public interface IFeedbackService {

    /**
     * 创建品论
     * @param feedBack {@Feedback}
     * @return {@link Response}
     */
    Response createFeedback(FeedBack feedBack);

    /**
     * 根据userId，获取该用户的所有评论
     * @param userId 用户id
     * @return {@link Response}
     */
    Response getFeedback(Long userId);

}
