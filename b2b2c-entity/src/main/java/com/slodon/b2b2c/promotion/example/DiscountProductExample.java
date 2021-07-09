package com.slodon.b2b2c.promotion.example;

import java.io.Serializable;
import java.math.BigDecimal;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class DiscountProductExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer discountProductIdNotEquals;

    /**
     * 用于批量操作
     */
    private String discountProductIdIn;

    /**
     * 折扣活动货品ID
     */
    private Integer discountProductId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 货品ID
     */
    private Long productId;

    /**
     * 商户ID
     */
    private Long storeId;

    /**
     * 是
     */
    private Integer isCustomPrice;

    /**
     * 如果为按比例折扣，此价格为空，折扣活动价格
     */
    private BigDecimal discountPrice;

    /**
     * 规格属性值ID，用逗号分隔
     */
    private String specAttrId;

    /**
     * 规格值，用逗号分隔
     */
    private String specName;

    /**
     * 规格值，用逗号分隔,用于模糊查询
     */
    private String specNameLike;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 库存预警值
     */
    private Integer stockWarning;

    /**
     * 是否启用 1-启用 0-不启用
     */
    private Integer state;

    /**
     * 重量kg
     */
    private BigDecimal weight;

    /**
     * 体积立方厘米;长(CM)×宽(CM)×高(CM)
     */
    private Long volume;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照discountProductId倒序排列
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