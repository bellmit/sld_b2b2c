package com.slodon.b2b2c.msg.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 商家消息模板--内置表
 */
@Data
public class StoreTpl implements Serializable {
    private static final long serialVersionUID = -5942883514264593256L;
    @ApiModelProperty("模板编码")
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

    @ApiModelProperty("邮件内容")
    private String emailContent;

    @ApiModelProperty("站内信内容")
    private String msgContent;
}