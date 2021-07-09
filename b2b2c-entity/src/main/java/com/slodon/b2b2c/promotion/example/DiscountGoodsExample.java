package com.slodon.b2b2c.promotion.example;

import java.io.Serializable;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class DiscountGoodsExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer discountGoodsIdNotEquals;

    /**
     * 用于批量操作
     */
    private String discountGoodsIdIn;

    /**
     * 折扣活动商品ID
     */
    private Integer discountGoodsId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 活动ID
     */
    private Integer discountId;

    /**
     * 商户ID
     */
    private Long storeId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照discountGoodsId倒序排列
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