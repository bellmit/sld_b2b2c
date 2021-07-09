package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class CouponGoodsCategoryExample implements Serializable {
    private static final long serialVersionUID = -5836492132789985752L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer couponCategoryIdNotEquals;

    /**
     * 用于批量操作
     */
    private String couponCategoryIdIn;

    /**
     * 优惠活动商品分类ID
     */
    private Integer couponCategoryId;

    /**
     * 优惠券ID
     */
    private Integer couponId;

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
     * 分类级别（1、2、3级分类）
     */
    private Integer categoryGrade;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照couponCategoryId倒序排列
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
     * 用于批量操作
     */
    private String categoryIdIn;
}