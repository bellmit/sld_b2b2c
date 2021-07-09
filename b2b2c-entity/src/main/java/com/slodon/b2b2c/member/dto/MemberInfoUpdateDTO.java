package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberInfoUpdateDTO implements Serializable {

    private static final long serialVersionUID = 3638067910294757118L;
    @ApiModelProperty("用户头像")
    private String memberAvatar;

    @ApiModelProperty("会员昵称")
    private String memberNickName;

    @ApiModelProperty("真实姓名")
    private String memberTrueName;

    @ApiModelProperty("性别：0、保密；1、男；2、女")
    private Integer gender;

    @ApiModelProperty("生日")
    private Date memberBirthday;

}
