package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 提交订单页展示信息vo
 */
@Data
public class OrderSubmitPageVO {

    public OrderSubmitPageVO(OrderSubmitDTO dto, List<BigDecimal> expressFeeList) {
        this.availableCouponList = dto.getAvailableCouponList();
        this.disabledCouponList = dto.getDisableCouponList();
        this.totalDiscount = dto.getTotalDiscount().toString();
        this.totalAmount = dto.getTotalAmount().toString();
        //合计+运费
        expressFeeList.forEach(fee -> {
            this.totalAmount = new BigDecimal(this.totalAmount).add(fee).toString();
        });
        this.integral = dto.getIntegral();
        this.integralCashAmount = StringUtils.isEmpty(dto.getIntegralCashAmount()) ? null : dto.getIntegralCashAmount().toString();
        for (int i = 0; i < dto.getOrderInfoList().size(); i++) {
            storeGroupList.add(new StoreGroupVO(dto.getOrderInfoList().get(i), expressFeeList.get(i)));
        }
    }

    @ApiModelProperty("可用优惠券列表")
    private List<OrderSubmitDTO.CouponVO> availableCouponList;
    @ApiModelProperty("不可用优惠券列表")
    private List<OrderSubmitDTO.CouponVO> disabledCouponList;
    @ApiModelProperty("活动优惠总额")
    private String totalDiscount;
    @ApiModelProperty("合计")
    private String totalAmount;
    @ApiModelProperty("使用的积分数")
    private Integer integral;
    @ApiModelProperty("积分抵扣金额")
    private String integralCashAmount;
    @ApiModelProperty("店铺分组列表")
    private List<StoreGroupVO> storeGroupList = new ArrayList<>();
    @ApiModelProperty("是否可以开增值税发票,true-可以")
    private Boolean isVatInvoice;
    @ApiModelProperty("活动类型")
    private Integer promotionType = 0;
    @ApiModelProperty("预售信息")
    private PresellInfo presellInfo;
    @ApiModelProperty("阶梯团信息")
    private LadderGroupInfo ladderGroupInfo;

    /**
     * 按照店铺分组的订单信息
     */
    @Data
    public static class StoreGroupVO {
        public StoreGroupVO(OrderSubmitDTO.OrderInfo orderInfo, BigDecimal expressFee) {
            this.storeId = orderInfo.getStoreId();
            this.storeName = orderInfo.getStoreName();
            this.availableCouponList = orderInfo.getAvailableCouponList();
            this.disabledCouponList = orderInfo.getDisableCouponList();
            this.expressFee = expressFee;
            //计算店铺内金额
            this.totalDiscount = orderInfo.getTotalDiscount();//包含平台优惠
            this.totalAmount = orderInfo.getTotalAmount();//包含平台优惠
            orderInfo.getOrderProductInfoList().forEach(orderProductInfo -> {
                this.productList.add(new ProductVO(orderProductInfo));
                orderProductInfo.getPromotionInfoList().forEach(promotionInfo -> {
                    if (!promotionInfo.getIsStore()) {
                        //不是店铺活动，将优惠金额去除
                        this.totalDiscount = this.totalDiscount.subtract(promotionInfo.getDiscount());
                        this.totalAmount = this.totalAmount.add(promotionInfo.getDiscount());
                    }
                });
            });
            this.goodsAmount = orderInfo.getGoodsAmount();
            //构造赠品列表
            if (!CollectionUtils.isEmpty(orderInfo.getPromotionInfoList())) {
                Map<Long/*productId*/, OrderSubmitDTO.PromotionInfo.SendProduct> sendProductMap = new HashMap<>();
                orderInfo.getPromotionInfoList().forEach(promotionInfo -> {
                    if (!CollectionUtils.isEmpty(promotionInfo.getSendProductList())) {
                        promotionInfo.getSendProductList().forEach(sendProduct -> {
                            if (sendProductMap.containsKey(sendProduct.getProductId())) {
                                //相同的赠品，数量相加
                                sendProductMap.get(sendProduct.getProductId())
                                        .setNum(sendProductMap.get(sendProduct.getProductId()).getNum() + sendProduct.getNum());
                            } else {
                                //新的赠品
                                sendProductMap.put(sendProduct.getProductId(), sendProduct);
                            }
                        });
                    }
                });
                if (!CollectionUtils.isEmpty(sendProductMap)) {
                    sendProductMap.keySet().forEach(productId -> {
                        this.sendProductList.add(sendProductMap.get(productId));
                    });
                }
            }
        }

        @ApiModelProperty("店铺id")
        private Long storeId;
        @ApiModelProperty("店铺名称")
        private String storeName;
        @ApiModelProperty("可用优惠券列表")
        private List<OrderSubmitDTO.CouponVO> availableCouponList;
        @ApiModelProperty("不可用优惠券列表")
        private List<OrderSubmitDTO.CouponVO> disabledCouponList;
        @ApiModelProperty("运费")
        private BigDecimal expressFee;
        @ApiModelProperty("店铺内活动优惠总额")
        private BigDecimal totalDiscount;
        @ApiModelProperty("店铺内小计")
        private BigDecimal totalAmount;
        @ApiModelProperty("商品金额，等于订单中所有的商品的单价乘以数量之和")
        private BigDecimal goodsAmount;
        @ApiModelProperty("货品列表")
        private List<ProductVO> productList = new ArrayList<>();
        @ApiModelProperty("赠品列表")
        private List<OrderSubmitDTO.PromotionInfo.SendProduct> sendProductList = new ArrayList<>();

        @Data
        public static class ProductVO {
            public ProductVO(Cart cart) {
                this.productId = cart.getProductId();
                this.goodsId = cart.getGoodsId();
                this.goodsName = cart.getGoodsName();
                this.specValues = cart.getSpecValues();
                this.image = FileUrlUtil.getFileUrl(cart.getProductImage(), ImageSizeEnum.SMALL);
                this.price = cart.getProductPrice();
                this.buyNum = cart.getBuyNum();
            }

            @ApiModelProperty("skuId")
            private Long productId;
            @ApiModelProperty("商品id")
            private Long goodsId;
            @ApiModelProperty("商品名称")
            private String goodsName;
            @ApiModelProperty("sku规格")
            private String specValues;
            @ApiModelProperty("sku图片")
            private String image;
            @ApiModelProperty("sku价格")
            private BigDecimal price;
            @ApiModelProperty("购买数量")
            private Integer buyNum;
        }
    }

    @Data
    public static class PresellInfo {
        @ApiModelProperty("预售活动id")
        private Integer presellId;

        @ApiModelProperty("预售类型（1-定金预售，2-全款预售）")
        private Integer type;

        @ApiModelProperty("预售状态：101-待付定金；102-待付尾款")
        private Integer presellState;

        @ApiModelProperty("发货时间")
        private Date deliverTime;

        @ApiModelProperty("活动商品id(sku)")
        private Long productId;

        @ApiModelProperty("预售价格")
        private String presellPrice;

        @ApiModelProperty("预售定金（全款预售不需要此项，定金预售需要）")
        private String firstMoney;

        @ApiModelProperty("订金可以抵现的金额（全款预售不需要此项，定金预售需要）")
        private String firstExpand;

        @ApiModelProperty("预售尾款（全款预售不需要此项，定金预售需要）")
        private String secondMoney;

        @ApiModelProperty("尾款优惠")
        private String finalDiscount;

        @ApiModelProperty("支付尾款的开始时间")
        private Date remainStartTime;
    }

    @Data
    public static class LadderGroupInfo {
        @ApiModelProperty("阶梯团活动id")
        private Integer groupId;

        @ApiModelProperty("阶梯团状态：101-待付定金；102-待付尾款；103-已完成付款")
        private Integer ladderGroupState;

        @ApiModelProperty("活动商品id(sku)")
        private Long productId;

        @ApiModelProperty("商品原价")
        private String productPrice;

        @ApiModelProperty("预付定金")
        private String advanceDeposit;

        @ApiModelProperty("尾款金额")
        private String remainAmount;

        @ApiModelProperty("实付尾款金额")
        private String realRemainAmount;

        @ApiModelProperty("尾款支付的开始时间")
        private Date remainStartTime;
    }

}
