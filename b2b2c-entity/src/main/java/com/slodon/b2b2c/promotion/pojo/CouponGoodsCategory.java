package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 优惠券和商品分类的绑定表
 */
@Data
public class CouponGoodsCategory implements Serializable {
    private static final long serialVersionUID = 3651764676506982861L;
    @ApiModelProperty("优惠活动商品分类ID")
    private Integer couponCategoryId;

    @ApiModelProperty("优惠券ID")
    private Integer couponId;

    @ApiModelProperty("分类ID")
    private Integer categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("分类级别（1、2、3级分类）")
    private Integer categoryGrade;
}