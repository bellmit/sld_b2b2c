package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.MemberInvoiceConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员发票信息表
 */
@Data
public class InvoiceVO implements Serializable {

    @ApiModelProperty("发票id")
    private Integer invoiceId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("是否默认发票：0-非默认发票，1-默认发票")
    private Integer isDefault;

    @ApiModelProperty("是否默认发票：0-非默认发票，1-默认发票")
    private String isDefaultValue;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("发票类型：1-普通发票；2-增值税发票")
    private Integer invoiceType;

    @ApiModelProperty("发票类型：1-普通发票；2-增值税发票")
    private String invoiceTypeValue;

    @ApiModelProperty("发票抬头(通用信息）")
    private String invoiceTitle;

    @ApiModelProperty("发票内容：1-商品明细；2-商品类别")
    private Integer invoiceContent;

    @ApiModelProperty("收票人姓名(通用信息）")
    private String receiverName;

    @ApiModelProperty("收票人手机号(通用信息）")
    private String receiverMobile;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("区县编码")
    private String districtCode;

    @ApiModelProperty("省市区组合")
    private String addressAll;

    @ApiModelProperty("收票人详细地址(通用信息）")
    private String receiverAddress;

    public InvoiceVO(MemberInvoice memberInvoice) {
        invoiceId = memberInvoice.getInvoiceId();
        memberId = memberInvoice.getMemberId();
        isDefault = memberInvoice.getIsDefault();
        isDefaultValue = dealIsDefaultValue(memberInvoice.getIsDefault());
        createTime = memberInvoice.getCreateTime();
        invoiceType = memberInvoice.getInvoiceType();
        invoiceTypeValue = dealInvoiceType(memberInvoice.getInvoiceType());
        invoiceTitle = memberInvoice.getInvoiceTitle();
        invoiceContent = memberInvoice.getInvoiceContent();
        receiverName = memberInvoice.getReceiverName();
        receiverMobile = CommonUtil.dealMobile(memberInvoice.getReceiverMobile());
        provinceCode = memberInvoice.getProvinceCode();
        cityCode = memberInvoice.getCityCode();
        districtCode = memberInvoice.getDistrictCode();
        addressAll = memberInvoice.getAddressAll();
        receiverAddress = memberInvoice.getReceiverAddress();
    }

    public static String dealInvoiceType(Integer invoiceType) {
        //发票类型：1-普通发票；2-增值税发票
        String value = null;
        if (StringUtils.isEmpty(invoiceType)) return Language.translate("未知");
        switch (invoiceType) {
            case MemberInvoiceConst.INVOICE_TYPE_1:
                value = "普通发票";
                break;
            case MemberInvoiceConst.INVOICE_TYPE_2:
                value = "增值税发票";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static String dealIsDefaultValue(Integer isDefault) {
        //是否默认发票：0-非默认发票，1-默认发票
        String value = null;
        if (StringUtils.isEmpty(isDefault)) return Language.translate("未知");
        switch (isDefault) {
            case MemberInvoiceConst.IS_DEFAULT_0:
                value = "非默认发票";
                break;
            case MemberInvoiceConst.IS_DEFAULT_1:
                value = "默认发票";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}