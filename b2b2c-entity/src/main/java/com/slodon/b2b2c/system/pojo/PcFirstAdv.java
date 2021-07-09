package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * pc首页弹层广告表
 */
@Data
public class PcFirstAdv implements Serializable {
    private static final long serialVersionUID = -8097888524748390485L;
    @ApiModelProperty("广告id")
    private Integer advId;

    @ApiModelProperty("弹层广告数据")
    private String data;
}