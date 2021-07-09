package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.StoreBindExpress;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 店铺绑定快递VO对象
 */
@Data
public class StoreBindExpressVO {

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("快递公司ID")
    private Integer expressId;

    @ApiModelProperty("物流名称")
    private String expressName;

    @ApiModelProperty("快递公司状态：1-启用，0-不启用")
    private String expressState;

    @ApiModelProperty("添加时间")
    private Date createTime;

    public StoreBindExpressVO(StoreBindExpress storeBindExpress) {
        bindId = storeBindExpress.getBindId();
        expressId = storeBindExpress.getExpressId();
        expressName = storeBindExpress.getExpressName();
        expressState = storeBindExpress.getExpressState();
        createTime = storeBindExpress.getCreateTime();
    }
}
