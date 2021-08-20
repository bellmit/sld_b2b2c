package com.slodon.b2b2c.seller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class StoreApplyDTO implements Serializable {

    private static final long serialVersionUID = 2352099088057109478L;

    @ApiModelProperty("资质id,重新编辑时使用")
    private Integer certificateId;

    @ApiModelProperty(value = "入驻类型：0-个人入驻，1-企业入驻",required = true)
    private Integer enterType;

    @ApiModelProperty(value = "公司名称,入驻类型为企业入驻时必传")
    private String companyName;

    @ApiModelProperty(value = "企业注册省编码",required = true)
    private String companyProvinceCode;

    @ApiModelProperty(value = "企业注册市编码",required = true)
    private String companyCityCode;

    @ApiModelProperty(value = "企业注册区编码",required = true)
    private String companyAreaCode;

    @ApiModelProperty(value = "企业注册省市区组合",required = true)
    private String areaInfo;

    @ApiModelProperty(value = "公司详细地址",required = true)
    private String companyAddress;

    @ApiModelProperty(value = "营业执照扫描件,企业入驻时必传")
    private String businessLicenseImage;

    @ApiModelProperty(value = "身份证正面扫描件",required = true)
    private String personCardUp;

    @ApiModelProperty(value = "身份证背面扫描件",required = true)
    private String personCardDown;

    @ApiModelProperty(value = "联系人电话",required = true)
    private String contactPhone;

    @ApiModelProperty(value = "联系人姓名",required = true)
    private String contactName;

    @ApiModelProperty("补充认证一")
    private String moreQualification1;

    @ApiModelProperty("补充认证二")
    private String moreQualification2;

    @ApiModelProperty("补充认证三")
    private String moreQualification3;

    @ApiModelProperty("申请id,重新编辑时使用")
    private Integer applyId;

    @ApiModelProperty(value = "店铺名称",required = true)
    private String storeName;

    @ApiModelProperty(value = "开店时长",required = true)
    private Integer applyYear;

    @ApiModelProperty(value = "店铺等级",required = true)
    private Integer storeGradeId;

    @ApiModelProperty(value = "申请分类id字符串,例1级-2级-3级,1级-2级-3级",required = true)
    private String goodsCategoryIds;

    @ApiModelProperty(value = "邮政编码", required = true)
    private String postCode;

}
