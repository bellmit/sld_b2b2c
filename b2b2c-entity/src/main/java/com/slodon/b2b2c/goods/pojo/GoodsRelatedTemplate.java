package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品的店铺关联模版
 */
@Data
public class GoodsRelatedTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("模板id")
    private Integer templateId;

    @ApiModelProperty("模版名称")
    private String templateName;

    @ApiModelProperty("模版位置(1-顶部，2-底部）")
    private Integer templatePosition;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("创建人ID（店铺管理员）")
    private Long createVendorId;

    @ApiModelProperty("模版内容")
    private String templateContent;
}