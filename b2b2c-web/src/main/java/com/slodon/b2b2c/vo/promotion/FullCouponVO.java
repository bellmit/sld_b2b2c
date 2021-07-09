package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.Coupon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @description: 封装满优惠送优惠券VO对象
 * @author: wuxy
 * @create: 2020.11.03 15:22
 **/
@Data
public class FullCouponVO implements Serializable {

    private static final long serialVersionUID = -5294940150404862497L;
    @ApiModelProperty("优惠券id")
    private Integer couponId;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠券类型(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
    private Integer couponType;

    @ApiModelProperty("优惠券类型值(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
    private String couponTypeValue;

    @ApiModelProperty("优惠内容")
    private String couponContent;

    @ApiModelProperty("剩余可用数量")
    private Integer couponStock;

    public FullCouponVO(Coupon coupon) {
        couponId = coupon.getCouponId();
        couponName = coupon.getCouponName();
        couponType = coupon.getCouponType();
        couponTypeValue = CouponVO.dealCouponTypeValue(coupon.getCouponType());
        couponContent = coupon.getCouponContent();
        couponStock = coupon.getPublishNum() - coupon.getReceivedNum();
    }

}
