package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 结算单与订单绑定表example
 */
@Data
public class IntegralBillOrderBindExample implements Serializable {
    private static final long serialVersionUID = -7108915187790680577L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer bindIdNotEquals;

    /**
     * 用于批量操作
     */
    private String bindIdIn;

    /**
     * 绑定id
     */
    private Integer bindId;

    /**
     * 结算单号
     */
    private String billSn;

    /**
     * 结算单号,用于模糊查询
     */
    private String billSnLike;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

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
     * 排序条件，条件之间用逗号隔开，如果不传则按照bindId倒序排列
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