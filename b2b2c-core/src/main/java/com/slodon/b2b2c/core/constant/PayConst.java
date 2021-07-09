package com.slodon.b2b2c.core.constant;

/**
 * 支付常量
 */
public class PayConst {
    /**
     * 支付发起来源 pc-pc,mbrowser-移动设备浏览器,app-app,wxxcx-微信小程序,wxbrowser-微信内部浏览器
     */
    public final static String SOURCE_PC = "pc";
    public final static String SOURCE_MBROWSER = "mbrowser";
    public final static String SOURCE_APP = "app";
    public final static String SOURCE_WXXCX = "wxxcx";
    public final static String SOURCE_WXBROWSER = "wxbrowser";

    /**
     * 支付方式
     */
    public final static String METHOD_BALANCE = "balance";
    public final static String NAME_BALANCE = "余额支付";
    public final static String METHOD_WX = "wx";
    public final static String NAME_WX = "微信支付";
    public final static String METHOD_ALIPAY = "alipay";
    public final static String NAME_ALIPAY = "支付宝";

    /**
     * 余额支付类型
     */
    public final static String TYPE_BALANCE = "BALANCE";
    /**
     * 微信支付类型
     */
    public final static String WX_TYPE_NATIVE = "NATIVE";//原生扫码
    public final static String WX_TYPE_APP = "APP";//App支付
    public final static String WX_TYPE_JSAPI = "JSAPI";//公众号支付/小程序支付
    public final static String WX_TYPE_MWEB = "MWEB";//H5支付

    /**
     * 支付宝支付类型
     */
    public final static String ALI_TYPE_NATIVE = "NATIVE";//原生扫码
    public final static String ALI_TYPE_APP = "APP";//App支付
    public final static String ALI_TYPE_MWEB = "MWEB";//H5支付


}
