package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用
 */
@Data
public class GoodsParameterGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分组id")
    private Integer groupId;

    @ApiModelProperty("分组名称")
    private String groupName;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("创建人ID")
    private Long createVendorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty("是否展示：0-不展示，1-展示")
    private Integer isShow;
}