package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 预售定金赔偿表example
 */
@Data
public class PresellDepositCompensationExample implements Serializable {
    private static final long serialVersionUID = -8230055799997881210L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer compensationIdNotEquals;

    /**
     * 用于批量操作
     */
    private String compensationIdIn;

    /**
     * 赔偿id
     */
    private Integer compensationId;

    /**
     * 赔偿金额(定金倍数)
     */
    private BigDecimal compensationAmount;

    /**
     * 定金金额
     */
    private BigDecimal depositAmount;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

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
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照compensationId倒序排列
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