package com.slodon.b2b2c.system.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算表example
 */
@Data
public class BillExample implements Serializable {
    private static final long serialVersionUID = -9218148451333264577L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer billIdNotEquals;

    /**
     * 用于批量操作
     */
    private String billIdIn;

    /**
     * 结算id
     */
    private Integer billId;

    /**
     * 结算单号
     */
    private String billSn;

    /**
     * 用于批量操作
     */
    private String billSnIn;

    /**
     * 结算单号,用于模糊查询
     */
    private String billSnLike;

    /**
     * 店铺ID
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
     * 大于等于开始时间
     */
    private Date startTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date startTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date endTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date endTimeBefore;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 平台佣金
     */
    private BigDecimal commission;

    /**
     * 退还佣金
     */
    private BigDecimal refundCommission;

    /**
     * 退单金额
     */
    private BigDecimal refundAmount;

    /**
     * 平台活动优惠金额
     */
    private BigDecimal platformActivityAmount;

    /**
     * 平台优惠券
     */
    private BigDecimal platformVoucherAmount;

    /**
     * 积分抵扣金额
     */
    private BigDecimal integralCashAmount;

    /**
     * 应结金额(订单金额-平台佣金+退还佣金-退单金额-平台活动费用+平台优惠券+积分抵现金额)
     */
    private BigDecimal settleAmount;

    /**
     * 结算状态：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成
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
     * 打款备注
     */
    private String paymentRemark;

    /**
     * 打款凭证
     */
    private String paymentEvidence;

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
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照billId倒序排列
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