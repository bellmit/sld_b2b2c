package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VendorRolesExample implements Serializable {
    private static final long serialVersionUID = -1251859259097188266L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer rolesIdNotEquals;

    /**
     * 用于批量操作
     */
    private String rolesIdIn;

    /**
     * 角色id
     */
    private Integer rolesId;

    /**
     * 角色名称
     */
    private String rolesName;

    /**
     * 角色名称,用于模糊查询
     */
    private String rolesNameLike;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 角色描述
     */
    private String content;

    /**
     * 角色描述,用于模糊查询
     */
    private String contentLike;

    /**
     * 创建人id
     */
    private Long createVendorId;

    /**
     * 创建人名称
     */
    private String createVendorName;

    /**
     * 创建人名称,用于模糊查询
     */
    private String createVendorNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 更新人id
     */
    private Long updateVendorId;

    /**
     * 更新人名称
     */
    private String updateVendorName;

    /**
     * 更新人名称,用于模糊查询
     */
    private String updateVendorNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 状态：1、启用，0、未启用
     */
    private Integer state;

    /**
     * 是否系统内置：1-内置（不可删除、不可修改），0-非内置（可删除、修改）
     */
    private Integer isInner;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照rolesId倒序排列
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