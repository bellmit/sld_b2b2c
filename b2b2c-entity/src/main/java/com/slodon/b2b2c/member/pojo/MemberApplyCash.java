package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员提现申请
 */
@Data
public class MemberApplyCash implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("提现ID")
    private Integer cashId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("提现编号")
    private String cashSn;

    @ApiModelProperty("提现金额")
    private BigDecimal cashAmount;

    @ApiModelProperty("申请时间")
    private Date applyTime;

    @ApiModelProperty("审核时间")
    private Date auditingTime;

    @ApiModelProperty("付款时间")
    private Date payTime;

    @ApiModelProperty("付款账号")
    private String payAccount;

    @ApiModelProperty("收款银行")
    private String receiveBank;

    @ApiModelProperty("收款账号")
    private String receiveAccount;

    @ApiModelProperty("收款人姓名")
    private String receiveName;

    @ApiModelProperty("收款方式：银行、微信、支付宝")
    private String receiveType;

    @ApiModelProperty("状态：1、提交申请；2、申请通过；3、已打款；4、处理失败")
    private Integer state;

    @ApiModelProperty("失败原因")
    private String failReason;

    @ApiModelProperty("操作管理员ID")
    private Integer adminId;

    @ApiModelProperty("操作管理员名称")
    private String adminName;

    @ApiModelProperty("手续费")
    private BigDecimal serviceFee;

    @ApiModelProperty("交易流水号")
    private String transactionSn;
}