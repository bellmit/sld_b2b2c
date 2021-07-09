package com.slodon.b2b2c.cms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class ArticleCategoryUpdateDTO implements Serializable {

    private static final long serialVersionUID = 7725403260456250045L;

    @ApiModelProperty(value = "分类id",required = true)
    private Integer categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("排序：序号越小，越靠前")
    private Integer sort;

    @ApiModelProperty("是否显示：0、不显示；1、显示")
    private Integer isShow;

}
