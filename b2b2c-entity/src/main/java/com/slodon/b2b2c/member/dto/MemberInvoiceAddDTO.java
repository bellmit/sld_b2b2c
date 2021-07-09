package com.slodon.b2b2c.member.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 会员发票信息表
 */
@Data
public class MemberInvoiceAddDTO implements Serializable{

    private static final long serialVersionUID = -6382177987698759506L;
    @ApiModelProperty("是否默认发票：0-非默认发票，1-默认发票")
    private Integer isDefault;

    @ApiModelProperty("抬头类型：1-个人；2-公司")
    private Integer titleType;

    @ApiModelProperty("发票类型：1-普通发票；2-增值税发票")
    private Integer invoiceType;

    @ApiModelProperty("发票内容：1-商品明细；2-商品类别")
    private Integer invoiceContent;

    @ApiModelProperty("发票抬头(通用信息）")
    private String invoiceTitle;

    @ApiModelProperty("纳税人识别号(通用信息）")
    private String taxCode;

    @ApiModelProperty("注册地址(专用发票）")
    private String registerAddr;

    @ApiModelProperty("注册电话(专用发票）")
    private String registerPhone;

    @ApiModelProperty("开户银行(专用发票）")
    private String bankName;

    @ApiModelProperty("银行帐户(专用发票）")
    private String bankAccount;

    @ApiModelProperty("收票人姓名(通用信息）")
    private String receiverName;

    @ApiModelProperty("收票人手机号(通用信息）")
    private String receiverMobile;

    @ApiModelProperty("收票邮箱(通用信息）")
    private String receiverEmail;

    @ApiModelProperty("收票人详细地址(通用信息）")
    private String receiverAddress;
}