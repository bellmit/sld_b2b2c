package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品服务标签
 */
@Data
public class GoodsServiceLabelEditDTO implements Serializable {

    private static final long serialVersionUID = 7865456536854098612L;

    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("标签描述")
    private String description;


}