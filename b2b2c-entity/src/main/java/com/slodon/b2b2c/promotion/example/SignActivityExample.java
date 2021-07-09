package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SignActivityExample implements Serializable {
    private static final long serialVersionUID = 7157872905548889702L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer signActivityIdNotEquals;

    /**
     * 用于批量操作
     */
    private String signActivityIdIn;

    /**
     * 签到活动id
     */
    private Integer signActivityId;

    /**
     * 大于等于开始时间
     */
    private Date startTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date startTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date endTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date endTimeBefore;

    /**
     * 每次签到奖励，奖励积分数量，0表示不开启日签奖励
     */
    private Integer integralPerSign;

    /**
     * 是否提醒，1-是，0-否，默认0
     */
    private Integer isRemind;

    /**
     * 连续签到次数；达到送礼物，0表示无连续签到奖励
     */
    private Integer continueNum;

    /**
     * 规则说明
     */
    private String bonusRules;

    /**
     * 满足条件奖励积分数值
     */
    private Integer bonusIntegral;

    /**
     * 满足条件奖励优惠券类型ID
     */
    private Integer bonusVoucher;

    /**
     * 状态：0-关闭，1-开启，2-删除
     */
    private Integer state;

    /**
     * 模版配置数据，背景设置、分享设置、装修设置
     */
    private String templateJson;

    /**
     * 创建管理员ID
     */
    private Integer createAdminId;

    /**
     * 创建管理员名字
     */
    private String createAdminName;

    /**
     * 创建管理员名字,用于模糊查询
     */
    private String createAdminNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照signActivityId倒序排列
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

    /**
     * 开启状态：0-关闭，1-开启，2-删除
     */
    private Integer stateNotEquals;

    /**
     * 检查活动开始时间
     */
    private Date checkStartTime;

    /**
     * 检查活动结束时间
     */
    private Date checkEndTime;

}