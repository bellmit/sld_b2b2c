package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装预售查看商品VO对象
 * @Author wuxy
 * @date 2020.11.04 15:11
 */
@Data
@NoArgsConstructor
public class PreSellGoodsListVO implements Serializable {

    private static final long serialVersionUID = 8190160987239947448L;
    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("预售价格")
    private BigDecimal presellPrice;

    @ApiModelProperty("预售库存")
    private Integer presellStock;

    @ApiModelProperty("已售件数")
    private Integer saleNum;

    @ApiModelProperty("商品规格列表")
    private List<PreSellProductVO> productList;

    public PreSellGoodsListVO(PresellGoods presellGoods) {
        goodsId = presellGoods.getGoodsId();
        goodsName = presellGoods.getGoodsName();
        goodsImage = FileUrlUtil.getFileUrl(presellGoods.getGoodsImage(), null);
        productPrice = presellGoods.getProductPrice();
        presellPrice = presellGoods.getPresellPrice();
    }
}
