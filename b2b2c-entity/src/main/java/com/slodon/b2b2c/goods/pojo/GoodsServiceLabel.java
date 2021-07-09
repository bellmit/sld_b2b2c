package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品服务标签（比如：7天无理由退货、急速发货）
 */
@Data
public class GoodsServiceLabel implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("标签描述")
    private String description;

    @ApiModelProperty("创建人ID（系统管理员）")
    private Integer createAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人")
    private String updater;
}