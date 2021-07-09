package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 阶梯团订单扩展信息表example
 */
@Data
public class LadderGroupOrderExtendExample implements Serializable {
    private static final long serialVersionUID = -4243358693216818463L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer extendIdNotEquals;

    /**
     * 用于批量操作
     */
    private String extendIdIn;

    /**
     * 扩展id
     */
    private Integer extendId;

    /**
     * 阶梯团活动id
     */
    private Integer groupId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 用于批量操作
     */
    private String orderSnIn;

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
     * 商品id（spu）
     */
    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品名称,用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 货品id（sku）
     */
    private Long productId;

    /**
     * 商品原价
     */
    private BigDecimal productPrice;

    /**
     * 定金支付单号（关联order_pay表）
     */
    private String depositPaySn;

    /**
     * 定金支付单号（关联order_pay表）,用于模糊查询
     */
    private String depositPaySnLike;

    /**
     * 尾款支付单号（关联order_pay表）
     */
    private String remainPaySn;

    /**
     * 尾款支付单号（关联order_pay表）,用于模糊查询
     */
    private String remainPaySnLike;

    /**
     * 订单子状态：101-待付定金；102-待付尾款；103-已完成付款；0-已取消
     */
    private Integer orderSubState;

    /**
     * orderSubStateIn，用于批量操作
     */
    private String orderSubStateIn;

    /**
     * orderSubStateNotIn，用于批量操作
     */
    private String orderSubStateNotIn;

    /**
     * orderSubStateNotEquals，用于批量操作
     */
    private Integer orderSubStateNotEquals;

    /**
     * 预付定金
     */
    private BigDecimal advanceDeposit;

    /**
     * 尾款金额
     */
    private BigDecimal remainAmount;

    /**
     * 大于等于开始时间
     */
    private Date remainStartTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date remainStartTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date remainEndTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date remainEndTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date participateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date participateTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date successTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date successTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照extendId倒序排列
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