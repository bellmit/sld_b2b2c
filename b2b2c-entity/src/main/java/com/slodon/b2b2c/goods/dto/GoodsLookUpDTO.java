package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 *  商品下架参数
 * @author cwl
 */
@Data
public class GoodsLookUpDTO implements Serializable {

    private static final long serialVersionUID = 3783467674236321014L;
    @ApiModelProperty(value ="商品ID",required = true)
    private String goodsIds; //多个商品ID用，分割

    @ApiModelProperty(value ="违规下架原因",required = true)
    private String offlineReason;

    @ApiModelProperty("违规下架备注信息")
    private String offlineComment;
}
