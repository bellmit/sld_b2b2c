package com.slodon.b2b2c.vo.promotion;


import com.slodon.b2b2c.promotion.pojo.SignActivity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class MemberSignDetailVO implements Serializable {

    private static final long serialVersionUID = 4052860265455398416L;
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

    /**
     * 其他字段
     */
    @ApiModelProperty("满足条件奖励优惠券名称")
    private String bonusVoucherName;

    @ApiModelProperty("状态：0-关闭，1-开启，2-删除")
    private Integer state;

    @ApiModelProperty("日志-今日签到状态,1-未签到，2-已签到")
    private Integer isSign;

    @ApiModelProperty("今日获取的积分奖励")
    private Integer bonusIntegralToday;

    @ApiModelProperty("今日获取的优惠券名称")
    private String bonusVoucherNameToday;

    @ApiModelProperty("背景设置")
    private Map<String, Object> bgInfo = new HashMap<>();

    @ApiModelProperty("分享设置")
    private Map<String, Object> share = new HashMap<>();

    @ApiModelProperty("提醒设置")
    private Map<String, Object> notice = new HashMap<>();

    @ApiModelProperty("底部装修设置")
    private Map<String, Object> bottom = new HashMap<>();

    @ApiModelProperty("会员签到信息,list.size() = 活动时长")
    private List<memberSignInfo> memberSignInfoList;

    public MemberSignDetailVO(SignActivity signActivity) {
        this.signActivityId = signActivity.getSignActivityId();
        this.startTime = signActivity.getStartTime();
        this.endTime = signActivity.getEndTime();
        this.integralPerSign = signActivity.getIntegralPerSign();
        this.isRemind = signActivity.getIsRemind();
        this.continueNum = signActivity.getContinueNum();
        this.bonusRules = signActivity.getBonusRules();
        this.bonusIntegral = signActivity.getBonusIntegral();

        //其他字段
        this.bonusVoucher = signActivity.getBonusVoucher();
    }

    @Data
    public static class memberSignInfo implements Serializable {
        private static final long serialVersionUID = 3092759956278221500L;
        @ApiModelProperty("获取距离当前时间n天的时间，n为负数代表当前时间之前,格式为yyyy-MM-dd")
        private String date;

        @ApiModelProperty("记录-会员今日签到状态：1-未签到，2-已签到，3-待签到")
        private Integer signRecordState;
    }
}
