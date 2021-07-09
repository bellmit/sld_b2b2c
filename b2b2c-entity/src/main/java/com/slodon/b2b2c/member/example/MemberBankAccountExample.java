package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberBankAccountExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer accountIdNotEquals;

    /**
     * 用于批量操作
     */
    private String accountIdIn;

    /**
     * 账号id
     */
    private Integer accountId;

    /**
     * 会员id
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
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 账户code：UNIONPAY；ALIPAY,WXPAY
     */
    private String accountCode;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 账户名称,用于模糊查询
     */
    private String accountNameLike;

    /**
     * 账户号码（银行账号，支付宝/微信账号）
     */
    private String accountNumber;

    /**
     * 银行名称（如为第三方支付直接填微信、支付宝）
     */
    private String bankName;

    /**
     * 银行名称（如为第三方支付直接填微信、支付宝）,用于模糊查询
     */
    private String bankNameLike;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照accountId倒序排列
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