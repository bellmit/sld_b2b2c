package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺内“商品-标签”绑定关系
 */
@Data
public class StoreLabelBindGoods implements Serializable {
    private static final long serialVersionUID = -7359675950289582686L;

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("店铺内商品分类ID")
    private Integer innerLabelId;

    @ApiModelProperty("分类名称")
    private String innerLabelName;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("添加时间")
    private Date bindTime;

    @ApiModelProperty("绑定人ID")
    private Long bindVendorId;

    @ApiModelProperty("绑定人名字")
    private String bindVendorName;
}