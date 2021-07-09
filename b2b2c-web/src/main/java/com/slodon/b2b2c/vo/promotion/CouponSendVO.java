package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.Coupon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装优惠券赠送VO对象
 * @Author wuxy
 */
@Data
public class CouponSendVO implements Serializable {

    private static final long serialVersionUID = -5466665786281551626L;
    @ApiModelProperty("优惠券id")
    private Integer couponId;

    @ApiModelProperty("优惠券名称")
    private String couponName;

    @ApiModelProperty("优惠内容")
    private String couponContent;

    @ApiModelProperty("优惠券类型(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
    private Integer couponType;

    @ApiModelProperty("优惠券类型值(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
    private String couponTypeValue;

    @ApiModelProperty("剩余可用数量")
    private Integer remainNum;

    @ApiModelProperty("优惠券状态：1-未开始；2-已失效；3-进行中；4-已结束")
    private Integer state;

    @ApiModelProperty("优惠券状态值：1-未开始；2-已失效；3-进行中；4-已结束")
    private String stateValue;

    public CouponSendVO(Coupon coupon) {
        couponId = coupon.getCouponId();
        couponName = coupon.getCouponName();
        couponContent = coupon.getCouponContent();
        couponType = coupon.getCouponType();
        couponTypeValue = CouponVO.dealCouponTypeValue(coupon.getCouponType());
        remainNum = coupon.getPublishNum() - coupon.getReceivedNum();
        state = CouponVO.dealState(coupon.getState(), coupon.getPublishStartTime(), coupon.getPublishEndTime());
        stateValue = CouponVO.dealStateValue(coupon.getState(), coupon.getPublishStartTime(), coupon.getPublishEndTime());
    }

}
