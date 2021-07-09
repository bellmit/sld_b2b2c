package com.slodon.b2b2c.vo.helpdesk;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.seller.pojo.Vendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装店铺客服VO对象
 * @Author wuxy
 */
@Data
public class CustomerServiceVO implements Serializable {

    private static final long serialVersionUID = -5363795296590006498L;
    @ApiModelProperty("商户id")
    private Long vendorId;

    @ApiModelProperty("商户账号")
    private String vendorName;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("是否店铺管理员：0-否，1-是，每个店铺只有一个账号为1")
    private String isStoreAdmin;

    @ApiModelProperty("商家头像")
    private String avatarUrl;

    public CustomerServiceVO(Vendor vendor) {
        vendorId = vendor.getVendorId();
        vendorName = vendor.getVendorName();
        storeId = vendor.getStoreId();
        isStoreAdmin = vendor.getIsStoreAdmin();
        avatarUrl = FileUrlUtil.getFileUrl(vendor.getAvatarUrl(), ImageSizeEnum.SMALL);
    }
}
