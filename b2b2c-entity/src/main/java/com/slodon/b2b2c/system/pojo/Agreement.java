package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 协议表
 */
@Data
public class Agreement implements Serializable {
    private static final long serialVersionUID = -1950223521932618347L;
    @ApiModelProperty("协议编码")
    private String agreementCode;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("最后修改人id")
    private Integer updateAdminId;

    @ApiModelProperty("最后修改人名称")
    private String updateAdminName;

    @ApiModelProperty("内容")
    private String content;
}