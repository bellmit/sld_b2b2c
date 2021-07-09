package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 快递鸟平台支持快递面单的快递公司
 */
@Data
public class ExpressElectronic implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Integer electronicId;

    @ApiModelProperty("物流id")
    private Integer expressId;

    @ApiModelProperty("物流名称")
    private String expressName;

    @ApiModelProperty("物流编号")
    private String expressCode;

    @ApiModelProperty("状态是否启用：1-启用，0-不启用")
    private String expressState;

    @ApiModelProperty("公司网址")
    private String expressWebsite;

    @ApiModelProperty("是否需要平台商户申请客户号：1-需要，0-不需要")
    private Integer isApply;

    @ApiModelProperty("客户帐号")
    private String customerName;

    @ApiModelProperty("客户密码")
    private String customerPwd;

    @ApiModelProperty("月结号")
    private String mouthCode;

    @ApiModelProperty("所属网点")
    private String sendSite;

    @ApiModelProperty("所属人员")
    private String sendStaff;

    @ApiModelProperty("物流图片")
    private String expressImg;

    @ApiModelProperty("电子面单模板大小,参考快递鸟模板样式表(非必填)")
    private String templateSize;
}