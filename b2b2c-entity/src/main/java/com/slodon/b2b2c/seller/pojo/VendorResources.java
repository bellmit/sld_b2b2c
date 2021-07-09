package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 商家资源表
 */
@Data
public class VendorResources implements Serializable {
    private static final long serialVersionUID = 1232431114510169687L;

    @ApiModelProperty("资源id")
    private Integer resourcesId;

    @ApiModelProperty("父级id")
    private Integer pid;

    @ApiModelProperty("资源名称")
    private String content;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("资源状态：1-未删除 ;2-删除")
    private Integer state;

    @ApiModelProperty("资源等级：1-一级菜单，2-二级菜单，3-三级菜单，4-按钮")
    private Integer grade;

    @ApiModelProperty("对应路由")
    private String url;

    @ApiModelProperty("前端对应路由")
    private String frontPath;

    @ApiModelProperty("子资源")
    private List<VendorResources> children;
}