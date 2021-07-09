package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单退货表example
 */
@Data
public class OrderReturnExample implements Serializable {
    private static final long serialVersionUID = -2418615448608772333L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer returnIdNotEquals;

    /**
     * 用于批量操作
     */
    private String returnIdIn;

    /**
     * 退货id
     */
    private Integer returnId;

    /**
     * 售后服务单号
     */
    private String afsSn;

    /**
     * 售后服务单号,用于模糊查询
     */
    private String afsSnLike;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 用户ID
     */
    private Integer memberId;

    /**
     * 用户名称
     */
    private String memberName;

    /**
     * 用户名称,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 退款方式：0-原路退回，1-退账户余额
     */
    private Integer returnMoneyType;

    /**
     * 退款类型：1==仅退款 2=退货退款
     */
    private Integer returnType;

    /**
     * 退货数量
     */
    private Integer returnNum;

    /**
     * 退款金额
     */
    private BigDecimal returnMoneyAmount;

    /**
     * 退还积分
     */
    private Integer returnIntegralAmount;

    /**
     * 扣除积分数量（购物赠送的积分）
     */
    private Integer deductIntegralAmount;

    /**
     * 退还运费的金额（用于待发货订单仅退款时处理）
     */
    private BigDecimal returnExpressAmount;

    /**
     * 退还优惠券编码（最后一单退还优惠券）
     */
    private String returnVoucherCode;

    /**
     * 平台对应类别的佣金比例，0-1数字，
     */
    private BigDecimal commissionRate;

    /**
     * 佣金金额
     */
    private BigDecimal commissionAmount;

    /**
     * 退货退款状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家同意退款申请；201-商家同意退货退款申请；202-商家拒绝退款申请(退款关闭/拒收关闭)；203-商家确认收货；300-平台确认退款(已完成)
     */
    private Integer state;

    /**
     * stateIn，用于批量操作
     */
    private String stateIn;

    /**
     * stateNotIn，用于批量操作
     */
    private String stateNotIn;

    /**
     * stateNotEquals，用于批量操作
     */
    private Integer stateNotEquals;

    /**
     * 大于等于开始时间
     */
    private Date applyTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date applyTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date completeTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date completeTimeBefore;

    /**
     * 拒绝原因
     */
    private String refuseReason;

    /**
     * 拒绝原因,用于模糊查询
     */
    private String refuseReasonLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照returnId倒序排列
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
     * 订单货品id
     */
    private Long orderProductId;

    /**
     * 发货时间
     */
    private String deliverTimeEnd;
}