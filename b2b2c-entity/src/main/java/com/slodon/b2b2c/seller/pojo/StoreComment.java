package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺评论管理
 */
@Data
public class StoreComment implements Serializable {
    private static final long serialVersionUID = -4085544449169685310L;
    @ApiModelProperty("评论id")
    private Integer commentId;

    @ApiModelProperty("评价人ID")
    private Integer memberId;

    @ApiModelProperty("评价人账号")
    private String memberName;

    @ApiModelProperty("评价商家服务")
    private String storeAttitude;

    @ApiModelProperty("评价时间")
    private Date createTime;

    @ApiModelProperty("所属商家")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("描述相符(1到5星)")
    private Integer description;

    @ApiModelProperty("服务态度(1到5星)")
    private Integer serviceAttitude;

    @ApiModelProperty("发货速度(1到5星)")
    private Integer deliverSpeed;
}