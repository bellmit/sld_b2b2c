package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsRelatedTemplateUpdateDTO implements Serializable {
    private static final long serialVersionUID = 5974906673014525352L;

    @ApiModelProperty("模版id")
    private Integer templateId;

    @ApiModelProperty("模版名称")
    private String templateName;

    @ApiModelProperty("模版位置(1-顶部，2-底部）")
    private Integer templatePosition;

    @ApiModelProperty("模版内容")
    private String templateContent;
}
