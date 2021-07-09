package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装商品详情推荐VO对象
 * @Author wuxy
 */
@Data
public class GoodsRecommendVO implements Serializable {
    private static final long serialVersionUID = -3858843371729213391L;
    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("默认货品id")
    private Long defaultProductId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("市场价")
    private BigDecimal marketPrice;

    @ApiModelProperty("商品副标题")
    private String goodsBrief;

    @ApiModelProperty("销量")
    private Integer saleNum;

    @ApiModelProperty("活动列表")
    private List<GoodsListVO.PromotionVO> activityList;

    public GoodsRecommendVO() {
    }

    public GoodsRecommendVO(Goods goods) {
        this.goodsId = goods.getGoodsId();
        this.goodsName = goods.getGoodsName();
        this.goodsImage = FileUrlUtil.getFileUrl(goods.getMainImage(), null);
        this.goodsPrice = goods.getGoodsPrice();
        this.defaultProductId = goods.getDefaultProductId();
        this.storeId = goods.getStoreId();
        this.storeName = goods.getStoreName();
    }
}
