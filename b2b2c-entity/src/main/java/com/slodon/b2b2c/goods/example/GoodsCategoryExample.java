package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品分类example
 */
@Data
public class GoodsCategoryExample implements Serializable {
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
     * 分类别名
     */
    private String categoryAlias;

    /**
     * 父类ID
     */
    private Integer pid;

    /**
     * 分类描述
     */
    private String description;

    /**
     * 上级分类路径
     */
    private String path;

    /**
     * 分佣比例(商家给平台的分佣比例，填写0到1的数字)
     */
    private BigDecimal scaling;

    /**
     * 创建人id
     */
    private Integer createAdminId;

    /**
     * 更新人id
     */
    private Integer updateAdminId;

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
     * 排序
     */
    private Integer sort;

    /**
     * 分类状态：0-未启用；1-启用；默认是1
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
     * 分类级别: 1-一级，2-二级，3-三级
     */
    private Integer grade;

    /**
     * 移动端分类图片路径（仅在二三级分类使用）
     */
    private String categoryImage;

    /**
     * 广告图（仅在PC端一级分类下使用）
     */
    private String recommendPicture;

    /**
     * 移动端图片（仅在一级分类使用）
     */
    private String mobileImage;

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