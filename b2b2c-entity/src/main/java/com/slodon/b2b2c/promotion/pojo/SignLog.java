package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员签到日志，每次签到关联活动ID
 */
@Data
public class SignLog implements Serializable {
    private static final long serialVersionUID = -3591621317068808611L;
    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("签到活动ID")
    private Integer signActivityId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名字")
    private String memberName;

    @ApiModelProperty("登录IP")
    private String loginIp;

    @ApiModelProperty("签到时间")
    private Date signTime;

    @ApiModelProperty("会员来源：1、pc；2、H5；3、小程序；4、Android；5、IOS ;")
    private Integer signSource;

    @ApiModelProperty("签到类型：0-每日签到，1-连续签到")
    private Integer signType;

    @ApiModelProperty("签到奖励积分")
    private Integer bonusIntegral;

    @ApiModelProperty("签到奖励优惠券")
    private Integer bonusVoucher;

    @ApiModelProperty("连续签到次数，从record表中获取")
    private Integer continueNum;
}