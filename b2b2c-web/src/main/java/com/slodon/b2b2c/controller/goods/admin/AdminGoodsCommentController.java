package com.slodon.b2b2c.controller.goods.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.example.GoodsCommentExample;
import com.slodon.b2b2c.goods.pojo.GoodsComment;
import com.slodon.b2b2c.model.goods.GoodsCommentModel;
import com.slodon.b2b2c.vo.goods.GoodsCommentVO;
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
@RequestMapping("v3/goods/admin/goodsComment")
public class AdminGoodsCommentController extends BaseController {

    @Resource
    private GoodsCommentModel goodsCommentModel;

    @ApiOperation("商品评价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "评价人", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsCommentVO>> getList(HttpServletRequest request, String goodsName, String storeName,
                                                      String memberName, Date startTime, Date endTime) {
        //分页
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //模糊查询
        GoodsCommentExample example = new GoodsCommentExample();
        example.setGoodsNameLike(goodsName);
        example.setStoreNameLike(storeName);
        example.setMemberNameLike(memberName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);

        List<GoodsComment> list = goodsCommentModel.getGoodsCommentList(example, pager);
        List<GoodsCommentVO> vos = new ArrayList<>();
        list.forEach(goodsComment -> {
            GoodsCommentVO vo = new GoodsCommentVO(goodsComment);
            vos.add(vo);
        });

        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("删除商品评价")
    @OperationLogger(option = "删除商品评价")
    @PostMapping("del")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentIds", value = "评论id集合,中间用逗号隔开", paramType = "query", required = true)
    })
    public JsonResult deleteGoodsComment(HttpServletRequest request, @RequestParam("commentIds") String commentIds) {
        //校验
        AssertUtil.notEmpty(commentIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(commentIds,"commentIds格式错误");

        goodsCommentModel.deleteGoodsComment(commentIds);
        return SldResponse.success("删除成功");
    }

}
