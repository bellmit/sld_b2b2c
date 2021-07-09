package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品表（SKU），指定特定规格
 */
@Data
public class Product implements Serializable {
    private static final long serialVersionUID = 8947116473478684299L;
    @ApiModelProperty("自增物理主键")
    private Long id;

    @ApiModelProperty("货品id")
    private Long productId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("规格值的ID，用逗号分隔")
    private String specValueIds;

    @ApiModelProperty("品牌id")
    private Integer brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("1级分类ID")
    private Integer categoryId1;

    @ApiModelProperty("2级分类ID")
    private Integer categoryId2;

    @ApiModelProperty("3级分类ID")
    private Integer categoryId3;

    @ApiModelProperty("商品所属分类路径，(例如：分类1/分类2/分类3，前后都无斜杠)")
    private String categoryPath;

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("活动价格，发布单减活动时，设置活动价")
    private BigDecimal activityPrice;

    @ApiModelProperty("库存")
    private Integer productStock;

    @ApiModelProperty("库存预警值")
    private Integer productStockWarning;

    @ApiModelProperty("是否已经预警过 1==否 2==是")
    private Integer productStockWarningState;

    @ApiModelProperty("所有规格销量相加等于good表商品总销量")
    private Integer actualSales;

    @ApiModelProperty("货品货号：即SKU ID（店铺内唯一）")
    private String productCode;

    @ApiModelProperty("商品条形码（标准的商品条形码）")
    private String barCode;

    @ApiModelProperty("重量kg")
    private BigDecimal weight;

    @ApiModelProperty("长度cm")
    private BigDecimal length;

    @ApiModelProperty("宽度cm")
    private BigDecimal width;

    @ApiModelProperty("高度cm")
    private BigDecimal height;

    @ApiModelProperty("商品主图路径；每个SKU一张主图；如果启用图片规格，即为图片规格值对应组图中的主图。如果没有启用图片规格，即为SPU主图。")
    private String mainImage;

    @ApiModelProperty("货品状态: 0-已删除,1-正常,2-禁用（发布编辑商品禁用规格）,3-锁定（单品活动锁定sku）")
    private Integer state;

    @ApiModelProperty("是否默认展示或加车货品：0-否；1-是")
    private Integer isDefault;

}