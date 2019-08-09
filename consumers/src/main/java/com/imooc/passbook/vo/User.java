package com.imooc.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * 用户id
     * RowKey
     */
    private Long id;

    /**
     * 用户基本信息
     */
    private BaseInfo baseInfo;

    /**
     * 用户额外信息
     */
    private OtherInfo otherInfo;

    /**
     * b列族
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseInfo {
        private String name;
        private Integer age;
        private String sex;
    }

    /**
     * o列族
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OtherInfo {
        private String phone;
        private String address;
    }

}
