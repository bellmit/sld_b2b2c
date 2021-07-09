package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.member.example.MemberFollowStoreExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberFollowStore;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.member.MemberFollowStoreModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.vo.member.FollowStoreVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@Api(tags = "front-关注店铺")
@Slf4j
@RestController
@RequestMapping("v3/member/front/followStore")
public class MemberFollowStoreController extends BaseController {

    @Resource
    private MemberFollowStoreModel memberFollowStoreModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private StoreModel storeModel;

    @ApiOperation("关注/取消关注商铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeIds", value = "商铺ID集合,中间用逗号隔开", paramType = "query", required = true),
            @ApiImplicitParam(name = "isCollect", value = "是否被关注,true 关注,false 取消关注", paramType = "query", required = true),
    })
    @PostMapping("edit")
    public JsonResult editMemberFollowStore(HttpServletRequest request, String storeIds, Boolean isCollect) {

        //参数校验
        AssertUtil.notEmpty(storeIds, "商铺id不能为空");
        AssertUtil.notFormatFrontIds(storeIds, "storeIds格式错误,请重试");

        Member member = UserUtil.getUser(request, Member.class);
        memberFollowStoreModel.editMemberFollowStore(storeIds, isCollect, member.getMemberId());

        Map<String, Object> map = new HashMap<>();
        String[] ids = storeIds.split(",");
        for (String storeId : ids) {
            Store store = storeModel.getStoreByStoreId(Long.valueOf(storeId));
            AssertUtil.notNull(store, "商店信息为空");
            map.put("followNumber", store.getFollowNumber());
            break;
        }
        return SldResponse.success(isCollect ? "关注商铺成功" : "取消关注商铺成功", map);
    }

    @ApiOperation("关注商铺列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<FollowStoreVO> getList(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);

        //根据条件查询集合
        MemberFollowStoreExample example = new MemberFollowStoreExample();
        example.setMemberId(member.getMemberId());
        example.setIsTop(StoreConst.IS_TOP_1);
        Integer topNumber = memberFollowStoreModel.getMemberFollowStoreCount(example);
        example.setIsTop(null);
        example.setOrderBy("is_top DESC");
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<MemberFollowStore> list = memberFollowStoreModel.getMemberFollowStoreList(example, pager);
        List<FollowStoreVO.MemberFollowStoreVO> storeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(memberFollowStore -> {
                //查询店铺
                Store store = storeModel.getStoreByStoreId(memberFollowStore.getStoreId());
                AssertUtil.notNull(store, "店铺不存在");
                FollowStoreVO.MemberFollowStoreVO followStoreVO = new FollowStoreVO.MemberFollowStoreVO(memberFollowStore, store);
                //查询店铺推荐商品信息,最多三件
                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setStoreId(memberFollowStore.getStoreId());
                goodsExample.setStoreIsRecommend(GoodsConst.STORE_IS_RECOMMEND_YES);
                goodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
                List<Goods> goodsList = goodsModel.getGoodsList(goodsExample, null);
                List<FollowStoreVO.MemberFollowStoreVO.RecommendGoodsVO> recommendGoodsVOList = followStoreVO.getGoodsList();
                if (!CollectionUtils.isEmpty(goodsList)) {
                    for (Goods goods : goodsList) {
                        FollowStoreVO.MemberFollowStoreVO.RecommendGoodsVO recommendGoodsVO = new FollowStoreVO.MemberFollowStoreVO.RecommendGoodsVO(goods);
                        if (recommendGoodsVOList.size() < 12) {
                            recommendGoodsVOList.add(recommendGoodsVO);
                        }
                    }
                }
                //查询店铺内本月上新商品
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //获取30天之前的时间
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DATE, -30);
                String dayDate = simpleDateFormat.format(calendar.getTime());
                //该店铺本月上新的商品
                GoodsExample newGoodsExample = new GoodsExample();
                newGoodsExample.setStoreId(store.getStoreId());
                newGoodsExample.setState(GoodsConst.GOODS_STATE_UPPER);
                newGoodsExample.setOnlineTimeLikeAfter(dayDate);
                newGoodsExample.setOrderBy("online_time DESC");
                List<Goods> newGoodsList = goodsModel.getGoodsList(newGoodsExample, null);
                List<FollowStoreVO.MemberFollowStoreVO.RecommendGoodsVO> newGoodsVOList = followStoreVO.getNewGoodsList();
                if (!CollectionUtils.isEmpty(newGoodsList)) {
                    for (Goods goods : newGoodsList) {
                        FollowStoreVO.MemberFollowStoreVO.RecommendGoodsVO recommendGoodsVO = new FollowStoreVO.MemberFollowStoreVO.RecommendGoodsVO(goods);
                        if (newGoodsVOList.size() < 12) {
                            newGoodsVOList.add(recommendGoodsVO);
                        }
                    }
                }
                storeList.add(followStoreVO);
            });
        }
        return SldResponse.success(new FollowStoreVO(topNumber, storeList, pager));
    }

    @ApiOperation("特别关注/取消特别关注商铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "followId", value = "收藏id", paramType = "query", required = true),
            @ApiImplicitParam(name = "isTop", value = "是否置顶：0、不置顶；1、置顶(特别关注)", paramType = "query", required = true),
    })
    @PostMapping("editSpecial")
    public JsonResult editSpecialMemberFollowStore(HttpServletRequest request, Integer followId, Integer isTop) throws Exception {
        //校验
        MemberFollowStore detail = memberFollowStoreModel.getMemberFollowStoreByFollowId(followId);
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.isTrue(!member.getMemberId().equals(detail.getMemberId()), "无权限");

        memberFollowStoreModel.editSpecialMemberFollowStore(followId, isTop);
        return SldResponse.success(isTop.equals(StoreConst.IS_TOP_1) ? "特别关注商铺成功" : "取消特别关注商铺成功");
    }

}
