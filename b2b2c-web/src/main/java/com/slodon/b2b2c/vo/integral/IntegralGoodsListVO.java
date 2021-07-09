package com.slodon.b2b2c.vo.integral;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 封装积分商品搜索列表VO对象
 */
@Data
public class IntegralGoodsListVO implements Serializable {

    private static final long serialVersionUID = 7695118498624027141L;
    @ApiModelProperty("商品id")
    private Long integralGoodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
    private Integer integralPrice;

    @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
    private BigDecimal cashPrice;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("商品副标题")
    private String goodsBrief;

    @ApiModelProperty("默认skuId")
    private Long defaultProductId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("销量")
    private Integer saleNum;

    @ApiModelProperty("是否自营")
    private Integer isOwnShop;

}
