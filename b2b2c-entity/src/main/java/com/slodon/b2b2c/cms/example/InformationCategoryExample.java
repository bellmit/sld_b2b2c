package com.slodon.b2b2c.cms.example;


import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class InformationCategoryExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer cateIdNotEquals;

    /**
     * 用于批量操作
     */
    private String cateIdIn;

    /**
     * 分类id
     */
    private Integer cateId;

    /**
     * 分类名称
     */
    private String cateName;

    /**
     * 分类名称,用于模糊查询
     */
    private String cateNameLike;

    /**
     * 排序，值越小越靠前
     */
    private Integer sort;

    /**
     * 是否显示，0-不显示，1-显示，默认0
     */
    private Integer isShow;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

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
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

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
     * 排序条件，条件之间用逗号隔开，如果不传则按照cateId倒序排列
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