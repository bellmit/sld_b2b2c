package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 提交订单页展示信息vo
 */
@Data
public class IntegralOrderSubmitVO implements Serializable {

    private static final long serialVersionUID = 523637748866037032L;
    @ApiModelProperty("店铺id")
    private Long storeId;
    @ApiModelProperty("店铺名称")
    private String storeName;
    @ApiModelProperty("积分")
    private Integer integralAmount;
    @ApiModelProperty("现金")
    private BigDecimal cashAmount;
    @ApiModelProperty("可用积分")
    private Integer memberIntegral;
    @ApiModelProperty("积分换算比例")
    private String integralScale;
    @ApiModelProperty("是否可以开增值税发票,true-可以")
    private Boolean isVatInvoice;
    @ApiModelProperty("货品信息")
    private ProductVO product;
    @ApiModelProperty("积分列表")
    private List<Integer> integralList;

    @Data
    public static class ProductVO implements Serializable {

        private static final long serialVersionUID = 5603177148207712650L;
        @ApiModelProperty("skuId")
        private Long productId;
        @ApiModelProperty("商品id")
        private Long goodsId;
        @ApiModelProperty("商品名称")
        private String goodsName;
        @ApiModelProperty("sku规格")
        private String specValues;
        @ApiModelProperty("sku图片")
        private String goodsImage;
        @ApiModelProperty("积分")
        private Integer integralPrice;
        @ApiModelProperty("现金")
        private BigDecimal cashPrice;
        @ApiModelProperty("购买数量")
        private Integer buyNum;

        public ProductVO(IntegralProduct integralProduct) {
            this.productId = integralProduct.getIntegralProductId();
            this.goodsId = integralProduct.getGoodsId();
            this.goodsName = integralProduct.getGoodsName();
            this.specValues = integralProduct.getSpecValues();
            this.goodsImage = FileUrlUtil.getFileUrl(integralProduct.getMainImage(), null);
            this.integralPrice = integralProduct.getIntegralPrice();
            this.cashPrice = integralProduct.getCashPrice();
        }
    }

}
