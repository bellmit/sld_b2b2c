package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 结算账号表
 */
@Data
public class BillAccount implements Serializable {

    private static final long serialVersionUID = 6002546372977804064L;

    @ApiModelProperty("账号id")
    private Integer accountId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

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

    @ApiModelProperty("是否默认账号：1-默认账号，0-非默认账号")
    private Integer isDefault;

    @ApiModelProperty("创建人id")
    private Long createVendorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("邮政编码")
    private String postcode;

}