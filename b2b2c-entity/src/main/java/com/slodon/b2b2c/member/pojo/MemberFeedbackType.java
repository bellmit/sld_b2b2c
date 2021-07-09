package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 反馈类型
 */
@Data
public class MemberFeedbackType implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("类型id")
    private Integer typeId;

    @ApiModelProperty("类型名称")
    private String typeName;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否启用，0：不启用（默认）；1：启用")
    private Integer isUse;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人姓名")
    private String creator;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("修改人姓名")
    private String updater;
}