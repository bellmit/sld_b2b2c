package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 折扣活动mobile装修数据
 */
@Data
public class DiscountMbDeco implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("装修页id")
    private Integer id;

    @ApiModelProperty("装修页名称")
    private String name;

    @ApiModelProperty("装修页类型(首页home/专题topic/店铺seller/活动activity)")
    private String type;

    @ApiModelProperty("店铺id,0==平台")
    private Long storeId;

    @ApiModelProperty("是否启用")
    private Integer android;

    @ApiModelProperty("是否启用")
    private Integer ios;

    @ApiModelProperty("是否启用")
    private Integer h5;

    @ApiModelProperty("是否启用")
    private Integer weixinXcx;

    @ApiModelProperty("是否启用")
    private Integer alipayXcx;

    @ApiModelProperty("创建人")
    private Integer createUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人")
    private Integer updateUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("装修页数据")
    private String data;
}