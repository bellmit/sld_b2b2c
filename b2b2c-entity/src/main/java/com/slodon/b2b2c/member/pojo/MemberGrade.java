package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员等级配置表
 */
@Data
public class MemberGrade implements Serializable {
    private static final long serialVersionUID = -4562258992864812272L;

    @ApiModelProperty("等级ID")
    private Integer gradeId;

    @ApiModelProperty("等级名称")
    private String gradeName;

    @ApiModelProperty("等级图标")
    private String gradeImg;

    @ApiModelProperty("经验值")
    private Integer experienceValue;

    @ApiModelProperty("是否内置数据：0-否；1-是，内置数据不可修改、删除")
    private Integer isInner;

    @ApiModelProperty("创建人id")
    private Integer createAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人id")
    private Integer updateAdminId;

    @ApiModelProperty("修改时间")
    private Date updateTime;
}