package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class StoreGradeAddDTO implements Serializable {

    private static final long serialVersionUID = -689320889381321153L;

    @ApiModelProperty(value = "等级名称",required = true)
    private String gradeName;

    @ApiModelProperty(value = "允许发布的商品数量",required = true)
    private Integer goodsLimit;

    @ApiModelProperty(value = "可推荐的商品数量",required = true)
    private Integer recommendLimit;

    @ApiModelProperty(value = "费用",required = true)
    private String price;

    @ApiModelProperty(value = "级别，数目越大级别越高",required = true)
    private Integer sort;

    @ApiModelProperty("申请说明")
    private String description;
}
