package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预售订单扩展信息表
 */
@Data
public class PresellOrderExtend implements Serializable {
    private static final long serialVersionUID = -6576788456044224071L;
    @ApiModelProperty("扩展id")
    private Integer extendId;

    @ApiModelProperty("预售活动id")
    private Integer presellId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("定金支付单号（关联order_pay表）")
    private String depositPaySn;

    @ApiModelProperty("尾款支付单号（关联order_pay表）")
    private String remainPaySn;

    @ApiModelProperty("订单状态：101-待付定金；102-待付尾款；103-已付全款")
    private Integer orderSubState;

    @ApiModelProperty("活动商品id(spu)")
    private Long goodsId;

    @ApiModelProperty("活动商品id(sku)")
    private Long productId;

    @ApiModelProperty("商品数量")
    private Integer productNum;

    @ApiModelProperty("预售价格")
    private BigDecimal presellPrice;

    @ApiModelProperty("定金可以抵现的金额（全款预售不需要此项，定金预售需要）")
    private BigDecimal firstExpand;

    @ApiModelProperty("定金金额")
    private BigDecimal depositAmount;

    @ApiModelProperty("尾款金额")
    private BigDecimal remainAmount;

    @ApiModelProperty("是否全款订单：1-全款订单，0-定金预售订单")
    private Integer isAllPay;

    @ApiModelProperty("支付定金截止时间")
    private Date depositEndTime;

    @ApiModelProperty("尾款支付的开始时间")
    private Date remainStartTime;

    @ApiModelProperty("尾款支付的截止时间")
    private Date remainEndTime;

    @ApiModelProperty("发货时间")
    private Date deliverTime;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}