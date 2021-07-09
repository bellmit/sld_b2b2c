package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 签到活动编辑DTO
 * @Author wuxy
 * @date 2020.11.09 09:49
 */
@Data
public class SignActivityUpdateDTO implements Serializable {

    private static final long serialVersionUID = -3078384287650859669L;
    @ApiModelProperty(value = "签到活动id", required = true)
    private Integer signActivityId;

    @ApiModelProperty(value = "活动开始时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间", required = true)
    private Date endTime;

    @ApiModelProperty("日签奖励积分，0表示不开启日签奖励")
    private Integer integralPerSign;

    @ApiModelProperty("连续签到天数；0表示无连签奖励")
    private Integer continueNum;

    @ApiModelProperty("规则说明")
    private String bonusRules;

    @ApiModelProperty("连签奖励:积分")
    private Integer bonusIntegral;

    @ApiModelProperty("连签奖励优惠券ID")
    private Integer bonusVoucher;

    @ApiModelProperty("模版配置数据，背景设置、分享设置、装修设置")
    private String templateJson;
}
