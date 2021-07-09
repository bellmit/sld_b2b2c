package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户反馈
 */
@Data
public class MemberFeedback implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("反馈id")
    private Integer feedbackId;

    @ApiModelProperty("反馈类型id")
    private Integer typeId;

    @ApiModelProperty("反馈内容")
    private String content;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("用户id")
    private Integer memberId;

    @ApiModelProperty("状态：0为未处理、1为已处理")
    private Integer state;

    @ApiModelProperty("反馈时间")
    private Date createTime;

    @ApiModelProperty("处理人")
    private String handler;

    @ApiModelProperty("处理时间")
    private Date handleTime;
}