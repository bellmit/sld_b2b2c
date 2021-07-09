package com.slodon.b2b2c.vo.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装品牌绑定分类VO对象
 */
@Data
public class BrandAndCateVO {

    @ApiModelProperty("商品分类id")
    private Integer categoryId;

    @ApiModelProperty("商品分类名称")
    private String categoryName;

    @ApiModelProperty("子分类集合")
    private List<BrandAndCateVO> children;

}