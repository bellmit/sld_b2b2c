package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 店铺资质信息表
 */
@Data
public class StoreCertificate implements Serializable {

    private static final long serialVersionUID = 4007324042835782893L;

    @ApiModelProperty("资质id")
    private Integer certificateId;

    @ApiModelProperty("商户用户id")
    private Long vendorId;

    @ApiModelProperty("商户用户名")
    private String vendorName;

    @ApiModelProperty("入驻类型：0-个人入驻，1-企业入驻")
    private Integer enterType;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("企业注册省编码")
    private String companyProvinceCode;

    @ApiModelProperty("企业注册市编码")
    private String companyCityCode;

    @ApiModelProperty("企业注册区编码")
    private String companyAreaCode;

    @ApiModelProperty("省市区组合")
    private String areaInfo;

    @ApiModelProperty("公司详细地址")
    private String companyAddress;

    @ApiModelProperty("营业执照扫描件")
    private String businessLicenseImage;

    @ApiModelProperty("身份证正面扫描件")
    private String personCardUp;

    @ApiModelProperty("身份证背面扫描件")
    private String personCardDown;

    @ApiModelProperty("联系人电话")
    private String contactPhone;

    @ApiModelProperty("联系人姓名")
    private String contactName;

    @ApiModelProperty("补充认证一")
    private String moreQualification1;

    @ApiModelProperty("补充认证二")
    private String moreQualification2;

    @ApiModelProperty("补充认证三")
    private String moreQualification3;

    @ApiModelProperty("邮政编码")
    private String postcode;

}