package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.member.pojo.MemberAddress;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberAddressVO implements Serializable {
    private static final long serialVersionUID = 5181067744880790053L;
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

    @ApiModelProperty("电话")
    private String telPhone;

    @ApiModelProperty("是否默认地址：1-默认地址，0-非默认地址")
    private Integer isDefault;

    public MemberAddressVO(MemberAddress memberaddress) {
        addressId = memberaddress.getAddressId();
        memberId = memberaddress.getMemberId();
        memberName = memberaddress.getMemberName();
        provinceCode = memberaddress.getProvinceCode();
        cityCode = memberaddress.getCityCode();
        districtCode = memberaddress.getDistrictCode();
        postCode = memberaddress.getPostCode();
        addressAll = memberaddress.getAddressAll();
        detailAddress = memberaddress.getDetailAddress();
        telMobile = CommonUtil.dealMobile(memberaddress.getTelMobile());
        telPhone = CommonUtil.dealMobile(memberaddress.getTelPhone());
        isDefault = memberaddress.getIsDefault();
    }
}
