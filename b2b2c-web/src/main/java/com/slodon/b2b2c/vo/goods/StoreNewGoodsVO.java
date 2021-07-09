package com.slodon.b2b2c.vo.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装店铺上新商品列表VO对象
 */
@Data
public class StoreNewGoodsVO {

    @ApiModelProperty("上新时间")
    private String onLineTime;

    @ApiModelProperty("商品列表")
    private List<GoodsVO> goodsVOList;
}