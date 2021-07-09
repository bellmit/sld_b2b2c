package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单活动赠送优惠券表example
 */
@Data
public class OrderPromotionSendCouponExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer sendCouponIdNotEquals;

    /**
     * 用于批量操作
     */
    private String sendCouponIdIn;

    /**
     * 赠送优惠券id
     */
    private Integer sendCouponId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 活动等级 1-商品活动；2-店铺活动；3-平台活动;4-积分、优惠券
     */
    private Integer promotionGrade;

    /**
     * 活动类型  
     */
    private Integer promotionType;

    /**
     * 活动id，积分抵扣时为0,优惠券为优惠券code，其他为活动id
     */
    private String promotionId;

    /**
     * 是否店铺活动，1-是，0-否
     */
    private Integer isStore;

    /**
     * 赠品id
     */
    private Integer couponId;

    /**
     * 赠品数量
     */
    private Integer number;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照sendCouponId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;
}