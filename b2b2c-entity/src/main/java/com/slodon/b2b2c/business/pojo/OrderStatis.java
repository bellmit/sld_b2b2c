package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单统计表
 */
@Data
public class OrderStatis implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("自增物理主键")
    private Integer id;

    @ApiModelProperty("统计编号(年月)")
    private Integer osMonth;

    @ApiModelProperty("年")
    private Integer osYear;

    @ApiModelProperty("开始日期")
    private Integer osStartDate;

    @ApiModelProperty("结束日期")
    private Integer osEndDate;

    @ApiModelProperty("订单金额")
    private BigDecimal osOrderTotals;

    @ApiModelProperty("运费")
    private BigDecimal osShippingTotals;

    @ApiModelProperty("退单金额")
    private BigDecimal osOrderReturnTotals;

    @ApiModelProperty("佣金金额")
    private BigDecimal osCommisTotals;

    @ApiModelProperty("退还佣金")
    private BigDecimal osCommisReturnTotals;

    @ApiModelProperty("店铺促销活动费用")
    private BigDecimal osStoreCostTotals;

    @ApiModelProperty("本期应结")
    private BigDecimal osResultTotals;

    @ApiModelProperty("创建记录日期")
    private Integer osCreateDate;
}