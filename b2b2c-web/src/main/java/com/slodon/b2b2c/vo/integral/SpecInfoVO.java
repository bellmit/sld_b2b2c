package com.slodon.b2b2c.vo.integral;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 规格信息转化VO对象
 * @Author wuxy
 */
@Data
public class SpecInfoVO implements Serializable {

    @ApiModelProperty(value = "规格id")
    private Integer specId;
    @ApiModelProperty(value = "规格名称")
    private String specName;
    @ApiModelProperty(value = "是否主规格,1-是，0-不是")
    private Integer isMainSpec;
    @ApiModelProperty(value = "规格值列表")
    private List<SpecValueInfo> specValueList;

    /**
     * 规格值信息
     */
    @Data
    public static class SpecValueInfo implements Serializable {

        private static final long serialVersionUID = -5280176152206755973L;
        @ApiModelProperty(value = "规格值id")
        private Integer specValueId;

        @ApiModelProperty(value = "规格值")
        private String specValue;
    }
}
