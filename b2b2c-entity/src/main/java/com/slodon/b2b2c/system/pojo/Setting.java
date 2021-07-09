package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 系统设置表
 */
@Data
public class Setting implements Serializable {
    private static final long serialVersionUID = -6057031392190529061L;
    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("主题，前端展示使用")
    private String title;

    @ApiModelProperty("名称描述")
    private String description;

    @ApiModelProperty("类型，1-字符串，2-图片，3-固定不能修改，4-开关")
    private Integer type;

    @ApiModelProperty("值")
    private String value;

    @ApiModelProperty("图片绝对地址（类型为图片时返回）")
    private String imageUrl;
}