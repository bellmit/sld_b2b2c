package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装选择商品VO对象
 */
@Data
public class GoodsSelectVO implements Serializable {

    private static final long serialVersionUID = 6187665760948457870L;
    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    @ApiModelProperty("参与其他活动状态:1-未参与其他活动，2-参与其他活动")
    private Integer activityState;

    @ApiModelProperty("参与活动情况描述")
    private String activityDesc;

    @ApiModelProperty("秒杀货品列表")
    private List<ProductSelectVO> seckillProductVOList;

    public GoodsSelectVO(Goods goods) {
        goodsId = goods.getGoodsId();
        goodsName = goods.getGoodsName();
        goodsImage = FileUrlUtil.getFileUrl(goods.getMainImage(), ImageSizeEnum.SMALL);
        goodsPrice = goods.getGoodsPrice();
        goodsStock = goods.getGoodsStock();
    }
}
