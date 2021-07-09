package com.slodon.b2b2c.vo.promotion;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装优惠券指定商品分类VO对象
 * @Author wuxy
 * @date 2020.11.03 20:37
 */
@Data
public class CouponGoodsCategoryVO implements Serializable {

    private static final long serialVersionUID = 2319989524011776843L;
    @ApiModelProperty("一级商品分类ID")
    private Integer categoryId1;

    @ApiModelProperty("一级分类名称")
    private String categoryName1;

    @ApiModelProperty("二级商品分类ID")
    private Integer categoryId2;

    @ApiModelProperty("二级分类名称")
    private String categoryName2;

    @ApiModelProperty("三级商品分类ID")
    private Integer categoryId3;

    @ApiModelProperty("三级分类名称")
    private String categoryName3;
}
