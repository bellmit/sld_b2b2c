package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 签到临时记录表，没有记录增加，有记录更新表
 */
@Data
public class SignRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("记录id")
    private Integer recordId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("活动id")
    private Integer activityId;

    @ApiModelProperty("每签到一次掩码设置为1, 未签到设置为0")
    private Long mask;

    @ApiModelProperty("已连续签到次数，中间未签到自动置0")
    private Integer continueNum;

    @ApiModelProperty("最后签到时间")
    private Date lastTime;

    @ApiModelProperty("是否已领过连续签到奖励，0-未领，1-已领")
    private Integer isBonus;
}