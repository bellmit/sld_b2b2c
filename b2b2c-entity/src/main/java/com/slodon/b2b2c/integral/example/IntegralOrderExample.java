package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分商城订单表example
 */
@Data
public class IntegralOrderExample implements Serializable {
    private static final long serialVersionUID = 6645309888455627669L;
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
     * 支付单号
     */
    private String paySn;

    /**
     * 支付单号,用于模糊查询
     */
    private String paySnLike;

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
     * 买家ID
     */
    private Integer memberId;

    /**
     * 买家name
     */
    private String memberName;

    /**
     * 买家name,用于模糊查询
     */
    private String memberNameLike;

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
     * 订单总金额(用户需要支付的现金金额)
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
     * 积分抵扣金额
     */
    private BigDecimal integralCashAmount;

    /**
     * 积分支付数量
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
     * 用户订单备注
     */
    private String orderRemark;

    /**
     * 订单来源1、pc；2、H5；3、Android；4、IOS; 5-微信小程序
     */
    private Integer orderFrom;

    /**
     * 发票信息 json格式
     */
    private String invoiceInfo;

    /**
     * 发货类型：0-物流发货，1-无需物流
     */
    private Integer deliverType;

    /**
     * 发货人电话,自行配送时填写
     */
    private String deliverMobile;

    /**
     * 发货人,自行配送时填写
     */
    private String deliverName;

    /**
     * 发货人,自行配送时填写,用于模糊查询
     */
    private String deliverNameLike;

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
     * 发货地址ID
     */
    private Integer deliverAddressId;

    /**
     * 是否是电子面单
     */
    private Integer isDzmd;

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
     * 发货备注
     */
    private String deliverRemark;

    /**
     * 大于等于发货开始时间
     */
    private Date deliverTimeAfter;

    /**
     * 小于等于发货结束时间
     */
    private Date deliverTimeBefore;

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
}