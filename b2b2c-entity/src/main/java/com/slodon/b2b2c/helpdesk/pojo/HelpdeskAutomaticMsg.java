package com.slodon.b2b2c.helpdesk.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客服自动回复消息表
 */
@Data
public class HelpdeskAutomaticMsg implements Serializable {
    private static final long serialVersionUID = 3928407077373329302L;

    @ApiModelProperty("自动回复消息ID")
    private Integer autoMsgId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("消息内容")
    private String msgContent;

    @ApiModelProperty("是否显示 0-不显示 1-显示")
    private Integer isShow;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建人id")
    private Long createVendorId;

    @ApiModelProperty("添加时间")
    private Date createTime;
}