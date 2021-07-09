package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.StoreRenew;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import static com.slodon.b2b2c.vo.seller.StoreApplyVO.getRealStateValue;


/**
 * @author lxk
 * @program: slodon
 * @Description 封装续签详情VO对象
 */
@Data
public class RenewStateVO {

    @ApiModelProperty("续签id")
    private Integer renewId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("续签状态：1：待付款；2续签成功")
    private Integer state;

    @ApiModelProperty("状态值:1：待付款；2:已完成")
    private String stateValue;

    public RenewStateVO(StoreRenew storeRenew) {
        renewId = storeRenew.getRenewId();
        storeId = storeRenew.getStoreId();
        storeName = storeRenew.getStoreName();
        state = storeRenew.getState();
        stateValue = getRealStateValue(state);
    }


}
