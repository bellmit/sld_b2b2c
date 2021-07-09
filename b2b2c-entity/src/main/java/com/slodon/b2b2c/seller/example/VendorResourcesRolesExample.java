package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VendorResourcesRolesExample implements Serializable {
    private static final long serialVersionUID = -1200083838993063089L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer resourcesRolesIdNotEquals;

    /**
     * 用于批量操作
     */
    private String resourcesRolesIdIn;

    /**
     * 资源角色id
     */
    private Integer resourcesRolesId;

    /**
     * 资源id
     */
    private Integer resourcesId;

    /**
     * resourcesIdIn
     */
    private String resourcesIdIn;

    /**
     * 角色id
     */
    private Integer rolesId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照resourcesRolesId倒序排列
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