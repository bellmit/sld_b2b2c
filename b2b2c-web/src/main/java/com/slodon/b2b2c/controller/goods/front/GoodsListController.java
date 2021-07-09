package com.slodon.b2b2c.controller.goods.front;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.SearchConditionDTO;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.example.MemberProductLookLogExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberProductLookLog;
import com.slodon.b2b2c.model.goods.*;
import com.slodon.b2b2c.model.member.MemberProductLookLogModel;
import com.slodon.b2b2c.model.system.SearchLogModel;
import com.slodon.b2b2c.system.pojo.SearchLog;
import com.slodon.b2b2c.vo.goods.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Api(tags = "front-商品列表")
@RestController
@RequestMapping("v3/goods/front/goods")
public class GoodsListController extends BaseController {

    @Resource
    private ESGoodsModel esGoodsModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsExtendModel goodsExtendModel;
    @Resource
    private GoodsBrandModel goodsBrandModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private MemberProductLookLogModel memberProductLookLogModel;
    @Resource
    private SearchLogModel searchLogModel;

    @ApiOperation("商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("goodsList")
    public JsonResult<PageVO<GoodsListVO>> goodsList(HttpServletRequest request, SearchConditionDTO qc) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);
        if (!StringUtils.isEmpty(qc.getKeyword())) {
            String keyword = qc.getKeyword();
            //搜索词不为空,记录搜索日志
            Integer memberId = 0;
            if (member != null && member.getMemberId() != null) {
                memberId = member.getMemberId();
            }
            if (!StringUtil.isEmpty(keyword)) {
                SearchLog searchLog = new SearchLog();
                searchLog.setKeyword(keyword);
                searchLog.setIp(WebUtil.getRealIp(request));
                searchLog.setMemberId(memberId);
                searchLog.setCreateTime(new Date());
                searchLogModel.saveSearchLog(searchLog);
            }
        }
        List<GoodsListVO> vos = esGoodsModel.searchGoodsByES(qc, pager, member);
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("精品推荐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryType", value = "查询类型(goods==商品详情;cart==购物车,我的;hot==热卖推荐)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "productId", value = "货品Id(查询类型为商品详情时必传)", paramType = "query"),
            @ApiImplicitParam(name = "categoryId3", value = "三级分类Id(查询类型为热卖推荐时必传)", paramType = "query")
    })
    @GetMapping("recommendList")
    public JsonResult<PageVO<GoodsRecommendVO>> recommendList(HttpServletRequest request, String queryType, Long productId, Integer categoryId3) {
        Member member = UserUtil.getUser(request, Member.class);
        List<GoodsRecommendVO> vos = new ArrayList<>();
        if ("cart".equals(queryType)) {
            Goods goods = null;
            //未登录按销量查询，登录按最近浏览商品相关分类推荐
            if (!StringUtil.isNullOrZero(member.getMemberId())) {
                //查询用户过往浏览商品类型
                PagerInfo pager1 = new PagerInfo(1, 1);
                MemberProductLookLogExample lookLogExample = new MemberProductLookLogExample();
                lookLogExample.setMemberId(member.getMemberId());
                lookLogExample.setOrderBy("create_time desc");
                lookLogExample.setPager(pager1);
                List<MemberProductLookLog> lookLogList = memberProductLookLogModel.getMemberProductLookLogList(lookLogExample, null);
                if (!CollectionUtils.isEmpty(lookLogList)) {
                    MemberProductLookLog productLookLog = lookLogList.get(0);
                    goods = goodsModel.getGoodsByGoodsId(productLookLog.getGoodsId());
                }
            }
            PagerInfo pager = WebUtil.handlerPagerInfo(request);
            //查询商品列表
            SearchConditionDTO qc = new SearchConditionDTO();
            if (goods != null) {
                qc.setCategoryIds(goods.getCategoryId3().toString());
            }
            qc.setSort(1);
            List<GoodsListVO> goodsList = esGoodsModel.searchGoodsByES(qc, pager, member);
            if (!CollectionUtils.isEmpty(goodsList)) {
                goodsList.forEach(goodsListVO -> {
                    GoodsRecommendVO vo = new GoodsRecommendVO();
                    vo.setGoodsId(goodsListVO.getGoodsId());
                    vo.setGoodsName(goodsListVO.getGoodsName());
                    vo.setGoodsImage(goodsListVO.getGoodsImage());
                    vo.setGoodsPrice(goodsListVO.getGoodsPrice());
                    vo.setDefaultProductId(goodsListVO.getDefaultProductId());
                    vo.setStoreId(goodsListVO.getStoreId());
                    vo.setStoreName(goodsListVO.getStoreName());
                    vo.setMarketPrice(goodsListVO.getMarketPrice());
                    vo.setGoodsBrief(goodsListVO.getGoodsBrief());
                    vo.setSaleNum(goodsListVO.getSaleNum());
                    vo.setActivityList(goodsListVO.getActivityList());
                    vos.add(vo);
                });
            }
            //不足八个从数据库查询，按销量查询
            if (goodsList.size() < 8) {
                PagerInfo pager1 = new PagerInfo(8 - goodsList.size(), 1);
                //查询商品列表
                SearchConditionDTO conditionDTO = new SearchConditionDTO();
                conditionDTO.setSort(1);
                List<GoodsListVO> list = esGoodsModel.searchGoodsByES(conditionDTO, pager1, member);
                if (!CollectionUtils.isEmpty(list)) {
                    list.forEach(goods1 -> {
                        GoodsRecommendVO vo = new GoodsRecommendVO();
                        vo.setGoodsId(goods1.getGoodsId());
                        vo.setGoodsName(goods1.getGoodsName());
                        vo.setGoodsImage(goods1.getGoodsImage());
                        vo.setGoodsPrice(goods1.getGoodsPrice());
                        vo.setDefaultProductId(goods1.getDefaultProductId());
                        vo.setStoreId(goods1.getStoreId());
                        vo.setStoreName(goods1.getStoreName());
                        vo.setMarketPrice(goods1.getMarketPrice());
                        vo.setGoodsBrief(goods1.getGoodsBrief());
                        vo.setSaleNum(goods1.getSaleNum());
                        vo.setActivityList(goods1.getActivityList());
                        vos.add(vo);
                    });
                }
            }
            return SldResponse.success(new PageVO<>(vos, pager));
        } else if ("goods".equals(queryType)) {
            AssertUtil.notNullOrZero(productId, "货品id不能为空");

            Product product = productModel.getProductByProductId(productId);
            AssertUtil.notNull(product, "获取货品信息为空");

            PagerInfo pager = new PagerInfo(6, 1);
            //查询商品列表
            GoodsExample example = new GoodsExample();
            example.setGoodsIdNotEquals(product.getGoodsId());
            example.setStoreId(product.getStoreId());
            example.setState(GoodsConst.GOODS_STATE_UPPER);
            example.setStoreIsRecommend(GoodsConst.STORE_IS_RECOMMEND_YES);
            example.setOrderBy("actual_sales desc");
            List<Goods> goodsList = goodsModel.getGoodsList(example, pager);
            if (!CollectionUtils.isEmpty(goodsList)) {
                goodsList.forEach(goods -> {
                    vos.add(new GoodsRecommendVO(goods));
                });
            }
            return SldResponse.success(new PageVO<>(vos, null));
        } else if ("hot".equals(queryType)) {
            AssertUtil.notNullOrZero(categoryId3, "三级分类id不能为空");

            PagerInfo pager = new PagerInfo(4, 1);
            //查询商品列表
            GoodsExample example = new GoodsExample();
            example.setCategoryId3(categoryId3);
            example.setState(GoodsConst.GOODS_STATE_UPPER);
            example.setOrderBy("actual_sales desc");
            List<Goods> goodsList = goodsModel.getGoodsList(example, pager);
            if (!CollectionUtils.isEmpty(goodsList)) {
                goodsList.forEach(goods -> {
                    vos.add(new GoodsRecommendVO(goods));
                });
            }
            return SldResponse.success(new PageVO<>(vos, null));
        }
        return SldResponse.success(new PageVO<>(new ArrayList<>(), null));
    }

    @ApiOperation("品牌中心")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("goodsBrandList")
    public JsonResult<PageVO<GoodsBrandFrontVO>> goodsList(HttpServletRequest request) {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据品牌首字母分组查询
        List<GoodsBrandFrontVO> vos = goodsBrandModel.getGoodsBrandListBy(pager);
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取商品上新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("newGoods")
    public JsonResult<PageVO<StoreNewGoodsVO>> newGoods(HttpServletRequest request, @RequestParam("storeId") Long storeId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取30天之前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -30);
        String dayDate = simpleDateFormat.format(calendar.getTime());

        //该店铺近一个月上架的时间
        String timeFields = "LEFT(online_time,10) onTime";
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setStoreId(storeId);
        goodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
        goodsExample.setOnlineTimeLikeAfter(dayDate);
        goodsExample.setGroupBy("onTime");
        goodsExample.setOrderBy("onTime DESC");
        List<String> goodsTimeList = goodsModel.getGoodsFieldList(timeFields, goodsExample, pager);
        List<StoreNewGoodsVO> storeNewGoodsVOS = new ArrayList<>();
        //遍历时间列表
        for (String goodsTime : goodsTimeList) {
            StoreNewGoodsVO storeNewGoodsVO = new StoreNewGoodsVO();
            //根据上架时间查询商品列表
            GoodsExample goodsExampleByOnlineTime = new GoodsExample();
            goodsExampleByOnlineTime.setOnlineTimeLike(goodsTime);
            goodsExampleByOnlineTime.setStoreId(storeId);
            goodsExampleByOnlineTime.setState(GoodsConst.GOODS_STATE_UPPER);
            List<Goods> goodsList = goodsModel.getGoodsList(goodsExampleByOnlineTime, null);
            List<GoodsVO> vos = new ArrayList();
            if (!CollectionUtils.isEmpty(goodsList)) {
                for (Goods goods : goodsList) {
                    GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
                    goodsExtendExample.setGoodsId(goods.getGoodsId());
                    List<GoodsExtend> goodsExtendList = goodsExtendModel.getGoodsExtendList(goodsExtendExample, null);
                    GoodsVO vo = new GoodsVO(goods, goodsExtendList.get(0));
                    vos.add(vo);
                }
            }
            storeNewGoodsVO.setOnLineTime(goodsTime);
            storeNewGoodsVO.setGoodsVOList(vos);
            storeNewGoodsVOS.add(storeNewGoodsVO);
        }
        return SldResponse.success(new PageVO<>(storeNewGoodsVOS, pager));
    }

    @ApiOperation("去凑单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "promotionId", value = "活动id", required = true)
    })
    @GetMapping("goCollectOrder")
    public JsonResult<PageVO<GoodsListVO>> goCollectOrder(HttpServletRequest request, Integer promotionId) {
        GoodsPromotion goodsPromotion = goodsPromotionModel.getGoodsPromotionByPromotionId(promotionId);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SearchConditionDTO qc = new SearchConditionDTO();
        //待修改
        if (goodsPromotion.getPromotionGrade() == PromotionConst.PROMOTION_GRADE_1 && !StringUtil.isNullOrZero(goodsPromotion.getGoodsId())) {
            qc.setGoodsIds(goodsPromotion.getGoodsId().toString());
        } else if (goodsPromotion.getPromotionGrade() == PromotionConst.PROMOTION_GRADE_2) {
            qc.setStoreId(goodsPromotion.getStoreId());
        } else if (goodsPromotion.getPromotionGrade() == PromotionConst.PROMOTION_GRADE_3 && !StringUtil.isNullOrZero(goodsPromotion.getGoodsCategoryId3())) {
            qc.setCategoryIds(goodsPromotion.getGoodsCategoryId3().toString());
        }
        qc.setSort(1);
        List<GoodsListVO> vos = esGoodsModel.searchGoodsByES(qc, pager, null);
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取聊天界面店铺推荐商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("storeGoods")
    public JsonResult<PageVO<RecommendGoodsVO>> sellerGoods(HttpServletRequest request, Long storeId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsExample example = new GoodsExample();
        example.setStoreId(storeId);
        example.setStoreIsRecommend(GoodsConst.IS_RECOMMEND_YES);
        example.setState(GoodsConst.GOODS_STATE_UPPER);
        example.setStoreState(GoodsConst.STORE_STATE_1);
        List<Goods> list = goodsModel.getGoodsList(example, pager);
        List<RecommendGoodsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goods -> {
                vos.add(new RecommendGoodsVO(goods));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("直播商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true),
            @ApiImplicitParam(name = "keyword", value = "关键词"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("liveGoodsList")
    public JsonResult<PageVO<LiveGoodsVO>> liveGoodsList(HttpServletRequest request, Long storeId, String keyword) {
        AssertUtil.notNullOrZero(storeId, "请选择店铺id");

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsExample example = new GoodsExample();
        example.setStoreId(storeId);
        example.setGoodsNameLike(keyword);
        example.setState(GoodsConst.GOODS_STATE_UPPER);
        example.setIsLock(GoodsConst.IS_LOCK_NO);
        example.setIsDelete(GoodsConst.GOODS_IS_DELETE_NO);
        List<LiveGoodsVO> vos = new ArrayList<>();
        List<Goods> list = goodsModel.getGoodsList(example, pager);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goods -> {
                vos.add(new LiveGoodsVO(goods));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
