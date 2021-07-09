package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.VendorResources;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装商户登陆返回信息VO对象
 */
@Data
public class VendorLoginVO {

    @ApiModelProperty("商户id")
    private Long vendorId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("授权token")
    private String access_token;

    @ApiModelProperty("刷新token")
    private String refresh_token;

    @ApiModelProperty("申请状态")
    private Integer applyState;

    @ApiModelProperty("申请信息")
    private String message;

    @ApiModelProperty("拒绝原因")
    private String reason;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("是否店铺管理员：0-否，1-是，每个店铺只有一个账号为1")
    private String isStoreAdmin;

    @ApiModelProperty("资源列表")
    private List<VendorResources> resourceList;
}
