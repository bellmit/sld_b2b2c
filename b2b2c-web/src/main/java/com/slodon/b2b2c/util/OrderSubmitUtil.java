package com.slodon.b2b2c.util;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitParamDTO;
import com.slodon.b2b2c.business.example.CartExample;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.CartConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PreSellConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.business.CartModel;
import com.slodon.b2b2c.model.business.OrderProductModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.*;
import com.slodon.b2b2c.promotion.example.*;
import com.slodon.b2b2c.promotion.pojo.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 提交订单构造工具
 */
@Component
public class OrderSubmitUtil {

    @Resource
    private CartModel cartModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private PresellModel presellModel;
    @Resource
    private PresellGoodsModel presellGoodsModel;
    @Resource
    private PresellOrderExtendModel presellOrderExtendModel;
    @Resource
    private LadderGroupGoodsModel ladderGroupGoodsModel;
    @Resource
    private LadderGroupOrderExtendModel ladderGroupOrderExtendModel;
    @Resource
    private SpellGoodsModel spellGoodsModel;

    /**
     * 根据前端参数获取后台操作的dto
     *
     * @param dto         前端参数
     * @param memberId    会员id
     * @param refreshCart 是否刷新购物车，true==刷新，检测购物车时不需要刷新
     * @return
     */
    public OrderSubmitDTO getOrderSubmitDTO(OrderSubmitParamDTO dto, Integer memberId, Boolean refreshCart) {
        List<Cart> cartList;
        Integer singlePromotionType = null;//需要单独提交订单的单品活动类型
        Integer singlePromotionId = null;//需要单独提交订单的单品活动id
        if (dto.getIsCart()) {
            //购物车结算,获取用户选中的购物车
            if (refreshCart) {
                cartModel.refreshCart(memberId);
            }
            CartExample cartExample = new CartExample();
            cartExample.setMemberId(memberId);
            cartExample.setIsChecked(CartConst.IS_CHECKED_YES);
            cartList = cartModel.getCartList(cartExample, null);
            AssertUtil.notEmpty(cartList, "请选择要购买的商品！");
        } else {
            //非购物车下单，立即购买或活动下单
            AssertUtil.isTrue(StringUtil.isNullOrZero(dto.getProductId()), "请选择要购买的商品");
            AssertUtil.isTrue(StringUtil.isNullOrZero(dto.getNumber()), "请选择要购买的数量");
            Product product = productModel.getProductByProductId(dto.getProductId());
            Cart cart = new Cart();
            cart.setMemberId(memberId);
            cart.setStoreId(product.getStoreId());
            cart.setStoreName(product.getStoreName());
            cart.setGoodsId(product.getGoodsId());
            cart.setGoodsName(product.getGoodsName());
            cart.setBuyNum(dto.getNumber());
            cart.setProductId(dto.getProductId());
            //单独购买普通商品
            if (dto.getIsAloneBuy()) {
                cart.setProductPrice(product.getProductPrice());
            } else {
                GoodsPromotion singlePromotion = cartModel.getSinglePromotion(product.getProductId());
                if (singlePromotion != null && promotionCommonModel.isPromotionAvailable(singlePromotion.getPromotionType(), singlePromotion.getPromotionId().toString())) {
                    if (singlePromotion.getPromotionType() == PromotionConst.PROMOTION_TYPE_102) {
                        //查询拼团活动类型
                        SpellGoodsExample example = new SpellGoodsExample();
                        example.setSpellId(singlePromotion.getPromotionId());
                        example.setProductId(dto.getProductId());
                        List<SpellGoods> spellGoodsList = spellGoodsModel.getSpellGoodsList(example, null);
                        AssertUtil.notEmpty(spellGoodsList, "阶梯团商品不存在");
                        SpellGoods spellGoods = spellGoodsList.get(0);
                        //团长开团
                        if (StringUtil.isNullOrZero(dto.getSpellTeamId()) && !StringUtil.isNullOrZero(spellGoods.getLeaderPrice())) {
                            cart.setProductPrice(spellGoods.getLeaderPrice());
                        } else {
                            cart.setProductPrice(spellGoods.getSpellPrice());
                        }
                    } else if (singlePromotion.getPromotionType() == PromotionConst.PROMOTION_TYPE_103) {
                        //查询预售活动类型
                        Presell presell = presellModel.getPresellByPresellId(singlePromotion.getPromotionId());
                        AssertUtil.notNull(presell, "预售活动不存在");
                        PresellGoodsExample example = new PresellGoodsExample();
                        example.setPresellId(singlePromotion.getPromotionId());
                        example.setProductId(dto.getProductId());
                        List<PresellGoods> presellGoodsList = presellGoodsModel.getPresellGoodsList(example, null);
                        AssertUtil.notEmpty(presellGoodsList, "预售商品不存在");
                        if (presell.getType() == PreSellConst.PRE_SELL_TYPE_1) {
                            //定金预售
                            cart.setProductPrice(presellGoodsList.get(0).getFirstMoney());
                        } else {
                            //全款预售
                            cart.setProductPrice(presellGoodsList.get(0).getPresellPrice());
                        }
                    } else if (singlePromotion.getPromotionType() == PromotionConst.PROMOTION_TYPE_105) {
                        //查询阶梯团活动类型
                        LadderGroupGoodsExample example = new LadderGroupGoodsExample();
                        example.setGroupId(singlePromotion.getPromotionId());
                        example.setProductId(dto.getProductId());
                        List<LadderGroupGoods> groupGoodsList = ladderGroupGoodsModel.getLadderGroupGoodsList(example, null);
                        AssertUtil.notEmpty(groupGoodsList, "阶梯团商品不存在");
                        cart.setProductPrice(groupGoodsList.get(0).getAdvanceDeposit());
                    } else {
                        if (!StringUtil.isNullOrZero(product.getActivityPrice())) {
                            //有单品活动，并且有活动价格
                            cart.setProductPrice(product.getActivityPrice());
                        }
                    }

                    //查询是否有单品活动
                    singlePromotionType = singlePromotion.getPromotionType();
                    singlePromotionId = singlePromotion.getPromotionId();
                } else {
                    cart.setProductPrice(product.getProductPrice());
                }
            }
            cart.setProductImage(product.getMainImage());
            cart.setSpecValueIds(product.getSpecValueIds());
            cart.setSpecValues(product.getSpecValues());
            cart.setIsChecked(CartConst.IS_CHECKED_YES);
            //获取单条购物车可参与的最优惠的活动
            cartModel.getBestPromotion(product, cart);
            cartList = Collections.singletonList(cart);

        }

        //构造计算优惠dto
        OrderSubmitDTO orderSubmitDTO = new OrderSubmitDTO(cartList, dto);
        if (singlePromotionType != null) {
            //单品活动下单
            for (OrderSubmitDTO.OrderInfo orderInfo : orderSubmitDTO.getOrderInfoList()) {
                orderInfo.setPromotionType(singlePromotionType);
                orderInfo.setPromotionId(singlePromotionId);
                if (promotionCommonModel.specialOrder(singlePromotionType)) {
                    //需要订单扩展信息
                    orderInfo.setOrderType(singlePromotionType);
                } else {
                    //普通订单
                    orderInfo.setOrderType(OrderConst.ORDER_TYPE_1);
                }
                for (OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductInfo : orderInfo.getOrderProductInfoList()) {
                    orderProductInfo.setSinglePromotionType(singlePromotionType);
                    if (!StringUtil.isNullOrZero(dto.getSpellTeamId())) {
                        orderProductInfo.setSpellTeamId(dto.getSpellTeamId());
                    }
                }
            }
        }
        return orderSubmitDTO;
    }

    /**
     * 根据前端参数获取后台操作的dto
     *
     * @param dto      前端参数
     * @param memberId 会员id
     * @param order    订单
     * @return
     */
    public OrderSubmitDTO getOrderSubmitDTO(OrderSubmitParamDTO dto, Integer memberId, Order order) {
        List<Cart> cartList;
        Integer singlePromotionType = null;//需要单独提交订单的单品活动类型
        Integer singlePromotionId = null;//需要单独提交订单的单品活动id

        //查询订单货品
        OrderProductExample orderProductExample = new OrderProductExample();
        orderProductExample.setOrderSn(dto.getOrderSn());
        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(orderProductExample, null);
        AssertUtil.notEmpty(orderProductList, "订单货品不存在");
        OrderProduct orderProduct = orderProductList.get(0);
        Product product = productModel.getProductByProductId(orderProduct.getProductId());
        Cart cart = new Cart();
        cart.setMemberId(memberId);
        cart.setStoreId(orderProduct.getStoreId());
        cart.setStoreName(orderProduct.getStoreName());
        cart.setGoodsId(orderProduct.getGoodsId());
        cart.setGoodsName(orderProduct.getGoodsName());
        cart.setBuyNum(orderProduct.getProductNum());
        cart.setProductId(orderProduct.getProductId());
        if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_103) {
            PresellOrderExtendExample example = new PresellOrderExtendExample();
            example.setOrderSn(dto.getOrderSn());
            List<PresellOrderExtend> orderExtendList = presellOrderExtendModel.getPresellOrderExtendList(example, null);
            AssertUtil.notEmpty(orderExtendList, "获取预售订单扩展信息为空");
            PresellOrderExtend orderExtend = orderExtendList.get(0);
            cart.setProductPrice(orderExtend.getPresellPrice());

            singlePromotionType = PromotionConst.PROMOTION_TYPE_103;
            singlePromotionId = orderExtend.getPresellId();
        } else if (order.getOrderType() == PromotionConst.PROMOTION_TYPE_105) {
            LadderGroupOrderExtendExample example = new LadderGroupOrderExtendExample();
            example.setOrderSn(dto.getOrderSn());
            List<LadderGroupOrderExtend> orderExtendList = ladderGroupOrderExtendModel.getLadderGroupOrderExtendList(example, null);
            AssertUtil.notEmpty(orderExtendList, "获取阶梯团订单扩展信息为空");
            LadderGroupOrderExtend orderExtend = orderExtendList.get(0);
            cart.setProductPrice(orderExtend.getRemainAmount());

            singlePromotionType = PromotionConst.PROMOTION_TYPE_105;
            singlePromotionId = orderExtend.getGroupId();
        }
        cart.setProductImage(orderProduct.getProductImage());
        cart.setSpecValues(orderProduct.getSpecValues());
        cart.setIsChecked(CartConst.IS_CHECKED_YES);
        //获取单条购物车可参与的最优惠的活动
        cartModel.getBestPromotion(product, cart);
        cartList = Collections.singletonList(cart);

        //构造计算优惠dto
        OrderSubmitDTO orderSubmitDTO = new OrderSubmitDTO(cartList, dto);
        if (singlePromotionType != null) {
            //单品活动下单
            for (OrderSubmitDTO.OrderInfo orderInfo : orderSubmitDTO.getOrderInfoList()) {
                orderInfo.setPromotionType(singlePromotionType);
                orderInfo.setPromotionId(singlePromotionId);
                if (promotionCommonModel.specialOrder(singlePromotionType)) {
                    //需要订单扩展信息
                    orderInfo.setOrderType(singlePromotionType);
                } else {
                    //普通订单
                    orderInfo.setOrderType(OrderConst.ORDER_TYPE_1);
                }
                for (OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductInfo : orderInfo.getOrderProductInfoList()) {
                    orderProductInfo.setSinglePromotionType(singlePromotionType);
                }
            }
        }
        return orderSubmitDTO;
    }

}
