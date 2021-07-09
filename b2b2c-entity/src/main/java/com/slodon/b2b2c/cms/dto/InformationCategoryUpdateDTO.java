package com.slodon.b2b2c.cms.dto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class InformationCategoryUpdateDTO implements Serializable {

    private static final long serialVersionUID = -5308015061603544684L;

    @ApiModelProperty(value = "分类id",required = true)
    private Integer cateId;

    @ApiModelProperty("分类名称")
    private String cateName;

    @ApiModelProperty("排序，值越小越靠前")
    private Integer sort;

    @ApiModelProperty("是否显示，0-不显示，1-显示，默认0，用于显示不显示的开关")
    private Integer isShow;

}
