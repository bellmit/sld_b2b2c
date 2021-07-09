/**
 * Copyright@Slodon since 2015, All rights reserved.
 * <p>
 * 注意：
 * 本软件为北京商联达科技有限公司开发研制，未经许可不得使用
 * 购买后可获得源代码使用权（禁止转卖、分享、上传到码云、github等开源平台）
 * 一经发现盗用、分享等行为，将追究法律责任，后果自负
 * 商业使用请联系: bd@slodon.com 或 拨打全国统一热线 400-881-0877
 * 网址：http://www.slodon.com
 */
package com.slodon.b2b2c.controller.business.front;

import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.example.CartExample;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.constant.CartConst;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.CartModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.member.MemberFollowProductModel;
import com.slodon.b2b2c.model.promotion.PromotionCommonModel;
import com.slodon.b2b2c.vo.business.CartListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-购物车管理")
@RestController
@RequestMapping("v3/business/front/cart")
public class FrontCartController {

    @Resource
    private CartModel cartModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private MemberFollowProductModel memberFollowProductModel;

    @PostMapping("add")
    @ApiOperation("添加购物车接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "skuId", required = true, paramType = "query"),
            @ApiImplicitParam(name = "number", value = "购买数量，默认为1", defaultValue = "1", paramType = "query")
    })
    public JsonResult addCart(HttpServletRequest request, Long productId, Integer number) {
        Member member = UserUtil.getUser(request, Member.class);

        //查询商品
        Product product = productModel.getProductByProductId(productId);
        AssertUtil.notNull(product, "货品不存在");
        AssertUtil.isTrue(!product.getState().equals(GoodsConst.PRODUCT_STATE_1) && !product.getState().equals(GoodsConst.PRODUCT_STATE_3), "货品不存在！");

        //查询商品
        Goods goods = goodsModel.getGoodsByGoodsId(product.getGoodsId());
        AssertUtil.notNull(goods, "商品不存在");
        AssertUtil.isTrue(!goods.getState().equals(GoodsConst.GOODS_STATE_UPPER), "该商品已下架");

        //库存判断
        AssertUtil.isTrue(product.getProductStock() < number, "库存不足");

        //添加购物车
        cartModel.addToCart(product, number, member.getMemberId());

        return SldResponse.success("已加入购物车");
    }


    @GetMapping("cartList")
    @ApiOperation("购物车列表接口")
    public JsonResult<CartListVO> cartList(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);
        CartListVO vo = this.getCartList(member.getMemberId());

        return SldResponse.success(vo);
    }


    @PostMapping("changeNum")
    @ApiOperation("修改购物车数量接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartId", value = "购物车id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "number", value = "修改后数量", required = true, paramType = "query")
    })
    public JsonResult<CartListVO> changeNum(HttpServletRequest request, Integer cartId, Integer number) {
        Member member = UserUtil.getUser(request, Member.class);
        CartExample cartExample = new CartExample();
        cartExample.setMemberId(member.getMemberId());
        cartExample.setCartIdIn(cartId + "");
        Cart cart = new Cart();
        cart.setBuyNum(number);
        cartModel.updateCartByExample(cart, cartExample);

        //刷新购物车列表
        CartListVO vo = this.getCartList(member.getMemberId());

        return SldResponse.success(vo);
    }


    @PostMapping("deleteCarts")
    @ApiOperation("批量删除购物车接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartIds", value = "购物车id集合", required = true, paramType = "query")
    })
    public JsonResult<CartListVO> deleteCarts(HttpServletRequest request, String cartIds) {

        //参数校验
        AssertUtil.notEmpty(cartIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(cartIds, "cartIds格式错误,请重试");

        Member member = UserUtil.getUser(request, Member.class);
        CartExample cartExample = new CartExample();
        cartExample.setMemberId(member.getMemberId());
        cartExample.setCartIdIn(cartIds);
        cartModel.deleteCartByExample(cartExample);

        //刷新购物车列表
        CartListVO vo = this.getCartList(member.getMemberId());

        return SldResponse.success(vo);
    }


    @PostMapping("emptyInvalid")
    @ApiOperation("清空失效购物车")
    public JsonResult<CartListVO> emptyInvalid(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);
        CartExample cartExample = new CartExample();
        cartExample.setProductState(CartConst.STATTE_INVALID);
        cartExample.setMemberId(member.getMemberId());
        cartModel.deleteCartByExample(cartExample);

        //刷新购物车列表
        CartListVO vo = this.getCartList(member.getMemberId());

        return SldResponse.success(vo);
    }

    @PostMapping("checkedCarts")
    @ApiOperation("修改购物车选中状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartIds", value = "购物车id集合", required = true, paramType = "query"),
            @ApiImplicitParam(name = "checked", value = "是否选中：0=全不选、1=全选中", required = true, paramType = "query")
    })
    public JsonResult<CartListVO> checkedCarts(HttpServletRequest request, String cartIds, Integer checked) {

        //参数校验
        AssertUtil.notEmpty(cartIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(cartIds, "cartIds格式错误,请重试");
        Member member = UserUtil.getUser(request, Member.class);
        CartExample cartExample = new CartExample();
        cartExample.setMemberId(member.getMemberId());
        cartExample.setCartIdIn(cartIds);
        cartExample.setProductState(CartConst.STATTE_NORMAL);
        Cart cart = new Cart();
        cart.setIsChecked(checked);
        cartModel.updateCartByExample(cart, cartExample);

        //刷新购物车列表
        CartListVO vo = this.getCartList(member.getMemberId());

        return SldResponse.success(vo);
    }

    @PostMapping("invertChecked")
    @ApiOperation("购物车反选接口")
    public JsonResult<CartListVO> invertChecked(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);

        CartExample cartExample = new CartExample();
        cartExample.setMemberId(member.getMemberId());
        cartExample.setProductState(CartConst.STATTE_NORMAL);
        List<Cart> cartList = cartModel.getCartList(cartExample, null);
        if (!CollectionUtils.isEmpty(cartList)) {
            cartList.forEach(cartDb -> {
                Cart update = new Cart();
                update.setCartId(cartDb.getCartId());
                update.setIsChecked(Math.abs(cartDb.getIsChecked() - 1));
                cartModel.updateCart(update);
            });
        }


        //刷新购物车列表
        CartListVO vo = this.getCartList(member.getMemberId());

        return SldResponse.success(vo);
    }

    @PostMapping("changePromotion")
    @ApiOperation("修改购物车参与活动接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartId", value = "购物车id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "promotionId", value = "活动id，0表示不参与活动", required = true, paramType = "query"),
            @ApiImplicitParam(name = "promotionType", value = "活动类型", required = true, paramType = "query"),
            @ApiImplicitParam(name = "promotionDescription", value = "活动描述", required = true, paramType = "query")
    })
    public JsonResult<CartListVO> changePromotion(HttpServletRequest request, Integer cartId, Integer promotionId,
                                                  Integer promotionType, String promotionDescription) {
        Member member = UserUtil.getUser(request, Member.class);

        CartExample cartExample = new CartExample();
        cartExample.setMemberId(member.getMemberId());
        cartExample.setCartIdIn(cartId + "");
        Cart cart = new Cart();
        cart.setPromotionType(promotionType);
        cart.setPromotionId(promotionId);
        cart.setPromotionDescription(promotionDescription);
        cartModel.updateCartByExample(cart, cartExample);

        //刷新购物车列表
        CartListVO vo = this.getCartList(member.getMemberId());

        return SldResponse.success(vo);
    }

    @PostMapping("moveToCollection")
    @ApiOperation("移入收藏夹接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cartIds", value = "购物车id集合", required = true, paramType = "query"),
    })
    public JsonResult<CartListVO> moveToCollection(HttpServletRequest request, String cartIds) {

        //参数校验
        AssertUtil.notEmpty(cartIds, "请选择要移入收藏夹的cartIds");
        AssertUtil.notFormatFrontIds(cartIds, "cartIds格式错误,请重试");

        Member member = UserUtil.getUser(request, Member.class);

        CartExample cartExample = new CartExample();
        cartExample.setMemberId(member.getMemberId());
        cartExample.setCartIdIn(cartIds);
        List<Cart> cartList = cartModel.getCartList(cartExample, null);
        if (!CollectionUtils.isEmpty(cartList)) {
            StringBuilder productIds = new StringBuilder();
            cartList.forEach(cart -> {
                productIds.append(",").append(cart.getProductId());
            });
            // 收藏
            memberFollowProductModel.editMemberFollowProduct(productIds.toString().substring(1), true, member.getMemberId());
            //收藏完毕删除购物车
            cartModel.deleteCartByExample(cartExample);
        }


        //刷新购物车列表
        CartListVO vo = this.getCartList(member.getMemberId());

        return SldResponse.success(vo);
    }


    /**
     * 获取用户购物车列表
     *
     * @param memberId 用户id
     * @return
     */
    private CartListVO getCartList(Integer memberId) {
        //刷新购物车
        cartModel.refreshCart(memberId);

        //查询购物车列表
        CartExample cartExample = new CartExample();
        cartExample.setMemberId(memberId);
        cartExample.setOrderBy("update_time DESC");
        List<Cart> cartList = cartModel.getCartList(cartExample, null);
        if (CollectionUtils.isEmpty(cartList)) {
            return new CartListVO();
        }

        List<Cart> availableList = new ArrayList<>(); //有效购物车
        List<Cart> invalidList = new ArrayList<>(); // 无效购物车
        cartList.forEach(cart -> {
            if (cart.getProductState().equals(CartConst.STATTE_INVALID)) {
                invalidList.add(cart);
            } else {
                availableList.add(cart);
            }
        });

        //构造dto计算活动优惠
        OrderConfirmDTO dto = new OrderConfirmDTO(availableList);
        dto = promotionCommonModel.orderConfirmCalculationDiscount(dto);

        //构造返回数据
        CartListVO vo = new CartListVO(dto, invalidList);

        //有效的购物车个数
        int availableCartNum = 0;
        //查询有效购物车的库存和可参与的活动
        for (CartListVO.StoreCartGroup storeCartGroup : vo.getStoreCartGroupList()) {
            for (CartListVO.StoreCartGroup.PromotionCartGroup promotionCartGroup : storeCartGroup.getPromotionCartGroupList()) {
                availableCartNum += promotionCartGroup.getCartList().size();
                promotionCartGroup.getCartList().forEach(cartVO -> {
                    Product product = productModel.getProductByProductId(cartVO.getProductId());
                    //货品可参与的活动
                    List<GoodsPromotion> promotionList = cartModel.getPromotionList(product);

                    cartVO.setProductStock(product.getProductStock());
                    if (!CollectionUtils.isEmpty(promotionList)) {
                        promotionList.forEach(goodsPromotion -> {
                            cartVO.getPromotionList().add(new CartListVO.CartVO.Promotion(goodsPromotion));
                        });
                        //增加一个不参与活动的选项
                        cartVO.getPromotionList().add(new CartListVO.CartVO.Promotion(0, 0, "不参与任何优惠活动"));
                    }
                    if (cartVO.getIsChecked().equals(CartConst.IS_CHECKED_YES)) {
                        vo.setTotalCheck(vo.getTotalCheck() + 1);//选中的购物车数量
                    }
                });
            }
        }
        vo.setAvailableCartNum(availableCartNum);
        return vo;
    }


}
