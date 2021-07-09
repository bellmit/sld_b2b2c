package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单活动赠送优惠券表
 */
@Data
public class OrderPromotionSendCoupon implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("赠送优惠券id")
    private Integer sendCouponId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("活动等级 1-商品活动；2-店铺活动；3-平台活动;4-积分、优惠券")
    private Integer promotionGrade;

    @ApiModelProperty("活动类型  ")
    private Integer promotionType;

    @ApiModelProperty("活动id，积分抵扣时为0,优惠券为优惠券code，其他为活动id")
    private String promotionId;

    @ApiModelProperty("是否店铺活动，1-是，0-否")
    private Integer isStore;

    @ApiModelProperty("赠品id")
    private Integer couponId;

    @ApiModelProperty("赠品数量")
    private Integer number;
}