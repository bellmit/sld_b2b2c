package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SystemResourceExample implements Serializable {
    private static final long serialVersionUID = -1379011293892681489L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer resourceIdNotEquals;

    /**
     * 用于批量操作
     */
    private String resourceIdIn;

    /**
     * 资源id
     */
    private Integer resourceId;

    /**
     * 父级id
     */
    private Integer pid;

    /**
     * 资源名称
     */
    private String content;

    /**
     * 资源名称,用于模糊查询
     */
    private String contentLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 资源状态：0-未启用，1-启用
     */
    private Integer state;

    /**
     * 资源等级：1-一级菜单，2-二级菜单，3-三级菜单，4-按钮
     */
    private Integer grade;

    /**
     * 对应路由
     */
    private String url;

    /**
     * 前端对应路由
     */
    private String frontPath;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照resourceId倒序排列
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

    /**
     * 角色id
     */
    private Integer roleId;
}