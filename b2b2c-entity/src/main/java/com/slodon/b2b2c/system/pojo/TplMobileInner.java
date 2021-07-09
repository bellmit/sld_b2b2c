package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * Mobile装修模板页（平台内置）
 */
@Data
public class TplMobileInner implements Serializable {
    private static final long serialVersionUID = 1334487464333037506L;
    @ApiModelProperty("模板id")
    private Integer tplId;

    @ApiModelProperty("模板类型")
    private String type;

    @ApiModelProperty("模板名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否启用，0==不启用；1==启用")
    private Integer isEnable;

    @ApiModelProperty("模板缩略图")
    private String image;

    @ApiModelProperty("模板描述")
    private String desc;

    @ApiModelProperty("模板应用范围（商城首页==home,专题==topic,店铺首页==seller,活动==activity）")
    private String apply;

    @ApiModelProperty("模板数据")
    private String data;
}