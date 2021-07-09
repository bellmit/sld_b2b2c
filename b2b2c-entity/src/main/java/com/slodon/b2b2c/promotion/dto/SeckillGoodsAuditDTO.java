package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *  秒杀商品审核参数
 */
@Data
public class SeckillGoodsAuditDTO implements Serializable {

    private static final long serialVersionUID = -4527242755762466083L;
    @ApiModelProperty(value = "商品ID",required = true)
    private String goodsIds; //多个商品ID用，分割

    @ApiModelProperty(value = "场次ID",required = true)
    private String stageIds; //多个场次ID用，分割

    @ApiModelProperty(value ="审核拒绝或通过,0-拒绝,1-通过",required = true)
    private Integer state; //

    @ApiModelProperty("审核拒绝原因,当审核拒绝时必填")
    private String auditReason;
}
