package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.member.example.MemberFollowProductExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberFollowProduct;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.member.MemberFollowProductModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.vo.member.FollowProductVO;
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
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-我的收藏")
@Slf4j
@RestController
@RequestMapping("v3/member/front/followProduct")
public class MemberFollowProductController extends BaseController {

    @Resource
    private MemberFollowProductModel memberFollowProductModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private GoodsModel goodsModel;

    @ApiOperation("收藏/取消收藏商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productIds", value = "货品ID集合,中间用逗号隔开", paramType = "query", required = true),
            @ApiImplicitParam(name = "isCollect", value = "是否被收藏,true 收藏,false 取消收藏", paramType = "query", required = true)
    })
    @PostMapping("edit")
    public JsonResult editMemberFollowProduct(HttpServletRequest request, String productIds, Boolean isCollect) throws Exception {
        //参数校验
        AssertUtil.notEmpty(productIds, "货品id不能为空");
        AssertUtil.notFormatFrontIds(productIds, "productIds格式错误,请重试");

        Member member = UserUtil.getUser(request, Member.class);
        memberFollowProductModel.editMemberFollowProduct(productIds, isCollect, member.getMemberId());
        return SldResponse.success(isCollect ? "收藏成功" : "取消成功");
    }

    @ApiOperation("收藏商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<FollowProductVO>> getList(HttpServletRequest request) {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);

        //根据条件查询集合
        MemberFollowProductExample example = new MemberFollowProductExample();
        example.setMemberId(member.getMemberId());
        List<MemberFollowProduct> list = memberFollowProductModel.getMemberFollowProductList(example, pager);

        //响应
        List<FollowProductVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(memberFollowProduct -> {
                Store store = storeModel.getStoreByStoreId(memberFollowProduct.getStoreId());
                AssertUtil.notNull(store, "查询的商铺信息为空");
                Goods goods = goodsModel.getGoodsByGoodsId(memberFollowProduct.getGoodsId());
                AssertUtil.notNull(goods, "查询的商品信息为空");
                FollowProductVO vo = new FollowProductVO(memberFollowProduct, store, goods);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

}
