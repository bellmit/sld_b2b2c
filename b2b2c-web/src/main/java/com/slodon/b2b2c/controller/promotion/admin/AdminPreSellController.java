package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.PreSellConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.PresellGoodsModel;
import com.slodon.b2b2c.model.promotion.PresellModel;
import com.slodon.b2b2c.promotion.example.PresellExample;
import com.slodon.b2b2c.promotion.example.PresellGoodsExample;
import com.slodon.b2b2c.promotion.pojo.Presell;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import com.slodon.b2b2c.vo.promotion.PreSellDetailVO;
import com.slodon.b2b2c.vo.promotion.PreSellGoodsListVO;
import com.slodon.b2b2c.vo.promotion.PreSellVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-预售")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/preSell")
public class AdminPreSellController extends BaseController {

    @Resource
    private PresellModel presellModel;
    @Resource
    private PresellGoodsModel presellGoodsModel;
    @Resource
    private ProductModel productModel;

    @ApiOperation("预售列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "预售类型（1-定金预售，2-全款预售, 默认是1）", defaultValue = "1", required = true, paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "presellName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态(1-待发布;2-未开始;3-进行中;4-已失效;5-已结束)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<PreSellVO>> list(HttpServletRequest request, String storeName, String presellName,
                                              Date startTime, Date endTime, Integer state,
                                              @RequestParam(value = "type", required = false, defaultValue = "1") Integer type) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        PresellExample example = new PresellExample();
        example.setStoreNameLike(storeName);
        example.setPresellNameLike(presellName);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == PreSellConst.STATE_1) {
                example.setState(PreSellConst.ACTIVITY_STATE_1);
            } else if (state == PreSellConst.STATE_2) {
                example.setState(PreSellConst.ACTIVITY_STATE_2);
                example.setStartTimeAfter(new Date());
            } else if (state == PreSellConst.STATE_3) {
                example.setState(PreSellConst.ACTIVITY_STATE_2);
                example.setStartTimeBefore(new Date());
                example.setEndTimeAfter(new Date());
            } else if (state == PreSellConst.STATE_4) {
                example.setState(PreSellConst.ACTIVITY_STATE_3);
            } else if (state == PreSellConst.STATE_5) {
                example.setState(PreSellConst.ACTIVITY_STATE_2);
                example.setEndTimeBefore(new Date());
            }
        }
        example.setType(type);
        example.setStateNotEquals(PreSellConst.ACTIVITY_STATE_4);
        List<Presell> list = presellModel.getPresellList(example, pager);
        List<PreSellVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(presell -> {
                vos.add(new PreSellVO(presell));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("预售详情")
    @GetMapping("detail")
    public JsonResult<PreSellDetailVO> detail(HttpServletRequest request, Integer presellId) {
        AssertUtil.notNullOrZero(presellId, "预售活动id不能为空");

        Presell presell = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presell, "获取预售活动信息为空，请重试！");
        PreSellDetailVO detailVO = new PreSellDetailVO(presell);

        //查询预售活动商品信息
        String fields = "goods_id, goods_name, goods_image, product_price";
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(presellId);
        example.setGroupBy("goods_id, goods_name, goods_image, product_price");
        example.setOrderBy("product_price");
        List<PresellGoods> list = presellGoodsModel.getPresellGoodsListFieldsByExample(fields, example);
        List<PreSellDetailVO.PresellGoodsInfo> goodsList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (PresellGoods presellGoods : list) {
                PreSellDetailVO.PresellGoodsInfo goodsVO = new PreSellDetailVO.PresellGoodsInfo(presellGoods);

                PresellGoodsExample goodsExample = new PresellGoodsExample();
                goodsExample.setPresellId(presellId);
                goodsExample.setGoodsId(presellGoods.getGoodsId());
                List<PresellGoods> presellGoodsList = presellGoodsModel.getPresellGoodsList(goodsExample, null);
                List<PreSellDetailVO.PresellGoodsInfo.PreSellProductVO> productList = new ArrayList<>();
                for (PresellGoods goods : presellGoodsList) {
                    PreSellDetailVO.PresellGoodsInfo.PreSellProductVO productVO = new PreSellDetailVO.PresellGoodsInfo.PreSellProductVO(goods);
                    //查询sku库存
                    Product product = productModel.getProductByProductId(goods.getProductId());
                    AssertUtil.notNull(product, "获取货品品信息为空，请重试");
                    productVO.setStock(product.getProductStock());
                    productList.add(productVO);
                }
                goodsVO.setProductList(productList);

                goodsList.add(goodsVO);
            }
        }
        detailVO.setGoodsList(goodsList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("删除预售")
    @OperationLogger(option = "删除预售")
    @PostMapping("del")
    public JsonResult delPreSell(HttpServletRequest request, Integer presellId) {
        presellModel.deletePresell(presellId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("失效预售")
    @OperationLogger(option = "失效预售")
    @PostMapping("invalid")
    public JsonResult invalidPreSell(HttpServletRequest request, Integer presellId) {
        Presell presell = new Presell();
        presell.setPresellId(presellId);
        presell.setState(PreSellConst.ACTIVITY_STATE_3);
        presellModel.updatePresell(presell);
        return SldResponse.success("失效成功");
    }

    @ApiOperation("查看商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "预售活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query")
    })
    @GetMapping("goodList")
    public JsonResult<PageVO<PreSellGoodsListVO>> goodList(HttpServletRequest request, Integer presellId, String goodsName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(presellId);
        example.setGoodsNameLike(goodsName);
        List<PreSellGoodsListVO> list = presellGoodsModel.getPreSellGoodsList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

}
