package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 乡镇(街道)信息表
 */
@Data
public class RegionTown implements Serializable {
    private static final long serialVersionUID = 1863313132641424405L;
    @ApiModelProperty("自增物理主键")
    private Integer id;

    @ApiModelProperty("国际编码")
    private String nativeCode;

    @ApiModelProperty("区县名称")
    private String townName;

    @ApiModelProperty("街道名称")
    private String shortName;

    @ApiModelProperty("邮政编码")
    private String postName;

    @ApiModelProperty("区县名称")
    private String districtName;

    @ApiModelProperty("区县编码")
    private String districtCode;

    @ApiModelProperty("城市名称")
    private String cityName;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("国家编码")
    private String countryCode;

    @ApiModelProperty("国家名称")
    private String countryName;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("省份名称")
    private String provinceName;

    @ApiModelProperty("备注省份编码")
    private String remarkCode;

    @ApiModelProperty("地区级别 1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县 4-街道")
    private Integer regionLevel;
}