package com.slodon.b2b2c.cms.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class ArticleCategoryAddDTO implements Serializable {

    private static final long serialVersionUID = 1863529423228942801L;

    @ApiModelProperty(value = "分类名称",required = true)
    private String categoryName;

    @ApiModelProperty(value = "排序：序号越小，越靠前",required = true)
    private Integer sort;

}
