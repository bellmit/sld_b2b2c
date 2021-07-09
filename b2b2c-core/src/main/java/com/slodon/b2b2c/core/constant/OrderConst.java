package com.slodon.b2b2c.core.constant;

/**
 * 订单常量
 **/
public class OrderConst {

    /**
     * 支付状态： 0、未支付,1已支付
     */
    public final static String API_PAY_STATE_0 = "0";
    public final static String API_PAY_STATE_1 = "1";

    /**
     * 订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭
     */
    public final static int ORDER_STATE_0 = 0;
    public final static int ORDER_STATE_10 = 10;
    public final static int ORDER_STATE_20 = 20;
    public final static int ORDER_STATE_30 = 30;
    public final static int ORDER_STATE_40 = 40;
    public final static int ORDER_STATE_50 = 50;

    /**
     * 订单子状态：101-待付定金；102-待付尾款；103-已付全款
     */
    public final static int ORDER_SUB_STATE_101 = 101;
    public final static int ORDER_SUB_STATE_102 = 102;
    public final static int ORDER_SUB_STATE_103 = 103;

    /**
     * 是否全款订单：1-全款订单，0-定金预售订单
     */
    public final static int IS_ALL_PAY_1 = 1;
    public final static int IS_ALL_PAY_0 = 0;

    /**
     * 支付类型：1-全款支付；2-定金支付；3-尾款支付
     */
    public final static int PAY_TYPE_1 = 1;
    public final static int PAY_TYPE_2 = 2;
    public final static int PAY_TYPE_3 = 3;

    /**
     * 订单类型：0-货到付款订单； 1-普通订单
     */
    public final static int ORDER_TYPE_0 = 0;
    public final static int ORDER_TYPE_1 = 1;

    /**
     * 发票状态：0、未开；、1-已开
     */
    public final static int INVOICE_STATE_0 = 0;
    public final static int INVOICE_STATE_1 = 1;

    /**
     * 订单评价状态：1.未评价,2.部分评价,3.全部评价;
     */
    public static final int EVALUATE_STATE_1 = 1;
    public static final int EVALUATE_STATE_2 = 2;
    public static final int EVALUATE_STATE_3 = 3;

    /**
     * 订单删除状态： 0-未删除；1-放入回收站；2-彻底删除
     */
    public final static int DELETE_STATE_0 = 0;
    public final static int DELETE_STATE_1 = 1;
    public final static int DELETE_STATE_2 = 2;

    /**
     * 操作人角色(1-系统管理员，2-商户，3-会员）
     */
    public final static int LOG_ROLE_ADMIN = 1;
    public final static int LOG_ROLE_VENDOR = 2;
    public final static int LOG_ROLE_MEMBER = 3;

    /**
     * 订单货品参与的活动是否为店铺活动，1==是；0==否
     */
    public final static int IS_STORE_PROMOTION_YES = 1;
    public final static int IS_STORE_PROMOTION_NO = 0;

    /**
     * 订单货品是否赠品，1==是；0==否
     */
    public final static int IS_GIFT_YES = 1;
    public final static int IS_GIFT_NO = 0;

    /**
     * 是否结算：0-未结算；1-已结算
     */
    public final static int IS_SETTLEMENT_YES = 1;
    public final static int IS_SETTLEMENT_NO = 0;

    /**
     * 发货类型：0-物流发货，1-无需物流
     */
    public final static int DELIVER_TYPE_0 = 0;
    public final static int DELIVER_TYPE_1 = 1;

    /**
     * 是否已生成电子面单 1 - 已生成 2 - 未生成
     */
    public final static int IS_GENERATE_FACE__SHEET_1 = 1;
    public final static int IS_GENERATE_FACE__SHEET_2 = 2;

    /**
     * 订单处理进度，1-排队处理中，2-提交订单失败，3-提交成功
     */
    public final static int ORDER_SUBMIT_DEAL_STATE_1 = 1;
    public final static int ORDER_SUBMIT_DEAL_STATE_2 = 2;
    public final static int ORDER_SUBMIT_DEAL_STATE_3 = 3;
}
