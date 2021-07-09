package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 拼团活动表，店铺创建活动，然后绑定活动商品
 */
@Data
public class Spell implements Serializable {
    private static final long serialVersionUID = -7740866731365279585L;
    @ApiModelProperty("拼团活动id")
    private Integer spellId;

    @ApiModelProperty("拼团活动名称")
    private String spellName;

    @ApiModelProperty("拼团活动标签id")
    private Integer spellLabelId;

    @ApiModelProperty("拼团活动标签名称")
    private String spellLabelName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("活动状态(1-创建；2-发布；3-失效；4-删除)")
    private Integer state;

    @ApiModelProperty("要求成团人数")
    private Integer requiredNum;

    @ApiModelProperty("成团周期（开团-截团时长），单位：小时")
    private Integer cycle;

    @ApiModelProperty("活动最大限购数量；0为不限购")
    private Integer buyLimit;

    @ApiModelProperty("是否模拟成团(0-关闭/1-开启）")
    private Integer isSimulateGroup;

    @ApiModelProperty("团长是否有优惠(0-没有/1-有）")
    private Integer leaderIsPromotion;

    @ApiModelProperty("拼团规则说明")
    private String spellDesc;

    @ApiModelProperty("创建商户ID")
    private Long createVendorId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("活动编辑时间")
    private Date updateTime;
}