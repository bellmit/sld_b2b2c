package com.slodon.b2b2c.vo.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装地区VO对象
 * @Author wuxy
 */
@Data
public class RegionVO implements Serializable {

    private static final long serialVersionUID = 2888069396815951441L;
    @ApiModelProperty("上级地区编码")
    private String parentCode;

    @ApiModelProperty("地区编码")
    private String regionCode;

    @ApiModelProperty("地区名称")
    private String regionName;

    @ApiModelProperty("地区级别 1-省、自治区、直辖市 2-地级市、地区、自治州、盟 3-市辖区、县级市、县 4-街道")
    private Integer regionLevel;

    @ApiModelProperty("子地区列表")
    private List<RegionVO> children;
}
