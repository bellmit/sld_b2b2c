package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 平台检索属性，系统创建，和商品分类绑定
 */
@Data
public class GoodsAttribute implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("属性id")
    private Integer attributeId;

    @ApiModelProperty("属性名称")
    private String attributeName;

    @ApiModelProperty("创建人ID")
    private Integer createAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty("是否展示：0-不展示，1-展示")
    private Integer isShow;
}