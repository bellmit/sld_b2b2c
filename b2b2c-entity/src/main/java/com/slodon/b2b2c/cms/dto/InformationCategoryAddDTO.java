package com.slodon.b2b2c.cms.dto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class InformationCategoryAddDTO implements Serializable {

    private static final long serialVersionUID = -6977819583383782269L;

    @ApiModelProperty(value = "分类名称",required = true)
    private String cateName;

    @ApiModelProperty(value = "排序，值越小越靠前",required = true)
    private Integer sort;

}
