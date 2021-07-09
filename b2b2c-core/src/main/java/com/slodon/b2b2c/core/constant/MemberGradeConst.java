package com.slodon.b2b2c.core.constant;

/**
 * 会员等级配置常量（经验值，积分）
 **/
public class MemberGradeConst {

    /**
     * 是否内置数据，0-否；1-是，内置数据不可修改、删除
     */
    public static final int IS_INNER_0 = 0;
    public static final int IS_INNER_1 = 1;

    /**
     * 经验值变化类型 1-升级;2-降级
     */
    public static final String CHANGE_TYPE_1 = "1";
    public static final String CHANGE_TYPE_2 = "2";

    /**
     * 操作类型：
     * 1、会员注册（增加）；
     * 2、会员登录（增加）；
     * 3、商品购买（增加）；
     * 4、商品评论（增加）；
     * 5、系统添加；
     * 6、系统减少；
     * 7、订单消费（减少，下单时积分支付扣减）；
     * 8、商品退货（增加，用户退货时如果订单有积分支付则返回用户已使用的积分）；
     * 9、年度减少扣减经验值（减少）；
     * 10、用户签到赠送积分（增加）；
     * 11、订单取消退回积分（增加）；
     * 12、订单货品明细退货追回积分（减少，订单货品明细发生退货后，扣除用户因为购物（类型3）得到的积分，注意与8、13的区别）；
     * 13、订单取消追回积分（减少，订单取消时，扣除用户因为购物（类型3）得到的积分，注意与8、12的区别）；
     * 14、积分兑换
     */
    public final static int MEMBER_GRD_INT_LOG_OPT_T_1 = 1;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_2 = 2;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_3 = 3;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_4 = 4;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_5 = 5;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_6 = 6;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_7 = 7;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_8 = 8;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_9 = 9;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_10 = 10;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_11 = 11;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_12 = 12;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_13 = 13;
    public final static int MEMBER_GRD_INT_LOG_OPT_T_14 = 14;

    /**
     * 类型：1、经验值;2、积分
     */
    public final static int MEMBER_GRD_INT_LOG_T_1 = 1;
    public final static int MEMBER_GRD_INT_LOG_T_2 = 2;
}
