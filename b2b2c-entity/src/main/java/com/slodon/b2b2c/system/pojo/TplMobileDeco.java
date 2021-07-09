package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * mobile装修页表（用户装修实例）
 */
@Data
public class TplMobileDeco implements Serializable {
    private static final long serialVersionUID = 7749949699892547502L;
    @ApiModelProperty("装修页id")
    private Integer decoId;

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

    @ApiModelProperty("创建人id")
    private Long createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人id")
    private Long updateUserId;

    @ApiModelProperty("更新人名称")
    private String updateUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("装修页数据")
    private String data;

    @ApiModelProperty("开屏图数据")
    private String showTip;
}