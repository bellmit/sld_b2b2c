package com.slodon.b2b2c.core.constant;

public class OrderPaymentConst {

    /**
     * 付款code：OFFLINE，货到付款
     */
    public final static String PAYMENT_CODE_OFFLINE = "OFFLINE";
    /**
     * 付款name：OFFLINE，货到付款
     */
    public final static String PAYMENT_NAME_OFFLINE = "货到付款";
    /**
     * 付款code：ONLINE，在线支付
     */
    public final static String PAYMENT_CODE_ONLINE = "ONLINE";
    /**
     * 付款name：ONLINE，在线支付
     */
    public final static String PAYMENT_NAME_ONLINE = "在线支付";

    /**
     * 付款code：INTEGRAL，积分支付
     */
    public final static String PAYMENT_CODE_INTEGRAL = "INTEGRAL";
    /**
     * 付款name：INTEGRAL，积分支付
     */
    public final static String PAYMENT_NAME_INTEGRAL = "积分支付";

    /**
     * 付款code：BALANCE，余额支付
     */
    public final static String PAYMENT_CODE_BALANCE = "BALANCE";
    /**
     * 付款name：BALANCE，余额支付
     */
    public final static String PAYMENT_NAME_BALANCE = "余额支付";

    /**
     * 付款code：WXPAY，微信支付
     */
    public final static String PAYMENT_CODE_WXPAY = "WXPAY";
    /**
     * 付款name：WXPAY，微信支付
     */
    public final static String PAYMENT_NAME_WXPAY = "微信支付";

    /**
     * 付款code：ALIPAY，支付宝支付
     */
    public final static String PAYMENT_CODE_ALIPAY = "ALIPAY";
    /**
     * 付款name：ALIPAY，支付宝支付
     */
    public final static String PAYMENT_NAME_ALIPAY = "支付宝支付";

    /**
     * 付款code：PCALIPAY，PC支付宝
     */
    public final static String PAYMENT_CODE_PCALIPAY = PayConst.ALI_TYPE_NATIVE + PayConst.METHOD_ALIPAY.toUpperCase();
    /**
     * 付款name：PCALIPAY，PC支付宝
     */
    public final static String PAYMENT_NAME_PCALIPAY = PayConst.ALI_TYPE_NATIVE + PayConst.NAME_ALIPAY;

    /**
     * 付款code：H5ALIPAY，H5支付宝
     */
    public final static String PAYMENT_CODE_H5ALIPAY = PayConst.ALI_TYPE_MWEB + PayConst.METHOD_ALIPAY.toUpperCase();
    /**
     * 付款name：H5ALIPAY，H5支付宝
     */
    public final static String PAYMENT_NAME_H5ALIPAY = PayConst.ALI_TYPE_MWEB + PayConst.NAME_ALIPAY;

    /**
     * 付款code：APPALIPAY，APP支付宝
     */
    public final static String PAYMENT_CODE_APPALIPAY = PayConst.ALI_TYPE_APP + PayConst.METHOD_ALIPAY.toUpperCase();
    /**
     * 付款name：APPALIPAY，APP支付宝
     */
    public final static String PAYMENT_NAME_APPALIPAY = PayConst.ALI_TYPE_APP + PayConst.NAME_ALIPAY;

    /**
     * 付款code：PCWXPAY，PC微信
     */
    public final static String PAYMENT_CODE_PCWXPAY = PayConst.WX_TYPE_NATIVE + PayConst.METHOD_WX.toUpperCase();
    /**
     * 付款name：PCWXPAY，PC微信
     */
    public final static String PAYMENT_NAME_PCWXPAY = PayConst.WX_TYPE_NATIVE + PayConst.NAME_WX;

    /**
     * 付款code：H5WXPAY，H5微信
     */
    public final static String PAYMENT_CODE_H5WXPAY = PayConst.WX_TYPE_MWEB + PayConst.METHOD_WX.toUpperCase();
    /**
     * 付款name：H5WXPAY，H5微信
     */
    public final static String PAYMENT_NAME_H5WXPAY = PayConst.WX_TYPE_MWEB + PayConst.NAME_WX;

    /**
     * 付款code：APPWXPAY，APP微信
     */
    public final static String PAYMENT_CODE_APPWXPAY = PayConst.WX_TYPE_APP + PayConst.METHOD_WX.toUpperCase();
    /**
     * 付款name：APPWXPAY，APP微信
     */
    public final static String PAYMENT_NAME_APPWXPAY = PayConst.WX_TYPE_APP + PayConst.NAME_WX;

    /**
     * 付款code：MINIAPPWX，MINIAPP微信
     */
    public final static String PAYMENT_CODE_MINIAPPWXPAY = PayConst.WX_TYPE_JSAPI + PayConst.METHOD_WX.toUpperCase();
    /**
     * 付款name：MINIAPPWX，MINIAPP微信
     */
    public final static String PAYMENT_NAME_MINIAPPWXPAY = PayConst.WX_TYPE_JSAPI + PayConst.NAME_WX;

}
