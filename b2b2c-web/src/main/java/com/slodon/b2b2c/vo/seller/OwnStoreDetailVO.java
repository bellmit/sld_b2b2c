package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装店铺详情VO对象
 */
@Data
public class OwnStoreDetailVO {

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("联系人名称")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("注册省编码")
    private String provinceCode;

    @ApiModelProperty("注册市编码")
    private String cityCode;

    @ApiModelProperty("注册区编码")
    private String areaCode;

    @ApiModelProperty(value = "省市区名称组合")
    private String areaInfo;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty(value = "结算方式：1-按月结算，2-按周结算")
    private Integer billType;

    @ApiModelProperty(value = "结算方式值：1-按月结算，2-按周结算")
    private String billTypeValue;

    @ApiModelProperty("结算周期")
    private String billDay;

    public OwnStoreDetailVO(Store store, StoreCertificate storeCertificate) {
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        contactName = storeCertificate.getContactName();
        contactPhone = CommonUtil.dealMobile(storeCertificate.getContactPhone());
        provinceCode = store.getProvinceCode();
        cityCode = store.getCityCode();
        areaCode = store.getAreaCode();
        areaInfo = store.getAreaInfo();
        address = store.getAddress();
        billType = store.getBillType();
        billTypeValue = getRealBillTypeValue(billType);
        billDay = store.getBillDay();
    }

    public static String getRealBillTypeValue(Integer billType) {
        String value = null;
        if (StringUtils.isEmpty(billType)) return Language.translate("未知");
        switch (billType) {
            case StoreConst.BILL_TYPE_MONTH:
                value = "按月结算";
                break;
            case StoreConst.BILL_TYPE_WEEK:
                value = "按周结算";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}
