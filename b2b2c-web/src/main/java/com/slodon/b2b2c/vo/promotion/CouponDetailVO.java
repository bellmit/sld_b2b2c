package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.Coupon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装优惠券详情VO对象
 * @Author wuxy
 */
@Data
public class CouponDetailVO implements Serializable {

    private static final long serialVersionUID = -2452125571848430188L;
    @ApiModelProperty("优惠券id")
    private Integer couponId;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠内容")
    private String couponContent;

    @ApiModelProperty("优惠券获取类型(1-免费领取；3-活动赠送）")
    private Integer publishType;

    @ApiModelProperty("优惠券获取类型值(1-免费领取；3-活动赠送）")
    private String publishTypeValue;

    @ApiModelProperty("优惠券类型(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
    private Integer couponType;

    @ApiModelProperty("优惠券类型值(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
    private String couponTypeValue;

    @ApiModelProperty("统计信息-已经领取数量")
    private Integer receivedNum;

    @ApiModelProperty("统计信息-已使用数量")
    private Integer usedNum;

    @ApiModelProperty("发行限制-优惠券类型对应的具体值(满减金额:例如20元，折扣：例如90为90%）")
    private BigDecimal publishValue;

    @ApiModelProperty("发行限制-随机金额的最大值")
    private BigDecimal randomMax;

    @ApiModelProperty("发行限制-随机金额最小值")
    private BigDecimal randomMin;

    @ApiModelProperty("发行限制-发行总金额=发行数量*面额；随机金额券为所有券的和；折扣券不需要此项")
    private BigDecimal publishAmount;

    @ApiModelProperty("发行限制-发行数量")
    private Integer publishNum;

    @ApiModelProperty("发行限制-领取开始时间")
    private Date publishStartTime;

    @ApiModelProperty("发行限制-领取结束时间")
    private Date publishEndTime;

    @ApiModelProperty("发行限制-限制领取次数，0-为不限制，1-限制领1次，默认为1")
    private Integer limitReceive;

    @ApiModelProperty("发行限制-限制领取次数值，0-为不限制，1-限制领1次")
    private String limitReceiveValue;

    @ApiModelProperty("使用限制-（1-固定起止时间，2-固定有效期）")
    private Integer effectiveTimeType;

    @ApiModelProperty("使用限制-使用时间类型(固定起始时间）")
    private Date effectiveStart;

    @ApiModelProperty("使用限制-使用时间类型(固定结束时间）")
    private Date effectiveEnd;

    @ApiModelProperty("使用限制-固定有效期时长，单位天")
    private Integer cycle;

    @ApiModelProperty("优惠券状态(1-正常；2-失效；3-删除）")
    private Integer state;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("使用限制-适用商品类型(1-全部商品；2-指定商品；3-指定分类）")
    private Integer useType;

    @ApiModelProperty("订单金额使用限制，超过quota后可用")
    private BigDecimal limitQuota;

    @ApiModelProperty("折扣最多优惠金额")
    private BigDecimal discountLimitAmount;

    @ApiModelProperty("不同优惠券类型是否允许叠加使用(0-不允许；1-允许）（店铺和平台的券可以叠加使用）")
    private Integer plusQualification;

    @ApiModelProperty("指定商品分类列表")
    private List<CouponGoodsCategoryVO> goodsCategoryList;

    public CouponDetailVO(Coupon coupon) {
        couponId = coupon.getCouponId();
        couponName = coupon.getCouponName();
        couponContent = coupon.getCouponContent();
        publishType = coupon.getPublishType();
        publishTypeValue = CouponVO.dealPublishTypeValue(coupon.getPublishType());
        couponType = coupon.getCouponType();
        couponTypeValue = CouponVO.dealCouponTypeValue(coupon.getCouponType());
        receivedNum = coupon.getReceivedNum();
        usedNum = coupon.getUsedNum();
        publishValue = coupon.getPublishValue();
        randomMax = coupon.getRandomMax();
        randomMin = coupon.getRandomMin();
        publishAmount = coupon.getPublishAmount();
        publishNum = coupon.getPublishNum();
        publishStartTime = coupon.getPublishStartTime();
        publishEndTime = coupon.getPublishEndTime();
        limitReceive = coupon.getLimitReceive();
        limitReceiveValue = coupon.getLimitReceive() == 0 ? "不限制" : "限制领" + coupon.getLimitReceive() + "次";
        effectiveTimeType = coupon.getEffectiveTimeType();
        effectiveStart = coupon.getEffectiveStart();
        effectiveEnd = coupon.getEffectiveEnd();
        cycle = coupon.getCycle();
        state = coupon.getState();
        storeId = coupon.getStoreId();
        storeName = coupon.getStoreName();
        useType = coupon.getUseType();
        limitQuota = coupon.getLimitQuota();
        discountLimitAmount = coupon.getDiscountLimitAmount();
        plusQualification = coupon.getPlusQualification();
    }

}
