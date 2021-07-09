package com.slodon.b2b2c.core.constant;

public class MemberIntegralLogConst {
    /**
     * 具体操作1、会员注册；2、会员登录；3、商品购买；4、商品评论；5、系统添加；6、系统减少；
     * 7、订单消费（减少，下单时积分支付扣减）；8、商品退货（增加，用户退货时如果订单有积分支付则返回用户已使用的积分）；
     * 9、年度减少扣减经验值（减少）；10、用户签到赠送积分（增加）；11、订单取消退回积分（增加）；
     * 12、订单货品明细退货追回积分（减少，订单货品明细发生退货后，扣除用户因为购物（类型3）得到的积分，注意与8、13的区别）；
     * 13、订单取消追回积分（减少，订单取消时，扣除用户因为购物（类型3）得到的积分，注意与8、12的区别）；14、积分兑换
     */
    public static final int TYPE_1 = 1;
    public static final int TYPE_2 = 2;
    public static final int TYPE_3 = 3;
    public static final int TYPE_4 = 4;
    public static final int TYPE_5 = 5;
    public static final int TYPE_6 = 6;
    public static final int TYPE_7 = 7;
    public static final int TYPE_8 = 8;
    public static final int TYPE_9 = 9;
    public static final int TYPE_10 = 10;
    public static final int TYPE_11 = 11;
    public static final int TYPE_12 = 12;
    public static final int TYPE_13 = 13;
    public static final int TYPE_14 = 14;

    /**
     * admin-积分设置 1 添加；2 减少
     */
    public static final int ADMIN_TYPE_1 = 1;
    public static final int ADMIN_TYPE_2 = 2;

    /**
     * 类型:收入 1, 支出 2
     */
    public static final int TYPE_IN = 1;
    public static final int TYPE_OUT = 2;
}
