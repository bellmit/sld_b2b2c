package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.example.CartExample;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.CartConst;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.business.CartReadMapper;
import com.slodon.b2b2c.dao.read.promotion.*;
import com.slodon.b2b2c.dao.write.business.CartWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsPromotionExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.PromotionCommonModel;
import com.slodon.b2b2c.promotion.example.PresellGoodsExample;
import com.slodon.b2b2c.promotion.example.SpellGoodsExample;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.vo.business.OrderSubmitCheckVO;
import com.slodon.b2b2c.vo.business.OrderSubmitPageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Component
@Slf4j
public class CartModel {

    @Resource
    private CartReadMapper cartReadMapper;
    @Resource
    private CartWriteMapper cartWriteMapper;
    @Resource
    private LadderGroupReadMapper ladderGroupReadMapper;
    @Resource
    private PresellReadMapper presellReadMapper;
    @Resource
    private PresellGoodsReadMapper presellGoodsReadMapper;
    @Resource
    private SpellReadMapper spellReadMapper;
    @Resource
    private SpellGoodsReadMapper spellGoodsReadMapper;

    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 购物车列表刷新用户购物车
     * 1.商品下架，货品禁用、删除、锁定（预售、拼团活动锁定sku）时，将购物车置为失效
     * 2.sku库存不足时，取消选中状态
     * 3.更新购物车货品其他信息
     *
     * @param memberId
     */
    public void refreshCart(Integer memberId) {
        CartExample cartExample = new CartExample();
        cartExample.setMemberId(memberId);

        List<Cart> cartList = cartReadMapper.listByExample(cartExample);
        if (CollectionUtils.isEmpty(cartList)) {
            return;
        }
        for (Cart cartDb : cartList) {//商品信息
            Goods goods = goodsModel.getGoodsByGoodsId(cartDb.getGoodsId());
            //查询货品信息
            Product product = productModel.getProductByProductId(cartDb.getProductId());
            if (!goods.getState().equals(GoodsConst.GOODS_STATE_UPPER)) {
                //商品下架，置为失效
                this.invalidCart(cartDb.getCartId());
                continue;
            }
            if (product.getState().equals(GoodsConst.PRODUCT_STATE_0) || product.getState().equals(GoodsConst.PRODUCT_STATE_2)) {
                //货品禁用或删除，置为失效
                this.invalidCart(cartDb.getCartId());
                continue;
            }
            if (product.getState().equals(GoodsConst.PRODUCT_STATE_3)) {
                //商品为锁定状态，查询商品活动信息
                GoodsPromotion singlePromotion = this.getSinglePromotion(product.getProductId());
                if (singlePromotion != null) {
                    if (singlePromotion.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_104)) {
                        //秒杀
                        if (promotionCommonModel.isPromotionAvailable(singlePromotion.getPromotionType(), product.getProductId().toString())) {
                            //秒杀活动进行中，置为失效
                            this.invalidCart(cartDb.getCartId());
                            continue;
                        }
                    }
                    //其他单品活动
                    if (promotionCommonModel.isPromotionAvailable(singlePromotion.getPromotionType(), singlePromotion.getPromotionId().toString())) {
                        //活动进行中，置为失效
                        this.invalidCart(cartDb.getCartId());
                        continue;
                    }
                }
            }
            if (cartDb.getBuyNum().compareTo(product.getProductStock()) > 0) {
                //2.sku库存不足时，取消选中状态
                if (!cartDb.getProductState().equals(CartConst.STATTE_INVALID)) {
                    Cart update = new Cart();
                    update.setCartId(cartDb.getCartId());
                    update.setProductState(CartConst.STATTE_LACK);
                    update.setUpdateTime(new Date());
                    update.setIsChecked(CartConst.IS_CHECKED_NO);
                    cartWriteMapper.updateByPrimaryKeySelective(update);
                    continue;
                }
            }
            if (!cartDb.getProductState().equals(CartConst.STATTE_NORMAL)) {
                //修改为正常状态
                Cart update = new Cart();
                update.setCartId(cartDb.getCartId());
                update.setProductState(CartConst.STATTE_NORMAL);
                update.setUpdateTime(new Date());
                update.setIsChecked(CartConst.IS_CHECKED_NO);
                cartWriteMapper.updateByPrimaryKeySelective(update);
                continue;
            }

            if (cartDb.getIsChecked() == CartConst.IS_CHECKED_YES) {
                //3.更新购物车货品其他信息（货品单价、满减等活动信息）
                boolean changed = false;//购物车信息是否发生改变
                if (cartDb.getProductPrice().compareTo(product.getProductPrice()) != 0) {
                    //货品单价发生改变
                    cartDb.setProductPrice(product.getProductPrice());
                    changed = true;
                }
                if (!StringUtil.isNullOrZero(cartDb.getPromotionType())
                        && !promotionCommonModel.isPromotionAvailable(cartDb.getPromotionType(), cartDb.getPromotionId().toString())) {
                    //选中的购物车参与了活动并且活动失效,重新选择活动
                    this.getBestPromotion(product, cartDb);
                    changed = true;
                }
                if (changed) {
                    this.updateCart(cartDb);
                }
            }

        }
    }

    /**
     * 将购物车置为失效
     *
     * @param cartId
     */
    private void invalidCart(Integer cartId) {
        Cart update = new Cart();
        update.setCartId(cartId);
        update.setProductState(CartConst.STATTE_INVALID);
        update.setUpdateTime(new Date());
        update.setIsChecked(CartConst.IS_CHECKED_NO);
        cartWriteMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 订单确认、订单提交前检测购物车状态
     *
     * @param dto
     * @return
     */
    public OrderSubmitCheckVO checkCart(OrderSubmitDTO dto) {
        OrderSubmitCheckVO vo = new OrderSubmitCheckVO();
        List<OrderSubmitDTO.OrderInfo.OrderProductInfo> cartList = new ArrayList<>();//所有购物车列表
        dto.getOrderInfoList().forEach(orderInfo -> {
            cartList.addAll(orderInfo.getOrderProductInfoList());
        });

        Map<Long/*goodsId*/, Goods> goodsMap = new HashMap<>();//存放商品的map，减少查库次数
        Map<Long/*skuId*/, Product> productMap = new HashMap<>();//存放货品的map，减少查库次数

        //检测下架
        cartList.forEach(cart -> {
            Goods goods = this.getGoodsByGoodsId(goodsMap, cart.getGoodsId());
            if (!goods.getState().equals(GoodsConst.GOODS_STATE_UPPER)) {
                //商品已下架
                vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
            } else {
                //商品未下架，查看货品状态
                Product product = this.getProductByProductId(productMap, cart.getProductId());
                if (product.getState().equals(GoodsConst.PRODUCT_STATE_0) || product.getState().equals(GoodsConst.PRODUCT_STATE_2)) {
                    //删除或禁用
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                } else if (product.getState().equals(GoodsConst.PRODUCT_STATE_3)) {
                    //锁定状态
                    GoodsPromotion singlePromotion = this.getSinglePromotion(product.getProductId());
                    if (singlePromotion != null && cart.getCartId() != null) {
                        //购物车下单，如果活动正在进行则置为下架
                        if (singlePromotion.getPromotionType().equals(PromotionConst.PROMOTION_TYPE_104)) {
                            //秒杀
                            if (promotionCommonModel.isPromotionAvailable(singlePromotion.getPromotionType(), product.getProductId().toString())) {
                                //秒杀活动进行中，置为失效
                                vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                            }
                        } else if (promotionCommonModel.isPromotionAvailable(singlePromotion.getPromotionType(), singlePromotion.getPromotionId().toString())) {
                            //其他单品活动
                            //活动进行中，置为失效
                            vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                        }
                    }
                }
            }
        });
        if (!CollectionUtils.isEmpty(vo.getProductList())) {
            //检测出有下架货品
            if (vo.getProductList().size() == cartList.size()) {
                //全部下架
                vo.setState(OrderSubmitCheckVO.STATE_1);
            } else {
                //部分下架
                vo.setState(OrderSubmitCheckVO.STATE_2);
            }
            return vo;
        }

        //库存检测
        cartList.forEach(cart -> {
            if (cart.getSinglePromotionType() != null && cart.getSinglePromotionType().equals(PromotionConst.PROMOTION_TYPE_104)) {
                //有正在进行的秒杀，检测秒杀库存
                if (!this.checkSeckillStock(cart.getProductId(), cart.getBuyNum())) {
                    //库存不足
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                }
            } else if (cart.getSinglePromotionType() != null && cart.getSinglePromotionType().equals(PromotionConst.PROMOTION_TYPE_103)) {
                //有正在进行的预售，检测预售库存
                if (!this.checkPreSellStock(cart.getPromotionId(), cart.getProductId(), cart.getBuyNum())) {
                    //库存不足
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                }
            } else if (cart.getSinglePromotionType() != null && cart.getSinglePromotionType().equals(PromotionConst.PROMOTION_TYPE_102)) {
                //有正在进行的拼团，检测拼团库存
                if (!this.checkSpellStock(cart.getPromotionId(), cart.getProductId(), cart.getBuyNum())) {
                    //库存不足
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                }
            } else {
                Product product = this.getProductByProductId(productMap, cart.getProductId());
                Integer totalStock = product.getProductStock();

                //查询是否有未开始的秒杀
                String stockStr = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + cart.getProductId());
                if (stockStr != null) {
                    totalStock = totalStock - Integer.parseInt(stockStr);
                }

                if (totalStock.compareTo(cart.getBuyNum()) < 0) {
                    //库存不足
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                }
            }
        });
        if (!CollectionUtils.isEmpty(vo.getProductList())) {
            //检测出有库存不足货品
            if (vo.getProductList().size() == cartList.size()) {
                //全部无货
                vo.setState(OrderSubmitCheckVO.STATE_3);
            } else {
                //部分无货
                vo.setState(OrderSubmitCheckVO.STATE_4);
            }
            return vo;
        }

        //限购检测
        cartList.forEach(cart -> {
            if (cart.getSinglePromotionType() != null && cart.getSinglePromotionType().equals(PromotionConst.PROMOTION_TYPE_102)) {
                //有正在进行的拼团，检测拼团限购
                if (!this.checkSpellLimit(dto.getOrderInfoList().get(0).getPromotionId(), cart.getGoodsId(), cart.getMemberId(), cart.getBuyNum())) {
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                }
            }
            if (cart.getSinglePromotionType() != null && cart.getSinglePromotionType().equals(PromotionConst.PROMOTION_TYPE_103)) {
                //有正在进行的预售，检测预售限购
                if (!this.checkPreSellLimit(dto.getOrderInfoList().get(0).getPromotionId(), cart.getGoodsId(), cart.getMemberId(), cart.getBuyNum())) {
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                }
            }
            if (cart.getSinglePromotionType() != null && cart.getSinglePromotionType().equals(PromotionConst.PROMOTION_TYPE_104)) {
                //有正在进行的秒杀，检测秒杀限购
                if (!this.checkSeckillLimit(cart.getProductId(), cart.getMemberId(), cart.getBuyNum())) {
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                }
            }
            if (cart.getSinglePromotionType() != null && cart.getSinglePromotionType().equals(PromotionConst.PROMOTION_TYPE_105)) {
                //有正在进行的阶梯团，检测阶梯团限购
                if (!this.checkLadderGroupLimit(dto.getOrderInfoList().get(0).getPromotionId(), cart.getGoodsId(), cart.getMemberId(), cart.getBuyNum())) {
                    vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
                }
            }
        });
        if (!CollectionUtils.isEmpty(vo.getProductList())) {
            //检测出有限购的货品
            vo.setState(OrderSubmitCheckVO.STATE_6);
            return vo;
        }

        //活动检测
        cartList.forEach(cart -> {
            if (!StringUtil.isNullOrZero(cart.getPromotionType())
                    && !promotionCommonModel.isPromotionAvailable(cart.getPromotionType(), cart.getPromotionId().toString())) {
                //参与了活动并且活动失效
                vo.getProductList().add(new OrderSubmitPageVO.StoreGroupVO.ProductVO(cart));
            }
        });
        if (!CollectionUtils.isEmpty(vo.getProductList())) {
            //检测出有活动失效货品
            vo.setState(OrderSubmitCheckVO.STATE_5);
            return vo;
        }

        return vo;
    }

    /**
     * 拼团活动检测商品库存
     *
     * @param promotionId 活动id
     * @param productId   货品id
     * @param buyNum      此次购买数量
     * @return true==可以购买
     */
    private Boolean checkSpellStock(Integer promotionId, Long productId, Integer buyNum) {
        //剩余库存
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(promotionId);
        example.setProductId(productId);
        List<SpellGoods> goodsList = spellGoodsReadMapper.listByExample(example);
        //没有说明是脏数据，不处理
        if (CollectionUtils.isEmpty(goodsList)) {
            return true;
        }
        Integer spellStock = goodsList.get(0).getSpellStock();
        //购买数量大于库存
        return spellStock != null && buyNum.compareTo(spellStock) <= 0;
    }

    /**
     * 预售活动检测商品库存
     *
     * @param promotionId 活动id
     * @param productId   货品id
     * @param buyNum      此次购买数量
     * @return true==可以购买
     */
    private Boolean checkPreSellStock(Integer promotionId, Long productId, Integer buyNum) {
        //剩余库存
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(promotionId);
        example.setProductId(productId);
        List<PresellGoods> goodsList = presellGoodsReadMapper.listByExample(example);
        //没有说明是脏数据，不处理
        if (CollectionUtils.isEmpty(goodsList)) {
            return true;
        }
        Integer presellStock = goodsList.get(0).getPresellStock();
        //购买数量大于库存
        return presellStock != null && buyNum.compareTo(presellStock) <= 0;
    }

    /**
     * 秒杀活动，从reids中检测商品库存
     *
     * @param productId 货品id
     * @param buyNum    此次购买数量
     * @return true==可以购买
     */
    private Boolean checkSeckillStock(Long productId, Integer buyNum) {
        //剩余库存
        String stockStr = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + productId);
        //购买数量大于库存
        return stockStr != null && buyNum.compareTo(Integer.parseInt(stockStr)) <= 0;
    }

    /**
     * 校验拼团活动限购
     *
     * @param promotionId
     * @param goodsId
     * @param memberId
     * @param buyNum
     * @return
     */
    private Boolean checkSpellLimit(Integer promotionId, Long goodsId, Integer memberId, Integer buyNum) {
        //限购数量
        Spell spell = spellReadMapper.getByPrimaryKey(promotionId);
        AssertUtil.notNull(spell, "获取拼团活动信息为空，请重试！");
        if (spell.getBuyLimit() == 0) {
            //不限购
            return true;
        }
        if (buyNum.compareTo(spell.getBuyLimit()) > 0) {
            //购买数量大于限购数量
            return false;
        }
        //会员已购数量
        String memberAlreadyBuyNumStr = stringRedisTemplate.opsForValue().get(RedisConst.SPELL_PURCHASED_NUM_PREFIX + goodsId + "_" + memberId);
        if (StringUtils.isEmpty(memberAlreadyBuyNumStr)) {
            //会员未购买过
            return true;
        }
        //判断此次购买后是否超过限购数量
        return Integer.parseInt(memberAlreadyBuyNumStr) + buyNum <= spell.getBuyLimit();
    }

    /**
     * 校验预售活动限购
     *
     * @param promotionId
     * @param goodsId
     * @param memberId
     * @param buyNum
     * @return
     */
    private Boolean checkPreSellLimit(Integer promotionId, Long goodsId, Integer memberId, Integer buyNum) {
        //限购数量
        Presell presell = presellReadMapper.getByPrimaryKey(promotionId);
        AssertUtil.notNull(presell, "获取预售活动信息为空，请重试！");
        if (presell.getBuyLimit() == 0) {
            //不限购
            return true;
        }
        if (buyNum.compareTo(presell.getBuyLimit()) > 0) {
            //购买数量大于限购数量
            return false;
        }
        //会员已购数量
        String memberAlreadyBuyNumStr = stringRedisTemplate.opsForValue().get(RedisConst.PRE_SELL_PURCHASED_NUM_PREFIX + goodsId + "_" + memberId);
        if (StringUtils.isEmpty(memberAlreadyBuyNumStr)) {
            //会员未购买过
            return true;
        }
        //判断此次购买后是否超过限购数量
        return Integer.parseInt(memberAlreadyBuyNumStr) + buyNum <= presell.getBuyLimit();
    }

    /**
     * 秒杀活动，从redis中检测商品限购
     *
     * @param productId
     * @param memberId
     * @param buyNum
     * @return
     */
    private Boolean checkSeckillLimit(Long productId, Integer memberId, Integer buyNum) {
        //限购数量
        String buyLimitStr = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_PRODUCT_BUY_LIMIT_PREFIX + productId);
        if (StringUtils.isEmpty(buyLimitStr) || Integer.parseInt(buyLimitStr) == 0) {
            //不限购
            return true;
        }
        if (buyNum.compareTo(Integer.valueOf(buyLimitStr)) > 0) {
            //购买数量大于限购数量
            return false;
        }
        //会员已购数量
        String memberAlreadyBuyNumStr = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_MEMBER_BUY_NUM_PREFIX + productId + "_" + memberId);
        if (StringUtils.isEmpty(memberAlreadyBuyNumStr)) {
            //会员未购买过
            return true;
        }
        //判断此次购买后是否超过限购数量
        return Integer.parseInt(memberAlreadyBuyNumStr) + buyNum <= Integer.parseInt(buyLimitStr);
    }

    /**
     * 校验阶梯团活动限购
     *
     * @param promotionId
     * @param goodsId
     * @param memberId
     * @param buyNum
     * @return
     */
    private Boolean checkLadderGroupLimit(Integer promotionId, Long goodsId, Integer memberId, Integer buyNum) {
        //限购数量
        LadderGroup ladderGroup = ladderGroupReadMapper.getByPrimaryKey(promotionId);
        AssertUtil.notNull(ladderGroup, "获取阶梯团活动信息为空，请重试！");
        if (ladderGroup.getBuyLimitNum() == 0) {
            //不限购
            return true;
        }
        if (buyNum.compareTo(ladderGroup.getBuyLimitNum()) > 0) {
            //购买数量大于限购数量
            return false;
        }
        //会员已购数量
        String memberAlreadyBuyNumStr = stringRedisTemplate.opsForValue().get(RedisConst.LADDER_GROUP_PURCHASED_NUM_PREFIX + goodsId + "_" + memberId);
        if (StringUtils.isEmpty(memberAlreadyBuyNumStr)) {
            //会员未购买过
            return true;
        }
        //判断此次购买后是否超过限购数量
        return Integer.parseInt(memberAlreadyBuyNumStr) + buyNum <= ladderGroup.getBuyLimitNum();
    }

    /**
     * 根据商品id获取商品，从map中取商品或者读取数据库
     *
     * @param goodsMap
     * @param goodsId
     * @return
     */
    private Goods getGoodsByGoodsId(Map<Long, Goods> goodsMap, Long goodsId) {
        if (goodsMap.containsKey(goodsId)) {
            return goodsMap.get(goodsId);
        }
        Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
        goodsMap.put(goodsId, goods);
        return goods;
    }

    /**
     * 根据skuId获取货品，从map中取商品或者读取数据库
     *
     * @param productMap
     * @param productId
     * @return
     */
    private Product getProductByProductId(Map<Long, Product> productMap, Long productId) {
        if (productMap.containsKey(productId)) {
            return productMap.get(productId);
        }
        Product product = productModel.getProductByProductId(productId);
        productMap.put(productId, product);
        return product;
    }

    /**
     * 新增商城购物车
     *
     * @param cart
     * @return
     */
    public Integer saveCart(Cart cart) {
        int count = cartWriteMapper.insert(cart);
        if (count == 0) {
            throw new MallException("添加商城购物车失败，请重试");
        }
        return count;
    }

    /**
     * 根据cartId删除商城购物车
     *
     * @param cartId cartId
     * @return
     */
    public Integer deleteCart(Integer cartId) {
        if (StringUtils.isEmpty(cartId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = cartWriteMapper.deleteByPrimaryKey(cartId);
        if (count == 0) {
            log.error("根据cartId：" + cartId + "删除商城购物车失败");
            throw new MallException("删除商城购物车失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件删除商城购物车
     *
     * @param example example
     * @return
     */
    public void deleteCartByExample(CartExample example) {
        cartWriteMapper.deleteByExample(example);
    }

    /**
     * 根据cartId更新商城购物车
     *
     * @param cart
     * @return
     */
    public Integer updateCart(Cart cart) {
        if (StringUtils.isEmpty(cart.getCartId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = cartWriteMapper.updateByPrimaryKeySelective(cart);
        if (count == 0) {
            log.error("根据cartId：" + cart.getCartId() + "更新商城购物车失败");
            throw new MallException("更新商城购物车失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件更新商城购物车
     *
     * @param cart
     * @param cartExample
     * @return
     */
    public void updateCartByExample(Cart cart, CartExample cartExample) {
        cartWriteMapper.updateByExampleSelective(cart, cartExample);
    }

    /**
     * 根据cartId获取商城购物车详情
     *
     * @param cartId cartId
     * @return
     */
    public Cart getCartByCartId(Integer cartId) {
        return cartReadMapper.getByPrimaryKey(cartId);
    }

    /**
     * 根据条件获取商城购物车列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Cart> getCartList(CartExample example, PagerInfo pager) {
        List<Cart> cartList;
        if (pager != null) {
            pager.setRowsCount(cartReadMapper.countByExample(example));
            cartList = cartReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            cartList = cartReadMapper.listByExample(example);
        }
        return cartList;
    }

    /**
     * 添加到购物车
     *
     * @param product  货品明细
     * @param number   购买数量
     * @param memberId 会员id
     */
    public void addToCart(Product product, Integer number, Integer memberId) {
        //查询购物车中是否已经有当前货品
        CartExample cartExample = new CartExample();
        cartExample.setProductId(product.getProductId());
        cartExample.setMemberId(memberId);
        List<Cart> cartList = cartReadMapper.listByExample(cartExample);
        if (CollectionUtils.isEmpty(cartList)) {
            //没有当前货品，新增购物车
            this.newCart(product, number, memberId);
        } else {
            //有当前货品的购物车，修改数量
            Cart updateCart = new Cart();
            updateCart.setCartId(cartList.get(0).getCartId());
            updateCart.setIsChecked(CartConst.IS_CHECKED_YES);//选中
            updateCart.setUpdateTime(new Date());
            updateCart.setBuyNum(cartList.get(0).getBuyNum() + number);//数量相加
            updateCart.setProductState(CartConst.STATTE_NORMAL);
            //更新价格
            updateCart.setProductPrice(product.getProductPrice());
            this.updateCart(updateCart);
        }
    }

    /**
     * 新增购物车
     *
     * @param product  货品明细
     * @param number   购买数量
     * @param memberId 会员id
     */
    private void newCart(Product product, Integer number, Integer memberId) {
        Cart cart = new Cart();
        cart.setMemberId(memberId);
        cart.setStoreId(product.getStoreId());
        cart.setStoreName(product.getStoreName());
        cart.setGoodsId(product.getGoodsId());
        cart.setGoodsName(product.getGoodsName());
        cart.setProductId(product.getProductId());
        cart.setBuyNum(number);
        //优先取活动价
        cart.setProductPrice(product.getProductPrice());
        cart.setProductImage(product.getMainImage());
        cart.setSpecValueIds(product.getSpecValueIds());
        cart.setSpecValues(product.getSpecValues());
        cart.setIsChecked(CartConst.IS_CHECKED_YES);
        cart.setUpdateTime(new Date());
        cart.setProductState(CartConst.STATTE_NORMAL);
        //获取单条购物车可参与的最优惠的活动
        this.getBestPromotion(product, cart);
        this.saveCart(cart);
    }

    /**
     * 获取单条购物车可参与的最优惠的活动
     *
     * @param product
     * @param cart
     */
    public void getBestPromotion(Product product, Cart cart) {
        //查询当前商品参与的活动
        List<GoodsPromotion> promotionList = this.getPromotionList(product);
        if (!CollectionUtils.isEmpty(promotionList)) {
            BigDecimal maxDiscount = new BigDecimal("-1.00");//最大优惠
            GoodsPromotion bestPromotion = null;
            //查询最优惠的活动
            for (GoodsPromotion goodsPromotion : promotionList) {
                cart.setPromotionId(goodsPromotion.getPromotionId());
                cart.setPromotionType(goodsPromotion.getPromotionType());
                BigDecimal currentDiscount = promotionCommonModel.calculationCartDiscount(cart);
                if (maxDiscount.compareTo(currentDiscount) < 0) {
                    //更优惠的活动
                    maxDiscount = currentDiscount;
                    bestPromotion = goodsPromotion;
                }
            }
            if (bestPromotion == null) {
                bestPromotion = promotionList.get(0);
            }
            log.debug("---------------------------------------------------------------------------");
            log.debug("默认最优活动：type-" + bestPromotion.getPromotionType() + ",id-" + bestPromotion.getPromotionId());
            log.debug("---------------------------------------------------------------------------");
            cart.setPromotionType(bestPromotion.getPromotionType());
            cart.setPromotionId(bestPromotion.getPromotionId());
            cart.setPromotionDescription(bestPromotion.getDescription());
        } else {
            cart.setPromotionType(0);
            cart.setPromotionId(0);
            cart.setPromotionDescription("");
        }
    }

    /**
     * 获取当前货品参与的单品活动
     *
     * @param productId
     * @return
     */
    public GoodsPromotion getSinglePromotion(Long productId) {
        //查询商品级别的活动
        GoodsPromotionExample example = new GoodsPromotionExample();
        example.setBindType(PromotionConst.BIND_TYPE_1);
        example.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
        example.setProductId(productId);
        List<GoodsPromotion> singlePromotionList = goodsPromotionModel.getGoodsPromotionList(example, null);
        if (!CollectionUtils.isEmpty(singlePromotionList)) {
            GoodsPromotion goodsPromotion = singlePromotionList.get(0);
            String promotionId = "";
            if (goodsPromotion.getPromotionType() == PromotionConst.PROMOTION_TYPE_104) {
                //秒杀活动传货品id
                promotionId = goodsPromotion.getProductId().toString();
            } else {
                promotionId = goodsPromotion.getPromotionId().toString();
            }
            if (promotionCommonModel.isPromotionAvailable(goodsPromotion.getPromotionType(), promotionId)) {
                //活动可用
                return goodsPromotion;
            }
        }
        //无可用的单品活动
        return null;
    }

    /**
     * 获取当前货品可用的活动列表（店铺活动、平台活动）
     *
     * @param product
     * @return
     */
    public List<GoodsPromotion> getPromotionList(Product product) {
        List<GoodsPromotion> list = new ArrayList<>();
        //查询店铺活动
        GoodsPromotionExample example = new GoodsPromotionExample();
        example.setBindType(PromotionConst.BIND_TYPE_2);
        example.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
        example.setStoreId(product.getStoreId());
        List<GoodsPromotion> storeList = goodsPromotionModel.getGoodsPromotionList(example, null);
        if (!CollectionUtils.isEmpty(storeList)) {
            storeList.forEach(goodsPromotion -> {
                if (promotionCommonModel.isPromotionAvailable(goodsPromotion.getPromotionType(), goodsPromotion.getPromotionId().toString())) {
                    //活动可用
                    list.add(goodsPromotion);
                }
            });
        }

        //查询平台活动
        example.setStoreId(null);
        example.setBindType(PromotionConst.BIND_TYPE_3);
        example.setGoodsCategoryId3(product.getCategoryId3());
        List<GoodsPromotion> platformList = goodsPromotionModel.getGoodsPromotionList(example, null);
        if (!CollectionUtils.isEmpty(platformList)) {
            platformList.forEach(goodsPromotion -> {
                if (promotionCommonModel.isPromotionAvailable(goodsPromotion.getPromotionType(), goodsPromotion.getPromotionId().toString())) {
                    //活动可用
                    list.add(goodsPromotion);
                }
            });
        }
        return list;
    }

    /**
     * 登录时添加离线购物车信息
     *
     * @param cartList
     * @param memberId
     * @return
     */
    public void addCartByMemberId(List<Cart> cartList, Integer memberId) {
        if (!CollectionUtils.isEmpty(cartList)) {
            for (Cart cart : cartList) {
                Product product = productModel.getProductByProductId(cart.getProductId());
                AssertUtil.notNull(product, "获取商品sku信息为空");
                this.addToCart(product, cart.getBuyNum(), memberId);
            }
        }
    }
}