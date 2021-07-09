package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.member.example.MemberProductLookLogExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberProductLookLog;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.member.MemberProductLookLogModel;
import com.slodon.b2b2c.vo.member.ChatProductLookLogVO;
import com.slodon.b2b2c.vo.member.ProductLookLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-我的足迹")
@Slf4j
@RestController
@RequestMapping("v3/member/front/productLookLog")
public class MemberProductLookLogController extends BaseController {

    @Resource
    private MemberProductLookLogModel memberProductLookLogModel;
    @Resource
    private ProductModel productModel;

    @ApiOperation("我的足迹列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<ProductLookLogVO>> getList(HttpServletRequest request) throws Exception {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Member member = UserUtil.getUser(request, Member.class);
        List<ProductLookLogVO> vos = memberProductLookLogModel.getList(member.getMemberId(), pager);
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("删除我的足迹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logIds", value = "记录id集合,用逗号隔开", paramType = "query", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteMemberProductLookLog(HttpServletRequest request, String logIds) {
        //参数校验
        AssertUtil.notEmpty(logIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(logIds, "logIds格式错误,请重试");

        Member member = UserUtil.getUser(request, Member.class);
        memberProductLookLogModel.deleteMemberProductLookLog(logIds, member.getMemberId());
        return SldResponse.success("删除成功");
    }

    @ApiOperation("清空我的足迹")
    @GetMapping("empty")
    public JsonResult emptyMemberProductLookLog(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);
        memberProductLookLogModel.emptyMemberProductLookLog(member.getMemberId());
        return SldResponse.success("清空成功");
    }

    @PostMapping("add")
    @ApiOperation("添加我的足迹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "skuId", required = true, paramType = "query")
    })
    public JsonResult addMemberProductLookLog(HttpServletRequest request, Long productId) {

        Member member = UserUtil.getUser(request, Member.class);
        //判断会员是否登录.     登录 添加我的足迹; 未登录 直接返回
        if (!StringUtils.isEmpty(member.getMemberId())) {
            //查询货品sku
            Product product = productModel.getProductByProductId(productId);
            AssertUtil.notNull(product, "货品不存在");
            AssertUtil.isTrue(!product.getState().equals(GoodsConst.PRODUCT_STATE_1) && !product.getState().equals(GoodsConst.PRODUCT_STATE_3), "货品不存在！");

            //添加我的足迹
            memberProductLookLogModel.saveMemberProductLookLog(product, member.getMemberId());
        }
        //不需要返回
        return SldResponse.success();
    }

    @ApiOperation("获取聊天界面我的足迹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("myFootprint")
    public JsonResult<PageVO<ChatProductLookLogVO>> myFootprint(HttpServletRequest request, Long storeId) {
        Member member = UserUtil.getUser(request, Member.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        MemberProductLookLogExample example = new MemberProductLookLogExample();
        example.setMemberId(member.getMemberId());
        example.setStoreId(storeId);
        example.setPager(pager);
        List<MemberProductLookLog> list = memberProductLookLogModel.getMemberProductLookLogList(example, pager);
        List<ChatProductLookLogVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(productLookLog -> {
                vos.add(new ChatProductLookLogVO(productLookLog));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
