package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 阶梯团活动表
 */
@Data
public class LadderGroup implements Serializable {
    private static final long serialVersionUID = 7960967493588589569L;

    @ApiModelProperty("阶梯团活动id")
    private Integer groupId;

    @ApiModelProperty("阶梯团活动名称")
    private String groupName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("活动标签id")
    private Integer labelId;

    @ApiModelProperty("活动标签名称")
    private String labelName;

    @ApiModelProperty("尾款时间(活动结束多少小时内需要支付尾款)")
    private Integer balanceTime;

    @ApiModelProperty("限购件数，0为不限购")
    private Integer buyLimitNum;

    @ApiModelProperty("是否退还定金：1-是；0-否")
    private Integer isRefundDeposit;

    @ApiModelProperty("阶梯优惠方式：1-阶梯价格；2-阶梯折扣")
    private Integer discountType;

    @ApiModelProperty("商品id(spu)")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("状态：1-创建；2-发布；3-失效；4-删除")
    private Integer state;

    @ApiModelProperty("是否生成尾款信息：0、未生成；1、已生成")
    private Integer executeState;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("创建商户ID")
    private Long createVendorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}