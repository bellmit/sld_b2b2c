package com.slodon.b2b2c.integral.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品搜索条件DTO
 */
@Data
public class GoodsSearchConditionDTO implements Serializable {

    private static final long serialVersionUID = -5908176315005180581L;
    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("排序 0:默认排序；1销量；2店铺推荐")
    private Integer sort;

    @ApiModelProperty("标签id")
    private String labelId;

}
