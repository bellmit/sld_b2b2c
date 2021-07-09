package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberApplyCashExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer cashIdNotEquals;

    /**
     * 用于批量操作
     */
    private String cashIdIn;

    /**
     * 提现ID
     */
    private Integer cashId;

    /**
     * 会员ID
     */
    private Integer memberId;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员名称,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 提现编号
     */
    private String cashSn;

    /**
     * 提现编号,用于模糊查询
     */
    private String cashSnLike;

    /**
     * 提现金额
     */
    private BigDecimal cashAmount;

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
    private Date auditingTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date auditingTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date payTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date payTimeBefore;

    /**
     * 付款账号
     */
    private String payAccount;

    /**
     * 收款银行
     */
    private String receiveBank;

    /**
     * 收款账号
     */
    private String receiveAccount;

    /**
     * 收款人姓名
     */
    private String receiveName;

    /**
     * 收款人姓名,用于模糊查询
     */
    private String receiveNameLike;

    /**
     * 收款方式：银行、微信、支付宝
     */
    private String receiveType;

    /**
     * 状态：1、提交申请；2、申请通过；3、已打款；4、处理失败
     */
    private Integer state;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 失败原因,用于模糊查询
     */
    private String failReasonLike;

    /**
     * 操作管理员ID
     */
    private Integer adminId;

    /**
     * 操作管理员名称
     */
    private String adminName;

    /**
     * 操作管理员名称,用于模糊查询
     */
    private String adminNameLike;

    /**
     * 手续费
     */
    private BigDecimal serviceFee;

    /**
     * 交易流水号
     */
    private String transactionSn;

    /**
     * 交易流水号,用于模糊查询
     */
    private String transactionSnLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照cashId倒序排列
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