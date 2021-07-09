package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 规格表（系统维护，平台可以看到店铺创建的规格，但不做修改）
 */
@Data
public class GoodsSpec implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("规格id")
    private Integer specId;

    @ApiModelProperty("规格名称")
    private String specName;

    @ApiModelProperty("规格类型1、文字；2、图片")
    private Integer specType;

    @ApiModelProperty("店铺id，0为系统创建")
    private Long storeId;

    @ApiModelProperty("创建人id（如果是系统创建是adminID，如果是商户是vendorID）")
    private Integer createId;

    @ApiModelProperty("创建人名称")
    private String createName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人id（如果是系统更新是adminID，如果是商户是vendorID）")
    private Integer updateId;

    @ApiModelProperty("更新人名称")
    private String updateName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("状态 0-不展示 1-展示，可以删除，但是必须没有其他商品使用")
    private Integer state;

    @ApiModelProperty("排序")
    private Integer sort;
}