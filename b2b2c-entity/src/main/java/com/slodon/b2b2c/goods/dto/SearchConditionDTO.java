package com.slodon.b2b2c.goods.dto;

import com.slodon.b2b2c.core.response.PagerInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 商品搜索条件DTO
 * @Author wuxy
 */
@Data
public class SearchConditionDTO implements Serializable {

    private static final long serialVersionUID = -5754528220519227826L;
    @ApiModelProperty("商品id集合")
    private String goodsIds;

    @ApiModelProperty("商品分类id")
    private String categoryIds;

    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("排序 0:默认排序；1销量；2评论；3价格从低到高；4、价格从高到低；5、人气从高到低；6、收藏数从高到低；7、店铺推荐")
    private Integer sort;

    @ApiModelProperty("是否自营 0所有商品；1自营商品")
    private Integer isSelf;

    @ApiModelProperty("有货无货 [0所有商品；1有货商品]")
    private Integer store;

    @ApiModelProperty("商品属性信息，格式为：属性1名称_属性值名称,属性2名称_属性值名称")
    private String attributeInfo;

    @ApiModelProperty("品牌id集合")
    private String brandIds;

    @ApiModelProperty("最低价格")
    private BigDecimal lowPrice;

    @ApiModelProperty("最高价格")
    private BigDecimal highPrice;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺内分类id")
    private Integer storeInnerLabelId;

    @ApiModelProperty(value = "分页对象", hidden = true)
    private PagerInfo pager;
}
