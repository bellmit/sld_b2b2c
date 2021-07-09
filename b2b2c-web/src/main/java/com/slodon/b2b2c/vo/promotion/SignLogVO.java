package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.SignConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.SignLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description
 * @Author wuxy
 * @date 2020.11.09 11:36
 */
@Data
public class SignLogVO implements Serializable {

    private static final long serialVersionUID = 2377472675790496736L;
    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("签到活动ID")
    private Integer signActivityId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名字")
    private String memberName;

    @ApiModelProperty("签到时间")
    private Date signTime;

    @ApiModelProperty("签到奖励积分")
    private Integer bonusIntegral;

    @ApiModelProperty("签到奖励优惠券")
    private Integer bonusVoucher;

    @ApiModelProperty("签到奖励优惠券名称")
    private String bonusVoucherName;

    @ApiModelProperty("签到类型：0-每日签到，1-连续签到")
    private Integer signType;

    @ApiModelProperty("签到类型：0-每日签到，1-连续签到")
    private String signTypeValue;

    @ApiModelProperty("/签到奖励类型：1、积分，2、积分+优惠券,3、无")
    private Integer bonusType;

    @ApiModelProperty("/签到奖励类型：1、积分，2、积分+优惠券,3、无")
    private String bonusTypeValue;

    public SignLogVO(SignLog signLog) {
        logId = signLog.getLogId();
        signActivityId = signLog.getSignActivityId();
        memberId = signLog.getMemberId();
        memberName = signLog.getMemberName();
        signTime = signLog.getSignTime();
        bonusIntegral = signLog.getBonusIntegral();
        bonusVoucher = signLog.getBonusVoucher();
        signType = signLog.getSignType();
        signTypeValue = dealSignTypeValue(signLog.getSignType());
    }

    public static String dealSignTypeValue(Integer signType) {
        String value = null;
        if (StringUtils.isEmpty(signType)) return null;
        switch (signType) {
            case SignConst.SIGN_TYPE_0:
                value = "每日签到";
                break;
            case SignConst.SIGN_TYPE_1:
                value = "连续签到";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
