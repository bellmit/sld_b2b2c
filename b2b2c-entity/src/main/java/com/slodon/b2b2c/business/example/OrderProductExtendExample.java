package com.slodon.b2b2c.business.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderProductExtendExample implements Serializable {
    private static final long serialVersionUID = 5449196186375066844L;

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
     * 订单货品id
     */
    private Long orderProductId;

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
     * 活动id
     */
    private String promotionId;

    /**
     * 优惠金额
     */
    private BigDecimal promotionAmount;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 是否店铺活动，1-是，0-否
     */
    private Integer isStore;

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