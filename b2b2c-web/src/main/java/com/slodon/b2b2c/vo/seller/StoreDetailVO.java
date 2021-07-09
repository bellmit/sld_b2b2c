package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.slodon.b2b2c.vo.seller.OwnStoreDetailVO.getRealBillTypeValue;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装店铺详情VO对象
 */
@Data
public class StoreDetailVO {

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺Logo")
    private String storeLogo;

    @ApiModelProperty("店铺Logo绝对地址")
    private String storeLogoPath;

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
    private Integer openTime;

    @ApiModelProperty("店铺等级名称")
    private String storeGradeName;

    @ApiModelProperty("应付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("支付方式code")
    private String paymentCode;

    @ApiModelProperty("支付方式名称")
    private String paymentName;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("营业执照")
    private String businessLicenseImage;

    @ApiModelProperty("营业执照绝对地址")
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

    @ApiModelProperty("经营类目集合")
    private List<StoreGoodsCateVO> storeGoodsCateVOList;

    @ApiModelProperty("结算周期")
    private String billCycle;

    @ApiModelProperty(value = "结算方式：1-按月结算，2-按周结算")
    private Integer billType;

    @ApiModelProperty(value = "结算方式值：1-按月结算，2-按周结算")
    private String billTypeValue;

    @ApiModelProperty("结算周期")
    private String billDay;

    public StoreDetailVO(Store store, StoreCertificate storeCertificate) {
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        storeLogo = store.getStoreLogo();
        storeLogoPath = FileUrlUtil.getFileUrl(store.getStoreLogo(), null);
        storeGradeId = store.getStoreGradeId();
        storeGradeName = store.getStoreGradeName();
        openTime = store.getOpenTime();
        billType = store.getBillType();
        billTypeValue = getRealBillTypeValue(billType);
        billDay = store.getBillDay();
        contactName = storeCertificate.getContactName();
        contactPhone = CommonUtil.dealMobile(storeCertificate.getContactPhone());
        enterType = storeCertificate.getEnterType();
        enterTypeValue = getRealEnterTypeValue(enterType);
        companyProvinceCode = storeCertificate.getCompanyProvinceCode();
        companyCityCode = storeCertificate.getCompanyCityCode();
        companyAreaCode = storeCertificate.getCompanyAreaCode();
        areaInfo = storeCertificate.getAreaInfo();
        companyAddress = storeCertificate.getCompanyAddress();
        personCardUp = storeCertificate.getPersonCardUp();
        personCardUpPath = FileUrlUtil.getFileUrl(storeCertificate.getPersonCardUp(), null);
        personCardDown = storeCertificate.getPersonCardDown();
        personCardDownPath = FileUrlUtil.getFileUrl(storeCertificate.getPersonCardDown(), null);
        companyName = storeCertificate.getCompanyName();
        businessLicenseImage = storeCertificate.getBusinessLicenseImage();
        businessLicenseImagePath = FileUrlUtil.getFileUrl(storeCertificate.getBusinessLicenseImage(), null);
        moreQualification1 = storeCertificate.getMoreQualification1();
        moreQualification1Path = FileUrlUtil.getFileUrl(storeCertificate.getMoreQualification1(), null);
        moreQualification2 = storeCertificate.getMoreQualification2();
        moreQualification2Path = FileUrlUtil.getFileUrl(storeCertificate.getMoreQualification2(), null);
        moreQualification3 = storeCertificate.getMoreQualification3();
        moreQualification3Path = FileUrlUtil.getFileUrl(storeCertificate.getMoreQualification3(), null);
    }

    public static String getRealEnterTypeValue(Integer enterType) {
        String value = null;
        if (StringUtils.isEmpty(enterType))
            return Language.translate("未知");
        switch (enterType) {
            case StoreConst.APPLY_TYPE_PERSON:
                value = "个人入驻";
                break;
            case StoreConst.APPLY_TYPE_COMPANY:
                value = "企业入驻";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
