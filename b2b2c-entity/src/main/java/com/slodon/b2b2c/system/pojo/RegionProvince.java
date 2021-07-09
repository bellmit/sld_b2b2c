package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 省份信息表
 */
@Data
public class RegionProvince implements Serializable {
    private static final long serialVersionUID = 7580135620840277045L;
    @ApiModelProperty("自增物理主键")
    private Integer id;

    @ApiModelProperty("国际编码")
    private String nativeCode;

    @ApiModelProperty("省名称")
    private String provinceName;

    @ApiModelProperty("国家编码")
    private String countryCode;

    @ApiModelProperty("国家名称")
    private String countryName;

    @ApiModelProperty("备注省份编码")
    private String remarkCode;

    @ApiModelProperty("地区级别 1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县 4-街道")
    private Integer regionLevel;

    @ApiModelProperty("市级列表")
    private List<RegionCity> cityList;
}