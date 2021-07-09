package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 签到活动表
 */
@Data
public class SignActivity implements Serializable {
    private static final long serialVersionUID = 3584858364622212684L;
    @ApiModelProperty("签到活动id")
    private Integer signActivityId;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("每次签到奖励，奖励积分数量，0表示不开启日签奖励")
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

    @ApiModelProperty("状态：0-关闭，1-开启，2-删除")
    private Integer state;

    @ApiModelProperty("模版配置数据，背景设置、分享设置、装修设置")
    private String templateJson;

    @ApiModelProperty("创建管理员ID")
    private Integer createAdminId;

    @ApiModelProperty("创建管理员名字")
    private String createAdminName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    //数据库外字段
    private Integer signState;                            //活动状态，1未开始、2进行中、3已结束,0未知

    public Integer getSignState() {
        int state;
        Date date = new Date();
        if (this.getStartTime() == null || this.getEndTime() == null) {
            state = 0;
        } else if (date.before(this.getStartTime())) {
            state = 1;
        } else if (date.after(this.getEndTime())) {
            state = 3;
        } else {
            state = 2;
        }

        return state;
    }
}