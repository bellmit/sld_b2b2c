package com.slodon.b2b2c.core.constant;

/**
 * 投诉常量
 **/
public class ComplainConst {

    /**
     * 是否展示：0-不展示，1-展示
     */
    public static final int IS_SHOW_0 = 0;
    public static final int IS_SHOW_1 = 1;

    /**
     * 投诉状态：1-新投诉/2-待申诉(投诉通过转给商家)/3-对话中(商家已申诉)/4-待仲裁/5-已撤销/6-会员胜诉/7-商家胜诉
     */
    public static final int COMPLAIN_STATE_1 = 1;
    public static final int COMPLAIN_STATE_2 = 2;
    public static final int COMPLAIN_STATE_3 = 3;
    public static final int COMPLAIN_STATE_4 = 4;
    public static final int COMPLAIN_STATE_5 = 5;
    public static final int COMPLAIN_STATE_6 = 6;
    public static final int COMPLAIN_STATE_7 = 7;

    /**
     * 投诉对话表
     * 投诉对话用户类型：1-会员，2-商户，3-平台
     */
    public static final int TALK_USER_TYPE_1 = 1;
    public static final int TALK_USER_TYPE_2 = 2;
    public static final int TALK_USER_TYPE_3 = 3;

    /**
     * 投诉审核:1 通过, 2 拒绝
     */
    public static final int AUDIT_TYPE_YES = 1;
    public static final int AUDIT_TYPE_NO = 2;
}
