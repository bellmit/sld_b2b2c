package com.slodon.b2b2c.business.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 投诉对话表
 */
@Data
public class ComplainTalk implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("投诉对话id")
    private Integer complainTalkId;

    @ApiModelProperty("投诉id编号")
    private Integer complainId;

    @ApiModelProperty("投诉对话用户id")
    private Long talkUserId;

    @ApiModelProperty("投诉对话用户名称")
    private String talkUserName;

    @ApiModelProperty("投诉对话用户类型：1-会员，2-商户，3-平台")
    private Integer talkUserType;

    @ApiModelProperty("投诉对话内容")
    private String talkContent;

    @ApiModelProperty("投诉对话时间")
    private Date talkTime;
}