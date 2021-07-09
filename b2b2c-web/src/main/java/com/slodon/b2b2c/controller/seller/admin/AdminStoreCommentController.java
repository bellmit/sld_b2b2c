package com.slodon.b2b2c.controller.seller.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreCommentModel;
import com.slodon.b2b2c.seller.example.StoreCommentExample;
import com.slodon.b2b2c.seller.pojo.StoreComment;
import com.slodon.b2b2c.vo.seller.StoreCommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-评价管理")
@RestController
@RequestMapping("v3/seller/admin/storeComment")
public class AdminStoreCommentController extends BaseController {

    @Resource
    private StoreCommentModel storeCommentModel;

    @ApiOperation("店铺评价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "评价人", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreCommentVO>> getList(HttpServletRequest request,
                                                      @RequestParam(value = "storeName", required = false) String storeName,
                                                      @RequestParam(value = "memberName", required = false) String memberName,
                                                      @RequestParam(value = "startTime", required = false) Date startTime,
                                                      @RequestParam(value = "endTime", required = false) Date endTime) {

        //分页
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //模糊查询
        StoreCommentExample example = new StoreCommentExample();
        example.setStoreNameLike(storeName);
        example.setMemberNameLike(memberName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);

        List<StoreComment> list = storeCommentModel.getStoreCommentList(example, pager);
        List<StoreCommentVO> vos = new ArrayList<>();
        list.forEach(storeComment -> {
            StoreCommentVO vo = new StoreCommentVO(storeComment);
            vos.add(vo);
        });

        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("删除店铺评价")
    @OperationLogger(option = "删除店铺评价")
    @PostMapping("del")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentIds", value = "评论id集合,中间用逗号隔开", paramType = "query", required = true)
    })
    public JsonResult deleteStoreComment(HttpServletRequest request, @RequestParam("commentIds") String commentIds) {
        //参数校验
        AssertUtil.notEmpty(commentIds,"请选择要删除的数据");
        AssertUtil.notFormatFrontIds(commentIds,"commentIds格式错误,请重试");

        storeCommentModel.deleteStoreComment(commentIds);
        return SldResponse.success("删除成功");
    }

}
