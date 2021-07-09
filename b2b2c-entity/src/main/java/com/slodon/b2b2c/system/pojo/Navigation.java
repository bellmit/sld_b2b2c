package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * PC导航栏
 */
@Data
public class Navigation implements Serializable {
    private static final long serialVersionUID = -8899551100169376628L;
    @ApiModelProperty("导航id")
    private Integer navId;

    @ApiModelProperty("导航名称")
    private String navName;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否显示，0：否；1：是（默认）")
    private Integer isShow;

    @ApiModelProperty("链接数据，json类型，包含链接类型、链接地址等")
    private String data;
}