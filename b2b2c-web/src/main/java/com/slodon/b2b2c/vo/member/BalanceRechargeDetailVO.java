package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.member.pojo.MemberBalanceRecharge;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员充值明细表
 */
@Data
public class BalanceRechargeDetailVO implements Serializable {


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

    @ApiModelProperty("支付状态 1-未支付 2-已支付")
    private String payStateValue;

    @ApiModelProperty("第三方支付接口交易号,第三方返回")
    private String tradeSn;

//    @ApiModelProperty("用户id")
//    private Integer memberId;

    @ApiModelProperty("用户名")
    private String memberName;

    @ApiModelProperty("会员手机号")
    private String memberMobile;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("支付完成时间")
    private Date payTime;

    @ApiModelProperty("默认为零，如果充值异常，管理员可修改充值单状态，记录管理员ID")
    private Integer adminId;

    @ApiModelProperty("默认为空， 管理员名称")
    private String adminName;

    @ApiModelProperty("默认为空，修改状态时添加备注信息")
    private String description;

    public BalanceRechargeDetailVO(MemberBalanceRecharge memberBalanceRecharge) {
        this.rechargeId = memberBalanceRecharge.getRechargeId();
        this.rechargeSn = memberBalanceRecharge.getRechargeSn();
        this.paymentCode = memberBalanceRecharge.getPaymentCode();
        this.paymentName = memberBalanceRecharge.getPaymentName();
        this.payAmount = memberBalanceRecharge.getPayAmount();
        this.payState = memberBalanceRecharge.getPayState();
        this.payStateValue = getPayStateValue(memberBalanceRecharge.getPayState());
        this.tradeSn = memberBalanceRecharge.getTradeSn();
        this.memberName = memberBalanceRecharge.getMemberName();
        this.memberMobile = CommonUtil.dealMobile(memberBalanceRecharge.getMemberMobile());
        this.createTime = memberBalanceRecharge.getCreateTime();
        this.payTime = memberBalanceRecharge.getPayTime();
        this.adminId = memberBalanceRecharge.getAdminId();
        this.adminName = memberBalanceRecharge.getAdminName();
        this.description = memberBalanceRecharge.getDescription();
    }

    public static String getPayStateValue(Integer payState) {
        String value = null;
        if (StringUtils.isEmpty(payState)) return Language.translate("未知");
        switch (payState) {
            case MemberConst.PAY_STATE_1:
                value = "未支付";
                break;
            case MemberConst.PAY_STATE_2:
                value = "已支付";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}