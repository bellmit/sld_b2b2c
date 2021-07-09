package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class VendorResourcesExample implements Serializable {
    private static final long serialVersionUID = 5704314564287927988L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer resourcesIdNotEquals;

    /**
     * 用于批量操作
     */
    private String resourcesIdIn;

    /**
     * 资源id
     */
    private Integer resourcesId;

    /**
     * 父级id
     */
    private Integer pid;

    /**
     * 父级id
     */
    private String pidIn;

    /**
     * 父级id不等于
     */
    private Integer pidNotEquals;

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
     * 资源状态：1-未删除 ;2-删除
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
     * 前端对应路由不等于
     */
    private String frontPathNotEquals;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照resourcesId倒序排列
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