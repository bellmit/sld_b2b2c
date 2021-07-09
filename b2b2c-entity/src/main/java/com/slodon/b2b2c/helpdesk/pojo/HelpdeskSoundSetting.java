package com.slodon.b2b2c.helpdesk.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 客服新消息声音设置表
 */
@Data
public class HelpdeskSoundSetting implements Serializable {
    private static final long serialVersionUID = -4741134694222816082L;

    @ApiModelProperty("设置id")
    private Integer settingId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("商户id")
    private Long vendorId;

    @ApiModelProperty("是否开启：0-关闭；1-开启")
    private Integer isOpen;
}