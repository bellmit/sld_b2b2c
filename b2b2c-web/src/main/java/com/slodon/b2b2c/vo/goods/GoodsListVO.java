package com.slodon.b2b2c.vo.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @program: slodon
 * @Description 封装商品搜索列表VO对象
 * @Author wuxy
 */
@Data
public class GoodsListVO implements Serializable {

    private static final long serialVersionUID = 7695118498624027141L;
    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("商品副标题")
    private String goodsBrief;

    @ApiModelProperty("默认skuId")
    private Long defaultProductId;

    @ApiModelProperty("1级分类ID")
    private Integer categoryId1;

    @ApiModelProperty("2级分类ID")
    private Integer categoryId2;

    @ApiModelProperty("3级分类ID")
    private Integer categoryId3;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺分类id列表")
    private Set<Integer> storeCategoryId = new HashSet<>();

    @ApiModelProperty("销量")
    private Integer saleNum;

    @ApiModelProperty("是否自营")
    private Integer isOwnShop;

    @ApiModelProperty("是否收藏")
    private Boolean isFollowGoods;

    @ApiModelProperty("评论数")
    private Integer commentsNumber;

    @ApiModelProperty("活动列表")
    private List<PromotionVO> activityList;

    @Data
    public static class PromotionVO implements Serializable {

        private static final long serialVersionUID = -8754680120155679765L;
        @ApiModelProperty("商品活动绑定id")
        private Integer goodsPromotionId;

        @ApiModelProperty("活动id")
        private Integer promotionId;

        @ApiModelProperty("活动名称")
        private String promotionName;

        @ApiModelProperty("活动等级 1-商品活动；2-店铺活动；3-平台活动")
        private Integer promotionGrade;

        @ApiModelProperty("活动类型")
        private Integer promotionType;
    }
}
