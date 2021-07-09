package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 商品列表查看规格VO
 */
@Data
public class ProductVO {
    public ProductVO(Product product) {
        this.productId = product.getProductId();
        this.goodsId = product.getGoodsId();
        this.goodsName = product.getGoodsName();
        this.specValueIds = product.getSpecValueIds();
        this.specValues = StringUtils.isEmpty(product.getSpecValues()) ? "默认" : product.getSpecValues();
        this.productPrice = product.getProductPrice();
        this.productStock = product.getProductStock();
        this.productStockWarning = product.getProductStockWarning();
        this.productCode = product.getProductCode();
        this.barCode = product.getBarCode();
        this.mainImageUrl = FileUrlUtil.getFileUrl(product.getMainImage(),null);
        this.isDefault = product.getIsDefault();
        this.warning = product.getProductStock().compareTo(product.getProductStockWarning()) <= 0;
    }

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

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("库存")
    private Integer productStock;

    @ApiModelProperty("库存预警值")
    private Integer productStockWarning;

    @ApiModelProperty("货品货号：即SKU ID（店铺内唯一）")
    private String productCode;

    @ApiModelProperty("商品条形码（标准的商品条形码）")
    private String barCode;

    @ApiModelProperty("商品主图路径；每个SKU一张主图；如果启用图片规格，即为图片规格值对应组图中的主图。如果没有启用图片规格，即为SPU主图。")
    private String mainImageUrl;

    @ApiModelProperty("是否默认展示或加车货品：0-否；1-是")
    private Integer isDefault;

    @ApiModelProperty("是否达到预警值，true==是")
    private Boolean warning;
}
