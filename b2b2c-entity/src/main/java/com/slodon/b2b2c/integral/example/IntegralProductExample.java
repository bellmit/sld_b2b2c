package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 积分货品表（SKU），指定特定规格example
 */
@Data
public class IntegralProductExample implements Serializable {
    private static final long serialVersionUID = 4794533893054251750L;
    /**
     * 用于编辑时的重复判断
     */
    private Long integralProductIdNotEquals;

    /**
     * 用于批量操作
     */
    private String integralProductIdIn;

    /**
     * 自增物理主键
     */
    private Long id;

    /**
     * 货品id
     */
    private Long integralProductId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 商品名称为3到50个字符(商品副标题)
     */
    private String goodsName;

    /**
     * 商品名称为3到50个字符(商品副标题),用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 规格值，用逗号分隔
     */
    private String specValues;

    /**
     * 规格值的ID，用逗号分隔
     */
    private String specValueIds;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零
     */
    private Integer integralPrice;

    /**
     * 价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分
     */
    private BigDecimal cashPrice;

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
     * 库存
     */
    private Integer productStock;

    /**
     * 库存预警值
     */
    private Integer productStockWarning;

    /**
     * 是否已经预警过 1==否 2==是
     */
    private Integer productStockWarningState;

    /**
     * productStockWarningStateIn，用于批量操作
     */
    private String productStockWarningStateIn;

    /**
     * productStockWarningStateNotIn，用于批量操作
     */
    private String productStockWarningStateNotIn;

    /**
     * productStockWarningStateNotEquals，用于批量操作
     */
    private Integer productStockWarningStateNotEquals;

    /**
     * 所有规格销量相加等于good表商品总销量
     */
    private Integer actualSales;

    /**
     * 货品货号：即SKU ID（店铺内唯一）
     */
    private String productCode;

    /**
     * 商品条形码（标准的商品条形码）
     */
    private String barCode;

    /**
     * 重量kg
     */
    private BigDecimal weight;

    /**
     * 长度cm
     */
    private BigDecimal length;

    /**
     * 宽度cm
     */
    private BigDecimal width;

    /**
     * 高度cm
     */
    private BigDecimal height;

    /**
     * 商品主图路径；每个SKU一张主图；如果启用图片规格，即为图片规格值对应组图中的主图。如果没有启用图片规格，即为SPU主图。
     */
    private String mainImage;

    /**
     * 货品状态: 0-已删除,1-正常,2-禁用（发布编辑商品禁用规格）,3-锁定（单品活动锁定sku）
     */
    private Integer state;

    /**
     * stateIn，用于批量操作
     */
    private String stateIn;

    /**
     * stateNotIn，用于批量操作
     */
    private String stateNotIn;

    /**
     * stateNotEquals，用于批量操作
     */
    private Integer stateNotEquals;

    /**
     * 是否默认展示或加车货品：0-否；1-是
     */
    private Integer isDefault;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照integralProductId倒序排列
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