package com.slodon.b2b2c.core.constant;

/**
 * 售后服务常量
 */
public class OrdersAfsConst {

    /**
     * 售后服务端类型：1-退货退款单，2-换货单，3-仅退款单
     */
    public static final int AFS_TYPE_RETURN         = 1;
    public static final int AFS_TYPE_REPLACEMENT    = 2;
    public static final int AFS_TYPE_REFUND         = 3;

    /**
     * 售后服务状态：1-正常；2-中断（可投诉）
     */
    public static final int AFS_STATE_NORMAL = 1;
    public static final int AFS_STATE_INTERRUPT = 2;

    /**
     * 换货状态：100-买家申请；101-买家发货；200-卖家审核失败；201-卖家审核通过；202-卖家确认收货；203-卖家拒收；204-卖家发货；301-买家收货（已完成）
     */
    public static final int REPLACEMENT_STATE_MEMBER_APPLY = 100;
    public static final int REPLACEMENT_STATE_MEMBER_DELIVERY = 101;
    public static final int REPLACEMENT_STATE_STORE_AUDIT_FAIL = 200;
    public static final int REPLACEMENT_STATE_STORE_AUDIT_PASS = 201;
    public static final int REPLACEMENT_STATE_STORE_RECEIVE = 202;
    public static final int REPLACEMENT_STATE_STORE_REJECTION = 203;
    public static final int REPLACEMENT_STATE_STORE_DELIVERY = 204;
    public static final int REPLACEMENT_STATE_DONE = 301;

    /**
     * 退货退款状态：
     * 100-买家申请仅退款；
     * 101-买家申请退货退款；
     * 102-买家退货给商家；
     * 200-商家同意退款申请；
     * 201-商家同意退货退款申请；
     * 202-商家拒绝退款申请(退款关闭/拒收关闭)；
     * 203-商家确认收货；
     * 300-平台确认退款(已完成)；
     */
    public static final int RETURN_STATE_100 = 100;
    public static final int RETURN_STATE_101 = 101;
    public static final int RETURN_STATE_102 = 102;
    public static final int RETURN_STATE_200 = 200;
    public static final int RETURN_STATE_201 = 201;
    public static final int RETURN_STATE_202 = 202;
    public static final int RETURN_STATE_203 = 203;
    public static final int RETURN_STATE_300 = 300;

    /**
     * 是否显示，1-展示，0-不显示
     */
    public static final int IS_VISIBLE_YES = 1;
    public static final int IS_VISIBLE_NO = 0;

    /**
     * 退款方式：0-原路退回，1-退账户余额
     */
    public static final int MONEY_RETURN_TYPE_TO_OLD = 0;
    public static final int MONEY_RETURN_TYPE_TO_BALANCE = 1;

    /**
     * 退款类型，1-仅退款，2-退货退款
     */
    public static final int RETURN_TYPE_1 = 1;
    public static final int RETURN_TYPE_2 = 2;

    /**
     * 退款状态：1-未退款，2-已退款
     */
    public static final int MONEY_RETURN_STATE_NO = 1;
    public static final int MONEY_RETURN_STATE_YES = 2;

    /**
     * 货物状态：0-未收到货，1-已收到货
     */
    public static final int GOODS_STATE_YES = 1;
    public static final int GOODS_STATE_NO = 0;

    /**
     * 是否有售后
     */
    public static final int IS_HAS_AFS  = 1;
    public static final int NOT_HAS_AFS = 0;

}
