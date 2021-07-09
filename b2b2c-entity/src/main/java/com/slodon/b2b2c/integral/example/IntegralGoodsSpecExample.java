package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分商品规格表example
 */
@Data
public class IntegralGoodsSpecExample implements Serializable {
    private static final long serialVersionUID = 6365845201101617453L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer specIdNotEquals;

    /**
     * 用于批量操作
     */
    private String specIdIn;

    /**
     * 规格id
     */
    private Integer specId;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 规格名称,用于模糊查询
     */
    private String specNameLike;

    /**
     * 规格类型1、文字；2、图片
     */
    private Integer specType;

    /**
     * 店铺id，0为系统创建
     */
    private Long storeId;

    /**
     * 创建人id（如果是系统创建是adminID，如果是商户是vendorID）
     */
    private Integer createId;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 创建人名称,用于模糊查询
     */
    private String createNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 更新人id（如果是系统更新是adminID，如果是商户是vendorID）
     */
    private Integer updateId;

    /**
     * 更新人名称
     */
    private String updateName;

    /**
     * 更新人名称,用于模糊查询
     */
    private String updateNameLike;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 状态 0-不展示 1-展示，可以删除，但是必须没有其他商品使用
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
     * 排序
     */
    private Integer sort;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照specId倒序排列
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