package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 等级变化日志表
 */
@Data
public class MemberGradeLog implements Serializable {
    private static final long serialVersionUID = -6826709113965148602L;

    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("变化类型：1-升级，2-降级")
    private String changeType;

    @ApiModelProperty("级别变化之前的经验值")
    private Integer beforeExper;

    @ApiModelProperty("级别变化之后的经验值")
    private Integer afterExper;

    @ApiModelProperty("级别变化之前的等级")
    private Integer beforeGrade;

    @ApiModelProperty("级别变化之后的等级")
    private Integer afterGrade;

    @ApiModelProperty("创建时间")
    private Date createTime;
}