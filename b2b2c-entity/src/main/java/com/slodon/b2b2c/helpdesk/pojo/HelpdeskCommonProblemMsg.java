package com.slodon.b2b2c.helpdesk.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 客服常见问题表
 */
@Data
public class HelpdeskCommonProblemMsg implements Serializable {
    private static final long serialVersionUID = 2134968259162844839L;

    @ApiModelProperty("常见问题消息ID")
    private Integer problemMsgId;

    @ApiModelProperty("商户ID")
    private Long storeId;

    @ApiModelProperty("消息内容")
    private String msgContent;

    @ApiModelProperty("回答内容")
    private String msgReply;

    @ApiModelProperty("是否显示 0-不显示 1-显示")
    private Integer isShow;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建人id")
    private Long createVendorId;

    @ApiModelProperty("添加时间")
    private Date createTime;
}