package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.seller.pojo.Vendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装操作员VO对象
 */
@Data
public class StoreVendorVO {

    @ApiModelProperty("操作员id")
    private Long vendorId;

    @ApiModelProperty("操作员账号")
    private String vendorName;

    @ApiModelProperty("手机号")
    private String vendorMobile;

    @ApiModelProperty("邮件")
    private String vendorEmail;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("是否店铺管理员：0-否，1-是，每个店铺只有一个账号为1")
    private String isStoreAdmin;

    @ApiModelProperty("注册时间")
    private Date registerTime;

    @ApiModelProperty("登陆时间")
    private Date latestLoginTime;

    @ApiModelProperty("是否允许登陆,0-禁止，1-允许")
    private Integer isAllowLogin;

    @ApiModelProperty("是否允许登陆,0-禁止，1-允许")
    private String isAllowLoginValue;

    @ApiModelProperty("角色id")
    private Integer rolesId;

    @ApiModelProperty("权限组名称")
    private String rolesName;

    public StoreVendorVO(Vendor vendor) {
        vendorId = vendor.getVendorId();
        vendorName = vendor.getVendorName();
        vendorMobile = CommonUtil.dealMobile(vendor.getVendorMobile());
        vendorEmail = vendor.getVendorEmail();
        storeId = vendor.getStoreId();
        isStoreAdmin = vendor.getIsStoreAdmin();
        registerTime = vendor.getRegisterTime();
        latestLoginTime = vendor.getLatestLoginTime();
        isAllowLogin = vendor.getIsAllowLogin();
        isAllowLoginValue = getRealIsAllowLoginValue(isAllowLogin);
        rolesId = vendor.getRolesId();
    }

    public static String getRealIsAllowLoginValue(Integer isAllowLogin) {
        String value = null;
        if (StringUtils.isEmpty(isAllowLogin)) return Language.translate("未知");
        switch (isAllowLogin) {
            case VendorConst.NOT_ALLOW_LOGIN:
                value = "冻结";
                break;
            case VendorConst.IS_ALLOW_LOGIN:
                value = "正常";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
