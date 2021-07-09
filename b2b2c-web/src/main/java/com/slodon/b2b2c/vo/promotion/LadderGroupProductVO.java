package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.promotion.pojo.LadderGroupGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装阶梯团商品VO对象
 * @Author wuxy
 */
@Data
public class LadderGroupProductVO implements Serializable {

    private static final long serialVersionUID = -4270747894530746442L;
    @ApiModelProperty("阶梯团商品id")
    private Integer groupGoodsId;

    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("规格值，用逗号分隔")
    private String specValues;

    @ApiModelProperty("货品id（sku）")
    private Long productId;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("库存")
    private Integer stock;

    @ApiModelProperty("第一阶梯价格或折扣")
    private BigDecimal ladderPrice1;

    @ApiModelProperty("第二阶梯价格或折扣")
    private BigDecimal ladderPrice2;

    @ApiModelProperty("第三阶梯价格或折扣")
    private BigDecimal ladderPrice3;

    public LadderGroupProductVO(LadderGroupGoods ladderGroupGoods) {
        this.groupGoodsId = ladderGroupGoods.getGroupGoodsId();
        this.groupId = ladderGroupGoods.getGroupId();
        this.goodsId = ladderGroupGoods.getGoodsId();
        this.specValues = StringUtil.isEmpty(ladderGroupGoods.getSpecValues()) ? "默认" : ladderGroupGoods.getSpecValues();
        this.productId = ladderGroupGoods.getProductId();
        this.productPrice = ladderGroupGoods.getProductPrice();
        this.ladderPrice1 = ladderGroupGoods.getLadderPrice1();
        this.ladderPrice2 = ladderGroupGoods.getLadderPrice2();
        this.ladderPrice3 = ladderGroupGoods.getLadderPrice3();
    }
}
