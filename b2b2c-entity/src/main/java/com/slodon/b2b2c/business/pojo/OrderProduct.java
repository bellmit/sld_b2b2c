package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单货品明细表
 */
@Data
public class OrderProduct implements Serializable {
    private static final long serialVersionUID = -6852129665331958072L;
    @ApiModelProperty("订单货品id")
    private Long orderProductId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("商品分类ID")
    private Integer goodsCategoryId;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("货品图片")
    private String productImage;

    @ApiModelProperty("规格详情")
    private String specValues;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("货品单价，与订单表中goods_amount对应")
    private BigDecimal productShowPrice;

    @ApiModelProperty("商品数量")
    private Integer productNum;

    @ApiModelProperty("货品分摊的活动优惠总额，与订单表中activity_discount_amount对应")
    private BigDecimal activityDiscountAmount;

    @ApiModelProperty("活动优惠明细，json存储(对应List<PromotionInfo>)")
    private String activityDiscountDetail;

    @ApiModelProperty("店铺活动优惠金额（包含优惠券）")
    private BigDecimal storeActivityAmount;

    @ApiModelProperty("平台活动优惠金额（包含积分、优惠券）")
    private BigDecimal platformActivityAmount;

    @ApiModelProperty("店铺优惠券优惠金额")
    private BigDecimal storeVoucherAmount;

    @ApiModelProperty("平台优惠券优惠金额")
    private BigDecimal platformVoucherAmount;

    @ApiModelProperty("使用积分数量")
    private Integer integral;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("订单货品明细金额（用户支付金额，发生退款时最高可退金额）（=货品单价*数量-活动优惠总额）")
    private BigDecimal moneyAmount;

    @ApiModelProperty("商品的平台佣金比率")
    private BigDecimal commissionRate;

    @ApiModelProperty("平台佣金")
    private BigDecimal commissionAmount;

    @ApiModelProperty("拼团团队id（拼团活动使用）")
    private Integer spellTeamId;

    @ApiModelProperty("是否是赠品，0、不是；1、是；")
    private Integer isGift;

    @ApiModelProperty("赠品ID 0:不是赠品；大于0：是赠品，存赠品的ID")
    private Integer giftId;

    @ApiModelProperty("退货数量，默认为0")
    private Integer returnNumber;

    @ApiModelProperty("换货数量，默认为0")
    private Integer replacementNumber;

    @ApiModelProperty("是否评价:0-未评价，1-已评价")
    private Integer isComment;

    @ApiModelProperty("评价时间")
    private Date commentTime;

    @ApiModelProperty(value = "售后按钮，100-退款（商家未发货），200-退款（商家发货,买家未收货），300-申请售后，401-退款中，402-退款完成,403-换货中，404-换货完成", hidden = true)
    private Integer afsButton;

    @ApiModelProperty(value = "售后单号,查看售后详情用", hidden = true)
    private String afsSn;

    @ApiModelProperty(value = "售后状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家处理仅退款申请；201-商家处理退货退款申请；300-退款完成；301-退款关闭（商家拒绝退款申请）", hidden = true)
    private Integer afsState;

    @ApiModelProperty("是否正在售后，0-无售后，1-正在售后")
    private Integer isHasAfs;

}