package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装自营店铺列表VO对象
 */
@Data
public class OwnStoreVO {

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺状态 1、开启；2、关闭")
    private Integer state;

    @ApiModelProperty("商户账号")
    private String vendorName;

    @ApiModelProperty("联系人电话")
    private String contactPhone;

    public OwnStoreVO(Store store) {
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        state = store.getState();
    }
}
