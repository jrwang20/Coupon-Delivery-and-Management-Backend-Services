package com.imooc.passbook.controller;

import com.imooc.passbook.log.LogConstants;
import com.imooc.passbook.log.LogGenerator;
import com.imooc.passbook.service.IFeedbackService;
import com.imooc.passbook.service.IGainPassTemplateService;
import com.imooc.passbook.service.IInventoryService;
import com.imooc.passbook.service.IUserPassService;
import com.imooc.passbook.vo.FeedBack;
import com.imooc.passbook.vo.GainPassTemplateRequest;
import com.imooc.passbook.vo.Pass;
import com.imooc.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Passbook Rest Controller
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class PassbookController {

    /** 用户个人优惠券信息及使用服务 */
    @Autowired
    private IUserPassService userPassService;

    /** 优惠券库存服务 */
    @Autowired
    private IInventoryService inventoryService;

    /** 领取优惠券服务 */
    @Autowired
    private IGainPassTemplateService gainPassTemplateService;

    /** 用户反馈服务 */
    @Autowired
    private IFeedbackService feedbackService;

    /** 封装了用户请求，需要通过servlet获取ip地址，打印日志 */
    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     * 获取用户可用优惠券信息
     * @param userId 用户Id
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/userpassinfo")
    Response userPassInfo(@RequestParam("userId") Long userId) throws Exception {

        //1. 首先打印日志
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.USER_PATH_INFO,
                null
        );

        //2. 实现功能
        return userPassService.getUserPassInfo(userId);
    }

    /**
     * 获取用户已使用的优惠券信息
     * @param userId 用户Id
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/userusedpassinfo")
    Response userUsedPassInfo(@RequestParam("userId") Long userId) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.USER_USED_PATH_INFO,
                null
        );

        return userPassService.getUserUsedPassInfo(userId);
    }

    /**
     * 用户使用优惠券
     * @param pass {@link Pass} 用户+优惠券
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/userusepass")
    Response userUsePass(@RequestBody Pass pass) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                pass.getUserId(),
                LogConstants.ActionName.USER_USE_PASS,
                pass
        );

        return userPassService.userUsePass(pass);
    }

    /**
     * 获取库存信息
     * @param userId 用户Id
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/inventoryInfo")
    Response inventoryInfo(@RequestParam("userId") Long userId) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.INVENTORY_INFO,
                null
        );

        return inventoryService.getInventoryInfo(userId);
    }

    /**
     * 用户领取优惠券
     * @param gainPassTemplateRequest {@link GainPassTemplateRequest}
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/gainpasstemplate")
    Response gainPassTemplate(@RequestBody GainPassTemplateRequest gainPassTemplateRequest) throws Exception {

        LogGenerator.genLog(
                httpServletRequest,
                gainPassTemplateRequest.getUserId(),
                LogConstants.ActionName.GAIN_PASS_TEMPLATE,
                gainPassTemplateRequest
        );

        return gainPassTemplateService.gainPassTemplate(gainPassTemplateRequest);
    }

    /**
     * 用户创建评论
     * @param feedBack {@link FeedBack}
     * @return
     */
    @ResponseBody
    @PostMapping("/createfeedback")
    Response createFeedback(@RequestBody FeedBack feedBack) {
        LogGenerator.genLog(
                httpServletRequest,
                feedBack.getUserId(),
                LogConstants.ActionName.CREATE_FEEDBACK,
                feedBack
        );

        return feedbackService.createFeedback(feedBack);
    }

    /**
     * 获取当前用户评论信息
     * @param userId
     * @return
     */
    @ResponseBody
    @GetMapping("/getfeedback")
    Response getFeedback(@RequestParam("userId") Long userId) {

        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.GET_FEEDBACK,
                null
        );

        return feedbackService.getFeedback(userId);
    }

    /**
     * 异常演示接口
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/exception")
    Response exception() throws Exception {
        throw new Exception("Welcome to IMooc");
    }


}
