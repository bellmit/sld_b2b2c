package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 优惠券新增DTO
 * @Author wuxy
 */
@Data
public class CouponAddDTO implements Serializable {

    private static final long serialVersionUID = -9085066587554603662L;
    @ApiModelProperty(value = "优惠券名称", required = true)
    private String couponName;

    @ApiModelProperty(value = "优惠券获取类型(1-免费领取；3-活动赠送）", required = true)
    private Integer publishType;

    @ApiModelProperty(value = "优惠券类型(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）", required = true)
    private Integer couponType;

    @ApiModelProperty(value = "发行限制-优惠券类型对应的具体值(满减金额:例如20元，折扣：例如90为90%）")
    private BigDecimal publishValue;

    @ApiModelProperty("发行限制-随机金额的最大值")
    private BigDecimal randomMax;

    @ApiModelProperty("发行限制-随机金额最小值")
    private BigDecimal randomMin;

    @ApiModelProperty("发行限制-发行总金额=发行数量*面额；随机金额券为所有券的和；折扣券不需要此项")
    private BigDecimal publishAmount;

    @ApiModelProperty(value = "发行限制-发行数量", required = true)
    private Integer publishNum;

    @ApiModelProperty(value = "发行限制-领取开始时间", required = true)
    private Date publishStartTime;

    @ApiModelProperty(value = "发行限制-领取结束时间", required = true)
    private Date publishEndTime;

    @ApiModelProperty(value = "发行限制-限制领取次数，0-为不限制，1-限制领1次，默认为1", required = true)
    private Integer limitReceive;

    @ApiModelProperty("使用限制-使用时间类型(固定起始时间）")
    private Date effectiveStart;

    @ApiModelProperty("使用限制-使用时间类型(固定结束时间）")
    private Date effectiveEnd;

    @ApiModelProperty("使用限制-固定有效期时长，单位天")
    private Integer cycle;

    @ApiModelProperty(value = "使用限制-适用商品类型(1-全部商品；2-指定商品；3-指定分类（平台新建优惠券使用））", required = true)
    private Integer useType;

    @ApiModelProperty(value = "订单金额使用限制，超过quota后可用", required = true)
    private BigDecimal limitQuota;

    @ApiModelProperty("折扣最多优惠金额")
    private BigDecimal discountLimitAmount;

    @ApiModelProperty("是否允许与店铺优惠券叠加使用(0-不允许；1-允许）（平台新建优惠券使用）")
    private Integer plusQualification;

    @ApiModelProperty(value = "指定商品id集合，用逗号隔开")
    private String goodsIds;

    @ApiModelProperty(value = "指定分类id集合，用逗号隔开（平台新建优惠券使用）")
    private String categoryIds;

}
