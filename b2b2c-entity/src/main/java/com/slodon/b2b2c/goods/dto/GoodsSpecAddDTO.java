package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cwl
 */
@Data
public class GoodsSpecAddDTO implements Serializable {

    private static final long serialVersionUID = 235646749090161014L;

    @ApiModelProperty(value ="规格名称",required = true)
    private String specName;

    @ApiModelProperty(value ="规格值,多个规格值用,分割")
    private String specValues;

    @ApiModelProperty(value ="排序0到255，越小越靠前展示",required = true)
    private Integer sort;

    @ApiModelProperty(value ="是否展示：0-不展示，1-展示",required = true)
    private Integer state;
}
