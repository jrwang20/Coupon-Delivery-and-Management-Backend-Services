package com.imooc.passbook.service;

import com.imooc.passbook.constant.Constants;
import com.imooc.passbook.constant.FeedbackType;
import com.imooc.passbook.vo.FeedBack;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IFeedbackServiceTest extends AbstractServiceTest{

    @Autowired
    IFeedbackService feedbackService;

    @Test
    public void createFeedback() {

        FeedBack feedBack = new FeedBack();
        feedBack.setUserId(userId);
        feedBack.setType(FeedbackType.APP.getCode());
        feedBack.setTemplateId("-1");
        feedBack.setComment("distributed coupon project");

        FeedBack passFeedback = new FeedBack();
        passFeedback.setUserId(userId);
        passFeedback.setType(FeedbackType.PASS.getCode());
        passFeedback.setTemplateId("99a8183d069aadfb629077d843912ceb");
        passFeedback.setComment("current pass template");

        System.out.println(feedbackService.createFeedback(feedBack));
        System.out.println(feedbackService.createFeedback(passFeedback));

    }

    @Test
    public void getFeedback() {
        System.out.println(feedbackService.getFeedback(userId));
    }
}