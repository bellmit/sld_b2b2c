package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.Product;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装选择货品VO对象
 */
@Data
public class ProductSelectVO implements Serializable {

    private static final long serialVersionUID = 6627859141443643882L;
    @ApiModelProperty("货品id")
    private Long productId;

    @ApiModelProperty("货品价格")
    private BigDecimal productPrice;

    @ApiModelProperty("货品库存")
    private Integer productStock;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    public ProductSelectVO(Product product) {
        this.productId = product.getProductId();
        this.productPrice = product.getProductPrice();
        this.productStock = product.getProductStock();
        this.specValues = product.getSpecValues();
    }

}
