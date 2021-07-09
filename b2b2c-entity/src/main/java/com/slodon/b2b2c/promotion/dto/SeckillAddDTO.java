package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 秒杀新增DTO
 */
@Data
public class SeckillAddDTO implements Serializable {

    private static final long serialVersionUID = -116528943724176043L;
    @ApiModelProperty(value = "活动名称", required = true)
    private String seckillName;

    @ApiModelProperty(value = "活动开始时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间", required = true)
    private Date endTime;

    @ApiModelProperty(value = "轮播图")
    private String banner;

    @ApiModelProperty(value = "活动场次,各场次用，隔开")
    private String stages;


}