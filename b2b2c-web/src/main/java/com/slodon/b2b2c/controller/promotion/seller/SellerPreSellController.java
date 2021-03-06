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

@Api(tags = "seller-??????")
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

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "???????????????1-???????????????2-????????????, ?????????1???", defaultValue = "1", required = true, paramType = "query"),
            @ApiImplicitParam(name = "presellName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "??????(1-?????????;2-?????????;3-?????????;4-?????????;5-?????????)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
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

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "??????id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<PreSellDetailVO> detail(HttpServletRequest request, Integer presellId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(presellId, "??????id????????????");

        Presell presell = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presell, "?????????????????????????????????????????????");
        AssertUtil.isTrue(!presell.getStoreId().equals(vendor.getStoreId()), "?????????");
        PreSellDetailVO detailVO = new PreSellDetailVO(presell);

        //??????????????????????????????
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
                    //??????sku??????
                    Product product = productModel.getProductByProductId(goods.getProductId());
                    AssertUtil.notNull(product, "???????????????????????????????????????");
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

    @ApiOperation("????????????????????????")
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

    @ApiOperation("??????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "??????id????????????????????????", required = true, paramType = "query")
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
            // ??????????????????
            Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
            AssertUtil.notNull(goods, "????????????????????????????????????");

            // ??????????????????
            ProductExample example = new ProductExample();
            example.setGoodsId(goodsId);
            List<Product> productList = productModel.getProductList(example, null);
            AssertUtil.notEmpty(productList, "????????????????????????????????????????????????");

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

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @PostMapping("add")
    public JsonResult addPreSell(HttpServletRequest request, @RequestBody PreSellAddDTO preSellAddDTO) throws Exception {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("????????????????????????????????????");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNull(preSellAddDTO.getDeliverTime(), "????????????????????????");
        AssertUtil.isTrue(StringUtil.isEmpty(preSellAddDTO.getGoodsList()), "??????????????????????????????");

        String goodsList = preSellAddDTO.getGoodsList();
        List<PresellGoods> preSellGoodsList = JSONArray.parseArray(goodsList, PresellGoods.class);

        Presell presell = new Presell();
        BeanUtils.copyProperties(preSellAddDTO, presell);
        if (preSellAddDTO.getType() == PreSellConst.PRE_SELL_TYPE_1) {
            AssertUtil.isTrue(StringUtils.isEmpty(preSellAddDTO.getRemainStartTime()), "???????????????????????????????????????");
            AssertUtil.isTrue(StringUtils.isEmpty(preSellAddDTO.getRemainEndTime()), "???????????????????????????????????????");
            presell.setDeliverStartTime(TimeUtil.countApartDay(preSellAddDTO.getDeliverTime(), preSellAddDTO.getRemainEndTime()));
        } else {
            presell.setDeliverStartTime(TimeUtil.countApartDay(preSellAddDTO.getDeliverTime(), preSellAddDTO.getEndTime()));
        }
        presell.setStoreId(vendor.getStoreId());
        presell.setStoreName(vendor.getStore().getStoreName());
        presell.setCreateVendorId(vendor.getVendorId());
        presellModel.savePresell(presell, preSellGoodsList);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @PostMapping("update")
    public JsonResult updatePreSell(HttpServletRequest request, @RequestBody PreSellUpdateDTO preSellUpdateDTO) throws Exception {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("????????????????????????????????????");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNull(preSellUpdateDTO.getDeliverTime(), "????????????????????????");
        AssertUtil.isTrue(StringUtil.isEmpty(preSellUpdateDTO.getGoodsList()), "??????????????????????????????");

        String goodsList = preSellUpdateDTO.getGoodsList();
        List<PresellGoods> preSellGoodsList = JSONArray.parseArray(goodsList, PresellGoods.class);

        Presell presell = new Presell();
        BeanUtils.copyProperties(preSellUpdateDTO, presell);
        if (preSellUpdateDTO.getType() == PreSellConst.PRE_SELL_TYPE_1) {
            AssertUtil.isTrue(StringUtils.isEmpty(preSellUpdateDTO.getRemainStartTime()), "???????????????????????????????????????");
            AssertUtil.isTrue(StringUtils.isEmpty(preSellUpdateDTO.getRemainEndTime()), "???????????????????????????????????????");
            presell.setDeliverStartTime(TimeUtil.countApartDay(preSellUpdateDTO.getDeliverTime(), preSellUpdateDTO.getRemainEndTime()));
        } else {
            presell.setDeliverStartTime(TimeUtil.countApartDay(preSellUpdateDTO.getDeliverTime(), preSellUpdateDTO.getEndTime()));
        }
        presell.setStoreId(vendor.getStoreId());
        presell.setStoreName(vendor.getStore().getStoreName());
        presellModel.updatePresell(presell, preSellGoodsList);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @PostMapping("del")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "??????id", required = true)
    })
    public JsonResult delPreSell(HttpServletRequest request, Integer presellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("????????????????????????????????????");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(presellId, "??????id????????????");
        Presell presellByPresellId = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presellByPresellId, "???????????????????????????,?????????");
        AssertUtil.isTrue(!presellByPresellId.getStoreId().equals(vendor.getStoreId()), "?????????");

        presellModel.deletePresell(presellId);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "??????id", required = true)
    })
    @PostMapping("publish")
    public JsonResult publishPreSell(HttpServletRequest request, Integer presellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("????????????????????????????????????");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(presellId, "??????id????????????");
        Presell presellByPresellId = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presellByPresellId, "???????????????????????????,?????????");
        AssertUtil.isTrue(!presellByPresellId.getStoreId().equals(vendor.getStoreId()), "?????????");

        Presell presell = new Presell();
        presell.setPresellId(presellId);
        presell.setState(PreSellConst.ACTIVITY_STATE_2);
        presellModel.updatePresell(presell);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "??????id", required = true)
    })
    @PostMapping("invalid")
    public JsonResult invalidPreSell(HttpServletRequest request, Integer presellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("????????????????????????????????????");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(presellId, "??????id????????????");
        Presell presellByPresellId = presellModel.getPresellByPresellId(presellId);
        AssertUtil.notNull(presellByPresellId, "???????????????????????????,?????????");
        AssertUtil.isTrue(!presellByPresellId.getStoreId().equals(vendor.getStoreId()), "?????????");

        Presell presell = new Presell();
        presell.setPresellId(presellId);
        presell.setState(PreSellConst.ACTIVITY_STATE_3);
        presellModel.updatePresell(presell);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????")
    @VendorLogger(option = "????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "??????id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copyPreSell(HttpServletRequest request, Integer presellId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("????????????????????????????????????");
        }

        AssertUtil.notNullOrZero(presellId, "???????????????????????????!");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Presell presell = new Presell();
        presell.setPresellId(presellId);
        presell.setStoreId(vendor.getStoreId());
        presellModel.copyPreSell(presell);
        return SldResponse.success("????????????");
    }

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "????????????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "????????????", paramType = "query")
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

    @ApiOperation("????????????????????????")
    @VendorLogger(option = "????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "presellId", value = "????????????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsId", value = "??????id", required = true, paramType = "query")
    })
    @PostMapping("delGoods")
    public JsonResult delPreSellGoods(HttpServletRequest request, Integer presellId, Long goodsId) {
        String isEnable = stringRedisTemplate.opsForValue().get("presale_is_enable");
        if ("0".equals(isEnable)) {
            return SldResponse.moduleDisabled("????????????????????????????????????");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.notNullOrZero(presellId, "????????????id????????????");
        AssertUtil.notNullOrZero(goodsId, "??????id????????????");

        presellGoodsModel.deletePreSellGoodsByGoodsId(presellId, goodsId, vendor.getStoreId());
        return SldResponse.success("????????????");
    }
}
