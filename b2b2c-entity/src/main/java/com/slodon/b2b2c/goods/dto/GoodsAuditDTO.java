package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *  商品审核参数
 * @author cwl
 */
@Data
public class GoodsAuditDTO implements Serializable {

    private static final long serialVersionUID = 3783467674236321014L;
    @ApiModelProperty(value = "商品ID",required = true)
    private String goodsIds; //多个商品ID用，分割

    @ApiModelProperty(value ="审核拒绝或通过,0-拒绝,1-通过",required = true)
    private String state; //

    @ApiModelProperty("审核拒绝原因,当审核拒绝时必填")
    private String auditReason;

    @ApiModelProperty("审核拒绝备注信息")
    private String auditComment;
}
