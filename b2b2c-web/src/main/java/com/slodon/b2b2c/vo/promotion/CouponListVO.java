package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装我的优惠券VO对象
 * @Author wuxy
 */
@Data
public class CouponListVO implements Serializable {

    private static final long serialVersionUID = 4927997437154945431L;
    @ApiModelProperty("会员优惠券ID")
    private Integer couponMemberId;

    @ApiModelProperty("优惠券编码")
    private String couponCode;

    @ApiModelProperty("优惠券id")
    private Integer couponId;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠内容")
    private String couponContent;

    @ApiModelProperty("优惠券使用说明")
    private String description;

    @ApiModelProperty("优惠券类型(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
    private Integer couponType;

    @ApiModelProperty("优惠券类型值(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
    private String couponTypeValue;

    @ApiModelProperty("使用状态(1-未使用；2-使用；3-过期无效）")
    private Integer useState;

    @ApiModelProperty("使用开始时间")
    private String effectiveStart;

    @ApiModelProperty("使用结束时间")
    private String effectiveEnd;

    @ApiModelProperty("发行限制-优惠券类型对应的具体值(满减金额:例如20元，折扣：例如90为90%）")
    private BigDecimal publishValue;

    @ApiModelProperty("不同优惠券类型是否允许叠加使用(0-不允许；1-允许）（店铺和平台的券可以叠加使用）")
    private String plusQualification;

    @ApiModelProperty("使用限制-适用商品类型(1-全部商品；2-指定商品；3-指定分类）")
    private Integer useType;

    @ApiModelProperty("使用限制-适用商品类型(1-全部商品；2-指定商品；3-指定分类）")
    private String useTypeValue;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("优惠券使用商品类型为商品时返商品id串")
    private String goodsIds;

    @ApiModelProperty("优惠券使用商品类型为分类时返分类id串")
    private String cateIds;

    public CouponListVO(Coupon coupon, CouponMember couponMember) {
        couponMemberId = couponMember.getCouponMemberId();
        couponCode = couponMember.getCouponCode();
        couponId = coupon.getCouponId();
        couponName = coupon.getCouponName();
        couponContent = StringUtil.isNullOrZero(coupon.getDiscountLimitAmount()) ? coupon.getCouponContent()
                : "最多优惠" + coupon.getDiscountLimitAmount().intValue() + "元";
        description = coupon.getDescription();
        couponType = coupon.getCouponType();
        couponTypeValue = CouponVO.dealCouponTypeValue(coupon.getCouponType());
        useState = couponMember.getUseState();
        effectiveStart = TimeUtil.dealTime(couponMember.getEffectiveStart());
        effectiveEnd = TimeUtil.dealTime(couponMember.getEffectiveEnd());
        publishValue = coupon.getCouponType() == CouponConst.COUPON_TYPE_3 ? couponMember.getRandomAmount()
                : coupon.getCouponType() == CouponConst.COUPON_TYPE_2 ? coupon.getPublishValue().divide(new BigDecimal(10)) : coupon.getPublishValue();
        plusQualification = coupon.getPlusQualification() == 1 ? "可与店铺优惠券共用。" : "";
        useType = coupon.getUseType();
        useTypeValue = dealUseTypeValueValue(coupon.getUseType());
        storeId = coupon.getStoreId();
        storeName = coupon.getCouponName();
    }

    public static String dealUseTypeValueValue(Integer useType) {
        String value = null;
        if (StringUtils.isEmpty(useType)) return null;
        switch (useType) {
            case CouponConst.USE_TYPE_1:
                value = "通用";
                break;
            case CouponConst.USE_TYPE_2:
                value = "指定商品使用";
                break;
            case CouponConst.USE_TYPE_3:
                value = "指定分类使用";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
