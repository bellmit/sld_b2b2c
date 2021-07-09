package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SystemRoleExample implements Serializable {
    private static final long serialVersionUID = 8899710563548079613L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer roleIdNotEquals;

    /**
     * 用于批量操作
     */
    private String roleIdIn;

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色名称,用于模糊查询
     */
    private String roleNameLike;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 创建人id
     */
    private Integer createAdminId;

    /**
     * 创建人名称
     */
    private String createAdminName;

    /**
     * 创建人名称,用于模糊查询
     */
    private String createAdminNameLike;

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
    private Integer updateAdminId;

    /**
     * 更新人名称
     */
    private String updateAdminName;

    /**
     * 更新人名称,用于模糊查询
     */
    private String updateAdminNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 状态：0-未启用，1-启用
     */
    private Integer state;

    /**
     * 是否系统内置 1-内置（不可删除、不可修改），0-非内置（可删除、修改）
     */
    private Integer isInner;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照roleId倒序排列
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