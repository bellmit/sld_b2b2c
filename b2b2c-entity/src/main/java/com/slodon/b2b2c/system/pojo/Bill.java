package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算表
 */
@Data
public class Bill implements Serializable {
    private static final long serialVersionUID = -3841187605711232004L;
    @ApiModelProperty("结算id")
    private Integer billId;

    @ApiModelProperty("结算单号")
    private String billSn;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("结算开始时间")
    private Date startTime;

    @ApiModelProperty("结算结束时间")
    private Date endTime;

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

    @ApiModelProperty("应结金额(订单金额-平台佣金+退还佣金-退单金额-平台活动费用+平台优惠券+积分抵现金额)")
    private BigDecimal settleAmount;

    @ApiModelProperty("结算状态：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成")
    private Integer state;

    @ApiModelProperty("打款备注")
    private String paymentRemark;

    @ApiModelProperty("打款凭证")
    private String paymentEvidence;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}