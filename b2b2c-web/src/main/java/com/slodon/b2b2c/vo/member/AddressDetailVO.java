package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.member.pojo.MemberAddress;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 收货地址
 */
@Data
public class AddressDetailVO implements Serializable {
    private static final long serialVersionUID = 756088093481372520L;
    @ApiModelProperty("地址id")
    private Integer addressId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("收货人")
    private String memberName;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("区县编码")
    private String districtCode;

    @ApiModelProperty("邮政编码")
    private String postCode;

    @ApiModelProperty("省市区组合")
    private String addressAll;

    @ApiModelProperty("详细地址")
    private String detailAddress;

    @ApiModelProperty("手机")
    private String telMobile;

    @ApiModelProperty("是否默认地址：1-默认地址，0-非默认地址")
    private Integer isDefault;

    public AddressDetailVO(MemberAddress memberAddress) {
        addressId = memberAddress.getAddressId();
        memberId = memberAddress.getMemberId();
        memberName = memberAddress.getMemberName();
        provinceCode = memberAddress.getProvinceCode();
        cityCode = memberAddress.getCityCode();
        districtCode = memberAddress.getDistrictCode();
        postCode = memberAddress.getPostCode();
        addressAll = memberAddress.getAddressAll();
        detailAddress = memberAddress.getDetailAddress();
        telMobile = memberAddress.getTelMobile();
        isDefault = memberAddress.getIsDefault();
    }
}