/**
 * Copyright@Slodon since 2015, All rights reserved.
 * <p>
 * 注意：
 * 本软件为北京商联达科技有限公司开发研制，未经许可不得使用
 * 购买后可获得源代码使用权（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 * 商业使用请联系: bd@slodon.com 或 拨打全国统一热线 400-881-0877
 * 网址：http://www.slodon.com
 */

package com.slodon.b2b2c.model.promotion.base;

import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.model.promotion.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 活动类型常量与活动model对应
 */
@Slf4j
public enum PromotionType {

    //枚举值格式
    //拼团
    SPELL(PromotionConst.PROMOTION_TYPE_102, SpellModel.class),
    //预售
    PRESELL(PromotionConst.PROMOTION_TYPE_103, PresellModel.class),
    //秒杀,
    SECKILL(PromotionConst.PROMOTION_TYPE_104, SeckillModel.class),
    //阶梯团活动
    LADDER_GROUP(PromotionConst.PROMOTION_TYPE_105, LadderGroupModel.class),
    //积分,
    INTEGRAL(PromotionConst.PROMOTION_TYPE_401, IntegralModel.class),
    //优惠券
    COUPON(PromotionConst.PROMOTION_TYPE_402, CouponModel.class),
    //阶梯满金额减活动
    FULL_AMOUNT_STEP_MINUS(PromotionConst.PROMOTION_TYPE_201, FullAmountStepMinusModel.class),
    //循环满减活动
    FULL_AMOUNT_CYCLE_MINUS(PromotionConst.PROMOTION_TYPE_202, FullAmountCycleMinusModel.class),
    //阶梯满折扣活动
    FULL_AMOUNT_LADDER_DISCOUNT(PromotionConst.PROMOTION_TYPE_203, FullAmountLadderDiscountModel.class),
    //阶梯满件折扣活动
    FULL_NUM_LADDER_DISCOUNT(PromotionConst.PROMOTION_TYPE_204, FullNumLadderDiscountModel.class);


    /**
     * 活动类型
     */
    private Integer type;
    /**
     * 活动类型对应的model类
     */
    private Class model;
    /**
     * 活动类型与对应的model匹配
     */
    private static Map<Integer, PromotionBaseModel> map = new HashMap<>();

    static {
        for (PromotionType value : PromotionType.values()) {
            try {
                map.put(value.getType(), (PromotionBaseModel) value.getModel().newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    PromotionType(Integer type, Class model) {
        this.type = type;
        this.model = model;
    }

    /**
     * 根据活动类型获取对应的model，用于按照活动规则计算优惠
     * 此处需要将获取到的model中所有注入属性，重新注入到spring中
     *
     * @param type
     * @return
     */
    public static PromotionBaseModel getPromotionBaseModel(Integer type) {
        PromotionBaseModel model = map.get(type);
        Class clazz = model.getClass();
        do {
            //当前model所有属性
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                if (f.isAnnotationPresent(org.springframework.beans.factory.annotation.Autowired.class)
                        || f.isAnnotationPresent(javax.annotation.Resource.class)) {
                    try {
                        //将属性注入到spring
                        String simpleName = f.getType().getSimpleName();
                        String beanName;
                        if (simpleName.equals("DataSourceTransactionManager")) {
                            //事务处理的bean命名规则不同
                            beanName = "transactionManager";
                        } else {
                            //其他bean按照类名首字母小写处理
                            beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                        }

                        Object bean = PromotionBaseModel.applicationContext.getBean(beanName);

                        boolean accessible = f.isAccessible();
                        f.setAccessible(true);
                        f.set(model, bean);
                        f.setAccessible(accessible);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            //对model的所有父类进行重新注入操作
            clazz = clazz.getSuperclass();
        } while (clazz != Object.class);
        return model;
    }

    public Integer getType() {
        return type;
    }

    public Class getModel() {
        return model;
    }
}
