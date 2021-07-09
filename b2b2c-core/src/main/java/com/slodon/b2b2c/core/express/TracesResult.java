package com.slodon.b2b2c.core.express;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 物流信息轨迹对象
 * @Author wuxy
 */
@Data
public class TracesResult implements Serializable {

    private static final long serialVersionUID = -6490747119315815275L;
    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("物流公司名称")
    private String expressName;

    @ApiModelProperty("物流单号")
    private String expressNumber;

    @ApiModelProperty("快递类型")
    private String type;

    @ApiModelProperty("物流信息")
    private List<?> routeList;
}
