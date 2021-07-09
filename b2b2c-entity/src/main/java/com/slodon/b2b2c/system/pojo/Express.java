package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 快递公司
 */
@Data
public class Express implements Serializable {
    private static final long serialVersionUID = -7249642432658460059L;
    @ApiModelProperty("物流ID")
    private Integer expressId;

    @ApiModelProperty("物流名称")
    private String expressName;

    @ApiModelProperty("物流状态，平台是否启用：1-启用，0-不启用")
    private Integer expressState;

    @ApiModelProperty("物流编号")
    private String expressCode;

    @ApiModelProperty("首字母")
    private String expressLetter;

    @ApiModelProperty("1-512；数值小排序靠前")
    private Integer sort;

    @ApiModelProperty("公司网址")
    private String website;
}