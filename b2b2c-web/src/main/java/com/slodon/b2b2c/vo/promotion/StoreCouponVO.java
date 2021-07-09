package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装店铺发放的优惠券VO对象
 * @Author wuxy
 */
@Data
public class StoreCouponVO implements Serializable {

    private static final long serialVersionUID = -6347971421685725921L;
    @ApiModelProperty("优惠券id")
    private Integer couponId;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠内容")
    private String couponContent;

    @ApiModelProperty("优惠券使用说明")
    private String description;

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

    @ApiModelProperty("优惠金额;折扣")
    private String publishValue;

    @ApiModelProperty("发行限制-随机金额的最大值")
    private BigDecimal randomMax;

    @ApiModelProperty("发行限制-随机金额最小值")
    private BigDecimal randomMin;

    @ApiModelProperty("发行限制-发行数量")
    private Integer publishNum;

    @ApiModelProperty("发行限制-领取开始时间")
    private String publishStartTime;

    @ApiModelProperty("发行限制-领取结束时间")
    private String publishEndTime;

    @ApiModelProperty("发行限制-限制领取次数，0-为不限制，1-限制领1次，默认为1")
    private Integer limitReceive;

    @ApiModelProperty("使用限制-（1-固定起止时间，2-固定有效期）")
    private Integer effectiveTimeType;

    @ApiModelProperty("使用限制-使用时间类型(固定起始时间）")
    private String effectiveStart;

    @ApiModelProperty("使用限制-使用时间类型(固定结束时间）")
    private String effectiveEnd;

    @ApiModelProperty("使用限制-固定有效期时长，单位天")
    private Integer cycle;

    @ApiModelProperty("优惠券状态(1-正常；2-失效；3-删除）")
    private Integer state;

    @ApiModelProperty("优惠券状态值(1-正常；2-失效；3-删除）")
    private String stateValue;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("订单金额使用限制，超过quota后可用")
    private BigDecimal limitQuota;

    @ApiModelProperty("是否推荐 1、推荐；0、不推荐")
    private Integer isRecommend;

    @ApiModelProperty("用户是否领取:0-未领取，1-已领取,2-已抢完")
    private Integer isReceive;

    @ApiModelProperty("适用商品类型：1-全部商品，2-指定商品")
    private Integer useType;

    @ApiModelProperty("适用商品类型值：1-全部商品，2-指定商品")
    private String useTypeValue;

    @ApiModelProperty("优惠券使用商品类型为指定商品时返商品id串")
    private String goodsIds;

    @ApiModelProperty("已领取百分比")
    private Integer receivePercent;

    public StoreCouponVO(Coupon coupon) {
        couponId = coupon.getCouponId();
        couponName = coupon.getCouponName();
        couponContent = coupon.getCouponContent();
        description = coupon.getDescription();
        publishType = coupon.getPublishType();
        publishTypeValue = dealPublishTypeValue(coupon.getPublishType());
        couponType = coupon.getCouponType();
        couponTypeValue = dealCouponTypeValue(coupon.getCouponType());
        receivedNum = coupon.getReceivedNum();
        usedNum = coupon.getUsedNum();
        randomMax = coupon.getRandomMax();
        randomMin = coupon.getRandomMin();
        publishNum = coupon.getPublishNum();
        limitReceive = coupon.getLimitReceive();
        effectiveTimeType = coupon.getEffectiveTimeType();
        cycle = coupon.getCycle();
        state = coupon.getState();
        stateValue = dealStateValue(coupon.getState(), coupon.getPublishStartTime(), coupon.getPublishEndTime());
        storeId = coupon.getStoreId();
        storeName = coupon.getStoreName();
        limitQuota = coupon.getLimitQuota();
        isRecommend = coupon.getIsRecommend();
        useType = coupon.getUseType();
        useTypeValue = dealUseTypeValue(useType);
        Integer receivedNum = (coupon.getReceivedNum() * 100) / coupon.getPublishNum();
        receivePercent = receivedNum == 0 && coupon.getReceivedNum() > 0 ? 1 : receivedNum;
    }

    public static String dealPublishTypeValue(Integer publishType) {
        String value = null;
        if (StringUtils.isEmpty(publishType)) return null;
        switch (publishType) {
            case CouponConst.PUBLISH_TYPE_1:
                value = "免费领取";
                break;
            case CouponConst.PUBLISH_TYPE_2:
                value = "积分兑换";
                break;
            case CouponConst.PUBLISH_TYPE_3:
                value = "活动赠送";
                break;
            case CouponConst.PUBLISH_TYPE_4:
                value = "礼包领取";
                break;
        }
        return value;
    }

    public static String dealCouponTypeValue(Integer couponType) {
        String value = null;
        if (StringUtils.isEmpty(couponType)) return null;
        switch (couponType) {
            case CouponConst.COUPON_TYPE_1:
                value = "满减券";
                break;
            case CouponConst.COUPON_TYPE_2:
                value = "折扣券";
                break;
            case CouponConst.COUPON_TYPE_3:
                value = "随机金额券";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static String dealStateValue(Integer state, Date publishStartTime, Date publishEndTime) {
        String value = null;
        if (StringUtils.isEmpty(state)) return null;
        if (state == CouponConst.ACTIVITY_STATE_1) {
            Date date = new Date();
            if (date.before(publishStartTime)) {
                value = "未开始";
            } else if (date.after(publishEndTime)) {
                value = "已结束";
            } else {
                value = "进行中";
            }
        } else {
            value = "已失效";
        }
        return value;
    }

    public static String dealUseTypeValue(Integer useType) {
        String value = null;
        if (StringUtils.isEmpty(useType)) return null;
        switch (useType) {
            case CouponConst.USE_TYPE_1:
                value = "全部店铺商品";
                break;
            case CouponConst.USE_TYPE_2:
                value = "指定商品";
                break;
        }
        return value;
    }
}
