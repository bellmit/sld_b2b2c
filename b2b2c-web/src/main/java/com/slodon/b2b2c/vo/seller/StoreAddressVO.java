package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.seller.pojo.StoreAddress;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装地址列表VO对象
 */
@Data
public class StoreAddressVO {

    @ApiModelProperty("地址ID")
    private Integer addressId;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String telphone;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("地区编码")
    private String areaCode;

    @ApiModelProperty("街道编码")
    private String streetCode;

    @ApiModelProperty("省市区组合")
    private String areaInfo;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("创建时间")
    private Date createdTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("是否默认：1-是;0-否")
    private Integer isDefault;

    @ApiModelProperty("类型：1-发货地址；2-收货地址")
    private Integer type;

    @ApiModelProperty("类型值：1-发货地址；2-收货地址")
    private String typeValue;

    public StoreAddressVO(StoreAddress storeAddress) {
        addressId = storeAddress.getAddressId();
        contactName = storeAddress.getContactName();
        telphone = CommonUtil.dealMobile(storeAddress.getTelphone());
        provinceCode = storeAddress.getProvinceCode();
        cityCode = storeAddress.getCityCode();
        areaCode = storeAddress.getAreaCode();
        streetCode = storeAddress.getStreetCode();
        areaInfo = storeAddress.getAreaInfo();
        address = storeAddress.getAddress();
        createdTime = storeAddress.getCreatedTime();
        updateTime = storeAddress.getUpdateTime();
        isDefault = storeAddress.getIsDefault();
        type = storeAddress.getType();
        typeValue = getRealTypeValue(type);
    }

    public static String getRealTypeValue(Integer type) {
        String value = null;
        if (StringUtils.isEmpty(type))
            return Language.translate("未知");
        switch (type) {
            case StoreConst.ADDRESS_TYPE_DELIVER:
                value = "发货地址";
                break;
            case StoreConst.ADDRESS_TYPE_RECEIVE:
                value = "收货地址";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
