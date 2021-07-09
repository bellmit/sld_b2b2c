package com.slodon.b2b2c.controller.member.seller;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.example.MemberProductLookLogExample;
import com.slodon.b2b2c.member.pojo.MemberProductLookLog;
import com.slodon.b2b2c.model.member.MemberProductLookLogModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.member.ChatProductLookLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "seller-用户足迹")
@RestController
@RequestMapping("v3/member/seller/productLook")
public class SellerProductLookController extends BaseController {

    @Resource
    private MemberProductLookLogModel memberProductLookLogModel;

    @ApiOperation("获取聊天用户足迹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "会员id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("userFootprint")
    public JsonResult<PageVO<ChatProductLookLogVO>> userFootprint(HttpServletRequest request, Integer memberId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        MemberProductLookLogExample example = new MemberProductLookLogExample();
        example.setMemberId(memberId);
        example.setStoreId(vendor.getStoreId());
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
