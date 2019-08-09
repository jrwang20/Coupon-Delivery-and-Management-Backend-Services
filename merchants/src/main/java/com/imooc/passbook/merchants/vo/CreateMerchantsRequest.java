package com.imooc.passbook.merchants.vo;

import com.imooc.passbook.merchants.constant.ErrorCode;
import com.imooc.passbook.merchants.dao.MerchantsDao;
import com.imooc.passbook.merchants.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * 创建商户请求对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMerchantsRequest {

    /**
     * 商户名称
     */
    private String name;

    /**
     * 商户logo
     */
    private String logoUrl;

    /**
     * 商户营业执照
     */
    private String businessLicenseUrl;

    /**
     * 商户联系电话
     */
    private String phone;

    /**
     * 商户地址
     */
    private String address;

    /**
     * 验证请求的有效性
     * 即，新传入的商户名称是否已经存在
     * 另外，校验创建请求中的各个必须的字段是否都填充完毕
     * @param merchantsDao {@link MerchantsDao}
     * @return {@link ErrorCode}
     */
    public ErrorCode validate(MerchantsDao merchantsDao) {

        if(merchantsDao.findByName(this.name) != null) {
            return ErrorCode.DUPLICATE_NAME;
        }

        if(null == this.logoUrl) {
            return ErrorCode.EMPTY_LOGO;
        }

        if(null == this.businessLicenseUrl) {
            return ErrorCode.EMPTY_BUSINESS_LICENSE;
        }

        if(null == this.address) {
            return ErrorCode.EMPTY_ADDRESS;
        }

        if(null == this.phone) {
            return ErrorCode.ERROR_PHONE;
        }

        return ErrorCode.SUCCESS;
    }

    /**
     * 将请求对象，转换为商户对象
     * 即，将通过校验的请求中的数据，转化成和数据库表互相map的Entity对象
     * @return {@link Merchants}
     */
    public Merchants toMerChants() {

        Merchants merchants = new Merchants();

//        BeanUtils.copyProperties(this, merchants);
        merchants.setName(name);
        merchants.setLogoUrl(logoUrl);
        merchants.setBusinessLicenseUrl(businessLicenseUrl);
        merchants.setPhone(phone);
        merchants.setAddress(address);

        return merchants;

    }

}
