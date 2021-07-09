package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺分类，二级分类
 */
@Data
public class StoreCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分类ID")
    private Integer categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("父ID")
    private Integer parentId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("分类级别：1-一级分类，2-二级分类")
    private Integer grade;
}