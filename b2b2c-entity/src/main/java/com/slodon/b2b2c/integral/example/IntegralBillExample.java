package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分商城结算表example
 */
@Data
public class IntegralBillExample implements Serializable {
    private static final long serialVersionUID = 1L;

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
     * 现金使用金额
     */
    private BigDecimal cashAmount;

    /**
     * 积分使用数量
     */
    private Integer integral;

    /**
     * 积分抵扣金额
     */
    private BigDecimal integralCashAmount;

    /**
     * 应结金额(现金使用金额+积分抵现金额)
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