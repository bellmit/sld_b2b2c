package com.slodon.b2b2c.helpdesk.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 客服最近联系人DTO
 * @Author wuxy
 */
@Data
public class VendorContactDTO implements Serializable {

    private static final long serialVersionUID = -4656781804086470567L;
    @ApiModelProperty("消息ID")
    private Long msgId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("商户ID")
    private Long vendorId;

    @ApiModelProperty("会员名称")
    private String memberName;
}
