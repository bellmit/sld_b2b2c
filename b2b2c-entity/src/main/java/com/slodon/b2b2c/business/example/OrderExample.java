package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单example
 */
@Data
public class OrderExample implements Serializable {

    private static final long serialVersionUID = 1413935572667961795L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer orderIdNotEquals;

    /**
     * 用于批量操作
     */
    private String orderIdIn;

    /**
     * 订单id
     */
    private Integer orderId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLikes;

    /**
     * 用于批量操作
     */
    private String orderSnIn;

    /**
     * 支付单号
     */
    private String paySn;

    /**
     * 支付单号,用于模糊查询
     */
    private String paySnLike;

    /**
     * 父订单号，无需拆单时，父订单号=订单号
     */
    private String parentSn;

    /**
     * 父订单号，无需拆单时，父订单号=订单号,用于模糊查询
     */
    private String parentSnLike;

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
     * 买家name
     */
    private String memberName;

    /**
     * 买家name,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 买家ID
     */
    private Integer memberId;

    /**
     * 大于等于开始时间
     */
    private Date payTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date payTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date finishTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date finishTimeBefore;

    /**
     * 订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭
     */
    private Integer orderState;

    /**
     * orderStateIn，用于批量操作
     */
    private String orderStateIn;

    /**
     * orderStateNotIn，用于批量操作
     */
    private String orderStateNotIn;

    /**
     * orderStateNotEquals，用于批量操作
     */
    private Integer orderStateNotEquals;

    /**
     * 支付方式名称，参考OrderPaymentConst类
     */
    private String paymentName;

    /**
     * 支付方式名称，参考OrderPaymentConst类,用于模糊查询
     */
    private String paymentNameLike;

    /**
     * 支付方式code, 参考OrderPaymentConst类
     */
    private String paymentCode;

    /**
     * 商品金额，等于订单中所有的商品的单价乘以数量之和
     */
    private BigDecimal goodsAmount;

    /**
     * 物流费用
     */
    private BigDecimal expressFee;

    /**
     * 活动优惠总金额 （= 店铺优惠券 + 平台优惠券 + 活动优惠【店铺活动 + 平台活动】 + 积分抵扣金额）
     */
    private BigDecimal activityDiscountAmount;

    /**
     * 活动优惠明细，json存储（对应List<PromotionInfo>）
     */
    private String activityDiscountDetail;

    /**
     * 订单总金额(用户需要支付的金额)，等于商品总金额＋运费-活动优惠金额总额activity_discount_amount
     */
    private BigDecimal orderAmount;

    /**
     * 余额账户支付总金额
     */
    private BigDecimal balanceAmount;

    /**
     * 三方支付金额
     */
    private BigDecimal payAmount;

    /**
     * 退款的金额，订单没有退款则为0
     */
    private BigDecimal refundAmount;

    /**
     * 积分抵扣金额
     */
    private BigDecimal integralCashAmount;

    /**
     * 订单使用的积分数量
     */
    private Integer integral;

    /**
     * 收货人
     */
    private String receiverName;

    /**
     * 收货人,用于模糊查询
     */
    private String receiverNameLike;

    /**
     * 省市区组合
     */
    private String receiverAreaInfo;

    /**
     * 收货人详细地址
     */
    private String receiverAddress;

    /**
     * 收货人手机号
     */
    private String receiverMobile;

    /**
     * 延长多少天收货
     */
    private Integer delayDays;

    /**
     * 物流公司ID
     */
    private Integer expressId;

    /**
     * 物流公司
     */
    private String expressName;

    /**
     * 物流公司,用于模糊查询
     */
    private String expressNameLike;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 快递单号,用于模糊查询
     */
    private String expressNumberLike;

    /**
     * 是否评价:1.未评价,2.部分评价,3.全部评价
     */
    private Integer evaluateState;

    /**
     * evaluateStateIn，用于批量操作
     */
    private String evaluateStateIn;

    /**
     * evaluateStateNotIn，用于批量操作
     */
    private String evaluateStateNotIn;

    /**
     * evaluateStateNotEquals，用于批量操作
     */
    private Integer evaluateStateNotEquals;

    /**
     * 订单类型：1-普通订单；其他直接存活动类型（具体类型查看ActivityConst）
     */
    private Integer orderType;

    /**
     * 锁定状态：0-是正常, 大于0是锁定状态，用户申请退款或退货时锁定状态加1，处理完毕减1。锁定后不能操作订单
     */
    private Integer lockState;

    /**
     * lockStateIn，用于批量操作
     */
    private String lockStateIn;

    /**
     * lockStateNotIn，用于批量操作
     */
    private String lockStateNotIn;

    /**
     * lockStateNotEquals，用于批量操作
     */
    private Integer lockStateNotEquals;

    /**
     * 删除状态：0-未删除；1-放入回收站；2-彻底删除
     */
    private Integer deleteState;

    /**
     * deleteStateIn，用于批量操作
     */
    private String deleteStateIn;

    /**
     * deleteStateNotIn，用于批量操作
     */
    private String deleteStateNotIn;

    /**
     * deleteStateNotEquals，用于批量操作
     */
    private Integer deleteStateNotEquals;

    /**
     * 取消原因
     */
    private String refuseReason;

    /**
     * 取消原因,用于模糊查询
     */
    private String refuseReasonLike;

    /**
     * 取消备注
     */
    private String refuseRemark;

    /**
     * 是否结算：0-未结算；1-已结算
     */
    private Integer isSettlement;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照orderId倒序排列
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

    /**
     * 商品名称，用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 发货时间-截止
     */
    private String deliverTimeEnd;
}