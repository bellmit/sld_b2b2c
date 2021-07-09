package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分商品规格值表example
 */
@Data
public class IntegralGoodsSpecValueExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer specValueIdNotEquals;

    /**
     * 用于批量操作
     */
    private String specValueIdIn;

    /**
     * 规格值id
     */
    private Integer specValueId;

    /**
     * 规格值
     */
    private String specValue;

    /**
     * 规格ID
     */
    private Integer specId;

    /**
     * 店铺id，0为系统创建
     */
    private Long storeId;

    /**
     * 创建人
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
     * 排序条件，条件之间用逗号隔开，如果不传则按照specValueId倒序排列
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