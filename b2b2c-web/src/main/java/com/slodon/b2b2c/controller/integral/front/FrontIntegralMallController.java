package com.slodon.b2b2c.controller.integral.front;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.IntegralConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.goods.dto.GoodsAddDTO;
import com.slodon.b2b2c.integral.dto.GoodsSearchConditionDTO;
import com.slodon.b2b2c.integral.example.*;
import com.slodon.b2b2c.integral.pojo.*;
import com.slodon.b2b2c.member.example.MemberFollowStoreExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberFollowStore;
import com.slodon.b2b2c.model.integral.*;
import com.slodon.b2b2c.model.member.MemberFollowStoreModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.vo.integral.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api(tags = "front-积分商城")
@RestController
@RequestMapping("v3/integral/front/integral/mall")
public class FrontIntegralMallController extends BaseController {

    @Resource
    private IntegralGoodsLabelModel integralGoodsLabelModel;
    @Resource
    private IntegralGoodsModel integralGoodsModel;
    @Resource
    private IntegralProductModel integralProductModel;
    @Resource
    private IntegralGoodsPictureModel integralGoodsPictureModel;
    @Resource
    private IntegralGoodsSpecValueModel integralGoodsSpecValueModel;
    @Resource
    private IntegralESGoodsModel integralESGoodsModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private MemberFollowStoreModel memberFollowStoreModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("积分商城列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<IntegralMallVO>> list(HttpServletRequest request, Integer labelId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralGoodsLabelExample example = new IntegralGoodsLabelExample();
        example.setParentLabelId(0);
        example.setState(IntegralConst.STATE_1);
        example.setQueryGoods("notNull");
        example.setOrderBy("sort asc, create_time desc");
        List<IntegralGoodsLabel> list = integralGoodsLabelModel.getIntegralGoodsLabelList(example, null);
        if (StringUtil.isNullOrZero(labelId) && !CollectionUtils.isEmpty(list)) {
            labelId = list.get(0).getLabelId();
        }
        List<IntegralMallVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            Integer finalLabelId = labelId;
            list.forEach(goodsLabel -> {
                IntegralMallVO mallVO = new IntegralMallVO(goodsLabel);
                if (goodsLabel.getLabelId().equals(finalLabelId)) {
                    //当前选中标签，查询其二级
                    IntegralGoodsLabelExample example2 = new IntegralGoodsLabelExample();
                    example2.setParentLabelId(finalLabelId);
                    example2.setState(IntegralConst.STATE_1);
                    example2.setQueryGoods2("notNull");
                    example2.setOrderBy("sort asc, create_time desc");
                    List<IntegralGoodsLabel> list2 = integralGoodsLabelModel.getIntegralGoodsLabelList(example2, null);
                    //构造二级列表
                    List<IntegralMallVO> vos2 = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(list2)) {
                        list2.forEach(goodsLabel2 -> {
                            vos2.add(new IntegralMallVO(goodsLabel2));
                        });
                    }
                    mallVO.setChildren(vos2);

                    //查询当前一级标签下的商品列表
                    IntegralGoodsExample goodsExample = new IntegralGoodsExample();
                    goodsExample.setLabelId(finalLabelId);
                    goodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
                    List<IntegralGoods> goodsList = integralGoodsModel.getIntegralGoodsList(goodsExample, pager);
                    List<IntegralGoodsListVO> goodsListVOS = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(goodsList)) {
                        goodsList.forEach(integralGoods -> {
                            IntegralGoodsListVO vo = new IntegralGoodsListVO();
                            BeanUtils.copyProperties(integralGoods, vo);
                            vo.setGoodsImage(FileUrlUtil.getFileUrl(integralGoods.getMainImage(), null));
                            vo.setSaleNum(integralGoods.getActualSales() + integralGoods.getVirtualSales());
                            vo.setIsOwnShop(integralGoods.getIsSelf());
                            goodsListVOS.add(vo);
                        });
                    }
                    mallVO.setGoodsList(goodsListVOS);
                }
                vos.add(mallVO);
            });
        }
        return SldResponse.success(new PageVO<>(vos, null));
    }

    @ApiOperation("商品列表")
    @GetMapping("goodsList")
    public JsonResult<PageVO<IntegralGoodsListVO>> goodsList(HttpServletRequest request, GoodsSearchConditionDTO qc) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<IntegralGoodsListVO> vos = integralESGoodsModel.searchGoodsByES(qc, pager);
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("推荐商品")
    @GetMapping("recommendList")
    public JsonResult<PageVO<RecommendGoodsVO>> recommendList(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralGoodsExample goodsExample = new IntegralGoodsExample();
        goodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
        goodsExample.setOrderBy("actual_sales desc");
        List<IntegralGoods> goodsList = integralGoodsModel.getIntegralGoodsList(goodsExample, pager);
        List<RecommendGoodsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            goodsList.forEach(integralGoods -> {
                vos.add(new RecommendGoodsVO(integralGoods));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "integralProductId", value = "积分货品Id", required = true, paramType = "query"),
    })
    @GetMapping("details")
    public JsonResult<FrontGoodsDetailVO> getGoodsDetails(HttpServletRequest request, Long integralProductId) {
        Member member = UserUtil.getUser(request, Member.class);

        IntegralProduct integralProduct = integralProductModel.getIntegralProductByIntegralProductId(integralProductId);
        AssertUtil.notNull(integralProduct, "积分货品不存在，请重试！");
        Long goodsId = integralProduct.getGoodsId();
        IntegralGoods integralGoods = integralGoodsModel.getIntegralGoodsByIntegralGoodsId(goodsId);
        AssertUtil.notNull(integralGoods, "积分商品不存在，请重试！");

        //是否关注店铺
        Boolean isFollowStore = checkIsFollowStore(member.getMemberId(), integralProduct.getStoreId());

        //查询货品信息
        IntegralProductExample productExample = new IntegralProductExample();
        productExample.setGoodsId(goodsId);
        productExample.setState(IntegralConst.PRODUCT_STATE_1);
        List<IntegralProduct> productList = integralProductModel.getIntegralProductList(productExample, null);

        //查询规格图片
        IntegralGoodsPictureExample goodsPictureExample = new IntegralGoodsPictureExample();
        goodsPictureExample.setOrderBy("is_main asc, picture_id asc");
        goodsPictureExample.setGoodsId(goodsId);
        //有主规格
        if (integralGoods.getMainSpecId() != 0) {
            goodsPictureExample.setSpecValueIdIn(integralProduct.getSpecValueIds());
        }
        List<IntegralGoodsPicture> goodsPictureList = integralGoodsPictureModel.getIntegralGoodsPictureList(goodsPictureExample, null);

        Store store = storeModel.getStoreByStoreId(integralGoods.getStoreId());
        //默认店铺logo
        if (StringUtils.isEmpty(store.getStoreLogo())) {
            store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
        }
        FrontGoodsDetailVO detailVO = new FrontGoodsDetailVO(integralGoods, integralProduct, productList, goodsPictureList, store, isFollowStore);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("获取货品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "integralGoodsId", value = "积分商品Id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "specValueIds", value = "规格属性值Id", paramType = "query"),
    })
    @GetMapping("productInfo")
    public JsonResult<FrontProductInfoVO> getProductInfo(HttpServletRequest request, Long integralGoodsId, String specValueIds) {
        //获得货品信息 包含某规格商品的库存及价格信息
        IntegralGoods goods = integralGoodsModel.getIntegralGoodsByIntegralGoodsId(integralGoodsId);
        AssertUtil.notNull(goods, "商品不存在，请重试！");

        //查询所有有效规格
        IntegralProductExample productExample = new IntegralProductExample();
        productExample.setGoodsId(integralGoodsId);
        productExample.setStateIn(IntegralConst.PRODUCT_STATE_1 + "," + IntegralConst.PRODUCT_STATE_3);
        List<IntegralProduct> productList = integralProductModel.getIntegralProductList(productExample, null);
        List<String> effectSpecValueIds = new ArrayList<>();
        productList.forEach(product1 -> effectSpecValueIds.add(product1.getSpecValueIds()));

        IntegralProduct product = null;//要选中的货品信息

        //查询要选中的规格id组合
        String[] specAttrIdArr = specValueIds.split(",");
        Arrays.sort(specAttrIdArr);
        for (IntegralProduct product1 : productList) {
            String[] effArr = product1.getSpecValueIds().split(",");
            Arrays.sort(effArr);
            if (Arrays.equals(effArr, specAttrIdArr)) {
                product = product1;
                break;
            }
        }
        if (product == null) {
            //未找到货品，默认第一个
            product = productList.get(0);
        }

        //无规格时specValueId取0
        Integer specValueId = 0;
        //查询主规格值列表
        //有主规格
        if (goods.getMainSpecId() != 0) {
            IntegralGoodsSpecValueExample goodsSpecValueExample = new IntegralGoodsSpecValueExample();
            goodsSpecValueExample.setSpecId(goods.getMainSpecId());
            goodsSpecValueExample.setSpecValueIdIn(product.getSpecValueIds());
            List<IntegralGoodsSpecValue> goodsSpecValueList = integralGoodsSpecValueModel.getIntegralGoodsSpecValueList(goodsSpecValueExample, null);
            specValueId = goodsSpecValueList.get(0).getSpecValueId();
        }

        //查询对应主规格下的图片
        IntegralGoodsPictureExample goodsPictureExample = new IntegralGoodsPictureExample();
        goodsPictureExample.setGoodsId(integralGoodsId);
        goodsPictureExample.setSpecValueId(specValueId);
        goodsPictureExample.setOrderBy("is_main asc,picture_id asc");
        List<IntegralGoodsPicture> goodsPictureList = integralGoodsPictureModel.getIntegralGoodsPictureList(goodsPictureExample, null);

        List<FrontGoodsDetailVO.SpecVO> specs = new ArrayList<>();//规格列表
        //构造规格列表
        for (GoodsAddDTO.SpecInfo specInfo : JSONObject.parseArray(goods.getSpecJson(), GoodsAddDTO.SpecInfo.class)) {
            if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                //主规格，放在规格列表的第一位
                specs.add(0, new FrontGoodsDetailVO.SpecVO(specInfo, product.getSpecValueIds(), effectSpecValueIds));
            } else {
                specs.add(new FrontGoodsDetailVO.SpecVO(specInfo, product.getSpecValueIds(), effectSpecValueIds));
            }
        }
        FrontProductInfoVO productInfoVO = new FrontProductInfoVO(goods, product, productList, goodsPictureList);
        return SldResponse.success(productInfoVO);
    }

    @ApiOperation("二级标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "一级标签id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("labelList")
    public JsonResult<PageVO<IntegralLabelVO>> labelList(HttpServletRequest request, Integer labelId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralGoodsLabelExample example = new IntegralGoodsLabelExample();
        example.setParentLabelId(labelId);
        example.setOrderBy("sort asc, create_time desc");
        List<IntegralGoodsLabel> list = integralGoodsLabelModel.getIntegralGoodsLabelList(example, pager);
        List<IntegralLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(label -> {
                vos.add(new IntegralLabelVO(label));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    /**
     * 判断用户是否关注店铺
     *
     * @param memberId
     * @param storeId
     * @return
     */
    private Boolean checkIsFollowStore(Integer memberId, Long storeId) {
        Boolean isFollowStore = false;
        if (memberId != null) {
            MemberFollowStoreExample memberFollowStoreExample = new MemberFollowStoreExample();
            memberFollowStoreExample.setMemberId(memberId);
            memberFollowStoreExample.setStoreId(storeId);
            List<MemberFollowStore> list = memberFollowStoreModel.getMemberFollowStoreList(memberFollowStoreExample, null);
            if (list.size() > 0) {
                isFollowStore = true;
            }
        }
        return isFollowStore;
    }
}
