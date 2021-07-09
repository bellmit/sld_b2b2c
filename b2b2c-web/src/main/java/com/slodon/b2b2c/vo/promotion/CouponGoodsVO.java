package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装指定商品VO
 * @Author wuxy
 */
@Data
public class CouponGoodsVO implements Serializable {

    private static final long serialVersionUID = 420544288365372868L;
    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品价格")
    private BigDecimal goodsPrice;  //商品价格

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    public CouponGoodsVO(Goods goods) {
        storeId = goods.getStoreId();
        storeName = goods.getStoreName();
        goodsImage = FileUrlUtil.getFileUrl(goods.getMainImage(), null);
        goodsId = goods.getGoodsId();
        goodsName = goods.getGoodsName();
        goodsPrice = goods.getGoodsPrice();
        goodsStock = goods.getGoodsStock();
    }
}
