package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品列表查看规格VO
 */
@Data
public class IntegralProductVO implements Serializable {

    private static final long serialVersionUID = -6967953358724376226L;
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

    @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
    private Integer integralPrice;

    @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
    private BigDecimal cashPrice;

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

    public IntegralProductVO(IntegralProduct integralProduct) {
        this.integralProductId = integralProduct.getIntegralProductId();
        this.goodsId = integralProduct.getGoodsId();
        this.goodsName = integralProduct.getGoodsName();
        this.specValueIds = integralProduct.getSpecValueIds();
        this.specValues = StringUtils.isEmpty(integralProduct.getSpecValues()) ? "默认" : integralProduct.getSpecValues();
        this.integralPrice = integralProduct.getIntegralPrice();
        this.cashPrice = integralProduct.getCashPrice();
        this.productStock = integralProduct.getProductStock();
        this.productStockWarning = integralProduct.getProductStockWarning();
        this.productCode = integralProduct.getProductCode();
        this.barCode = integralProduct.getBarCode();
        this.mainImageUrl = FileUrlUtil.getFileUrl(integralProduct.getMainImage(), null);
        this.isDefault = integralProduct.getIsDefault();
        this.warning = integralProduct.getProductStock().compareTo(integralProduct.getProductStockWarning()) <= 0;
    }

}
