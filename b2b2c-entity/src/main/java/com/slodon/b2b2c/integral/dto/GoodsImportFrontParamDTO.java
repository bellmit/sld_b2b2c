package com.slodon.b2b2c.integral.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商户导入商品前端传参dto
 */
@Data
public class GoodsImportFrontParamDTO implements Serializable {

    private static final long serialVersionUID = -3649294881855140609L;
    @ApiModelProperty(value = "商品id", required = true)
    private Long goodsId;
    @ApiModelProperty(value = "积分商品标签ids集合，用逗号隔开", required = true)
    private String labelIds;
    @ApiModelProperty(value = "是否立即售卖，true==是，false==放入仓库", required = true)
    private Boolean sellNow = false;
    @ApiModelProperty(value = "货品列表", required = true)
    private List<ProductInfo> productList;

    @Data
    public static class ProductInfo implements Serializable {

        private static final long serialVersionUID = 2401788530631869816L;
        @ApiModelProperty(value = "货品id", required = true)
        private Long productId;
        @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
        private Integer integralPrice;
        @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
        private BigDecimal cashPrice;
        @ApiModelProperty(value = "商品库存")
        private Integer productStock;
        @ApiModelProperty(value = "重量kg")
        private BigDecimal weight;
        @ApiModelProperty(value = "长度cm")
        private BigDecimal length;
        @ApiModelProperty(value = "宽度cm")
        private BigDecimal width;
        @ApiModelProperty(value = "高度cm")
        private BigDecimal height;
        @ApiModelProperty(value = "库存预警值")
        private Integer productStockWarning;
        @ApiModelProperty(value = "货号")
        private String productCode;
        @ApiModelProperty(value = "条形码")
        private String barCode;
    }

}
