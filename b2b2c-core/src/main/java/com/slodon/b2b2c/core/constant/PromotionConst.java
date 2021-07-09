package com.slodon.b2b2c.core.constant;

public class PromotionConst {

    /**
     * 活动是否有效：
     *
     * 0-无效；
     * 1-有效
     */
    public static final int   IS_EFFECTIVE_NO           = 0;
    public static final int   IS_EFFECTIVE_YES          = 1;

    /**
     * 活动等级：活动规则，
     * 1-商品活动；比如单品立减、单品立即折扣、秒杀、显示折扣等活动，直接修改商品价格，商品加入购物车或购买时直接按照商品的活动价格进行计价
     * 2-店铺活动；在购物车页基于店铺活动计算活动优惠金额；可与上面商品活动进行叠加
     * 3-平台活动；平台活动和店铺活动不能叠加，每个商品参与其中一个活动；
     * 4-积分、优惠券
     */
    public static final int   PROMOTION_GRADE_1          = 1;
    public static final int   PROMOTION_GRADE_2          = 2;
    public static final int   PROMOTION_GRADE_3          = 3;
    public static final int   PROMOTION_GRADE_4          = 4;

    /**
     * 活动类型，等级1的活动类型为 1xx；等级2的活动类型为 2xx；等级3的活动类型为 3xx;
     *
     * 活动类型 101-单品立减, 102-拼团活动, 103-预售活动 104-秒杀活动, 105-阶梯团活动
     * 活动类型 201-阶梯满金额减, 202-循环满减, 203-阶梯满折扣, 204-阶梯满件折扣
     * 活动类型 301-跨店满减
     */
    public static final int PROMOTION_TYPE_101 = 101;
    public static final int PROMOTION_TYPE_102 = 102;
    public static final int PROMOTION_TYPE_103 = 103;
    public static final int PROMOTION_TYPE_104 = 104;
    public static final int PROMOTION_TYPE_105 = 105;
    public static final int PROMOTION_TYPE_201 = 201;
    public static final int PROMOTION_TYPE_202 = 202;
    public static final int PROMOTION_TYPE_203 = 203;
    public static final int PROMOTION_TYPE_204 = 204;
    public static final int PROMOTION_TYPE_301 = 301;
    /**
     * 积分、优惠券活动类型
     * 401-积分兑换，402-优惠券
     */
    public static final int PROMOTION_TYPE_401 = 401;
    public static final int PROMOTION_TYPE_402 = 402;

    /**
     * 绑定类型：
     *
     * 1-商品绑定；
     * 2-店铺绑定；
     * 3-三级分类绑定
     */
    public static final int   BIND_TYPE_1               = 1;
    public static final int   BIND_TYPE_2               = 2;
    public static final int   BIND_TYPE_3               = 3;

    /**
     * 是否平台满减类活动 3xx类型
     * @param promotionType
     * @return
     */
    public static Boolean isPlatformPromotion(Integer promotionType){
        return promotionType != null && promotionType / 100 == PROMOTION_GRADE_3;
    }

    /**
     * 是否店铺满减类活动 2xx
     * @param promotionType
     * @return
     */
    public static Boolean isStorePromotion(Integer promotionType){
        return promotionType != null && promotionType / 100 == PROMOTION_GRADE_2;
    }

    /**
     * 是否店铺满减类活动 1xx
     * @param promotionType
     * @return
     */
    public static Boolean isSinglePromotion(Integer promotionType){
        return promotionType != null && promotionType / 100 == PROMOTION_GRADE_1;
    }

    /**
     * 活动类型：1-按金额计算，满N元;2-按件计算，满N件
     */
    public static final int TYPE_1                  = 1;
    public static final int TYPE_2                  = 2;

    /**
     * 活动状态：1-已创建，2-已发布，3-进行中，4-已结束  5-已失效  6-已删除
     */
    public static final int STATE_CREATED_1         = 1;
    public static final int STATE_RELEASE_2         = 2;
    public static final int STATE_ON_GOING_3        = 3;
    public static final int STATE_COMPLETE_4        = 4;
    public static final int STATE_EXPIRED_5         = 5;
    public static final int STATE_DELETED_6         = 6;

}
