package com.slodon.b2b2c.vo.promotion;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装签到活动统计VO对象
 * @Author wuxy
 * @date 2020.11.09 10:37
 */
@Data
public class SignActivityStatisticsVO implements Serializable {

    private static final long serialVersionUID = -6603711915094415008L;
    @ApiModelProperty("签到活动id")
    private Integer signActivityId;

    @ApiModelProperty("活动开始时间")
    private String startTime;

    @ApiModelProperty("活动结束时间")
    private String endTime;

    @ApiModelProperty("签到用户数")
    private Integer memberNum;

    @ApiModelProperty("总签到次数")
    private Integer totalSign;

    @ApiModelProperty("新签到用户数")
    private Integer newMemberNum;

    @ApiModelProperty("新签到用户占比，格式为00.00%")
    private String newMemberRate;
}
