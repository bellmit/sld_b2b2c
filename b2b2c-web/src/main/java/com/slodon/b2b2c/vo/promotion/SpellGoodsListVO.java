package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.SpellGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装拼团查看商品VO对象
 * @Author wuxy
 */
@Data
@NoArgsConstructor
public class SpellGoodsListVO implements Serializable {

    private static final long serialVersionUID = -5667949138495399321L;
    @ApiModelProperty("活动商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品原价")
    private BigDecimal productPrice;

    @ApiModelProperty("拼团价")
    private BigDecimal spellPrice;

    @ApiModelProperty("活动团队")
    private Integer spellTeamNum;

    @ApiModelProperty("商品规格列表")
    private List<SpellProductVO> productList;

    public SpellGoodsListVO(SpellGoods spellGoods) {
        goodsId = spellGoods.getGoodsId();
        goodsName = spellGoods.getGoodsName();
        goodsImage = FileUrlUtil.getFileUrl(spellGoods.getGoodsImage(), ImageSizeEnum.SMALL);
        productPrice = spellGoods.getProductPrice();
        spellPrice = spellGoods.getSpellPrice();
    }
}
