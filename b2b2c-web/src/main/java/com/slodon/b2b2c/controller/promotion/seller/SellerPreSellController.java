package com.slodon.b2b2c.controller.promotion.seller;

import com.alibaba.fastjson.JSONArray;
import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.PreSellConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.PresellGoodsModel;
import com.slodon.b2b2c.model.promotion.PresellLabelModel;
import com.slodon.b2b2c.model.promotion.PresellModel;
import com.slodon.b2b2c.promotion.dto.PreSellAddDTO;
import com.slodon.b2b2c.promotion.dto.PreSellUpdateDTO;
import com.slodon.b2b2c.promotion.example.PresellExample;
import com.slodon.b2b2c.promotion.example.PresellGoodsExample;
import com.slodon.b2b2c.promotion.example.PresellLabelExample;
import com.slodon.b2b2c.promotion.pojo.Presell;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import com.slodon.b2b2c.promotion.pojo.PresellLabel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-预售")
@RestController
@Slf4j
@RequestMapping("v3/promotion/seller/preSell")
public class SellerPreSellController extends BaseController {

    @Resource
    private PresellModel presellModel;
    @Resource
    private PresellGoodsModel presellGoodsModel;
    @Resource
    private PresellLabelModel presellLabelModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("预售列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "预售类型（1-定金预售，2-全款预售, 默认是1）", defaultValue = "1", required = true, paramType = "query"),
            @ApiImplicitParam(name = "presellName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态(1-待发布;2-未开始;3-进行中;4-已失效;5-已结束)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<PreSellVO>> list(HttpServletRequest request, String presellName, Date startTime, Date endTime, Integer state,
                                              @RequestParam(value = "type", required = false, defaultValue = "1") Integer type) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        PresellExample example = new PresellExample();
        example.setStoreId(vendor.getStoreId());
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "预售id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<PreSellDetailVO> detail(HttpServletRequest request, Integer presellId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(presellId, "预售id不能为空");

        Presell presell = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presell, "获取预售活动信息为空，请重试！");
        AssertUtil.isTrue(!presell.getStoreId().equals(vendor.getStoreId()), "无权限");
        PreSellDetailVO detailVO = new PreSellDetailVO(presell);

        //查询预售活动商品信息
        String fields = "goods_id, goods_name, goods_image";
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(presellId);
        example.setGroupBy(fields);
        example.setOrderBy("goods_id");
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

    @ApiOperation("预售活动标签列表")
    @GetMapping("labelList")
    public JsonResult<List<PreSellLabelVO>> labelList() {
        PresellLabelExample example = new PresellLabelExample();
        example.setIsShow(PreSellConst.IS_SHOW_1);
        example.setOrderBy("sort asc, create_time desc");
        List<PresellLabel> list = presellLabelModel.getPresellLabelList(example, null);
        List<PreSellLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(label -> {
                vos.add(new PreSellLabelVO(label));
            });
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("选择商品下的货品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "商品id集合，用逗号隔开", required = true, paramType = "query")
    })
    @GetMapping("productList")
    public JsonResult productList(@RequestParam(value = "goodsIds", required = false) String goodsIds) {
        String[] split = goodsIds.split(",");
        List<PreSellGoodsVO> vos = new ArrayList<>();
        for (String s : split) {
            if (StringUtils.isEmpty(s)) {
                continue;
            }
            Long goodsId = Long.valueOf(s);
            // 查询商品信息
            Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
            AssertUtil.notNull(goods, "获取商品信息为空，请重试");

            // 查询货品信息
            ProductExample example = new ProductExample();
            example.setGoodsId(goodsId);
            List<Product> productList = productModel.getProductList(example, null);
            AssertUtil.notEmpty(productList, "获取商品下的货品信息为空，请重试");

            List<PreSellProductVO> productVOS = new ArrayList<>();
            productList.forEach(product -> {
                productVOS.add(new PreSellProductVO(product));
            });
            PreSellGoodsVO vo = new PreSellGoodsVO(goods);
            vo.setProductList(productVOS);
            vos.add(vo);
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("新建预售")
    @VendorLogger(option = "新建预售")
    @PostMapping("add")
    public JsonResult addPreSell(HttpServletRequest request, @RequestBody PreSellAddDTO preSellAddDTO) throws Exception {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("预售模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNull(preSellAddDTO.getDeliverTime(), "发货时间不能为空");
        AssertUtil.isTrue(StringUtil.isEmpty(preSellAddDTO.getGoodsList()), "预售商品信息不能为空");

        String goodsList = preSellAddDTO.getGoodsList();
        List<PresellGoods> preSellGoodsList = JSONArray.parseArray(goodsList, PresellGoods.class);

        Presell presell = new Presell();
        BeanUtils.copyProperties(preSellAddDTO, presell);
        if (preSellAddDTO.getType() == PreSellConst.PRE_SELL_TYPE_1) {
            AssertUtil.isTrue(StringUtils.isEmpty(preSellAddDTO.getRemainStartTime()), "支付尾款的开始时间不能为空");
            AssertUtil.isTrue(StringUtils.isEmpty(preSellAddDTO.getRemainEndTime()), "支付尾款的结束时间不能为空");
            presell.setDeliverStartTime(TimeUtil.countApartDay(preSellAddDTO.getDeliverTime(), preSellAddDTO.getRemainEndTime()));
        } else {
            presell.setDeliverStartTime(TimeUtil.countApartDay(preSellAddDTO.getDeliverTime(), preSellAddDTO.getEndTime()));
        }
        presell.setStoreId(vendor.getStoreId());
        presell.setStoreName(vendor.getStore().getStoreName());
        presell.setCreateVendorId(vendor.getVendorId());
        presellModel.savePresell(presell, preSellGoodsList);
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑预售")
    @VendorLogger(option = "编辑预售")
    @PostMapping("update")
    public JsonResult updatePreSell(HttpServletRequest request, @RequestBody PreSellUpdateDTO preSellUpdateDTO) throws Exception {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("预售模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNull(preSellUpdateDTO.getDeliverTime(), "发货时间不能为空");
        AssertUtil.isTrue(StringUtil.isEmpty(preSellUpdateDTO.getGoodsList()), "预售商品信息不能为空");

        String goodsList = preSellUpdateDTO.getGoodsList();
        List<PresellGoods> preSellGoodsList = JSONArray.parseArray(goodsList, PresellGoods.class);

        Presell presell = new Presell();
        BeanUtils.copyProperties(preSellUpdateDTO, presell);
        if (preSellUpdateDTO.getType() == PreSellConst.PRE_SELL_TYPE_1) {
            AssertUtil.isTrue(StringUtils.isEmpty(preSellUpdateDTO.getRemainStartTime()), "支付尾款的开始时间不能为空");
            AssertUtil.isTrue(StringUtils.isEmpty(preSellUpdateDTO.getRemainEndTime()), "支付尾款的结束时间不能为空");
            presell.setDeliverStartTime(TimeUtil.countApartDay(preSellUpdateDTO.getDeliverTime(), preSellUpdateDTO.getRemainEndTime()));
        } else {
            presell.setDeliverStartTime(TimeUtil.countApartDay(preSellUpdateDTO.getDeliverTime(), preSellUpdateDTO.getEndTime()));
        }
        presell.setStoreId(vendor.getStoreId());
        presell.setStoreName(vendor.getStore().getStoreName());
        presellModel.updatePresell(presell, preSellGoodsList);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("删除预售")
    @VendorLogger(option = "删除预售")
    @PostMapping("del")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "预售id", required = true)
    })
    public JsonResult delPreSell(HttpServletRequest request, Integer presellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("预售模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(presellId, "预售id不能为空");
        Presell presellByPresellId = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presellByPresellId, "获取的预售信息为空,请重试");
        AssertUtil.isTrue(!presellByPresellId.getStoreId().equals(vendor.getStoreId()), "无权限");

        presellModel.deletePresell(presellId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("发布预售")
    @VendorLogger(option = "发布预售")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "预售id", required = true)
    })
    @PostMapping("publish")
    public JsonResult publishPreSell(HttpServletRequest request, Integer presellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("预售模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(presellId, "预售id不能为空");
        Presell presellByPresellId = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presellByPresellId, "获取的预售信息为空,请重试");
        AssertUtil.isTrue(!presellByPresellId.getStoreId().equals(vendor.getStoreId()), "无权限");

        Presell presell = new Presell();
        presell.setPresellId(presellId);
        presell.setState(PreSellConst.ACTIVITY_STATE_2);
        presellModel.updatePresell(presell);
        return SldResponse.success("发布成功");
    }

    @ApiOperation("失效预售")
    @VendorLogger(option = "失效预售")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "预售id", required = true)
    })
    @PostMapping("invalid")
    public JsonResult invalidPreSell(HttpServletRequest request, Integer presellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("预售模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(presellId, "预售id不能为空");
        Presell presellByPresellId = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presellByPresellId, "获取的预售信息为空,请重试");
        AssertUtil.isTrue(!presellByPresellId.getStoreId().equals(vendor.getStoreId()), "无权限");

        Presell presell = new Presell();
        presell.setPresellId(presellId);
        presell.setState(PreSellConst.ACTIVITY_STATE_3);
        presellModel.updatePresell(presell);
        return SldResponse.success("失效成功");
    }

    @ApiOperation("复制预售")
    @VendorLogger(option = "复制预售")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "预售id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copyPreSell(HttpServletRequest request, Integer presellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("预售模块已关闭，不能操作");
        }

        AssertUtil.notNullOrZero(presellId, "请选择要复制的数据!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Presell presell = new Presell();
        presell.setPresellId(presellId);
        presell.setStoreId(vendor.getStoreId());
        presellModel.copyPreSell(presell);
        return SldResponse.success("复制成功");
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

    @ApiOperation("删除预售活动商品")
    @VendorLogger(option = "删除预售活动商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "预售活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, paramType = "query")
    })
    @PostMapping("delGoods")
    public JsonResult delPreSellGoods(HttpServletRequest request, Integer presellId, Long goodsId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("预售模块已关闭，不能操作");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.notNullOrZero(presellId, "预售活动id不能为空");
        AssertUtil.notNullOrZero(goodsId, "商品id不能为空");

        presellGoodsModel.deletePreSellGoodsByGoodsId(presellId, goodsId, vendor.getStoreId());
        return SldResponse.success("删除成功");
    }
}
