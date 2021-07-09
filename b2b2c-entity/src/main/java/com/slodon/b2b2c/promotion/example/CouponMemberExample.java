package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员优惠券领取表、使用表example
 */
@Data
public class CouponMemberExample implements Serializable {
    private static final long serialVersionUID = 3479271523170628561L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer couponMemberIdNotEquals;

    /**
     * 用于批量操作
     */
    private String couponMemberIdIn;

    /**
     * 会员优惠券ID
     */
    private Integer couponMemberId;

    /**
     * 优惠券id
     */
    private Integer couponId;

    /**
     * 优惠券编码
     */
    private String couponCode;

    /**
     * 店铺id，0为平台
     */
    private Long storeId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员名称,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 大于等于开始时间
     */
    private Date receiveTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date receiveTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date useTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date useTimeBefore;

    /**
     * 使用状态(1-未使用；2-使用；3-过期无效）
     */
    private Integer useState;

    /**
     * useStateIn，用于批量操作
     */
    private String useStateIn;

    /**
     * useStateNotIn，用于批量操作
     */
    private String useStateNotIn;

    /**
     * useStateNotEquals，用于批量操作
     */
    private Integer useStateNotEquals;

    /**
     * 使用订单号
     */
    private String orderSn;

    /**
     * 使用订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 大于等于使用开始时间
     */
    private Date effectiveStartAfter;

    /**
     * 小于等于使用开始时间
     */
    private Date effectiveStartBefore;

    /**
     * 大于等于使用结束时间
     */
    private Date effectiveEndAfter;

    /**
     * 小于等于使用结束时间
     */
    private Date effectiveEndBefore;

    /**
     * 适用商品类型(1-全部商品；2-指定商品；3-指定分类）
     */
    private Integer useType;

    /**
     * 随机金额（领取随机金额券必填）
     */
    private BigDecimal randomAmount;

    /**
     * 是否已经提醒过用户 1==未提醒  2==提醒过
     */
    private Integer expiredNoticeState;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照couponMemberId倒序排列
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