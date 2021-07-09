package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * mobile装修基础组件
 */
@Data
public class TplMobile implements Serializable {
    private static final long serialVersionUID = 2196623128302473050L;
    @ApiModelProperty("模板id")
    private Integer tplId;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("模板类型")
    private String type;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("是否展示 0--否，1--是")
    private Integer isUse;

    @ApiModelProperty("模板应用范围（商城首页==home,专题==topic,店铺首页==seller,活动==activity）")
    private String apply;

    @ApiModelProperty("排序，值越小级别越高")
    private String sort;
}