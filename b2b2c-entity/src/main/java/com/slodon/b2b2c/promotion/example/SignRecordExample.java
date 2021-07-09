package com.slodon.b2b2c.promotion.example;

import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class SignRecordExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer recordIdNotEquals;

    /**
     * 用于批量操作
     */
    private String recordIdIn;

    /**
     * 记录id
     */
    private Integer recordId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 活动id
     */
    private Integer activityId;

    /**
     * 每签到一次掩码设置为1, 未签到设置为0
     */
    private Long mask;

    /**
     * 已连续签到次数，中间未签到自动置0
     */
    private Integer continueNum;

    /**
     * 大于等于开始时间
     */
    private Date lastTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date lastTimeBefore;

    /**
     * 是否已领过连续签到奖励，0-未领，1-已领
     */
    private Integer isBonus;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照recordId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;
}