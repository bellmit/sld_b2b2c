package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberBalanceRechargeExample implements Serializable {
    private static final long serialVersionUID = 6682017417128648425L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer rechargeIdNotEquals;

    /**
     * 用于批量操作
     */
    private String rechargeIdIn;

    /**
     * 充值id
     */
    private Integer rechargeId;

    /**
     * 支付号，平台自行生成
     */
    private String rechargeSn;

    /**
     * 支付号，平台自行生成,用于模糊查询
     */
    private String rechargeSnLike;

    /**
     * 支付方式code：PCUNIONPAY；H5UNIONPAY；PCALIPAY；H5ALIPAY；PCWXPAY；H5WXPAY
     */
    private String paymentCode;

    /**
     * 支付方式code：WXPAY-微信支付；ALIPAY-支付宝支付,用于模糊查询
     */
    private String paymentCodeLike;

    /**
     * 支付方式名称：PC银联；H5银联；PC支付宝；H5支付宝；PC微信；H5微信
     */
    private String paymentName;

    /**
     * 支付方式名称：PC银联；H5银联；PC支付宝；H5支付宝；PC微信；H5微信,用于模糊查询
     */
    private String paymentNameLike;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付状态 1-未支付 2-已支付
     */
    private Integer payState;

    /**
     * 第三方支付接口交易号,第三方返回
     */
    private String tradeSn;

    /**
     * 第三方支付接口交易号,第三方返回,用于模糊查询
     */
    private String tradeSnLike;

    /**
     * 用户id
     */
    private Integer memberId;

    /**
     * 用户名
     */
    private String memberName;

    /**
     * 用户名,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 会员手机号
     */
    private String memberMobile;

    /**
     * 会员手机号,用于模糊查询
     */
    private String memberMobileLike;

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
    private Date payTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date payTimeBefore;

    /**
     * 默认为零，如果充值异常，管理员可修改充值单状态，记录管理员ID
     */
    private Integer adminId;

    /**
     * 默认为空， 管理员名称
     */
    private String adminName;

    /**
     * 默认为空， 管理员名称,用于模糊查询
     */
    private String adminNameLike;

    /**
     * 默认为空，修改状态时添加备注信息
     */
    private String description;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照rechargeId倒序排列
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