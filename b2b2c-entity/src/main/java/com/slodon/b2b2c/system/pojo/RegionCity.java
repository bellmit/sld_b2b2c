package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 市级信息表
 */
@Data
public class RegionCity implements Serializable {
    private static final long serialVersionUID = 9210486103614772506L;
    @ApiModelProperty("自增物理主键")
    private Integer id;

    @ApiModelProperty("国际编码")
    private String nativeCode;

    @ApiModelProperty("城市名称")
    private String cityName;

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

    @ApiModelProperty("区级列表")
    private List<RegionDistrict> districtList;
}