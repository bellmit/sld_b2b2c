package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员收藏商品表
 */
@Data
public class MemberFollowProductAddDTO implements Serializable {

    private static final long serialVersionUID = -6889447104559266874L;

    @ApiModelProperty("用户ID")
    private Integer memberId;

    @ApiModelProperty("货品ID")
    private Integer productId;

    @ApiModelProperty("收藏商品时的价格")
    private BigDecimal productPrice;

    @ApiModelProperty("商品图片")
    private String productImage;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题，长度建议140个字符内")
    private String goodsBrief;

    @ApiModelProperty("收藏店铺id")
    private Long storeId;

    @ApiModelProperty("收藏店铺名称")
    private String storeName;

    @ApiModelProperty("商品的分类id（三级分类ID）")
    private Integer goodsCategoryId;

    @ApiModelProperty("商品关联的店铺内部分类id")
    private Integer storeCategoryId;
}