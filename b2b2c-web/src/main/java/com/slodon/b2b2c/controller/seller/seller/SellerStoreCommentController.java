package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreCommentModel;
import com.slodon.b2b2c.seller.example.StoreCommentExample;
import com.slodon.b2b2c.seller.pojo.StoreComment;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.seller.SellerStoreCommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-评价管理")
@RestController
@RequestMapping("v3/seller/seller/storeComment")
public class SellerStoreCommentController extends BaseController {

    @Resource
    private StoreCommentModel storeCommentModel;

    @ApiOperation("店铺评价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName", value = "评价人", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SellerStoreCommentVO>> getList(HttpServletRequest request,
                                                            String memberName,
                                                            Date startTime,
                                                            Date endTime) {

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //分页
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //模糊查询
        StoreCommentExample example = new StoreCommentExample();
        example.setStoreId(vendor.getStoreId());
        example.setMemberNameLike(memberName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);

        List<StoreComment> list = storeCommentModel.getStoreCommentList(example, pager);
        List<SellerStoreCommentVO> vos = new ArrayList<>();
        list.forEach(storeComment -> {
            SellerStoreCommentVO vo = new SellerStoreCommentVO(storeComment);
            vos.add(vo);
        });

        return SldResponse.success(new PageVO<>(vos, pager));
    }

}
