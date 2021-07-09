package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装入驻详情VO对象
 */
@Data
public class StoreApplyDetailVO {

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("联系人名称")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("入驻类型：0-个人入驻，1-企业入驻")
    private Integer enterType;

    @ApiModelProperty("入驻类型值：0-个人入驻，1-企业入驻")
    private String enterTypeValue;

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

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("营业执照扫描件")
    private String businessLicenseImage;

    @ApiModelProperty("营业执照扫描件绝对地址")
    private String businessLicenseImagePath;

    @ApiModelProperty("补充认证一")
    private String moreQualification1;

    @ApiModelProperty("补充认证一绝对地址")
    private String moreQualification1Path;

    @ApiModelProperty("补充认证二")
    private String moreQualification2;

    @ApiModelProperty("补充认证二绝对地址")
    private String moreQualification2Path;

    @ApiModelProperty("补充认证三")
    private String moreQualification3;

    @ApiModelProperty("补充认证三绝对地址")
    private String moreQualification3Path;

    @ApiModelProperty("身份证正面扫描件")
    private String personCardUp;

    @ApiModelProperty("身份证正面扫描件绝对地址")
    private String personCardUpPath;

    @ApiModelProperty("身份证背面扫描件")
    private String personCardDown;

    @ApiModelProperty("身份证背面扫描件绝对地址")
    private String personCardDownPath;

    @ApiModelProperty("店铺等级id")
    private Integer storeGradeId;

    @ApiModelProperty("开店时长")
    private Integer applyYear;

    @ApiModelProperty("状态：1、店铺信息提交申请；2、店铺信息审核通过；3、店铺信息审核失败；4、开通店铺(支付完成)")
    private Integer state;

    @ApiModelProperty("状态值：1、审核已经提交，请等待管理员审核；2、您的审核已通过，请选择支付方式并缴纳费用；3、审核失败；4、入驻成功")
    private String stateValue;

    @ApiModelProperty("店铺等级名称")
    private String storeGradeName;

    @ApiModelProperty("拒绝理由")
    private String refuseReason;

    @ApiModelProperty("备注")
    private String auditInfo;

    @ApiModelProperty("经营类目集合")
    private List<StoreGoodsCateVO> storeGoodsCateVOList;

    public StoreApplyDetailVO(StoreApply storeApply, StoreCertificate storeCertificate) {
        storeName = storeApply.getStoreName();
        storeGradeId = storeApply.getStoreGradeId();
        refuseReason = storeApply.getRefuseReason();
        auditInfo = storeApply.getAuditInfo();
        applyYear = storeApply.getApplyYear();
        state = storeApply.getState();
        stateValue = getRealStateValue(state);
        contactName = storeCertificate.getContactName();
        contactPhone = CommonUtil.dealMobile(storeCertificate.getContactPhone());
        enterType = storeCertificate.getEnterType();
        enterTypeValue = getRealEnterTypeValue(enterType);
        companyProvinceCode = storeCertificate.getCompanyProvinceCode();
        companyCityCode = storeCertificate.getCompanyCityCode();
        companyAreaCode = storeCertificate.getCompanyAreaCode();
        areaInfo = storeCertificate.getAreaInfo();
        companyAddress = storeCertificate.getCompanyAddress();
        companyName = storeCertificate.getCompanyName();
        businessLicenseImage = storeCertificate.getBusinessLicenseImage();
        businessLicenseImagePath = FileUrlUtil.getFileUrl(businessLicenseImage, null);
        moreQualification1 = storeCertificate.getMoreQualification1();
        moreQualification1Path = FileUrlUtil.getFileUrl(moreQualification1, null);
        moreQualification2 = storeCertificate.getMoreQualification2();
        moreQualification2Path = FileUrlUtil.getFileUrl(moreQualification2, null);
        moreQualification3 = storeCertificate.getMoreQualification3();
        moreQualification3Path = FileUrlUtil.getFileUrl(moreQualification3, null);
        personCardUp = storeCertificate.getPersonCardUp();
        personCardUpPath = FileUrlUtil.getFileUrl(storeCertificate.getPersonCardUp(), null);
        personCardDown = storeCertificate.getPersonCardDown();
        personCardDownPath = FileUrlUtil.getFileUrl(storeCertificate.getPersonCardDown(), null);
    }

    public static String getRealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state))
            return Language.translate("未知");
        switch (state) {
            case StoreConst.STATE_1_SEND_APPLY:
                value = "审核已经提交，请等待管理员审核";
                break;
            case StoreConst.STATE_2_DONE_APPLY:
                value = "您的审核已通过，请选择支付方式并缴纳费用";
                break;
            case StoreConst.STATE_3_FAIL_APPLY:
                value = "审核失败";
                break;
            case StoreConst.STATE_4_STORE_OPEN:
                value = "入驻成功";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static String getRealEnterTypeValue(Integer enterType) {
        String value = null;
        if (StringUtils.isEmpty(enterType)) return Language.translate("未知");
        switch (enterType) {
            case StoreConst.APPLY_TYPE_PERSON:
                value = "个人入驻";
                break;
            case StoreConst.APPLY_TYPE_COMPANY:
                value = "企业入驻";
                break;
        }
        return value;
    }
}
