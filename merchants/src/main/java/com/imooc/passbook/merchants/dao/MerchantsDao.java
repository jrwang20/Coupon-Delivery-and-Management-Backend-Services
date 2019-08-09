package com.imooc.passbook.merchants.dao;

import com.imooc.passbook.merchants.entity.Merchants;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Merchants DAO 接口
 */
public interface MerchantsDao extends JpaRepository<Merchants, Integer> {

    /**
     * 根据Id获取商户对象
     * @param id 商户id
     * @return {@link Merchants}
     */
    Merchants findById(Integer id);

    /**
     * 根据商户名称获取商户对象
     * @param name 商户名称
     * @return {@link Merchants}
     */
    Merchants findByName(String name);
}
