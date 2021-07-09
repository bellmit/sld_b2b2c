package com.slodon.b2b2c.cms.pojo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 资讯分类表
 */
@Data
public class InformationCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("分类id")
    private Integer cateId;

    @ApiModelProperty("分类名称")
    private String cateName;

    @ApiModelProperty("排序，值越小越靠前")
    private Integer sort;

    @ApiModelProperty("是否显示，0-不显示，1-显示，默认0")
    private Integer isShow;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private Integer createAdminId;

    @ApiModelProperty("创建人名称")
    private String createAdminName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人id")
    private Integer updateAdminId;

    @ApiModelProperty("更新人名称")
    private String updateAdminName;
}