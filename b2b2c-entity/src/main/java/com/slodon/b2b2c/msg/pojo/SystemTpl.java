package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 系统消息模板
 */
@Data
public class SystemTpl implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("模板编码")
    private String tplCode;

    @ApiModelProperty("模板名称")
    private String tplName;

    @ApiModelProperty("模板类型")
    private String tplTypeCode;

    @ApiModelProperty("模板数据")
    private String tplContent;
}