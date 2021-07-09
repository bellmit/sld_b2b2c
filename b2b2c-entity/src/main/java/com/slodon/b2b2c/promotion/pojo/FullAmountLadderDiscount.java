package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 阶梯满折扣活动
 */
@Data
public class FullAmountLadderDiscount implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("阶梯满折扣活动id")
    private Integer fullId;

    @ApiModelProperty("阶梯满折扣活动名称")
    private String fullName;

    @ApiModelProperty("阶梯满折扣活动开始时间")
    private Date startTime;

    @ApiModelProperty("阶梯满折扣活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动状态( 1-已创建，2-已发布，3-进行中，4-已结束，5-已失效，6-已删除 )")
    private Integer state;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("创建用户id")
    private Long createVendorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人员vendorID，如果为0是系统或定时任务更新")
    private Long updateVendorId;

    @ApiModelProperty("活动更新更时间")
    private Date updateTime;
}