package com.slodon.b2b2c.helpdesk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 会员最近联系人DTO
 * @Author wuxy
 */
@Data
public class MemberContactDTO implements Serializable {

    private static final long serialVersionUID = -5174759728154428268L;
    @ApiModelProperty("消息ID")
    private Long msgId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("店铺ID")
    private Long storeId;
}
