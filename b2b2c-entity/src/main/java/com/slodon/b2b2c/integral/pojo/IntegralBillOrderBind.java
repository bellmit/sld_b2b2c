package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算单与订单绑定表
 */
@Data
public class IntegralBillOrderBind implements Serializable {
    private static final long serialVersionUID = -2956526086314021573L;
    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("结算单号")
    private String billSn;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("现金使用金额")
    private BigDecimal cashAmount;

    @ApiModelProperty("积分使用数量")
    private Integer integral;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("下单时间")
    private Date createTime;

    @ApiModelProperty("完成时间")
    private Date finishTime;
}