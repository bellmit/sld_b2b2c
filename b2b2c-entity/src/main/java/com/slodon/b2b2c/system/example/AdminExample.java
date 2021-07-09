package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 平台管理员表example
 */
@Data
public class AdminExample implements Serializable {
    private static final long serialVersionUID = 4954977315010529725L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer adminIdNotEquals;

    /**
     * 用于批量操作
     */
    private String adminIdIn;

    /**
     * 管理员id
     */
    private Integer adminId;

    /**
     * 登录名
     */
    private String adminName;

    /**
     * 登录名,用于模糊查询
     */
    private String adminNameLike;

    /**
     * 密码
     */
    private String password;

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
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：1-正常；2-冻结；3-删除（伪删除）
     */
    private Integer state;

    /**
     * stateIn，用于批量操作
     */
    private String stateIn;

    /**
     * stateNotIn，用于批量操作
     */
    private String stateNotIn;

    /**
     * stateNotEquals，用于批量操作
     */
    private Integer stateNotEquals;

    /**
     * 是否超级管理员：1-是；0-否
     */
    private Integer isSuper;

    /**
     * 大于等于开始时间
     */
    private Date loginTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date loginTimeBefore;

    /**
     * 创建管理员ID
     */
    private Integer createAdminId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照adminId倒序排列
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