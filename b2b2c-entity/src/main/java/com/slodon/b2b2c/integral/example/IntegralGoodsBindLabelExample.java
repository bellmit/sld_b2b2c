package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 积分商品绑定标签表example
 */
@Data
public class IntegralGoodsBindLabelExample implements Serializable {
    private static final long serialVersionUID = 2831084118454711284L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer bindIdNotEquals;

    /**
     * 用于批量操作
     */
    private String bindIdIn;

    /**
     * 绑定id
     */
    private Integer bindId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 用于批量操作
     */
    private String goodsIdIn;

    /**
     * 一级标签id
     */
    private Integer labelId1;

    /**
     * 二级标签id
     */
    private Integer labelId2;

    /**
     * 标签等级
     */
    private Integer grade;

    /**
     * 标签路径
     */
    private String labelPath;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照bindId倒序排列
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