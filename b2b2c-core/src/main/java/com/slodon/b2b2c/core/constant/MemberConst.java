package com.slodon.b2b2c.core.constant;

public class MemberConst {
    /**
     * 性别：0、保密；1、男；2、女
     */
    public static final int GENDER_SECRECY = 0;
    public static final int GENDER_MAN = 1;
    public static final int GENDER_WOMAN = 2;

    /**
     * 会员状态：0-禁用，1-启用；默认为1
     */
    public static final int STATE_0 = 0;
    public static final int STATE_1 = 1;

    /**
     * 邮箱是否验证：0-未验证；1-验证
     */
    public static final int IS_EMAIL_VERIFY_0 = 0;
    public static final int IS_EMAIL_VERIFY_1 = 1;

    /**
     * 手机是否验证：0-未验证；1-验证
     */
    public static final int IS_MOBILE_VERIFY_0 = 0;
    public static final int IS_MOBILE_VERIFY_1 = 1;

    /**
     * 是否接收短信：0-不接收；1-接收
     */
    public static final int IS_RECEIVE_SMS_0 = 0;
    public static final int IS_RECEIVE_SMS_1 = 1;

    /**
     * 是否接收邮件：0-不接收；1-接收
     */
    public static final int IS_RECEIVE_EMAIL_0 = 0;
    public static final int IS_RECEIVE_EMAIL_1 = 1;

    /**
     * 是否允许购买：1-允许，0-禁止
     */
    public static final int IS_ALLOW_BUY_0 = 0;
    public static final int IS_ALLOW_BUY_1 = 1;

    /**
     * 是否允许提问：1-允许，0-禁止
     */
    public static final int IS_ALLOW_ASK_0 = 0;
    public static final int IS_ALLOW_ASK_1 = 1;

    /**
     * 是否允许评论：1-允许，0-禁止
     */
    public static final int IS_ALLOW_COMMENT_0 = 0;
    public static final int IS_ALLOW_COMMENT_1 = 1;

    /**
     * member_balance_log
     * 类型：1、充值；2、退款；3、消费；4、提款；5、系统添加；6、系统减少；7、冻结（总额不变）；8、解冻（总额不变）',
     */
    public static final int TYPE_1=1;
    public static final int TYPE_2=2;
    public static final int TYPE_3=3;
    public static final int TYPE_4=4;
    public static final int TYPE_5=5;
    public static final int TYPE_6=6;
    public static final int TYPE_7=7;
    public static final int TYPE_8=8;

    /**
     * member_address
     * '是否默认地址：1-默认地址，0-非默认地址'
     */
    public static final int IS_DEFAULT_0=0;
    public static final int IS_DEFAULT_1=1;

    /**
     * member_balance_recharge
     * 支付状态 1-未支付 2-已支付
     */
    public static final int PAY_STATE_1=1;
    public static final int PAY_STATE_2=2;

    /**
     * register_channel
     * 会员来源：1、pc；2、H5；3、Android；4、IOS ;5 商城管理平台 ; 6 微信商城
     */
    public static final int MEMBER_FROM_PC      = 1;
    public static final int MEMBER_FROM_H5      = 2;
    public static final int MEMBER_FROM_Android = 3;
    public static final int MEMBER_FROM_IOS     = 4;
    public static final int MEMBER_FROM_MALL    = 5;
    public static final int MEMBER_FROM_WXMALL  = 6;


    /**
     * 非数据库
     * 登录,支付密码有无：0 无,1 有
     */
    public static final int IS_PWD_NO = 0;
    public static final int IS_PWD_YES = 1;
}
