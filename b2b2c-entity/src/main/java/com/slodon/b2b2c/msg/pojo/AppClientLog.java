package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * app客户端记录表，每个客户端有一个唯一的记录，用于app消息推送
 */
@Data
public class AppClientLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("绑定id")
    private Integer logId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户类型，1-会员，2-供应商管理员")
    private Integer userType;

    @ApiModelProperty("客户端身份ID")
    private String clientId;

    @ApiModelProperty("客户端身份别名")
    private String alias;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}