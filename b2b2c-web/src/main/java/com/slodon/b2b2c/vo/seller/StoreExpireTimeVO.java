package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装铺店铺到期时间VO对象
 */
@Data
public class StoreExpireTimeVO {

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺到期时间")
    private Date storeExpireTime;

    public StoreExpireTimeVO(Store store) {
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        storeExpireTime = store.getStoreExpireTime();
    }
}
