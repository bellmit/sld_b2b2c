package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsParameterUpdateDTO implements Serializable {

    private static final long serialVersionUID = 3768736403327797044L;

    @ApiModelProperty(value = "属性id",required = true)
    private Integer parameterId;

//    @ApiModelProperty("分组id")
//    private Integer groupId;

    @ApiModelProperty("属性名称")
    private String parameterName;

    @ApiModelProperty("排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty("是否展示：0-不展示，1-展示")
    private Integer isShow;

    @ApiModelProperty("属性值，用逗号隔开")
    private String parameterValue;
}
