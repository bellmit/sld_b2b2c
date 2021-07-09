package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀场次
 */
@Data
public class SeckillStage implements Serializable {

    private static final long serialVersionUID = 8578901329552811332L;
    @ApiModelProperty("场次id")
    private Integer stageId;

    @ApiModelProperty("场次名称")
    private String stageName;

    @ApiModelProperty("秒杀活动id")
    private Integer seckillId;

    @ApiModelProperty("秒杀活动名称")
    private String seckillName;

    @ApiModelProperty("场次开始时间")
    private Date startTime;

    @ApiModelProperty("场次结束时间")
    private Date endTime;

    @ApiModelProperty("添加时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}