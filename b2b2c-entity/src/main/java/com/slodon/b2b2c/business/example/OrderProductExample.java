package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderProductExample implements Serializable {
    private static final long serialVersionUID = 6560236678438743562L;

    /**
     * 用于编辑时的重复判断
     */
    private Long orderProductIdNotEquals;

    /**
     * 用于批量操作
     */
    private String orderProductIdIn;

    /**
     * orderProductIdNotIn,用于批量操作
     */
    private String orderProductIdNotIn;

    /**
     * 订单货品id
     */
    private Long orderProductId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 商家ID
     */
    private Long storeId;

    /**
     * 商家名称
     */
    private String storeName;

    /**
     * 商家名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 商品分类ID
     */
    private Integer goodsCategoryId;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品名称,用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 货品图片
     */
    private String productImage;

    /**
     * 规格详情
     */
    private String specValues;

    /**
     * 货品ID
     */
    private Long productId;

    /**
     * 货品单价，与订单表中goods_amount对应
     */
    private BigDecimal productShowPrice;

    /**
     * 商品数量
     */
    private Integer productNum;

    /**
     * 货品分摊的活动优惠总额，与订单表中activity_discount_amount对应
     */
    private BigDecimal activityDiscountAmount;

    /**
     * 活动优惠明细，json存储
     */
    private String activityDiscountDetail;

    /**
     * 店铺活动优惠金额
     */
    private BigDecimal storeActivityAmount;

    /**
     * 平台活动优惠金额
     */
    private BigDecimal platformActivityAmount;

    /**
     * 店铺优惠券优惠金额
     */
    private BigDecimal storeVoucherAmount;

    /**
     * 平台优惠券优惠金额
     */
    private BigDecimal platformVoucherAmount;

    /**
     * 使用积分数量
     */
    private Integer integral;

    /**
     * 积分抵扣金额
     */
    private BigDecimal integralCashAmount;

    /**
     * 订单货品明细金额（用户支付金额，发生退款时最高可退金额）（=货品单价*数量-活动优惠总额）
     */
    private BigDecimal moneyAmount;

    /**
     * 商品的平台佣金比率
     */
    private BigDecimal commissionRate;

    /**
     * 平台佣金
     */
    private BigDecimal commissionAmount;

    /**
     * 拼团团队id（拼团活动使用）
     */
    private Integer spellTeamId;

    /**
     * 是否是赠品，0、不是；1、是；
     */
    private Integer isGift;

    /**
     * 赠品ID 0:不是赠品；大于0：是赠品，存赠品的ID
     */
    private Integer giftId;

    /**
     * 退货数量，默认为0
     */
    private Integer returnNumber;

    /**
     * 换货数量，默认为0
     */
    private Integer replacementNumber;

    /**
     * 是否评价:0-未评价，1-已评价
     */
    private Integer isComment;

    /**
     * 大于等于开始时间
     */
    private Date commentTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date commentTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照orderProductId倒序排列
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