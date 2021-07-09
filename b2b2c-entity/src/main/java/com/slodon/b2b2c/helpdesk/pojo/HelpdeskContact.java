package com.slodon.b2b2c.helpdesk.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客服-会员关系表
 */
@Data
public class HelpdeskContact implements Serializable {
    private static final long serialVersionUID = 7485882835443403163L;
    @ApiModelProperty("关系ID")
    private Integer contactId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("会话客服ID")
    private Long vendorId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("关系创建时间")
    private Date createTime;

    @ApiModelProperty("最后一次聊天时间")
    private Date updateTime;

    @ApiModelProperty("聊天关系状态：1-活跃，0-不活跃（已被转接）")
    private Integer state;
}