package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.core.constant.SignConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.SignActivity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装签到活动详情VO对象
 * @Author wuxy
 * @date 2020.11.06 17:11
 */
@Data
public class SignActivityDetailVO implements Serializable {

    private static final long serialVersionUID = -3658977256487078095L;
    @ApiModelProperty("签到活动id")
    private Integer signActivityId;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("日签奖励")
    private Integer integralPerSign;

    @ApiModelProperty("是否提醒，1-是，0-否，默认0")
    private Integer isRemind;

    @ApiModelProperty("连续签到次数；达到送礼物，0表示无连续签到奖励")
    private Integer continueNum;

    @ApiModelProperty("规则说明")
    private String bonusRules;

    @ApiModelProperty("满足条件奖励积分数值")
    private Integer bonusIntegral;

    @ApiModelProperty("满足条件奖励优惠券类型ID")
    private Integer bonusVoucher;

    @ApiModelProperty("优惠券名称")
    private String bonusVoucherName;

    @ApiModelProperty("是否开启：0-关闭，1-开启")
    private Integer state;

    @ApiModelProperty("活动状态：1-未开始，2-进行中，3-已结束")
    private String stateValue;

    @ApiModelProperty("活动状态：1-未开始，2-进行中，3-已结束")
    private Integer signState;

    @ApiModelProperty("模版配置数据，背景设置、分享设置、装修设置")
    private String templateJson;

    public SignActivityDetailVO(SignActivity signActivity) {
        signActivityId = signActivity.getSignActivityId();
        startTime = signActivity.getStartTime();
        endTime = signActivity.getEndTime();
        integralPerSign = signActivity.getIntegralPerSign();
        isRemind = signActivity.getIsRemind();
        continueNum = signActivity.getContinueNum();
        bonusRules = signActivity.getBonusRules();
        bonusIntegral = signActivity.getBonusIntegral();
        bonusVoucher = signActivity.getBonusVoucher();
        state = signActivity.getState();
        stateValue = dealStateValue(signActivity.getStartTime(), signActivity.getEndTime());
        signState =dealSignState(signActivity.getStartTime(), signActivity.getEndTime());
        templateJson=signActivity.getTemplateJson();
    }

    public static String dealStateValue(Date startTime, Date endTime) {
        String value = null;
        Date date = new Date();
        if (date.before(startTime)) {
            value = "未开始";
        } else if (date.after(endTime)) {
            value = "已结束";
        } else {
            value = "进行中";
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static Integer dealSignState(Date startTime, Date endTime) {
        Integer value = null;
        Date date = new Date();
        if (date.before(startTime)) {
            value = SignConst.SIGN_ACTIVITY_STATE_1;
        } else if (date.after(endTime)) {
            value = SignConst.SIGN_ACTIVITY_STATE_3;
        } else {
            value = SignConst.SIGN_ACTIVITY_STATE_2;
        }
        return value;
    }
}
