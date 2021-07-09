package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *  品牌审核参数
 * @author cwl
 */
@Data
public class BrandAuditDTO implements Serializable {

    private static final long serialVersionUID = 3783456674235321564L;
    @ApiModelProperty("品牌ID")
    private String brandIds; //多个品牌ID用，分割

    @ApiModelProperty(value ="审核拒绝或通过,1-通过,0-拒绝",required = true)
    private Integer state; //

    @ApiModelProperty("审核拒绝原因")
    private String auditReason;
}
