package com.imooc.passbook.dao;

import com.imooc.passbook.entity.Merchants;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Merchants Dao 接口
 */
public interface MerchantsDao extends JpaRepository<Merchants, Integer> {

    /**
     * 通过id获取商户entity对象
     * @param id 商户id
     * @return {@link Merchants}
     */
    Merchants findById(Integer id);

    /**
     * 通过id获取商户entity对象
     * @param name 商户名称
     * @return {@link Merchants}
     */
    Merchants findByName(String name);

    /**
     * 根据一组商户的id，获取一组商户entity对象
     * @param idList 商户idList
     * @return {@link Merchants}
     */
    List<Merchants> findByIdIn(List<Integer> idList);
}
