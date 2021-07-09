package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺关联模版
 */
@Data
public class StoreRelatedTemplate implements Serializable {
    private static final long serialVersionUID = 8292234030958463133L;

    @ApiModelProperty("模板id")
    private Integer templateId;

    @ApiModelProperty("模版名称")
    private String templateName;

    @ApiModelProperty("模版位置(1-顶部，2-底部）")
    private Integer templatePosition;

    @ApiModelProperty("模版内容")
    private String templateContent;

    @ApiModelProperty("店铺ID")
    private Long storeId;
}