package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预售订单扩展信息表example
 */
@Data
public class PresellOrderExtendExample implements Serializable {
    private static final long serialVersionUID = -720767517316934813L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer extendIdNotEquals;

    /**
     * 用于批量操作
     */
    private String extendIdIn;

    /**
     * 扩展id
     */
    private Integer extendId;

    /**
     * 预售活动id
     */
    private Integer presellId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 用于批量操作
     */
    private String orderSnIn;

    /**
     * 定金支付单号（关联order_pay表）
     */
    private String depositPaySn;

    /**
     * 定金支付单号（关联order_pay表）,用于模糊查询
     */
    private String depositPaySnLike;

    /**
     * 尾款支付单号（关联order_pay表）
     */
    private String remainPaySn;

    /**
     * 尾款支付单号（关联order_pay表）,用于模糊查询
     */
    private String remainPaySnLike;

    /**
     * 订单状态：101-待付定金；102-待付尾款；103-已付全款
     */
    private Integer orderSubState;

    /**
     * orderSubStateIn，用于批量操作
     */
    private String orderSubStateIn;

    /**
     * orderSubStateNotIn，用于批量操作
     */
    private String orderSubStateNotIn;

    /**
     * orderSubStateNotEquals，用于批量操作
     */
    private Integer orderSubStateNotEquals;

    /**
     * 活动商品id(spu)
     */
    private Long goodsId;

    /**
     * 活动商品id(sku)
     */
    private Long productId;

    /**
     * 预售价格
     */
    private BigDecimal presellPrice;

    /**
     * 定金可以抵现的金额（全款预售不需要此项，定金预售需要）
     */
    private BigDecimal firstExpand;

    /**
     * 定金金额
     */
    private BigDecimal depositAmount;

    /**
     * 尾款金额
     */
    private BigDecimal remainAmount;

    /**
     * 是否全款订单：1-全款订单，0-定金预售订单
     */
    private Integer isAllPay;

    /**
     * 大于等于开始时间
     */
    private Date depositEndTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date depositEndTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date remainStartTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date remainStartTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date remainEndTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date remainEndTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date deliverTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date deliverTimeBefore;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照extendId倒序排列
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