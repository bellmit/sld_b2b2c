package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀标签
 */
@Data
public class SeckillLabelEditDTO implements Serializable {


    private static final long serialVersionUID = -7945904147530742958L;
    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("标签排序")
    private Integer sort;

}