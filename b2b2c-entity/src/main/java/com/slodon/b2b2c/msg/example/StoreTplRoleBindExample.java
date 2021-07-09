package com.slodon.b2b2c.msg.example;


import java.io.Serializable;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class StoreTplRoleBindExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer bindIdNotEquals;

    /**
     * 用于批量操作
     */
    private String bindIdIn;

    /**
     * 绑定id
     */
    private Integer bindId;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 商户id
     */
    private Long vendorId;

    /**
     * 店铺权限组id
     */
    private Integer roleId;

    /**
     * 店铺权限组消息模板code集合
     */
    private String tplCodes;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照bindId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;
}