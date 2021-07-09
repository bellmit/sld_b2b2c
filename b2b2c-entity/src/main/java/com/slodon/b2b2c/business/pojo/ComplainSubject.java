package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 投诉主题表
 */
@Data
public class ComplainSubject implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("投诉主题id")
    private Integer complainSubjectId;

    @ApiModelProperty("投诉主题名称")
    private String complainSubjectName;

    @ApiModelProperty("投诉主题描述")
    private String complainSubjectDesc;

    @ApiModelProperty("是否展示：0-不展示，1-展示，默认为1")
    private Integer isShow;

    @ApiModelProperty("排序(0-255),越小排序靠前")
    private Integer sort;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建管理员id")
    private Integer createAdminId;
}