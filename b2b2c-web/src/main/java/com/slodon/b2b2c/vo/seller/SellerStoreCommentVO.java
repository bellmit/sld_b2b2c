package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.seller.pojo.StoreComment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺评论管理
 */
@Data
public class SellerStoreCommentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("评论id")
    private Integer commentId;

    @ApiModelProperty("评价人ID")
    private Integer memberId;

    @ApiModelProperty("评价人账号")
    private String memberName;

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

    public SellerStoreCommentVO(StoreComment storeComment) {
        commentId = storeComment.getCommentId();
        memberId = storeComment.getMemberId();
        memberName = storeComment.getMemberName();
        createTime = storeComment.getCreateTime();
        storeId = storeComment.getStoreId();
        storeName = storeComment.getStoreName();
        orderSn = storeComment.getOrderSn();
        description = storeComment.getDescription();
        serviceAttitude = storeComment.getServiceAttitude();
        deliverSpeed = storeComment.getDeliverSpeed();
    }
}