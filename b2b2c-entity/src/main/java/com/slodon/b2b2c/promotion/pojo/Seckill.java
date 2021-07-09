package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀活动表
 */
@Data
public class Seckill implements Serializable {

    private static final long serialVersionUID = -3655496289580358509L;
    @ApiModelProperty("主键id")
    private Integer seckillId;

    @ApiModelProperty("活动名称")
    private String seckillName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("添加时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("轮播图")
    private String banner;

    @ApiModelProperty("创建人ID（系统管理员）")
    private Integer createAdminId;

    @ApiModelProperty("更新人ID（系统管理员）")
    private Integer updateAdminId;
}