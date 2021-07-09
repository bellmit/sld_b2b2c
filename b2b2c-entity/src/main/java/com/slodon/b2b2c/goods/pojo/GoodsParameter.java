package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺自定义商品参数表，仅作详情顶部的展示使用
 */
@Data
public class GoodsParameter implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("属性id")
    private Integer parameterId;

    @ApiModelProperty("分组id")
    private Integer groupId;

    @ApiModelProperty("属性名称")
    private String parameterName;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("创建人ID")
    private Long createVendorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty("是否展示：0-不展示，1-展示")
    private Integer isShow;

    @ApiModelProperty("属性值，用逗号隔开")
    private String parameterValue;
}