package com.slodon.b2b2c.controller.business.front;

import com.slodon.b2b2c.business.dto.OrderCommentAddDTO;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.OrderModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "front-待评价")
@RestController
@RequestMapping("v3/business/front/orderComment")
public class FrontOrderCommentController extends BaseController {

    @Resource
    private OrderModel orderModel;

    @ApiOperation("发布评价")
    @PostMapping("addOrderComment")
    public JsonResult addOrderComment(HttpServletRequest request, @RequestBody OrderCommentAddDTO orderCommentAddDTO) throws Exception {
        Member member = UserUtil.getUser(request, Member.class);
        orderModel.addOrderComment(orderCommentAddDTO, member);
        return SldResponse.success("评价成功");
    }

}
