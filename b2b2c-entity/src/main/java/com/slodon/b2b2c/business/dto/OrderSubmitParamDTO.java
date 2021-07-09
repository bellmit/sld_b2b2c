package com.slodon.b2b2c.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 订单提交前后端参数传递类
 * 用于：
 * 1.点击结算按钮、立即购买跳转提交订单页
 * 2.提交订单页，修改优惠券、修改积分、修改地址，更新页面数据
 * 3.提交订单，保存订单及相关数据表
 */
@Data
public class OrderSubmitParamDTO implements Serializable {
    private static final long serialVersionUID = -2964136902144210150L;
    @ApiModelProperty(value = "1==立即购买、去结算;2==提交订单页，修改优惠券、修改积分、修改地址，更新页面数据;3==提交订单", required = true)
    private Integer source;
    @ApiModelProperty(value = "订单来源1、pc；2、H5；3、Android；4、IOS; 5-微信小程序； 提交订单必传")
    private Integer orderFrom;
    @ApiModelProperty(value = "会员收货地址id； 提交订单必传")
    private Integer addressId;
    @ApiModelProperty(value = "使用平台优惠券编码")
    private String platformCouponCode;
    @ApiModelProperty(value = "使用积分数量")
    private Integer integral;
    @ApiModelProperty(value = "店铺信息")
    private List<StoreInfo> storeInfoList;

    @ApiModelProperty(value = "是否购物车下单,默认为true,立即下单、活动下单时为false", required = true)
    private Boolean isCart = true;
    @ApiModelProperty(value = "是否单独购买,默认为false（暂时拼团使用）")
    private Boolean isAloneBuy = false;
    @ApiModelProperty("拼团团队id（参团使用）")
    private Integer spellTeamId = 0;

    //非购物车下单信息
    @ApiModelProperty(value = "skuId，非购物车下单必传")
    private Long productId;
    @ApiModelProperty(value = "购买数量，非购物车下单必传")
    private Integer number;
    @ApiModelProperty(value = "订单号，尾款订单必传")
    private String orderSn;

    @Data
    public static class StoreInfo implements Serializable {
        private static final long serialVersionUID = 79094261106390951L;
        @ApiModelProperty(value = "店铺id", required = true)
        private Long storeId;
        @ApiModelProperty(value = "会员发票id")
        private Integer invoiceId;
        @ApiModelProperty(value = "使用店铺优惠券编码")
        private String storeCouponCode;
        @ApiModelProperty(value = "给商家留言")
        private String remark;
    }

    /**
     * 根据店铺id获取店铺信息
     *
     * @param storeId
     * @return
     */
    public StoreInfo getStoreInfoByStoreId(Long storeId) {
        if (!CollectionUtils.isEmpty(this.storeInfoList)) {
            for (StoreInfo storeInfo : storeInfoList) {
                if (storeId.equals(storeInfo.getStoreId())) {
                    return storeInfo;
                }
            }
        }
        return null;
    }
}
