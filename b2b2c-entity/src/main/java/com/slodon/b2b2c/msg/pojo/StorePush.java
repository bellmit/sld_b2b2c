package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 店铺推送消息表
 */
@Data
public class StorePush implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("推送id")
    private Integer pushId;

    @ApiModelProperty("系统推送内容")
    private String content;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("推送时间")
    private Date sendTime;

    @ApiModelProperty("发送方式 1--站内信，2--短信, 3--邮件")
    private Integer sendWay;

    @ApiModelProperty("是否记录查看状态 0--否，1--是")
    private Integer isCheck;

    @ApiModelProperty("推送内容描述")
    private String description;

    @ApiModelProperty("创建人id")
    private Long createId;

    @ApiModelProperty("接收人id串，如: ,1,2,3,5,")
    private String receiveIds;
}