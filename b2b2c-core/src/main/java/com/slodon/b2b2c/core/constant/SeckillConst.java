package com.slodon.b2b2c.core.constant;

/**
 * 秒杀常量
 **/
public class SeckillConst {

    /**
     * 活动状态 1-未开始；2-进行中；3-结束
     */
    public static final int SECKILL_STATE_1 = 1;
    public static final int SECKILL_STATE_2 = 2;
    public static final int SECKILL_STATE_3 = 3;

    /**
     * 场次状态 1-未开始；2-进行中；3-结束
     */
    public static final int SECKILL_STAGE_STATE_1 = 1;
    public static final int SECKILL_STAGE_STATE_2 = 2;
    public static final int SECKILL_STAGE_STATE_3 = 3;

    /**
     * 商品审核拒绝或通过,1-通过,0-拒绝
     */
    public final static int SECKILL_GOODS_AUDIT_AGREE = 1;
    public final static int SECKILL_GOODS_AUDIT_REJECT = 0;

    /**
     * 审核状态 1待审核 2审核通过，3拒绝
     */
    public static final int SECKILL_AUDIT_STATE_1 = 1;
    public static final int SECKILL_AUDIT_STATE_2 = 2;
    public static final int SECKILL_AUDIT_STATE_3 = 3;

    /**
     * 秒杀活动审核开关
     */
    public static final String SECKILL_AUDIT_SWITCH_CLOSE = "0";
    public static final String SECKILL_AUDIT_SWITCH_OPEN = "1";

    /**
     * 是否显示 0-不显示 1-显示
     */
    public static final int IS_SHOW_NO = 0;
    public static final int IS_SHOW_YES = 1;

    /**
     * 秒杀库存状态:1-去抢购, 2-已抢完
     */
    public static final int SECKILL_STOCK_STATE_1 = 1;
    public static final int SECKILL_STOCK_STATE_2 = 2;

}
