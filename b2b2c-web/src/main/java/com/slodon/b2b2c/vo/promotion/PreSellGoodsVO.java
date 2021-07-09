package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装预售活动选择商品VO对象
 * @Author wuxy
 * @date 2020.11.04 12:26
 */
@Data
public class PreSellGoodsVO implements Serializable {

    private static final long serialVersionUID = -179762806910114951L;
    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("货品列表")
    private List<PreSellProductVO> productList;

    public PreSellGoodsVO(Goods goods) {
        goodsId = goods.getGoodsId();
        goodsName = goods.getGoodsName();
        goodsImage = FileUrlUtil.getFileUrl(goods.getMainImage(), ImageSizeEnum.SMALL);
    }
}
