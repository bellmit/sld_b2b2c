package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 搜索历史记录表
 */
@Data
public class SearchLog implements Serializable {
    private static final long serialVersionUID = -783843885505665745L;
    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("搜索词")
    private String keyword;

    @ApiModelProperty("IP地址")
    private String ip;

    @ApiModelProperty("用户不登录存0")
    private Integer memberId;

    @ApiModelProperty("创建时间")
    private Date createTime;
}