package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算单与订单绑定表
 */
@Data
public class BillOrderBind implements Serializable {
    private static final long serialVersionUID = 5722385946318391823L;
    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("结算单号")
    private String billSn;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("平台佣金")
    private BigDecimal commission;

    @ApiModelProperty("退还佣金")
    private BigDecimal refundCommission;

    @ApiModelProperty("退单金额")
    private BigDecimal refundAmount;

    @ApiModelProperty("平台活动优惠金额")
    private BigDecimal platformActivityAmount;

    @ApiModelProperty("平台优惠券")
    private BigDecimal platformVoucherAmount;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("赔偿金额(定金倍数)")
    private BigDecimal compensationAmount;

    @ApiModelProperty("下单时间")
    private Date createTime;

    @ApiModelProperty("完成时间")
    private Date finishTime;
}