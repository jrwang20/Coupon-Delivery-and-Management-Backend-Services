package com.imooc.passbook.vo;

import com.google.common.base.Enums;
import com.imooc.passbook.constant.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户评论表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedBack {

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 评论类型
     */
    private String type;

    /**
     * PassTemplate的rowKey，外键关联
     */
    private String templateId;

    /**
     * 评论内容
     */
    private String comment;

    /**
     * 内置评论校验方法
     * 对评论的类型和评论内容进行校验，评论类型必须为Enum中规定的类型，并且评论不为空
     * @return boolean
     */
    public boolean validate() {
        FeedbackType feedbackType = Enums.getIfPresent(
                FeedbackType.class, this.type.toUpperCase()
        ).orNull();

        return !(null == feedbackType || null == comment);
    }
}
