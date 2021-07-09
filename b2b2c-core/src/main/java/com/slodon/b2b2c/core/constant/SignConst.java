package com.slodon.b2b2c.core.constant;

/**
 * 签到相关常量
 */
public class SignConst {

    /**
     * 是否提醒，1-是，0-否
     */
    public static final int IS_REMIND_YES = 1;
    public static final int IS_REMIND_NO = 0;

    /**
     * 签到活动状态：0-关闭，1-开启，2-删除
     */
    public static final int SIGN_STATE_0 = 0;
    public static final int SIGN_STATE_1 = 1;
    public static final int SIGN_STATE_2 = 2;

    /**
     * 签到类型：0-每日签到，1-连续签到
     */
    public static final int SIGN_TYPE_0 = 0;
    public static final int SIGN_TYPE_1 = 1;

    /**
     * 非数据库
     * 今日签到日志状态,1-未签到，2-已签到
     */
    public static final int IS_SIGN_1 = 1;
    public static final int IS_SIGN_2 = 2;

    /**
     * 非数据库
     * 会员今日签到记录状态：1-未签到，2-已签到，3-待签到
     */
    public static final int SIGN_RECORD_STATE_1 = 1;
    public static final int SIGN_RECORD_STATE_2 = 2;
    public static final int SIGN_RECORD_STATE_3 = 3;

    /**
     * 非数据库
     * 是否已领过连续签到奖励，0-未领，1-已领
     */
    public static final int IS_BONUS_0 = 0;
    public static final int IS_BONUS_1 = 1;

    /**
     * 非数据库
     * 活动状态：1-未开始，2-进行中，3-已结束
     */
    public static final int SIGN_ACTIVITY_STATE_1 = 1;
    public static final int SIGN_ACTIVITY_STATE_2 = 2;
    public static final int SIGN_ACTIVITY_STATE_3 = 3;
}
