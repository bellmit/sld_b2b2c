package com.slodon.b2b2c.msg.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 前后端交互数据dto
 * @Author wuxy
 * @date 2020.11.02 11:26
 */
@Data
public class MemberTplDTO implements Serializable {

    private static final long serialVersionUID = -4113838399322450590L;
    @ApiModelProperty(value = "用户消息模板编号", required = true)
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

    @ApiModelProperty("邮件内容(json格式：{\"email_subject\": \"邮件主题\", \"email_content\": \"邮件内容\"})")
    private String emailContent;

    @ApiModelProperty("站内信内容")
    private String msgContent;

    @ApiModelProperty("微信内容(json格式：{\"templateId\": \"微信模板id\", \"templateContent\": \"微信模板内容\"})")
    private String wxContent;

}
