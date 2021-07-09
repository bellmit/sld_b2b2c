package com.slodon.b2b2c.core.constant;

/**
 * 阶梯团常量
 **/
public class LadderGroupConst {

    /**
     * 是否退还定金：1-是；0-否
     */
    public static final int IS_REFUND_DEPOSIT_0 = 0;
    public static final int IS_REFUND_DEPOSIT_1 = 1;

    /**
     * 阶梯优惠方式：1-阶梯价格；2-阶梯折扣
     */
    public static final int DISCOUNT_TYPE_1 = 1;
    public static final int DISCOUNT_TYPE_2 = 2;

    /**
     * 状态：1-创建；2-发布；3-失效；4-删除
     */
    public static final int STATE_1 = 1;
    public static final int STATE_2 = 2;
    public static final int STATE_3 = 3;
    public static final int STATE_4 = 4;

    /**
     * 管理后台阶梯列表筛选状态
     * 1-待发布；2-未开始；3-进行中；4-已失效；5-已结束
     */
    public final static int ACTIVITY_STATE_1 = 1;
    public final static int ACTIVITY_STATE_2 = 2;
    public final static int ACTIVITY_STATE_3 = 3;
    public final static int ACTIVITY_STATE_4 = 4;
    public final static int ACTIVITY_STATE_5 = 5;

    /**
     * 订单子状态：101-待付定金；102-待付尾款；103-已完成付款；0-已取消
     */
    public final static int ORDER_SUB_STATE_1 = 101;
    public final static int ORDER_SUB_STATE_2 = 102;
    public final static int ORDER_SUB_STATE_3 = 103;
    public final static int ORDER_SUB_STATE_0 = 0;

    /**
     * 是否显示：0、不显示；1、显示
     */
    public static final int IS_SHOW_0 = 0;
    public static final int IS_SHOW_1 = 1;

    /**
     * 阶梯等级
     */
    public static final int LADDER_LEVEL_1 = 1;
    public static final int LADDER_LEVEL_2 = 2;
    public static final int LADDER_LEVEL_3 = 3;

    /**
     * 是否生成尾款信息：0、未生成；1、已生成
     */
    public static final int EXECUTE_STATE_0 = 0;
    public static final int EXECUTE_STATE_1 = 1;

}
