package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 秒杀标签
 */
@Data
public class SeckillLabelAddDTO implements Serializable {

    private static final long serialVersionUID = 362945729634411148L;
    @ApiModelProperty(value = "标签名称",required = true)
    private String labelName;

    @ApiModelProperty(value = "标签排序",required = true)
    private Integer sort;

}