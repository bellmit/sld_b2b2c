package com.slodon.b2b2c.core.constant;

/**
 * 优惠券常量
 **/
public class CouponConst {

    /**
     * 优惠券类型 1-固定金额券；2-折扣券（折扣比例）；3-随机金额券
     **/
    public static final int COUPON_TYPE_1 = 1;
    public static final int COUPON_TYPE_2 = 2;
    public static final int COUPON_TYPE_3 = 3;

    /**
     * 优惠券获取类型 1-免费领取；2-积分兑换；3-活动赠送；4-礼包券
     **/
    public static final int PUBLISH_TYPE_1 = 1;
    public static final int PUBLISH_TYPE_2 = 2;
    public static final int PUBLISH_TYPE_3 = 3;
    public static final int PUBLISH_TYPE_4 = 4;

    /**
     * 使用限制-适用商品类型:
     * 1-全部店铺商品（店铺优惠券）；
     * 2-指定商品（店铺或平台优惠券）；
     * 3-指定分类（平台优惠券）
     */
    public static final int USE_TYPE_1 = 1;
    public static final int USE_TYPE_2 = 2;
    public static final int USE_TYPE_3 = 3;

    /**
     * 活动状态 1-正常；2-失效；3-删除
     **/
    public static final int ACTIVITY_STATE_1 = 1;
    public static final int ACTIVITY_STATE_2 = 2;
    public static final int ACTIVITY_STATE_3 = 3;

    /**
     * 管理后台优惠券列表筛选状态
     * 1-未开始；2-进行中；3-已失效；4-已结束
     */
    public final static int STATE_1 = 1;
    public final static int STATE_2 = 2;
    public final static int STATE_3 = 3;
    public final static int STATE_4 = 4;

    /**
     * 是否推荐 0-不推荐; 1-推荐
     **/
    public static final int IS_RECOMMEND_0 = 0;
    public static final int IS_RECOMMEND_1 = 1;

    /**
     * 使用状态 1-未使用；2-使用；3-过期无效
     */
    public static final int USE_STATE_1 = 1;
    public static final int USE_STATE_2 = 2;
    public static final int USE_STATE_3 = 3;

    /**
     * 优惠券操作类型：1-领取;2-下单消费;3-订单取消返回;4-商品退货返回;5-积分兑换
     */
    public static final int LOG_TYPE_1 = 1;
    public static final int LOG_TYPE_2 = 2;
    public static final int LOG_TYPE_3 = 3;
    public static final int LOG_TYPE_4 = 4;
    public static final int LOG_TYPE_5 = 5;

    /**
     * 平台优惠券|店铺优惠券： 0-平台优惠券;1-店铺优惠券
     */
    public static final int PLATFORM_COUPON = 0;
    public static final int STORE_COUPON = 1;

    /**
     * 分类等级 1-一级，2-二级，3-三级
     */
    public static final int CATEGORY_GRADE_1 = 1;
    public static final int CATEGORY_GRADE_2 = 2;
    public static final int CATEGORY_GRADE_3 = 3;

    /**
     * 是否已经提醒过用户优惠券过期通知： 1==未提醒  2==提醒过
     */
    public static final int EXPIRED_NOTICE_STATE_1 = 1;
    public static final int EXPIRED_NOTICE_STATE_2 = 2;

    /**
     * 使用限制：1-固定起止时间，2-固定有效期
     */
    public static final int EFFECTIVE_TIME_TYPE_1 = 1;
    public static final int EFFECTIVE_TIME_TYPE_2 = 2;

    /**
     * 用户是否领取：1-未领取，2-已领取,3-已抢完
     */
    public static final int COUPON_NOT_RECEIVE  = 1;
    public static final int COUPON_IS_RECEIVE   = 2;
    public static final int COUPON_FINISHED     = 3;

    /**
     * 是否允许叠加使用：0-不允许；1-允许
     **/
    public static final int PLUS_QUALIFICATION_0 = 0;
    public static final int PLUS_QUALIFICATION_1 = 1;

}
