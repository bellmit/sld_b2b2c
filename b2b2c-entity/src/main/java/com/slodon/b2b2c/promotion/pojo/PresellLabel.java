package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 预售活动标签表
 */
@Data
public class PresellLabel implements Serializable {
    private static final long serialVersionUID = 1078459158387878373L;
    @ApiModelProperty("预售活动标签id")
    private Integer presellLabelId;

    @ApiModelProperty("预售活动名称")
    private String presellLabelName;

    @ApiModelProperty("是否展示，0为不展示，1为展示，默认为1")
    private Integer isShow;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建用户id")
    private Integer createUserId;
}