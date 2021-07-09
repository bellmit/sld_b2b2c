package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺消息接收设置
 */
@Data
public class StoreSetting implements Serializable {
    private static final long serialVersionUID = -6697630962102187050L;
    @ApiModelProperty("模板编码")
    private String tplCode;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("商户id")
    private Long vendorId;

    @ApiModelProperty("是否接收：0-关闭；1-开启")
    private Integer isReceive;
}