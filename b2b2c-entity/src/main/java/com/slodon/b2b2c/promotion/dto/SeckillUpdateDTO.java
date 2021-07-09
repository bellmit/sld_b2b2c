package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 秒杀编辑DTO

 */
@Data
public class SeckillUpdateDTO implements Serializable {

    private static final long serialVersionUID = 4313235215005181001L;
    @ApiModelProperty(value = "秒杀活动id", required = true)
    private Integer seckillId;

    @ApiModelProperty(value = "活动名称")
    private String seckillName;

    @ApiModelProperty(value = "活动开始时间")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    private Date endTime;

    @ApiModelProperty(value = "轮播图")
    private String banner;
}