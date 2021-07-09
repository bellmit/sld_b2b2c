package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.core.constant.MemberIntegralLogConst;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberIntegralLog;
import com.slodon.b2b2c.model.member.MemberIntegralLogModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.example.MemberIntegralLogExample;
import com.slodon.b2b2c.vo.member.IntegralLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Api(tags = "front-我的积分")
@RestController
@RequestMapping("v3/member/front/integralLog")
public class MemberIntegralLogController extends BaseController {

    @Resource
    private MemberIntegralLogModel memberIntegralLogModel;
    @Resource
    private MemberModel memberModel;

    /**
     * 我的积分
     *
     * @param request
     * @return
     */
    @ApiOperation("我的积分")
    @GetMapping("getMemberIntegral")
    public JsonResult getMemberIntegral(HttpServletRequest request) {

        Member member = UserUtil.getUser(request, Member.class);
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        //响应
        HashMap<String, Object> map = new HashMap<>();
        map.put("memberIntegral", memberDb.getMemberIntegral());   //会员可用积分

        return SldResponse.success(map);
    }

    /**
     * 收入/支出
     */
    @ApiOperation("收入/支出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型:收入 1, 支出 2", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @PostMapping("list")
    public JsonResult<PageVO<IntegralLogVO>> getList(HttpServletRequest request, Integer type) {

        Member member = UserUtil.getUser(request, Member.class);
        //分页
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //查询
        MemberIntegralLogExample example = new MemberIntegralLogExample();
        example.setMemberId(member.getMemberId());
        if (type == MemberIntegralLogConst.TYPE_OUT) {
            //支出
            example.setTypeValue(MemberIntegralLogConst.TYPE_6 + "," + MemberIntegralLogConst.TYPE_7 + "," + MemberIntegralLogConst.TYPE_9 + "," + MemberIntegralLogConst.TYPE_12 + "," + MemberIntegralLogConst.TYPE_13 + "," + MemberIntegralLogConst.TYPE_14);
        } else if (type == MemberIntegralLogConst.TYPE_IN){
            //收入
            example.setTypeValue(MemberIntegralLogConst.TYPE_1 + "," + MemberIntegralLogConst.TYPE_2 + "," + MemberIntegralLogConst.TYPE_3 + "," + MemberIntegralLogConst.TYPE_4 + "," + MemberIntegralLogConst.TYPE_5 + "," + MemberIntegralLogConst.TYPE_8 + "," + MemberIntegralLogConst.TYPE_10 + "," + MemberIntegralLogConst.TYPE_11);
        }
        List<MemberIntegralLog> list = memberIntegralLogModel.getMemberIntegralLogList(example, pager);
        ArrayList<IntegralLogVO> vos = new ArrayList<>();
        list.forEach(memberIntegralLog -> {
            IntegralLogVO vo = new IntegralLogVO(memberIntegralLog);
            vos.add(vo);
        });

        return SldResponse.success(new PageVO<>(vos, pager));
    }

}
