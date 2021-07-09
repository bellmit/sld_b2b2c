package com.slodon.b2b2c.goods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 计算运费dto
 */
@Data
public class CalculateExpressDTO implements Serializable {

    private static final long serialVersionUID = -7453414076415027798L;

    private String cityCode;                                         //城市编码
    private List<ProductInfo> productList = new ArrayList<>();       //货品列表


    /**
     * 计算运费用的商品信息
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo implements Serializable{

        private static final long serialVersionUID = 3200409853577690011L;
        private Long goodsId;//商品id
        private Long productId;//货品id
        private BigDecimal productPrice;//货品单价
        private Integer number;//购买数量
    }
}
