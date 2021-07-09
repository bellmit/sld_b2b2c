package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.GoodsSelectVO;
import com.slodon.b2b2c.vo.goods.ProductSelectVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 此接口适用于所有活动选择商品列表
 */
@Api(tags = "seller-选择商品列表")
@RestController
@Slf4j
@RequestMapping("v3/goods/seller/goodsSeckill")
public class GoodsSelectController extends BaseController {

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("获取商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("goodsList")
    public JsonResult<PageVO<GoodsSelectVO>> getGoodsList(HttpServletRequest request,
                                                          @RequestParam(value = "goodsName", required = false) String goodsName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setGoodsNameLike(goodsName);
        goodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
        goodsExample.setStoreId(vendor.getStoreId());
        List<Goods> goodsList = goodsModel.getGoodsList(goodsExample, pager);
        List<GoodsSelectVO> vos = new ArrayList();
        if (!CollectionUtils.isEmpty(goodsList)) {
            for (Goods goods : goodsList) {
                GoodsSelectVO vo = new GoodsSelectVO(goods);
                if (goods.getIsLock() == GoodsConst.IS_LOCK_YES) {
                    vo.setActivityState(GoodsConst.GOOD_ACTIVITY_STATE_2);
                    vo.setActivityDesc("已参加其他活动");
                } else {
                    //默认商品图片处理
                    if (StringUtils.isEmpty(goods.getMainImage())) {
                        goods.setMainImage(stringRedisTemplate.opsForValue().get("default_image_goods"));
                    }
                    //获取货品列表信息
                    ProductExample productExample = new ProductExample();
                    productExample.setGoodsId(goods.getGoodsId());
                    productExample.setStateIn(GoodsConst.PRODUCT_STATE_1 + "," + GoodsConst.PRODUCT_STATE_3);
                    productExample.setStoreId(vendor.getStoreId());
                    List<Product> productList = productModel.getProductList(productExample, null);
                    List<ProductSelectVO> productVOList = new ArrayList<>();
                    for (Product product : productList) {
                        //没有参与秒杀活动，判断是否参与其他活动，如果参与，则不能选择
                        if (product.getState() == GoodsConst.PRODUCT_STATE_3) {
                            //已参与其他活动，不能选
                            vo.setActivityState(GoodsConst.GOOD_ACTIVITY_STATE_2);
                            vo.setActivityDesc("已参加其他活动");
                        } else if (product.getState() == GoodsConst.PRODUCT_STATE_1) {
                            //未参加其他活动，可选
                            vo.setActivityState(GoodsConst.GOOD_ACTIVITY_STATE_1);
                            vo.setActivityDesc(null);
                        }
                        ProductSelectVO productVO = new ProductSelectVO(product);
                        productVOList.add(productVO);
                    }
                    vo.setSeckillProductVOList(productVOList);
                }
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
