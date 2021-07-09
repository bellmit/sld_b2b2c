package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员优惠券领取表、使用表
 */
@Data
public class CouponMember implements Serializable {
    private static final long serialVersionUID = -2220833518617032164L;
    @ApiModelProperty("会员优惠券ID")
    private Integer couponMemberId;

    @ApiModelProperty("优惠券id")
    private Integer couponId;

    @ApiModelProperty("优惠券编码")
    private String couponCode;

    @ApiModelProperty("店铺id，0为平台")
    private Long storeId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("领取时间")
    private Date receiveTime;

    @ApiModelProperty("使用时间")
    private Date useTime;

    @ApiModelProperty("使用状态(1-未使用；2-使用；3-过期无效）")
    private Integer useState;

    @ApiModelProperty("使用订单号")
    private String orderSn;

    @ApiModelProperty("使用限制-使用时间类型(固定起始时间）")
    private Date effectiveStart;

    @ApiModelProperty("使用限制-使用时间类型(固定结束时间）")
    private Date effectiveEnd;

    @ApiModelProperty("适用商品类型(1-全部商品；2-指定商品；3-指定分类）")
    private Integer useType;

    @ApiModelProperty("随机金额（领取随机金额券必填）")
    private BigDecimal randomAmount;

    @ApiModelProperty("是否已经提醒过用户 1==未提醒  2==提醒过")
    private Integer expiredNoticeState;
}