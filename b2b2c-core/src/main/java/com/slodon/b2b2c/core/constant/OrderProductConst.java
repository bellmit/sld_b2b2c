package com.slodon.b2b2c.core.constant;

/**
 * 订单货品相关常量
 */
public class OrderProductConst {

    /**
     * 是否是赠品：0、不是；1、是
     */
    public static final int IS_GIFT_0 = 0;
    public static final int IS_GIFT_1 = 1;

    /**
     * 是否评价:0.未评价，1.已评价
     */
    public static final int IS_EVALUATE_0 = 0;
    public static final int IS_EVALUATE_1 = 1;

    /**
     * 售后按钮，100-退款（商家未发货），200-退款（商家发货,买家未收货），300-申请售后，301-退款失败，401-退款中，402-退款完成,403-换货中，404-换货完成
     */
    public static final int AFS_BUTTON_100 = 100;
    public static final int AFS_BUTTON_200 = 200;
    public static final int AFS_BUTTON_300 = 300;
    public static final int AFS_BUTTON_301 = 301;
    public static final int AFS_BUTTON_401 = 401;
    public static final int AFS_BUTTON_402 = 402;
    public static final int AFS_BUTTON_403 = 403;
    public static final int AFS_BUTTON_404 = 404;

    public static final String AFS_BUTTON_VALUE_100 = "退款";
    public static final String AFS_BUTTON_VALUE_200 = "退款";
    public static final String AFS_BUTTON_VALUE_300 = "申请售后";
    public static final String AFS_BUTTON_VALUE_301 = "退款失败";
    public static final String AFS_BUTTON_VALUE_401 = "退款中";
    public static final String AFS_BUTTON_VALUE_402 = "退款完成";
    public static final String AFS_BUTTON_VALUE_403 = "换货中";
    public static final String AFS_BUTTON_VALUE_404 = "换货完成";
}
