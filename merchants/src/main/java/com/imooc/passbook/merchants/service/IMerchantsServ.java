package com.imooc.passbook.merchants.service;

import com.imooc.passbook.merchants.vo.CreateMerchantsRequest;
import com.imooc.passbook.merchants.vo.CreateMerchantsResponse;
import com.imooc.passbook.merchants.vo.PassTemplate;
import com.imooc.passbook.merchants.vo.Response;

/**
 * 对商户服务接口定义
 */
public interface IMerchantsServ {

    /**
     * 创建商户服务
     * @param request {@link CreateMerchantsRequest} 创建商户请求
     * @return {@link Response}
     */
    Response createMerchants(CreateMerchantsRequest request);

    /**
     * 根据Id构造商户信息
     * @param id 商户id
     * @return {@link Response}
     */
    Response buildMerchantsInfoById(Integer id);

    /**
     * 投放优惠券
     * @param template {@link PassTemplate} 优惠券对象
     * @return {@link Response}
     */
    Response dropPassTemplate(PassTemplate template);
}
