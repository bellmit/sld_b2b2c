package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * PC装修模板表
 */
@Data
public class TplPc implements Serializable {
    private static final long serialVersionUID = -979269264716410688L;
    @ApiModelProperty("模板id")
    private Integer tplPcId;

    @ApiModelProperty("模板分类")
    private String type;

    @ApiModelProperty("模板唯一标识")
    private String code;

    @ApiModelProperty("模板分类名称")
    private String typeName;

    @ApiModelProperty("模板风格")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否启用，0==不启用；1==启用")
    private Integer isEnable;

    @ApiModelProperty("应用位置 1==商城首页；2==店铺首页，3==通用")
    private Integer client;

    @ApiModelProperty("是否可实例化 0==否，1==是")
    private Integer isInstance;

    @ApiModelProperty("模板缩略图")
    private String image;

    @ApiModelProperty("模板描述")
    private String desc;

    @ApiModelProperty("模板数据")
    private String data;

    @ApiModelProperty("默认模板实例数据")
    private String defaultData;
}