package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.i18n.Language;
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
public class BalanceRechargeVO implements Serializable {

    @ApiModelProperty("充值id")
    private Integer rechargeId;

    @ApiModelProperty("支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("支付状态 1-未支付 2-已支付")
    private Integer payState;

    @ApiModelProperty("支付状态 1-未支付 2-已支付")
    private String payStateValue;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("支付完成时间")
    private Date payTime;

    public BalanceRechargeVO(MemberBalanceRecharge memberBalanceRecharge) {
        rechargeId = memberBalanceRecharge.getRechargeId();
        payAmount = memberBalanceRecharge.getPayAmount();
        payState = memberBalanceRecharge.getPayState();
        payStateValue = getPayStateValue(memberBalanceRecharge.getPayState());
        createTime = memberBalanceRecharge.getCreateTime();
        payTime = memberBalanceRecharge.getPayTime();
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