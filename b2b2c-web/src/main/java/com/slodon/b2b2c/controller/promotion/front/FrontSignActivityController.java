package com.slodon.b2b2c.controller.promotion.front;


import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.promotion.SignActivityModel;
import com.slodon.b2b2c.model.promotion.SignLogModel;
import com.slodon.b2b2c.vo.promotion.MemberSignDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "front-签到活动")
@RestController
@Slf4j
@RequestMapping("v3/promotion/front/sign/activity")
public class FrontSignActivityController extends BaseController {

    @Resource
    private SignActivityModel signActivityModel;
    @Resource
    private SignLogModel signLogModel;

    @ApiOperation("获取会员签到信息接口")
    @GetMapping("detail")
    public JsonResult<MemberSignDetailVO> getMemberSign(HttpServletRequest request) throws Exception{
        Member member = UserUtil.getUser(request, Member.class);
        MemberSignDetailVO vo = signActivityModel.getMemberSign(member.getMemberId());
        return SldResponse.success(vo);
    }

    @ApiOperation("会员签到")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signActivityId", value = "签到活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "source", value = "会员来源来源：1、pc；2、H5；3、Android；4、IOS；5、小程序", paramType = "query")
    })
    @PostMapping("doSign")
    public JsonResult<MemberSignDetailVO> doSign(HttpServletRequest request, Integer signActivityId,Integer source) throws Exception{
        AssertUtil.notNullOrZero(signActivityId,"签到活动id不能为空");
        Member member = UserUtil.getUser(request, Member.class);
        String ip = WebUtil.getRealIp(request);
        MemberSignDetailVO vo = signLogModel.doSign(signActivityId, source, member.getMemberId(), ip);
        return SldResponse.success("会员签到成功",vo);
    }

}
