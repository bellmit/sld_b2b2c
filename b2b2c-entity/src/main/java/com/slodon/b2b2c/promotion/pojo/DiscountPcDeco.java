package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 折扣活动PC首页装修数据
 */
@Data
public class DiscountPcDeco implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("装修模板数据id")
    private Integer integralDecoId;

    @ApiModelProperty("装修模板id")
    private Integer tplId;

    @ApiModelProperty("模板风格")
    private String tplName;

    @ApiModelProperty("装修模板名称")
    private String tplTypeName;

    @ApiModelProperty("装修模板数据名称(用于管理端展示)")
    private String name;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private Integer createAdminId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人id")
    private Integer updateAdminId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否启用；0==不启用，1==启用")
    private Integer isEnable;

    @ApiModelProperty("实例化装修模板(html片段)")
    private String html;

    @ApiModelProperty("装修模板数据(json)")
    private String json;
}