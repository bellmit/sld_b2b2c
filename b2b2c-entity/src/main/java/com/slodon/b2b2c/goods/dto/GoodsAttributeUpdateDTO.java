package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cwl
 */
@Data
public class GoodsAttributeUpdateDTO implements Serializable {

    private static final long serialVersionUID = 3423234564236761014L;

    @ApiModelProperty(value ="属性id",required = true)
    private Integer attributeId;

    @ApiModelProperty(value ="属性名称")
    private String attributeName;

    @ApiModelProperty(value ="属性值，多个属性用，分割")
    private String attributeValues;

    @ApiModelProperty(value ="排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty(value ="是否展示：0-不展示，1-展示")
    private Integer isShow;
}
