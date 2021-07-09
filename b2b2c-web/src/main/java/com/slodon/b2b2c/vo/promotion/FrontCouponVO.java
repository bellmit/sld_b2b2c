package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装front端领券中心VO对象
 * @Author wuxy
 * @date 2020.11.23 12:23
 */
@Data
public class FrontCouponVO implements Serializable {

    private static final long serialVersionUID = 6956956489938070046L;
    @ApiModelProperty("分类列表")
    private List<CategoryVO> categoryList;

    @ApiModelProperty("优惠券列表")
    private List<CouponVO> couponList;

    @ApiModelProperty("分页信息")
    private Pagination pagination;

    public FrontCouponVO(List<CategoryVO> categoryList, List<CouponVO> couponList, PagerInfo pager) {
        this.categoryList = categoryList;
        this.couponList = couponList;
        if (pager != null) {
            this.pagination = new Pagination(pager.getPageIndex(), pager.getPageSize(), pager.getRowsCount());
        }
    }

    @Data
    public static class CouponVO implements Serializable {

        private static final long serialVersionUID = 4566857184473131193L;
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

        @ApiModelProperty("已抢百分比")
        private Integer robbedRate;

        @ApiModelProperty("领取状态：1-立即领取，2-已领取，3-已抢完")
        private Integer receivedState;

        @ApiModelProperty("发行限制-优惠券类型对应的具体值(满减金额:例如20元，折扣：例如90为90%）")
        private BigDecimal publishValue;

        @ApiModelProperty("领取开始时间")
        private String publishStartTime;

        @ApiModelProperty("领取结束时间")
        private String publishEndTime;

        @ApiModelProperty("限制领取次数，0-为不限制，1-限制领1次，默认为1")
        private Integer limitReceive;

        @ApiModelProperty("使用限制-适用商品类型(1-全部商品；2-指定商品；3-指定分类）")
        private Integer useType;

        @ApiModelProperty("使用限制-适用商品类型(1-全部商品；2-指定商品；3-指定分类）")
        private String useTypeValue;

        public CouponVO(Coupon coupon) {
            couponId = coupon.getCouponId();
            couponName = coupon.getCouponName();
            couponContent = StringUtil.isNullOrZero(coupon.getDiscountLimitAmount()) ? coupon.getCouponContent()
                    : "最多优惠" + coupon.getDiscountLimitAmount().intValue() + "元";
            description = coupon.getDescription();
            couponType = coupon.getCouponType();
            couponTypeValue = com.slodon.b2b2c.vo.promotion.CouponVO.dealCouponTypeValue(coupon.getCouponType());
            Integer receivedNum = (coupon.getReceivedNum() * 100) / coupon.getPublishNum();
            robbedRate = receivedNum == 0 && coupon.getReceivedNum() > 0 ? 1 : receivedNum;
            publishValue = coupon.getCouponType() == CouponConst.COUPON_TYPE_3 ? coupon.getRandomMax()
                    : coupon.getCouponType() == CouponConst.COUPON_TYPE_2 ? coupon.getPublishValue().divide(new BigDecimal(10)) : coupon.getPublishValue();
            publishStartTime = TimeUtil.dealTime(coupon.getPublishStartTime());
            publishEndTime = TimeUtil.dealTime(coupon.getPublishEndTime());
            limitReceive = coupon.getLimitReceive();
            useType = coupon.getUseType();
            useTypeValue = CouponListVO.dealUseTypeValueValue(coupon.getUseType());
        }
    }

    @Data
    public static class CategoryVO implements Serializable {

        private static final long serialVersionUID = -5744611897228068267L;
        @ApiModelProperty("分类id")
        private Integer categoryId;

        @ApiModelProperty("分类名称")
        private String categoryName;

        public CategoryVO(Integer categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }
    }

    @Data
    private class Pagination implements Serializable {

        private static final long serialVersionUID = -2073104891195245268L;
        @ApiModelProperty("当前页面位置")
        private Integer current;
        @ApiModelProperty("分页大小")
        private Integer pageSize;
        @ApiModelProperty("总数")
        private Integer total;

        private Pagination(Integer current, Integer pageSize, Integer total) {
            this.current = current;
            this.pageSize = pageSize;
            this.total = total;
        }
    }
}
