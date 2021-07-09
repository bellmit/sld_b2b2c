package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单
 */
@Data
public class Order implements Serializable {
    private static final long serialVersionUID = 5881750375782758497L;
    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("父订单号，无需拆单时，父订单号=订单号")
    private String parentSn;

    @ApiModelProperty("商家ID")
    private Long storeId;

    @ApiModelProperty("商家名称")
    private String storeName;

    @ApiModelProperty("买家name")
    private String memberName;

    @ApiModelProperty("买家ID")
    private Integer memberId;

    @ApiModelProperty("支付成功时间")
    private Date payTime;

    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @ApiModelProperty("订单完成时间")
    private Date finishTime;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("支付方式名称，参考OrderPaymentConst类")
    private String paymentName;

    @ApiModelProperty("支付方式code, 参考OrderPaymentConst类")
    private String paymentCode;

    @ApiModelProperty("商品金额，等于订单中所有的商品的单价乘以数量之和")
    private BigDecimal goodsAmount;

    @ApiModelProperty("物流费用")
    private BigDecimal expressFee;

    @ApiModelProperty("活动优惠总金额 （= 店铺优惠券 + 平台优惠券 + 活动优惠【店铺活动 + 平台活动】 + 积分抵扣金额）")
    private BigDecimal activityDiscountAmount;

    @ApiModelProperty("活动优惠明细，json存储（对应List<PromotionInfo>）")
    private String activityDiscountDetail;

    @ApiModelProperty("订单总金额(用户需要支付的金额)，等于商品总金额＋运费-活动优惠金额总额activity_discount_amount")
    private BigDecimal orderAmount;

    @ApiModelProperty("余额账户支付总金额")
    private BigDecimal balanceAmount;

    @ApiModelProperty("三方支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("退款的金额，订单没有退款则为0")
    private BigDecimal refundAmount;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("订单使用的积分数量")
    private Integer integral;

    @ApiModelProperty("收货人")
    private String receiverName;

    @ApiModelProperty("省市区组合")
    private String receiverAreaInfo;

    @ApiModelProperty("收货人详细地址")
    private String receiverAddress;

    @ApiModelProperty("收货人手机号")
    private String receiverMobile;

    @ApiModelProperty("延长多少天收货")
    private Integer delayDays;

    @ApiModelProperty("物流公司ID")
    private Integer expressId;

    @ApiModelProperty("物流公司")
    private String expressName;

    @ApiModelProperty("快递单号")
    private String expressNumber;

    @ApiModelProperty("是否评价:1.未评价,2.部分评价,3.全部评价")
    private Integer evaluateState;

    @ApiModelProperty("订单类型：1-普通订单；其他直接存活动类型（具体类型查看ActivityConst）")
    private Integer orderType;

    @ApiModelProperty("锁定状态：0-是正常, 大于0是锁定状态，用户申请退款或退货时锁定状态加1，处理完毕减1。锁定后不能操作订单")
    private Integer lockState;

    @ApiModelProperty("删除状态：0-未删除；1-放入回收站；2-彻底删除")
    private Integer deleteState;

    @ApiModelProperty("取消原因")
    private String refuseReason;

    @ApiModelProperty("取消备注")
    private String refuseRemark;

    @ApiModelProperty("是否已生成电子面单 1 - 已生成 2 - 未生成")
    private Integer isGenerateFacesheet;

    @ApiModelProperty("是否结算：0-未结算；1-已结算")
    private Integer isSettlement;

    @ApiModelProperty("货品列表[数据库之外的额外属性]")
    private List<OrderProduct> orderProductList;
}