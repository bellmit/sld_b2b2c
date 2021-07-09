package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.GoodsCommentReplyUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsCommentExample;
import com.slodon.b2b2c.goods.pojo.GoodsComment;
import com.slodon.b2b2c.model.goods.GoodsCommentModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.SellerGoodsCommentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-评价管理")
@Slf4j
@RestController
@RequestMapping("v3/goods/seller/goodsComment")
public class SellerGoodsCommentController extends BaseController {

    @Resource
    private GoodsCommentModel goodsCommentModel;

    @ApiOperation("商品评价列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "评价人", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SellerGoodsCommentVO>> getList(HttpServletRequest request,
                                                            @RequestParam(value = "goodsName", required = false) String goodsName,
                                                            @RequestParam(value = "memberName", required = false) String memberName,
                                                            @RequestParam(value = "startTime", required = false) Date startTime,
                                                            @RequestParam(value = "endTime", required = false) Date endTime) {

        Vendor vendor = UserUtil.getUser(request,Vendor.class);
        //分页
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //模糊查询
        GoodsCommentExample example = new GoodsCommentExample();
        example.setStoreId(vendor.getStoreId());
        example.setGoodsNameLike(goodsName);
        example.setMemberNameLike(memberName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);

        List<GoodsComment> list = goodsCommentModel.getGoodsCommentList(example, pager);
        List<SellerGoodsCommentVO> vos = new ArrayList<>();
        list.forEach(goodsComment -> {
            SellerGoodsCommentVO vo = new SellerGoodsCommentVO(goodsComment);
            vos.add(vo);
        });

        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("回复")
    @VendorLogger(option = "回复")
    @PostMapping("editReply")
    public JsonResult editReply(HttpServletRequest request, GoodsCommentReplyUpdateDTO goodsCommentReplyUpdateDTO) throws Exception {
        AssertUtil.notNullOrZero(goodsCommentReplyUpdateDTO.getCommentId(), "请选择要回复的评论");
        AssertUtil.isTrue(!StringUtils.isEmpty(goodsCommentReplyUpdateDTO.getReplyContent()) && goodsCommentReplyUpdateDTO.getReplyContent().length() > 200, "商家回复最多输入200字");

        goodsCommentModel.editReply(goodsCommentReplyUpdateDTO);
        return SldResponse.success("回复成功");
    }

}
