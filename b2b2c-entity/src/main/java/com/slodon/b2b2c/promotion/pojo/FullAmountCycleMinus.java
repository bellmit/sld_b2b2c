package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 循环满减活动表
 */
@Data
public class FullAmountCycleMinus implements Serializable {
    private static final long serialVersionUID = 1629475794016792057L;
    @ApiModelProperty("循环满减活动id")
    private Integer fullId;

    @ApiModelProperty("循环满减活动名称")
    private String fullName;

    @ApiModelProperty("循环满减活动开始时间")
    private Date startTime;

    @ApiModelProperty("循环满减活动结束时间")
    private Date endTime;

    @ApiModelProperty("满指定金额")
    private BigDecimal fullValue;

    @ApiModelProperty("减指定金额")
    private BigDecimal minusValue;

    @ApiModelProperty("活动状态( 1-已创建，2-已发布，3-进行中，4-已结束，5-已失效，6-已删除 )")
    private Integer state;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建用户id")
    private Long createVendorId;

    @ApiModelProperty("活动更新更时间")
    private Date updateTime;

    @ApiModelProperty("更新人员vendorID，如果为0是系统或定时任务更新")
    private Long updateVendorId;

    @ApiModelProperty("赠送积分")
    private Integer sendIntegral;

    @ApiModelProperty("优惠券id集合")
    private String sendCouponIds;

    @ApiModelProperty("赠送商品id集合")
    private String sendGoodsIds;
}