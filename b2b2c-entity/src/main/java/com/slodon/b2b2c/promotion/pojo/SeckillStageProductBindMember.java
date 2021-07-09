package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 秒杀商品与会员绑定关系
 */
@Data
public class SeckillStageProductBindMember implements Serializable {

    private static final long serialVersionUID = 8019554092874884026L;
    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("秒杀商品id")
    private Integer stageProductId;

    @ApiModelProperty("秒杀活动id")
    private Integer seckillId;

    @ApiModelProperty("秒杀场次id")
    private Integer stageId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("用户名")
    private String memberName;

    @ApiModelProperty("创建时间")
    private Date createTime;
}