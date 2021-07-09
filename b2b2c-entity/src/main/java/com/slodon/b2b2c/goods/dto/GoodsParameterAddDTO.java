package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsParameterAddDTO implements Serializable {
    private static final long serialVersionUID = -5491840825210043676L;

    @ApiModelProperty(value = "分组id",required = true)
    private Integer groupId;

    @ApiModelProperty(value = "属性名称",required = true)
    private String parameterName;

    @ApiModelProperty(value = "排序0到255，越小越靠前展示",required = true)
    private Integer sort;

    @ApiModelProperty(value = "是否展示：0-不展示，1-展示",required = true)
    private Integer isShow;

    @ApiModelProperty(value = "属性值，用逗号隔开",required = true)
    private String parameterValue;
}
