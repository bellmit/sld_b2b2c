package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 系统推送消息表
 */
@Data
public class SystemPush implements Serializable {
    private static final long serialVersionUID = 7205653866059055181L;
    @ApiModelProperty("推送id")
    private Integer pushId;

    @ApiModelProperty("系统推送内容")
    private String content;

    @ApiModelProperty("推送时间")
    private Date sendTime;

    @ApiModelProperty("发送方式：1--站内信，2--短信, 3--邮件")
    private Integer sendWay;

    @ApiModelProperty("是否记录消息查看状态：0--否，1--是")
    private Integer isCheck;

    @ApiModelProperty("接收人类型：1--会员，2--商家")
    private Integer receiveType;

    @ApiModelProperty("推送内容描述")
    private String description;

    @ApiModelProperty("发送消息的管理员id")
    private Integer createId;

    @ApiModelProperty("接收人id串，如: ,1,2,3,5,")
    private String receiveIds;
}