package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.member.example.MemberBalanceLogExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import com.slodon.b2b2c.model.member.MemberBalanceLogModel;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.vo.member.BalanceLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-余额明细")
@RestController
@RequestMapping("v3/member/front/balanceLog")
public class FrontMemberBalanceLogController extends BaseController {

    @Resource
    private MemberBalanceLogModel balanceLogModel;

    @ApiOperation("余额明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "状态:0 默认, 1 收入, 2 支出", required = true),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<BalanceLogVO>> getList(HttpServletRequest request,
                                                    @RequestParam("state") Integer state) {
        Member member = UserUtil.getUser(request, Member.class);

        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //根据条件查询集合
        MemberBalanceLogExample example = new MemberBalanceLogExample();
        example.setMemberId(member.getMemberId());
        if (state == 1) {
            example.setTypeIn(MemberConst.TYPE_1 + "," + MemberConst.TYPE_2 + "," + MemberConst.TYPE_5);
        } else if (state == 2) {
            example.setTypeIn(MemberConst.TYPE_3 + "," + MemberConst.TYPE_4 + "," + MemberConst.TYPE_6);
        }

        //响应
        List<MemberBalanceLog> list = balanceLogModel.getMemberBalanceLogList(example, pager);
        ArrayList<BalanceLogVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(memberBalanceLog -> {
                BalanceLogVO vo = new BalanceLogVO(memberBalanceLog);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
