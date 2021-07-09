package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阶梯团标签表
 */
@Data
public class LadderGroupLabel implements Serializable {
    private static final long serialVersionUID = -5017734254549030207L;

    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否显示：0、不显示；1、显示")
    private Integer isShow;

    @ApiModelProperty("创建管理员ID")
    private Integer createAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}