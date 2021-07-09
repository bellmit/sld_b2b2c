package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 推手邀请新人注册dto
 */
@Data
public class SpreaderMemberRegisterDTO implements Serializable {
    private static final long serialVersionUID = 7438445220633971031L;

    @ApiModelProperty(value = "推手会员id（邀请人）",required = true)
    private Integer spreaderMemberId;

    @ApiModelProperty(value = "会员id（被邀请人）",required = true)
    private Integer memberId;
}
