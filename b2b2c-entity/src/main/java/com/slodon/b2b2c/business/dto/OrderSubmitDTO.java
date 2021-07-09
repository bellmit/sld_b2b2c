package com.slodon.b2b2c.business.dto;

import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单提交计算dto，
 * 用于提交订单，计算优惠
 * 一个 OrderSubmitDTO 为拆单后的订单列表信息
 *
 * 注：此类只用作订单模块与活动模块计算活动优惠的参数传递，不能用于提交订单接口接收前端参数
 */
@Data
@NoArgsConstructor
public class OrderSubmitDTO implements Serializable {

    private static final long serialVersionUID = 4814130213523644411L;

    /**
     * 构造方法
     * @param cartList 用户选中的购物车
     * @param paramDTO 前端提交的参数
     */
    public OrderSubmitDTO(List<Cart> cartList,OrderSubmitParamDTO paramDTO) {
        Map<Long/*storeId*/, List<Cart>> map = groupCartByStoreId(cartList);
        map.forEach((storeId,list) -> {
            this.orderInfoList.add(new OrderInfo(list,paramDTO));
        });
        this.integral = paramDTO.getIntegral();
        this.couponCode = paramDTO.getPlatformCouponCode();
        this.memberId = cartList.get(0).getMemberId();
    }


    /*-----------------前端传参---------------*/
    private Integer integral = 0;                                              //使用的积分数
    private String couponCode;                                                //优惠券编码（平台优惠券）
    private List<OrderInfo> orderInfoList = new ArrayList<>();                 //拆单后的订单信息列表
    private Integer memberId;                                               //会员id


    /*-----------------遍历计算---------------*/
    private BigDecimal totalAmount;                                       //所有订单总金额和（优惠后金额，不包含运费）
    private BigDecimal totalDiscount;                                     //活动优惠总额
    private Set<Integer> activityType;                                   //购物车选中的所有活动类型
    private List<PromotionCartGroup> promotionCartGroupList;  //订单货品按照活动分组,未参与活动的不分组


    /*-----------------活动模块计算---------------*/
    private List<CouponVO> availableCouponList = new ArrayList<>();         //可用的平台优惠券列表
    private List<CouponVO> disableCouponList = new ArrayList<>();         //不可用的平台优惠券列表
    private BigDecimal integralCashAmount;                                //积分抵扣金额

    //region -----------------------内部功能方法 -----------------------------------
    /**
     * 按照店铺id构造分组
     * @param cartList 用户选中的购物车
     * @return
     */
    private Map<Long,List<Cart>> groupCartByStoreId(List<Cart> cartList){
        Map<Long,List<Cart>> map = new HashMap<>();
        cartList.forEach(cart -> {
            if (map.containsKey(cart.getStoreId())){
                map.get(cart.getStoreId()).add(cart);
            }else {
                List<Cart> list = new ArrayList<>();
                list.add(cart);
                map.put(cart.getStoreId(),list);
            }
        });
        return map;
    }

    /**
     * 构造活动分组
     * @return
     */
    public List<PromotionCartGroup> getPromotionCartGroupList() {
        List<PromotionCartGroup> promotionCartGroupList = new ArrayList<>();
        Map<String, PromotionCartGroup> groupByPromotion = this.groupByPromotion();
        groupByPromotion.forEach((p,promotionGroup) -> {
            promotionCartGroupList.add(new PromotionCartGroup(promotionGroup.getCartList()));
        });
        return promotionCartGroupList;
    }

    /**
     * 将订单下的所有订单货品按照活动分组，用于计算满减类活动优惠
     * @return
     */
    private Map<String/*活动类型-活动id*/, PromotionCartGroup> groupByPromotion() {
        Map<String/*活动类型-活动id*/,PromotionCartGroup> mapByPromotionTypeAndId = new HashMap<>();
        this.orderInfoList.forEach(orderInfo -> {
            orderInfo.getOrderProductInfoList().forEach(orderProductInfo -> {
                if (!StringUtil.isNullOrZero(orderProductInfo.getPromotionType())){
                    //参与活动才分组
                    String key = orderProductInfo.getPromotionType() + "-" + orderProductInfo.getPromotionId();
                    if (mapByPromotionTypeAndId.containsKey(key)){
                        mapByPromotionTypeAndId.get(key).getCartList().add(orderProductInfo);
                    }else {
                        PromotionCartGroup promotionCartGroup = new PromotionCartGroup();
                        promotionCartGroup.setPromotionId(orderProductInfo.getPromotionId());
                        promotionCartGroup.setPromotionType(orderProductInfo.getPromotionType());
                        promotionCartGroup.getCartList().add(orderProductInfo);
                        mapByPromotionTypeAndId.put(key,promotionCartGroup);
                    }
                }
            });
        });
        return mapByPromotionTypeAndId;
    }
    //endregion


    //region ---------------遍历计算 start-----------------------------------------------------------------------------------
    /**
     * 计算所有订单总金额（优惠后金额，不包含运费）
     * @return
     */
    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = new BigDecimal("0.00");
        for (OrderInfo orderInfo : this.orderInfoList) {
            totalAmount = totalAmount.add(orderInfo.getTotalAmount());
        }
        return totalAmount.max(BigDecimal.ZERO);
    }

    /**
     * 计算所有订单活动总优惠额
     * @return
     */
    public BigDecimal getTotalDiscount() {
        BigDecimal totalDiscount = new BigDecimal("0.00");
        for (OrderInfo orderInfo : this.orderInfoList) {
            totalDiscount = totalDiscount.add(orderInfo.getTotalDiscount());
        }
        return totalDiscount;
    }

    /**
     * 查询购物车选中的所有活动类型
     * @return
     */
    public Set<Integer> getPromotionType() {
        Set<Integer> set = new HashSet<>();
        orderInfoList.forEach(orderInfo -> {
            orderInfo.getOrderProductInfoList().forEach(orderProductInfo -> {
                if (!StringUtil.isNullOrZero(orderProductInfo.getPromotionType())){
                    set.add(orderProductInfo.getPromotionType());
                }
            });
        });
        return set;
    }

    //endregion ---------------------------------------遍历计算 end --------------------------------------------------------

    /**
     * 订单信息，每个OrderInfo对应一个店铺的订单
     *
     */
    @Data
    @NoArgsConstructor
    public static class OrderInfo implements Serializable {
        private static final long serialVersionUID = 4238641044092507420L;

        /**
         * 构造订单信息
         * @param cartList 同一店铺内的购物车
         */
        public OrderInfo(List<Cart> cartList, OrderSubmitParamDTO dto) {
            this.storeId = cartList.get(0).getStoreId();
            this.storeName = cartList.get(0).getStoreName();
            if (dto.getStoreInfoByStoreId(storeId) != null) {
                this.voucherCode = dto.getStoreInfoByStoreId(storeId).getStoreCouponCode();
            }
            cartList.forEach(cart -> {
                this.orderProductInfoList.add(new OrderProductInfo(cart));
                this.goodsAmount = this.goodsAmount.add(cart.getProductPrice().multiply(new BigDecimal(cart.getBuyNum())));
            });
        }

        /*-----------------前端传参---------------*/
        private Long storeId;                                                    //店铺id
        private String storeName;                                                //店铺名称
        private String voucherCode;                                              //优惠券编码（店铺优惠券）
        private Integer orderType = OrderConst.ORDER_TYPE_1;                      //订单类型：1-普通订单；其他直接存活动类型（具体类型查看ActivityConst）
        private BigDecimal goodsAmount = new BigDecimal("0.00");             //商品金额，等于订单中所有的商品的单价乘以数量之和
        private List<OrderProductInfo> orderProductInfoList = new ArrayList<>();      //订单货品明细列表
        private Integer promotionId;                                            //促销活动id,立即购买时构造
        private Integer promotionType;                                          //促销活动类型,立即购买时构造


        /*-----------------遍历计算---------------*/
        private Integer integral;                                              //使用的积分数
        private BigDecimal integralCashAmount;                          //积分抵扣金额
        private List<PromotionInfo> promotionInfoList;                   //活动优惠明细列表
        private BigDecimal totalAmount;                                    //订单下所有货品的总金额（优惠后金额，不包含运费）
        private BigDecimal totalDiscount;                                  //活动优惠总额
        private BigDecimal storeVoucherAmount;                             //商家优惠券优惠金额
        private BigDecimal platformVoucherAmount;                          //平台优惠券优惠金额

        /*-----------------活动模块计算---------------*/
        private List<CouponVO> availableCouponList = new ArrayList<>();    //可用的店铺优惠券列表
        private List<CouponVO> disableCouponList = new ArrayList<>();    //不可用的店铺优惠券列表
        private BigDecimal voucherAmount;                                //店铺优惠券面额


        //region ---------------遍历计算 start-----------------------------------------------------------------------------------
        /**
         * 计算订单总金额（优惠后金额，不包含运费）
         * @return
         */
        public BigDecimal getTotalAmount() {
            BigDecimal totalAmount = new BigDecimal("0.00");
            for (OrderProductInfo orderProductInfo : this.orderProductInfoList) {
                totalAmount = totalAmount.add(orderProductInfo.getMoneyAmount());
            }
            return totalAmount.max(BigDecimal.ZERO);
        }

        /**
         * 计算订单活动总优惠额
         * @return
         */
        public BigDecimal getTotalDiscount() {
            BigDecimal totalDiscount = new BigDecimal("0.00");
            for (OrderProductInfo orderProductInfo : this.orderProductInfoList) {
                totalDiscount = totalDiscount.add(orderProductInfo.getTotalDiscount());
            }
            return totalDiscount;
        }

        /**
         * 计算订单使用积分
         * @return
         */
        public Integer getIntegral() {
            int integral = 0;
            for (OrderProductInfo orderProductInfo : this.orderProductInfoList) {
                integral += orderProductInfo.getIntegral();
            }
            return integral;
        }

        /**
         * 计算积分抵扣金额
         * @return
         */
        public BigDecimal getIntegralCashAmount() {
            BigDecimal integralCashAmount = new BigDecimal("0.00");
            for (OrderProductInfo orderProductInfo : orderProductInfoList) {
                integralCashAmount = integralCashAmount.add(orderProductInfo.getIntegralCashAmount());
            }
            return integralCashAmount;
        }

        /**
         * 构造订单使用的活动列表
         * 根据订单下所有货品的活动组合，构造订单使用的活动列表
         * 以 活动类型-活动id-是否店铺 作为key，相同的key，活动金额累加
         * @return
         */
        public List<PromotionInfo> getPromotionInfoList() {
            List<PromotionInfo> list = new ArrayList<>();
            Map<String/*活动类型-活动id-是否店铺*/,PromotionInfo> mapByPromotionType = new HashMap<>();
            orderProductInfoList.forEach(orderProductInfo -> {
                orderProductInfo.getPromotionInfoList().forEach(promotionInfo -> {
                    String key = promotionInfo.getPromotionType() + "-" + promotionInfo.getPromotionId() + "-" + promotionInfo.getIsStore();
                    if (mapByPromotionType.containsKey(key)){
                        //已有的活动类型，金额累加
                        mapByPromotionType.get(key).setDiscount(mapByPromotionType.get(key).getDiscount().add(promotionInfo.getDiscount()));
                    }else {
                        //不包含此活动类型，put
                        PromotionInfo promotionInfo1 = new PromotionInfo();
                        BeanUtils.copyProperties(promotionInfo,promotionInfo1);
                        mapByPromotionType.put(key,promotionInfo1);
                    }
                });
            });
            mapByPromotionType.keySet().forEach(promotionType -> {
                list.add(mapByPromotionType.get(promotionType));
            });
            return list;
        }

        /**
         * 获取店铺优惠券优惠额
         *
         * @return
         */
        public BigDecimal getStoreVoucherAmount() {
            BigDecimal storeVoucherAmount = new BigDecimal("0.00");
            for (OrderProductInfo orderProductInfo : this.orderProductInfoList) {
                storeVoucherAmount = storeVoucherAmount.add(orderProductInfo.getStoreVoucherAmount());
            }
            return storeVoucherAmount;
        }

        /**
         * 获取平台优惠券优惠额
         *
         * @return
         */
        public BigDecimal getPlatformVoucherAmount() {
            BigDecimal platformVoucherAmount = new BigDecimal("0.00");
            for (OrderProductInfo orderProductInfo : this.orderProductInfoList) {
                platformVoucherAmount = platformVoucherAmount.add(orderProductInfo.getPlatformVoucherAmount());
            }
            return platformVoucherAmount;
        }

        //endregion ---------------------------------------遍历计算 end --------------------------------------------------------



        /**
         * 订单货品信息
         */
        @EqualsAndHashCode(callSuper = true)
        @Data
        @NoArgsConstructor
        public static class OrderProductInfo extends Cart {
            private static final long serialVersionUID = 2571529321134033150L;

            public OrderProductInfo(Cart cart) {
                BeanUtils.copyProperties(cart,this);
            }

            /*-----------------前端传参---------------*/
            private Integer spellTeamId;                                                //拼团团队ID
            private Integer singlePromotionType;                                        //单品活动类型

            /*-----------------活动模块计算---------------*/
            private Integer integral = 0;                                              //使用的积分数
            private BigDecimal integralCashAmount = new BigDecimal("0.00");        //积分抵扣金额
            private List<PromotionInfo> promotionInfoList = new ArrayList<>();       //活动优惠明细列表

            /*-----------------遍历计算---------------*/
            private BigDecimal moneyAmount;                                          //订单货品明细金额（用户支付金额，发生退款时最高可退金额）（=货品单价*数量-活动优惠总额）
            private BigDecimal totalDiscount;                                        //活动优惠总额
            private BigDecimal storeActivityAmount;                                  //店铺活动优惠金额（包含优惠券）
            private BigDecimal platformActivityAmount;                               //平台活动优惠金额（包含积分、优惠券）
            private BigDecimal storeVoucherAmount;                                   //店铺优惠券优惠金额
            private BigDecimal platformVoucherAmount;                                //平台优惠券优惠金额

            //region--------------------------------------------------遍历计算 start -----------------------------------------------------------------------------------*/
            /**
             * 计算订单货品优惠后金额
             * @return
             */
            public BigDecimal getMoneyAmount() {
                return this.getProductPrice()
                        .multiply(new BigDecimal(this.getBuyNum()))
                        .subtract(getTotalDiscount())
                        .max(BigDecimal.ZERO);
            }

            /**
             * 计算优惠总额
             * @return
             */
            public BigDecimal getTotalDiscount() {
                BigDecimal totalDiscount = new BigDecimal("0.00");
                for (PromotionInfo promotionInfo : this.promotionInfoList) {
                    totalDiscount = totalDiscount.add(promotionInfo.getDiscount());
                }
                return totalDiscount;
            }

            /**
             * 店铺活动优惠金额（包含优惠券）
             * @return
             */
            public BigDecimal getStoreActivityAmount() {
                BigDecimal storeActivityAmount = new BigDecimal("0.00");
                for (PromotionInfo promotionInfo : this.promotionInfoList) {
                    if (promotionInfo.getIsStore()) {
                        storeActivityAmount = storeActivityAmount.add(promotionInfo.getDiscount());
                    }
                }
                return storeActivityAmount;
            }

            /**
             *平台活动优惠金额（包含积分、优惠券）
             * @return
             */
            public BigDecimal getPlatformActivityAmount() {
                BigDecimal platformActivityAmount = new BigDecimal("0.00");
                for (PromotionInfo promotionInfo : this.promotionInfoList) {
                    if (!promotionInfo.getIsStore()) {
                        platformActivityAmount = platformActivityAmount.add(promotionInfo.getDiscount());
                    }
                }
                return platformActivityAmount;
            }

            /**
             *店铺优惠券优惠金额
             * @return
             */
            public BigDecimal getStoreVoucherAmount() {
                BigDecimal storeVoucherAmount = new BigDecimal("0.00");
                for (PromotionInfo promotionInfo : this.promotionInfoList) {
                    if (promotionInfo.getIsStore()
                            && promotionInfo.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_402)) {
                        storeVoucherAmount = storeVoucherAmount.add(promotionInfo.getDiscount());
                    }
                }
                return storeVoucherAmount;
            }

            /**
             *平台优惠券优惠金额
             * @return
             */
            public BigDecimal getPlatformVoucherAmount() {
                BigDecimal platformVoucherAmount = new BigDecimal("0.00");
                for (PromotionInfo promotionInfo : this.promotionInfoList) {
                    if (!promotionInfo.getIsStore()
                            && promotionInfo.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_402)) {
                        platformVoucherAmount = platformVoucherAmount.add(promotionInfo.getDiscount());
                    }
                }
                return platformVoucherAmount;
            }

            //endregion--------------------------------------------------遍历计算 end -----------------------------------------------------------------------------------*/
        }

    }

    /**
     * 活动信息
     */
    @Data
    public static class PromotionInfo implements Serializable {
        private static final long serialVersionUID = -774800564028749867L;
        private Integer promotionType;             //活动类型
        private String promotionId;               //活动id，积分抵扣时为0,优惠券为优惠券code，其他为活动id
        private String promotionName;              //活动名称，由promotion模块写入
        private BigDecimal discount;               //优惠金额，由promotion模块写入
        private Boolean isStore;                   //是否店铺活动
        private Integer sendIntegral;              //赠送的积分数量
        private List<SendCoupon> sendCouponList = new ArrayList<>();      //赠送的优惠券id集合
        private List<SendProduct> sendProductList = new ArrayList<>();    //赠送的货品id集合

        /**
         * 赠送货品
         */
        @Data
        public static class SendProduct implements Serializable{
            private static final long serialVersionUID = -1871925987584505286L;
            @ApiModelProperty("赠品id")
            private Long productId;             //赠送货品id
            @ApiModelProperty("商品名称")
            private String goodsName;           //商品名称
            @ApiModelProperty("赠送数量")
            private Integer num = 1;              //赠送数量
        }

        /**
         * 赠送优惠券
         */
        @Data
        public static class SendCoupon implements Serializable{
            private static final long serialVersionUID = 1811766652146626650L;
            private Integer couponId;             //赠送优惠券id
            private Integer num = 1;              //赠送数量
        }
    }

    /**
     * 优惠券信息
     */
    @Data
    public static class CouponVO implements Serializable {
        private static final long serialVersionUID = 8142120691303314614L;
        @ApiModelProperty("会员优惠券ID")
        private Integer couponMemberId;
        @ApiModelProperty("优惠券id")
        private Integer couponId;
        @ApiModelProperty("优惠券编码")
        private String couponCode;
        @ApiModelProperty("优惠券名称")
        private String couponName;
        @ApiModelProperty("优惠券类型(1-固定金额券；2-折扣券（折扣比例）；3-随机金额券）")
        private Integer couponType;
        @ApiModelProperty("优惠券内容，满100-50、满100打5折等")
        private String content;
        @ApiModelProperty("优惠券使用说明")
        private String description;
        @ApiModelProperty("优惠券面值，￥100、5折等")
        private BigDecimal value;
        @ApiModelProperty("使用时间，格式为yyyy.MM.dd~yyyy.MM.dd")
        private String useTime;
        @ApiModelProperty("是否选中，true-选中")
        private Boolean checked = false;
        @ApiModelProperty("优惠金额，选中的优惠券计算优惠金额")
        private BigDecimal discount;
    }

    /**
     * 购物车活动分组
     * 每个店铺内的购物车按照活动类型-活动id分组
     */
    @Data
    @NoArgsConstructor
    public static class PromotionCartGroup implements Serializable {

        private static final long serialVersionUID = -6976307237843543834L;

        public PromotionCartGroup(List<OrderInfo.OrderProductInfo> carts) {
            this.promotionId = carts.get(0).getPromotionId();
            this.promotionType = carts.get(0).getPromotionType();
            this.cartList=carts;
            this.cartList.forEach(cart -> {
                this.goodsAmount = this.goodsAmount.add(cart.getProductPrice().multiply(new BigDecimal(cart.getBuyNum())));
                this.goodsNum += cart.getBuyNum();
            });
        }

        /*-----------------前端传参---------------*/
        private Integer promotionType;                                        //活动类型
        private Integer promotionId;                                          //活动id
        private Integer goodsNum = 0;                                      //总购买件数
        private BigDecimal goodsAmount = new BigDecimal("0.00");         //商品总金额，不包含优惠，不含运费
        private List<OrderInfo.OrderProductInfo> cartList = new ArrayList<>();                    //按活动分组的购物车列表

        /*-----------------遍历计算---------------*/
        private BigDecimal totalAmount;                       //分组总额（优惠后金额，不含运费）
        private BigDecimal totalDiscount;                     //活动优惠总金额

        /*-----------------活动计算---------------*/
        private Integer promotionSendIntegral = 0;                       //活动赠送的积分
        private List<Integer> promotionSendCoupons = new ArrayList<>();  //活动赠送的优惠券集合
        private List<Long> promotionSendProducts = new ArrayList<>();    //活动赠送的sku集合


        /*--------------------------------------------------遍历计算 start -----------------------------------------------------------------------------------*/
        /**
         * 计算总金额（优惠后金额，不包含运费）
         * @return
         */
        public BigDecimal getTotalAmount() {
            return this.goodsAmount.subtract(this.getTotalDiscount());
        }

        /**
         * 计算活动总优惠额
         * @return
         */
        public BigDecimal getTotalDiscount() {
            BigDecimal totalDiscount = new BigDecimal("0.00");
            for (Cart cart : this.cartList) {
                if (!StringUtil.isNullOrZero(cart.getOffPrice())){
                    totalDiscount = totalDiscount.add(cart.getOffPrice());
                }
            }
            return totalDiscount;
        }
        /*--------------------------------------------------遍历计算 end -----------------------------------------------------------------------------------*/
    }
}
