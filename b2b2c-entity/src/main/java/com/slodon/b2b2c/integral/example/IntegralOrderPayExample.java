package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分商城订单支付表example
 */
@Data
public class IntegralOrderPayExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private String paySnNotEquals;

    /**
     * 用于批量操作
     */
    private String paySnIn;

    /**
     * 支付id
     */
    private Integer payId;

    /**
     * 支付单号，全局唯一
     */
    private String paySn;

    /**
     * 支付单号，全局唯一,用于模糊查询
     */
    private String paySnLike;

    /**
     * 支付单号关联的订单编号
     */
    private String orderSn;

    /**
     * 支付单号关联的订单编号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 现金支付金额
     */
    private BigDecimal payAmount;

    /**
     * 买家ID
     */
    private Integer memberId;

    /**
     * 支付状态：0默认未支付1已支付(只有第三方支付接口通知到时才会更改此状态)
     */
    private String apiPayState;

    /**
     * apiPayStateIn，用于批量操作
     */
    private String apiPayStateIn;

    /**
     * apiPayStateNotIn，用于批量操作
     */
    private String apiPayStateNotIn;

    /**
     * apiPayStateNotEquals，用于批量操作
     */
    private String apiPayStateNotEquals;

    /**
     * 大于等于开始时间
     */
    private Date callbackTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date callbackTimeBefore;

    /**
     * 第三方支付交易流水号
     */
    private String tradeSn;

    /**
     * 第三方支付交易流水号,用于模糊查询
     */
    private String tradeSnLike;

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
     * 排序条件，条件之间用逗号隔开，如果不传则按照paySn倒序排列
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