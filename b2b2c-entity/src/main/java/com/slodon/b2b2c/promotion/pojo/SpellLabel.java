package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 拼团活动标签表
 */
@Data
public class SpellLabel implements Serializable {
    private static final long serialVersionUID = 2680996672828117415L;
    @ApiModelProperty("拼团活动标签id")
    private Integer spellLabelId;

    @ApiModelProperty("拼团活动标签名称")
    private String spellLabelName;

    @ApiModelProperty("是否展示：0为不展示，1为展示，默认为1")
    private Integer isShow;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建管理员id")
    private Integer createAdminId;
}