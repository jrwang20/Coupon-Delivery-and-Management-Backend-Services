package com.imooc.passbook.service;

import com.imooc.passbook.vo.PassTemplate;

/**
 * Pass Hbase服务
 */
public interface IHBasePassService {

    /**
     * 将PassTemplate写入HBase
     * @param passTemplate {@link PassTemplate}
     * @return true/false
     */
    boolean dropPassTemplateToHBase(PassTemplate passTemplate);
}
