package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @description: 封装满优惠送赠品VO对象
 * @author: wuxy
 * @create: 2020.11.03 15:11
 **/
@Data
public class FullGiftVO implements Serializable {

    private static final long serialVersionUID = 3134174802981826109L;
    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    public FullGiftVO(Goods goods) {
        goodsId = goods.getGoodsId();
        goodsName = goods.getGoodsName();
        goodsPrice = goods.getGoodsPrice();
        goodsStock = goods.getGoodsStock();
        goodsImage = FileUrlUtil.getFileUrl(goods.getMainImage(), ImageSizeEnum.SMALL);
    }

}
