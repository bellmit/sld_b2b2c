package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品标签和商品绑定关系
 */
@Data
public class GoodsBindLabel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("商品Id")
    private Long goodsId;

    @ApiModelProperty("商品标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("创建时间")
    private Date createTime;
}