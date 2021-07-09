package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * PC商城装修模板实例化数据表
 */
@Data
public class TplPcMallData implements Serializable {
    private static final long serialVersionUID = -7088606354703643242L;
    @ApiModelProperty("装修模板数据id")
    private Integer dataId;

    @ApiModelProperty("装修模板id")
    private Integer tplPcId;

    @ApiModelProperty("模板风格")
    private String tplPcName;

    @ApiModelProperty("模板类型")
    private String tplPcType;

    @ApiModelProperty("装修模板名称")
    private String tplPcTypeName;

    @ApiModelProperty("装修模板数据名称(用于管理端展示)")
    private String name;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private Long createUserId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人id")
    private Long updateUserId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否启用；0==不启用，1==启用")
    private Integer isEnable;

    @ApiModelProperty("店铺id(0为平台)")
    private Long storeId;

    @ApiModelProperty("实例化装修模板(html片段)")
    private String html;

    @ApiModelProperty("装修模板数据(json)")
    private String json;
}