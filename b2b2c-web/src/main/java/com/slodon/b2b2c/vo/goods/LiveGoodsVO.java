package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装直播商品VO对象
 * @Author wuxy
 */
@Data
public class LiveGoodsVO implements Serializable {

    private static final long serialVersionUID = -4625739827301083408L;
    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题")
    private String goodsBrief;

    @ApiModelProperty("商品价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("默认货品id")
    private Long defaultProductId;

    public LiveGoodsVO(Goods goods) {
        this.goodsId = goods.getGoodsId();
        this.goodsName = goods.getGoodsName();
        this.goodsBrief = goods.getGoodsBrief();
        this.goodsPrice = goods.getGoodsPrice();
        this.goodsStock = goods.getGoodsStock();
        this.storeId = goods.getStoreId();
        this.storeName = goods.getStoreName();
        this.goodsImage = FileUrlUtil.getFileUrl(goods.getMainImage(), null);
        this.defaultProductId = goods.getDefaultProductId();
    }
}
