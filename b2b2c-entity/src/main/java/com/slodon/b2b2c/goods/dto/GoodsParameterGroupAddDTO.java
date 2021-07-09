package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsParameterGroupAddDTO implements Serializable {
    private static final long serialVersionUID = -5204389277400759015L;

    @ApiModelProperty(value = "分组名称",required = true)
    private String groupName;

    @ApiModelProperty(value = "排序0到255，越小越靠前展示",required = true)
    private Integer sort;

    @ApiModelProperty(value = "是否展示：0-不展示，1-展示",required = true)
    private Integer isShow;
}
