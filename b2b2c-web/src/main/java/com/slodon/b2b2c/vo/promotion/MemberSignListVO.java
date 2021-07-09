package com.slodon.b2b2c.vo.promotion;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装用户签到统计列表VO对象
 * @Author wuxy
 * @date 2020.11.09 12:06
 */
@Data
public class MemberSignListVO implements Serializable {

    private static final long serialVersionUID = 3862956358740374868L;
    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("首次签到时间")
    private Date firstSignTime;

    @ApiModelProperty("最后签到时间")
    private Date lastSignTime;

    @ApiModelProperty("签到总次数")
    private Integer signTotal;
}
