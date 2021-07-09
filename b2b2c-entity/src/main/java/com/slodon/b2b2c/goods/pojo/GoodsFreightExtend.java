package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 运费模板扩展表-区域信息
 */
@Data
public class GoodsFreightExtend implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("运费模板扩展ID")
    private Integer freightExtendId;

    @ApiModelProperty("运费模板ID")
    private Integer freightTemplateId;

    @ApiModelProperty("首件数量(按件，重量，体积）")
    private Integer baseNumber;

    @ApiModelProperty("首件运费")
    private BigDecimal basePrice;

    @ApiModelProperty("续件数量(按件，重量，体积）")
    private Integer addNumber;

    @ApiModelProperty("续件运费")
    private BigDecimal addPrice;

    @ApiModelProperty("省级和市级地区code组成的json串")
    private String provinceInfo;

    @ApiModelProperty("市级地区code组成的串，以，隔开")
    private String cityCode;

    @ApiModelProperty("市级地区name组成的串，以，隔开")
    private String cityName;
}