package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsParameterGroupUpdateDTO implements Serializable {
    private static final long serialVersionUID = -7027042451064004101L;
    @ApiModelProperty(value = "分组id", required = true)
    private Integer groupId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty("是否展示：0-不展示，1-展示")
    private Integer isShow;
}
