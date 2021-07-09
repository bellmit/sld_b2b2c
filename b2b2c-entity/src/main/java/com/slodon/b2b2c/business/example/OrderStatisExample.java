package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OrderStatisExample implements Serializable {
    private static final long serialVersionUID = 2962599717337163648L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer idNotEquals;

    /**
     * 用于批量操作
     */
    private String idIn;

    /**
     * 自增物理主键
     */
    private Integer id;

    /**
     * 统计编号(年月)
     */
    private Integer osMonth;

    /**
     * 年
     */
    private Integer osYear;

    /**
     * 开始日期
     */
    private Integer osStartDate;

    /**
     * 结束日期
     */
    private Integer osEndDate;

    /**
     * 订单金额
     */
    private BigDecimal osOrderTotals;

    /**
     * 运费
     */
    private BigDecimal osShippingTotals;

    /**
     * 退单金额
     */
    private BigDecimal osOrderReturnTotals;

    /**
     * 佣金金额
     */
    private BigDecimal osCommisTotals;

    /**
     * 退还佣金
     */
    private BigDecimal osCommisReturnTotals;

    /**
     * 店铺促销活动费用
     */
    private BigDecimal osStoreCostTotals;

    /**
     * 本期应结
     */
    private BigDecimal osResultTotals;

    /**
     * 创建记录日期
     */
    private Integer osCreateDate;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照id倒序排列
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