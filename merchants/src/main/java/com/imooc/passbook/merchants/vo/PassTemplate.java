package com.imooc.passbook.merchants.vo;

import com.imooc.passbook.merchants.constant.ErrorCode;
import com.imooc.passbook.merchants.dao.MerchantsDao;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassTemplate {

    /**
     * 所属商户Id
     */
    private Integer id;

    /**
     * 优惠券标题
     */
    private String title;

    /**
     * 优惠券摘要
     */
    private String summary;

    /**
     * 优惠券详细信息
     */
    private String desc;

    /**
     * 最大个数显示
     */
    private Long limit;

    /**
     * 优惠券是否有token，用于商户核销
     * token存储于redis，每次从中获取
     */
    private Boolean hasToken;

    /**
     * 优惠券背景颜色
     */
    private Integer background;

    /**
     * 优惠券开始时间
     */
    private Date start;

    /**
     * 优惠券结束时间
     */
    private Date end;

    /**
     * 校验优惠券对象的有效性
     * 即，对当前PassTemplate的所属商户ID进行校验，检查是否已经保存在数据库当中
     * @param merchantsDao {@link MerchantsDao}
     * @return {@link ErrorCode}
     */
    public ErrorCode validate(MerchantsDao merchantsDao) {
        if(null == merchantsDao.findById(id)) {
            return ErrorCode.MERCHANTS_NOT_EXIST;
        }

        return ErrorCode.SUCCESS;
    }
}
