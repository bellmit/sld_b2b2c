package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 阶梯团订单扩展信息表
 */
@Data
public class LadderGroupOrderExtend implements Serializable {
    private static final long serialVersionUID = -442850685309252323L;

    @ApiModelProperty("扩展id")
    private Integer extendId;

    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("货品id（sku）")
    private Long productId;

    @ApiModelProperty("商品数量")
    private Integer productNum;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("定金支付单号（关联order_pay表）")
    private String depositPaySn;

    @ApiModelProperty("尾款支付单号（关联order_pay表）")
    private String remainPaySn;

    @ApiModelProperty("订单子状态：101-待付定金；102-待付尾款；103-已完成付款；0-已取消")
    private Integer orderSubState;

    @ApiModelProperty("预付定金")
    private BigDecimal advanceDeposit;

    @ApiModelProperty("尾款金额")
    private BigDecimal remainAmount;

    @ApiModelProperty("尾款支付的开始时间")
    private Date remainStartTime;

    @ApiModelProperty("尾款支付的截止时间")
    private Date remainEndTime;

    @ApiModelProperty("参团时间")
    private Date participateTime;

    @ApiModelProperty("成功时间")
    private Date successTime;
}