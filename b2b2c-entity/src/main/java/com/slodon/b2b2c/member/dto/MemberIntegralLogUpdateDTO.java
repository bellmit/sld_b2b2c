package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberIntegralLogUpdateDTO implements Serializable {
    private static final long serialVersionUID = 3135515654851990724L;
    @ApiModelProperty(value = "会员ID", required = true)
    private Integer memberId;

    @ApiModelProperty(value = "积分变化值", required = true)
    private Integer value;

    @ApiModelProperty(value = "1 添加；2 减少；", required = true)
    private Integer type;

    @ApiModelProperty("备注")
    private String description;
}
