package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 搜索敏感词过滤
 */
@Data
public class SearchFilter implements Serializable {
    private static final long serialVersionUID = -4198225890961385290L;
    @ApiModelProperty("过滤id")
    private Integer filterId;

    @ApiModelProperty("关键字")
    private String keyword;

    @ApiModelProperty("添加时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private Integer createId;

    @ApiModelProperty("创建时间")
    private String createName;

    @ApiModelProperty("修改时间")
    private Date updateTime;
}