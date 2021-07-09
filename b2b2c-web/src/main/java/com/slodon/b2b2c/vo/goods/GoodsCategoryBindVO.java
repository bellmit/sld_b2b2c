package com.slodon.b2b2c.vo.goods;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装商品分类绑定的品牌与属性VO对象
 */
@Data
public class GoodsCategoryBindVO {

    public GoodsCategoryBindVO(List<GoodsBrandVO> goodsBrandVOList,List<GoodsAttributeVO> goodsAttributeVOList) {
        this.goodsAttributeList=goodsAttributeVOList;
        this.goodsBrandList=goodsBrandVOList;
    }

    @ApiModelProperty(value="品牌列表")
    private List<GoodsBrandVO> goodsBrandList;

    @ApiModelProperty(value="商品属性列表")
    private List<GoodsAttributeVO> goodsAttributeList;
}