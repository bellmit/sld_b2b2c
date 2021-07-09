package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class StoreCategoryExample implements Serializable {
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
     * 分类ID
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
     * 父ID
     */
    private Integer parentId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 分类级别：1-一级分类，2-二级分类
     */
    private Integer grade;

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