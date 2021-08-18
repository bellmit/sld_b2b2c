package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.system.pojo.BillAccount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装结算信息VO对象
 * @Author wuxy
 */
@Data
public class BillAccountVO implements Serializable {

    private static final long serialVersionUID = 5374364194014839623L;
    @ApiModelProperty("账号id")
    private Integer accountId;

    @ApiModelProperty("账户类型：1-银行账号；2-支付宝账号")
    private Integer accountType;

    @ApiModelProperty("账户类型值：1-银行账号；2-支付宝账号")
    private String accountTypeValue;

    @ApiModelProperty("支付宝姓名")
    private String alipayName;

    @ApiModelProperty("支付宝账号")
    private String alipayAccount;

    @ApiModelProperty("银行开户名")
    private String bankAccountName;

    @ApiModelProperty("公司银行账号")
    private String bankAccountNumber;

    @ApiModelProperty("开户银行支行")
    private String bankBranch;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("区县编码")
    private String districtCode;

    @ApiModelProperty("省市区组合")
    private String addressAll;

    @ApiModelProperty("是否默认账号：1-默认账号，0-非默认账号")
    private Integer isDefault;

    @ApiModelProperty("结算账号")
    private String billNumber;

    @ApiModelProperty("邮政编码")
    private String postCode;

    public BillAccountVO(BillAccount billAccount) {
        accountId = billAccount.getAccountId();
        accountType = billAccount.getAccountType();
        accountTypeValue = dealAccountTypeValue(billAccount.getAccountType());
        alipayName = billAccount.getAlipayName();
        alipayAccount = billAccount.getAlipayAccount();
        bankAccountName = billAccount.getBankAccountName();
        bankAccountNumber = billAccount.getBankAccountNumber();
        bankBranch = billAccount.getBankBranch();
        provinceCode = billAccount.getProvinceCode();
        cityCode = billAccount.getCityCode();
        districtCode = billAccount.getDistrictCode();
        addressAll = billAccount.getAddressAll();
        isDefault = billAccount.getIsDefault();
        billNumber = billAccount.getAccountType() == 1 ? billAccount.getBankAccountNumber() : billAccount.getAlipayAccount();
        postCode = billAccount.getPostCode();
    }

    public static String dealAccountTypeValue(Integer accountType) {
        String value = null;
        if (StringUtils.isEmpty(accountType)) return null;
        switch (accountType) {
            case BillConst.ACCOUNT_TYPE_1:
                value = "银行账号";
                break;
            case BillConst.ACCOUNT_TYPE_2:
                value = "支付宝账号";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}
