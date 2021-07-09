package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商户编辑商品参数
 * @author cwl
 */
@Data
public class GoodsUpdateDTO extends  GoodsAddDTO {

    private static final long serialVersionUID = 3787874474286521914L;

    @ApiModelProperty(value ="商品ID",required = true)
    private Long goodsId;

}
