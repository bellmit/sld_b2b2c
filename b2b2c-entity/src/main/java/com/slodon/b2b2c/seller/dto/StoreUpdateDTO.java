package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class StoreUpdateDTO implements Serializable {

    private static final long serialVersionUID = -5875212587418246L;

    @ApiModelProperty(value = "店铺id", required = true)
    private Long storeId;

    @ApiModelProperty("联系人名称")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("注册省编码")
    private String companyProvinceCode;

    @ApiModelProperty("注册市编码")
    private String companyCityCode;

    @ApiModelProperty("注册区编码")
    private String companyAreaCode;

    @ApiModelProperty("注册省市区组合")
    private String areaInfo;

    @ApiModelProperty("详细地址")
    private String companyAddress;

    @ApiModelProperty("身份证正面扫描件")
    private String personCardUp;

    @ApiModelProperty("身份证背面扫描件")
    private String personCardDown;

    @ApiModelProperty("店铺等级id")
    private Integer storeGradeId;

    @ApiModelProperty("开店时长")
    private Integer openTime;

    private String paymentName;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("营业执照")
    private String businessLicenseImage;

    @ApiModelProperty("补充认证一")
    private String moreQualification1;

    @ApiModelProperty("补充认证二")
    private String moreQualification2;

    @ApiModelProperty("补充认证三")
    private String moreQualification3;

    @ApiModelProperty(value = "结算方式：1-按月结算，2-按周结算")
    private Integer billType;

    @ApiModelProperty(value = "结算日期字符串，以逗号隔开")
    private String billDays;
}
