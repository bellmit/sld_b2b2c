package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.promotion.pojo.Coupon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装领取优惠券返回VO对象
 * @Author wuxy
 * @date 2020.11.23 17:42
 */
@Data
public class CouponReceiveVO implements Serializable {

    private static final long serialVersionUID = -966201912363133246L;
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

    @ApiModelProperty("优惠券类型对应的具体值(满减金额:例如20元，折扣：例如90为90%，随机金额：例如83.20元）")
    private BigDecimal publishValue;

    public CouponReceiveVO(Coupon coupon) {
        couponId = coupon.getCouponId();
        couponName = coupon.getCouponName();
        couponContent = coupon.getCouponContent();
        couponType = coupon.getCouponType();
    }
}
