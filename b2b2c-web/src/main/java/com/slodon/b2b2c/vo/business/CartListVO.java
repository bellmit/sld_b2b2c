package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.CartConst;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车列表vo
 */
@Data
@NoArgsConstructor
public class CartListVO {

    public CartListVO(OrderConfirmDTO dto, List<Cart> invalidCartList) {
        dto.getStoreCartGroupList().forEach(storeCartGroup -> {
            storeCartGroupList.add(new StoreCartGroup(storeCartGroup));
            storeCartGroup.getPromotionCartGroupList().forEach(promotionCartGroup -> {
                promotionCartGroup.getCartList().forEach(cart -> {
                    if (cart.getIsChecked() == CartConst.IS_CHECKED_YES) {
                        totalAmount = totalAmount.add(cart.getProductPrice().multiply(new BigDecimal(cart.getBuyNum())));
                        if (!StringUtil.isNullOrZero(cart.getOffPrice())) {
                            totalDiscount = totalDiscount.add(cart.getOffPrice());
                        }
                    }
                });
            });
        });
        totalAmount = totalAmount.subtract(totalDiscount);
        storeCartGroupList.forEach(storeCartGroup -> {
            checkedAll = checkedAll && storeCartGroup.getCheckedAll();
            lackAll = lackAll && storeCartGroup.getLackAll();
        });
        if (!CollectionUtils.isEmpty(invalidCartList)) {
            invalidCartList.forEach(cart -> {
                invalidList.add(new CartVO(cart));
            });
        }
    }


    @ApiModelProperty("合计金额")
    private BigDecimal totalAmount = new BigDecimal("0.00");
    @ApiModelProperty("总优惠额")
    private BigDecimal totalDiscount = new BigDecimal("0.00");
    @ApiModelProperty("选中的购物车总数")
    private Integer totalCheck = 0;
    @ApiModelProperty("有效的购物车列表，按照店铺展示")
    private List<StoreCartGroup> storeCartGroupList = new ArrayList<>();
    @ApiModelProperty("失效的购物车列表，商品下架")
    private List<CartVO> invalidList = new ArrayList<>();
    @ApiModelProperty("是否全部选中")
    private Boolean checkedAll = true;
    @ApiModelProperty("是否全部无货")
    private Boolean lackAll = true;
    @ApiModelProperty("有效的购物车个数")
    private Integer availableCartNum;

    /**
     * 按照店铺分组的购物车列表
     */
    @Data
    public static class StoreCartGroup {
        public StoreCartGroup(OrderConfirmDTO.StoreCartGroup storeCartGroup) {
            totalAmount = storeCartGroup.getTotalAmount();
            totalDiscount = storeCartGroup.getTotalDiscount();
            storeId = storeCartGroup.getStoreId();
            storeName = storeCartGroup.getStoreName();
            hasCoupon = storeCartGroup.getHasCoupon();

            storeCartGroup.getPromotionCartGroupList().forEach(promotionCartGroup -> {
                promotionCartGroupList.add(new PromotionCartGroup(promotionCartGroup));
            });
            for (PromotionCartGroup promotionCartGroup : promotionCartGroupList) {
                if (!checkedAll && !lackAll) {
                    break;
                }
                for (CartVO cartVO : promotionCartGroup.getCartList()) {
                    if (cartVO.getIsChecked().equals(CartConst.IS_CHECKED_NO)) {
                        checkedAll = false;
                    }
                    if (cartVO.getProductState() == CartConst.STATTE_NORMAL) {
                        lackAll = false;
                    }
                    if (!checkedAll && !lackAll) {
                        break;
                    }
                }
            }
        }


        @ApiModelProperty("店铺小计金额")
        private BigDecimal totalAmount = new BigDecimal("0.00");
        @ApiModelProperty("店铺内优惠额")
        private BigDecimal totalDiscount = new BigDecimal("0.00");
        @ApiModelProperty("店铺id,平台时id为0")
        private Long storeId;
        @ApiModelProperty("店铺名称，平台时取")
        private String storeName;
        @ApiModelProperty("店铺内按照活动分组的购物车列表")
        private List<PromotionCartGroup> promotionCartGroupList = new ArrayList<>();
        @ApiModelProperty("是否全部选中")
        private Boolean checkedAll = true;
        @ApiModelProperty("是否全部无货")
        private Boolean lackAll = true;
        @ApiModelProperty("是否有店铺可用优惠券")
        private Boolean hasCoupon;

        /**
         * 按照活动分组的购物车列表
         */
        @Data
        public static class PromotionCartGroup {
            public PromotionCartGroup(OrderConfirmDTO.StoreCartGroup.PromotionCartGroup promotionCartGroup) {
                promotionType = promotionCartGroup.getPromotionType();
                promotionId = promotionCartGroup.getPromotionId();
                promotionDes = promotionCartGroup.getPromotionDes();
                totalAmount = promotionCartGroup.getTotalAmount();
                totalDiscount = promotionCartGroup.getTotalDiscount();
                promotionCartGroup.getCartList().forEach(cart -> {
                    this.cartList.add(new CartVO(cart));
                });
            }

            @ApiModelProperty("活动类型,无活动为0")
            private Integer promotionType;
            @ApiModelProperty("活动id，无活动为0")
            private Integer promotionId;
            @ApiModelProperty("活动描述，无活动为空")
            private String promotionDes;
            @ApiModelProperty("活动小计金额")
            private BigDecimal totalAmount = new BigDecimal("0.00");
            @ApiModelProperty("活动优惠额")
            private BigDecimal totalDiscount = new BigDecimal("0.00");
            @ApiModelProperty("购物车列表")
            private List<CartVO> cartList = new ArrayList<>();
        }

    }

    /**
     * 购物车VO
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class CartVO extends Cart {
        private static final long serialVersionUID = -7576991409031324282L;

        public CartVO(Cart cart) {
            BeanUtils.copyProperties(cart, this);
            this.setProductImage(FileUrlUtil.getFileUrl(cart.getProductImage(), ImageSizeEnum.SMALL));
        }

        @ApiModelProperty("货品库存")
        private Integer productStock;
        @ApiModelProperty("可参与的活动列表")
        private List<Promotion> promotionList = new ArrayList<>();


        /**
         * 活动信息
         */
        @Data
        @AllArgsConstructor
        public static class Promotion {
            public Promotion(GoodsPromotion goodsPromotion) {
                promotionType = goodsPromotion.getPromotionType();
                promotionId = goodsPromotion.getPromotionId();
                promotionDes = goodsPromotion.getDescription();
            }

            @ApiModelProperty("活动类型")
            private Integer promotionType;
            @ApiModelProperty("活动id")
            private Integer promotionId;
            @ApiModelProperty("活动描述")
            private String promotionDes;
        }
    }
}
