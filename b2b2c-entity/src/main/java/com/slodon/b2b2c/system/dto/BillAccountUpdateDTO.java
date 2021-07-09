package com.slodon.b2b2c.system.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 结算账号编辑DTO
 * @Author wuxy
 */
@Data
public class BillAccountUpdateDTO implements Serializable {

    private static final long serialVersionUID = -3467419575510316111L;
    @ApiModelProperty(value = "账号id", required = true)
    private Integer accountId;

    @ApiModelProperty("账户类型：1-银行账号；2-支付宝账号")
    private Integer accountType;

    @ApiModelProperty("支付宝姓名")
    private String alipayName;

    @ApiModelProperty("支付宝账号")
    private String alipayAccount;

    @ApiModelProperty("银行开户名")
    private String bankAccountName;

    @ApiModelProperty("公司银行账号")
    private String bankAccountNumber;

    @ApiModelProperty("开户银行支行")
    private String bankBranch;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("区县编码")
    private String districtCode;

    @ApiModelProperty("省市区组合")
    private String addressAll;

    @ApiModelProperty(value = "是否默认账号：1-默认账号，0-非默认账号", required = true)
    private Integer isDefault;
}
