package com.slodon.b2b2c.helpdesk.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客服快捷回复消息表
 */
@Data
public class HelpdeskQuickMsg implements Serializable {
    private static final long serialVersionUID = -6001308610407868189L;

    @ApiModelProperty("快捷回复消息ID")
    private Integer quickMsgId;

    @ApiModelProperty("商户ID")
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