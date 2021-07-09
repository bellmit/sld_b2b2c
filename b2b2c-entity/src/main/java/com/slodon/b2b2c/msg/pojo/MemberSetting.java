package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户消息接收设置表
 */
@Data
public class MemberSetting implements Serializable {
    private static final long serialVersionUID = 6593318322798810386L;
    @ApiModelProperty("消息模板编号")
    private String tplCode;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("是否接收 1是，0否")
    private Integer isReceive;
}