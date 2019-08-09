package com.imooc.passbook.merchants.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建商户响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMerchantsResponse {

    /**
     * 商户Id：创建失败则为-1
     * 商户创建成功后，服务器返回给商户分配给它的id
     */
    private Integer id;


}
