package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class FullAmountCycleMinusExample implements Serializable {
    private static final long serialVersionUID = -1861869828964986960L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer fullIdNotEquals;

    /**
     * 用于批量操作
     */
    private String fullIdIn;

    /**
     * 循环满减活动id
     */
    private Integer fullId;

    /**
     * 循环满减活动名称
     */
    private String fullName;

    /**
     * 循环满减活动名称,用于模糊查询
     */
    private String fullNameLike;

    /**
     * 大于等于开始时间
     */
    private Date startTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date startTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date endTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date endTimeBefore;

    /**
     * 满指定金额
     */
    private BigDecimal fullValue;

    /**
     * 减指定金额
     */
    private BigDecimal minusValue;

    /**
     * 活动状态( 1-已创建，2-已发布，3-进行中，4-已结束，5-已失效，6-已删除 )
     */
    private Integer state;

    /**
     * 店铺id
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 创建用户id
     */
    private Long createVendorId;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 更新人员vendorID，如果为0是系统或定时任务更新
     */
    private Long updateVendorId;

    /**
     * 赠送积分
     */
    private Integer sendIntegral;

    /**
     * 优惠券id集合
     */
    private String sendCouponIds;

    /**
     * 赠送商品id集合
     */
    private String sendGoodsIds;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照fullId倒序排列
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

    /**
     * stateIn，用于批量操作
     */
    private String stateIn;

    /**
     * stateNotIn，用于批量操作
     */
    private String stateNotIn;

    /**
     * 用于状态的过滤查询
     */
    private Integer stateNotEquals;

    /**
     * 用于处理时间不为空标识
     */
    private String queryTime;

    /**
     * 开始时间(和queryTime结合使用)
     */
    private Date startTime;

    /**
     * 开始时间(和queryTime结合使用)
     */
    private Date endTime;
}