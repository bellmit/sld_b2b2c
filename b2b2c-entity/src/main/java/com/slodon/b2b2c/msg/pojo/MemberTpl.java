package com.slodon.b2b2c.msg.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.xiaoymin.knife4j.annotations.Ignore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户消息模板
 */
@Data
public class MemberTpl implements Serializable {
    private static final long serialVersionUID = -6255630818622662750L;
    @ApiModelProperty("用户消息模板编号")
    private String tplCode;

    @ApiModelProperty("模板名称")
    private String tplName;

    @ApiModelProperty("模板类型")
    private String tplTypeCode;

    @ApiModelProperty("短信开关：0-关闭；1-开启")
    private Integer smsSwitch;

    @ApiModelProperty("短信内容")
    private String smsContent;

    @ApiModelProperty("邮件开关：0-关闭；1-开启")
    private Integer emailSwitch;

    @ApiModelProperty("站内信开关：0-关闭；1-开启")
    private Integer msgSwitch;

    @ApiModelProperty("微信开关：0-关闭；1-开启")
    private Integer wxSwitch;

    @ApiModelProperty("邮件内容")
    private String emailContent;

    @ApiModelProperty("站内信内容")
    private String msgContent;

    @ApiModelProperty("微信内容")
    private String wxContent;

    @JsonIgnore
    private Integer isReceive;      // 是否接收 1是，0否
}