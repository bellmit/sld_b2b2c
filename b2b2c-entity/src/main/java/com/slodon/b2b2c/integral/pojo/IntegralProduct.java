package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 积分货品表（SKU），指定特定规格
 */
@Data
public class IntegralProduct implements Serializable {
    private static final long serialVersionUID = -7085274515841439730L;
    @ApiModelProperty("自增物理主键")
    private Long id;

    @ApiModelProperty("货品id")
    private Long integralProductId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("规格值的ID，用逗号分隔")
    private String specValueIds;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
    private Integer integralPrice;

    @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
    private BigDecimal cashPrice;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

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