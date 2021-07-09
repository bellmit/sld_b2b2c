package com.slodon.b2b2c.core.constant;

/**
 * 短信相关常量
 */
public class SMSConst {
    /**
     * 默认固定线程数
     */
    public static final int DEFAULT_RUN_THREAD_NUM = 2;

    /**
     * 短信类型:1.注册；2.登录；3.找回密码；4.其它
     */
    public final static int SMS_TYPE_1 = 1;
    public final static int SMS_TYPE_2 = 2;
    public final static int SMS_TYPE_3 = 3;
    public final static int SMS_TYPE_4 = 4;

    /**
     * 短信发送形式：0-验证码短信；1-通知类短信
     *
     * 验证码短信：主要用于登录、修改密码等发送的6位随机数，由用户发起调用
     * 通知类短信：主要用户发送订单发货、状态变更等等的系统主动通知短信，由平台发起调用
     */
    public final static int VERIFY_SMS = 0;
    public final static int NOTIFICATION_SMS = 1;
}
