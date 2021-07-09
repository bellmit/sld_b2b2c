package com.slodon.b2b2c.cms.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class ArticleCategoryExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer categoryIdNotEquals;

    /**
     * 用于批量操作
     */
    private String categoryIdIn;

    /**
     * 分类id
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 分类名称,用于模糊查询
     */
    private String categoryNameLike;

    /**
     * 排序：序号越小，越靠前
     */
    private Integer sort;

    /**
     * 是否显示：0、不显示；1、显示
     */
    private Integer isShow;

    /**
     * 创建人id
     */
    private Integer createId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照categoryId倒序排列
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