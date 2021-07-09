package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.member.pojo.MemberBalanceRecharge;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员充值明细表
 */
@Data
public class MemberBalanceRechargeDetailVO implements Serializable {

    private static final long serialVersionUID = 8291248649420520841L;
    @ApiModelProperty("充值id")
    private Integer rechargeId;

    @ApiModelProperty("支付号，平台自行生成")
    private String rechargeSn;

    @ApiModelProperty("支付方式code：PCUNIONPAY；H5UNIONPAY；PCALIPAY；H5ALIPAY；PCWXPAY；H5WXPAY")
    private String paymentCode;

    @ApiModelProperty("支付方式名称：PC银联；H5银联；PC支付宝；H5支付宝；PC微信；H5微信")
    private String paymentName;

    @ApiModelProperty("支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("支付状态 1-未支付 2-已支付")
    private Integer payState;

    @ApiModelProperty("第三方支付接口交易号,第三方返回")
    private String tradeSn;

    @ApiModelProperty("用户id")
    private Integer memberId;

    @ApiModelProperty("用户名")
    private String memberName;

    @ApiModelProperty("会员手机号")
    private String memberMobile;

    @ApiModelProperty("支付完成时间")
    private Date payTime;

    public MemberBalanceRechargeDetailVO(MemberBalanceRecharge memberBalanceRecharge) {
        rechargeId = memberBalanceRecharge.getRechargeId();
        rechargeSn = memberBalanceRecharge.getRechargeSn();
        paymentCode = memberBalanceRecharge.getPaymentCode();
        paymentName = memberBalanceRecharge.getPaymentName();
        payAmount = memberBalanceRecharge.getPayAmount();
        payState = memberBalanceRecharge.getPayState();
        tradeSn = memberBalanceRecharge.getTradeSn();
        memberId = memberBalanceRecharge.getMemberId();
        memberName = memberBalanceRecharge.getMemberName();
        memberMobile = CommonUtil.dealMobile(memberBalanceRecharge.getMemberMobile());
        payTime = memberBalanceRecharge.getPayTime();
    }
}