package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsPromotionHistoryExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer historyIdNotEquals;

    /**
     * 用于批量操作
     */
    private String historyIdIn;

    /**
     * 活动历史id
     */
    private Integer historyId;

    /**
     * 商品活动id
     */
    private Integer goodsPromotionId;

    /**
     * 大于等于开始时间
     */
    private Date bindTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date bindTimeBefore;

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
     * 商品id（spu）
     */
    private Long goodsId;

    /**
     * 货品id（sku）
     */
    private Long productId;

    /**
     * 活动id
     */
    private Integer promotionId;

    /**
     * 活动类型
     */
    private Integer promotionType;

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
     * 描述信息
     */
    private String description;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照historyId倒序排列
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