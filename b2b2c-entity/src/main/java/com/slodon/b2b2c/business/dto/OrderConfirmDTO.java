package com.slodon.b2b2c.business.dto;

import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.CartConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.util.StringUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单结算页dto
 * 用于订单结算页数据分组，计算活动优惠
 *
 * 注：此类只用作订单模块与活动模块计算活动优惠的参数传递，不能用于提交订单接口接收前端参数
 */
@Data
@NoArgsConstructor
public class OrderConfirmDTO implements Serializable {

    private static final long serialVersionUID = -3009427219111085863L;

    /**
     * 构造方法
     * @param cartList 不包括失效状态的购物车列表
     */
    public OrderConfirmDTO(List<Cart> cartList) {
        groupCartByStoreId(cartList).forEach((storeId,list) -> {
            //循环一次为一个店铺
            this.storeCartGroupList.add(new StoreCartGroup(list));
        });
    }

    /**
     * 按照店铺id构造分组，如果购物车选择了平台活动，则自动组成店铺id为0的分组
     * @param cartList 用户的购物车
     * @return
     */
    private Map<Long,List<Cart>> groupCartByStoreId(List<Cart> cartList){
        Map<Long,List<Cart>> map = new LinkedHashMap<>();
        cartList.forEach(cart -> {
            Long storeId = cart.getStoreId();
            if (!StringUtil.isNullOrZero(cart.getPromotionType())
                    && PromotionConst.isPlatformPromotion(cart.getPromotionType())){
                //平台活动，店铺id设为0
                storeId = 0L;
            }
            if (map.containsKey(storeId)){
                map.get(storeId).add(cart);
            }else {
                List<Cart> list = new ArrayList<>();
                list.add(cart);
                map.put(storeId,list);
            }
        });
        return map;
    }

    /*-----------------前端传参---------------*/
    private List<StoreCartGroup> storeCartGroupList = new ArrayList<>();


    /*-----------------遍历计算---------------*/
    private BigDecimal totalAmount = new BigDecimal("0.00");             //购物车总额（优惠后金额，不含运费）
    private BigDecimal totalDiscount = new BigDecimal("0.00");            //活动优惠总金额


    /*--------------------------------------------------遍历计算 start -----------------------------------------------------------------------------------*/
    /**
     * 计算总金额（优惠后金额，不包含运费）
     * @return
     */
    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = new BigDecimal("0.00");
        for (StoreCartGroup storeCartGroup : this.storeCartGroupList) {
            totalAmount = totalAmount.add(storeCartGroup.getTotalAmount());
        }
        return totalAmount;
    }

    /**
     * 计算活动总优惠额
     * @return
     */
    public BigDecimal getTotalDiscount() {
        BigDecimal totalDiscount = new BigDecimal("0.00");
        for (StoreCartGroup storeCartGroup : this.storeCartGroupList) {
            totalDiscount = totalDiscount.add(storeCartGroup.getTotalDiscount());
        }
        return totalDiscount;
    }
    /*--------------------------------------------------遍历计算 end -----------------------------------------------------------------------------------*/


    /**
     * 购物车店铺分组
     * 根据店铺分组的购物车列表信息
     */
    @Data
    @NoArgsConstructor
    public static class StoreCartGroup  implements Serializable {

        private static final long serialVersionUID = 3602374391883780136L;

        public StoreCartGroup(List<Cart> cartList) {
            this.storeId = cartList.get(0).getStoreId();
            this.storeName = cartList.get(0).getStoreName();
            groupCartByPromotion(cartList).forEach((key,list) -> {
                //循环一次为一个活动
                this.promotionCartGroupList.add(new PromotionCartGroup(list));
            });
        }

        /**
         * 将购物车按照活动分组
         * @param cartList 同店铺的购物车列表
         * @return
         */
        private Map<String,List<Cart>> groupCartByPromotion(List<Cart> cartList){
            Map<String/*活动类型-活动id*/,List<Cart>> map = new HashMap<>();
            cartList.forEach(cart -> {
                if (cart.getProductState().equals(CartConst.STATTE_INVALID)){
                    //购物车失效，将活动置为空
                    cart.setPromotionType(null);
                }
                String key = StringUtil.isNullOrZero(cart.getPromotionType())
                        ? "0-0" : (cart.getPromotionType() + "-" + cart.getPromotionId());
                if (map.containsKey(key)){
                    map.get(key).add(cart);
                }else {
                    List<Cart> list = new ArrayList<>();
                    list.add(cart);
                    map.put(key,list);
                }
            });
            return map;
        }


        /*-----------------前端传参---------------*/
        private Long storeId;                                        //店铺id，平台活动对应店铺id为0
        private String storeName;                                       //店铺名称
        private List<PromotionCartGroup> promotionCartGroupList = new ArrayList<>();      //按活动分组的购物车列表

        /*-----------------遍历计算---------------*/
        private BigDecimal totalAmount = new BigDecimal("0.00");                       //分组总额（优惠后金额，不含运费）
        private BigDecimal totalDiscount = new BigDecimal("0.00");                     //活动优惠总金额

        /*-----------------活动计算---------------*/
        private Boolean hasCoupon;                       //店铺是否有可领的优惠券
        /*--------------------------------------------------遍历计算 start -----------------------------------------------------------------------------------*/
        /**
         * 计算总金额（优惠后金额，不包含运费）
         * @return
         */
        public BigDecimal getTotalAmount() {
            BigDecimal totalAmount = new BigDecimal("0.00");
            for (PromotionCartGroup promotionCartGroup : this.promotionCartGroupList) {
                totalAmount = totalAmount.add(promotionCartGroup.getTotalAmount());
            }
            return totalAmount;
        }

        /**
         * 计算活动总优惠额
         * @return
         */
        public BigDecimal getTotalDiscount() {
            BigDecimal totalDiscount = new BigDecimal("0.00");
            for (PromotionCartGroup promotionCartGroup : this.promotionCartGroupList) {
                totalDiscount = totalDiscount.add(promotionCartGroup.getTotalDiscount());
            }
            return totalDiscount;
        }
        /*--------------------------------------------------遍历计算 end -----------------------------------------------------------------------------------*/


        /**
         * 购物车活动分组
         * 每个店铺内的购物车按照活动类型-活动id分组
         */
        @Data
        @NoArgsConstructor
        public static class PromotionCartGroup implements Serializable {

            private static final long serialVersionUID = 4143831881803904394L;

            public PromotionCartGroup(List<Cart> cartList) {
                this.promotionId = cartList.get(0).getPromotionId();
                this.promotionType = cartList.get(0).getPromotionType();
                this.cartList.addAll(cartList);
                this.cartList.forEach(cart -> {
                    if (cart.getIsChecked() == CartConst.IS_CHECKED_YES) {
                        this.goodsNum += cart.getBuyNum();
                        this.goodsAmount = this.goodsAmount.add(cart.getProductPrice().multiply(new BigDecimal(cart.getBuyNum())));
                    }
                });
            }

            /*-----------------前端传参---------------*/
            private Integer promotionType;                                        //活动类型
            private Integer promotionId;                                          //活动id
            private Integer goodsNum = 0;                                      //总购买件数
            private BigDecimal goodsAmount = new BigDecimal("0.00");         //商品总金额，不包含优惠，不含运费
            private List<Cart> cartList = new ArrayList<>();                    //按活动分组的购物车列表

            /*-----------------遍历计算---------------*/
            private BigDecimal totalAmount;                       //分组总额（优惠后金额，不含运费）
            private BigDecimal totalDiscount;                     //活动优惠总金额

            /*-----------------活动计算---------------*/
            private Integer promotionSendIntegral = 0;                       //活动赠送的积分
            private List<Integer> promotionSendCoupons = new ArrayList<>();  //活动赠送的优惠券集合
            private List<Long> promotionSendProducts = new ArrayList<>();    //活动赠送的sku集合
            private String promotionDes;                         //活动优惠描述


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
}
