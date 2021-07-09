package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀标签
 */
@Data
public class SeckillLabel implements Serializable {

    private static final long serialVersionUID = -7351107964639995747L;
    @ApiModelProperty("主键id自增长")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("是否显示 0-不显示 1-显示")
    private Integer isShow;

    @ApiModelProperty("标签排序")
    private Integer sort;

    @ApiModelProperty("添加时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建人ID（系统管理员）")
    private Integer createAdminId;

    @ApiModelProperty("更新人ID（系统管理员）")
    private Integer updateAdminId;
}