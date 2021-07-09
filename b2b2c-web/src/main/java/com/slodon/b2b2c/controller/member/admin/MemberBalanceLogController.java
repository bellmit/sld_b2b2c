package com.slodon.b2b2c.controller.member.admin;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.example.MemberBalanceLogExample;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import com.slodon.b2b2c.model.member.MemberBalanceLogModel;
import com.slodon.b2b2c.vo.member.MemberBalanceLogVO;
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
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-资金明细")
@RestController
@RequestMapping("v3/member/admin/balanceLog")
public class MemberBalanceLogController extends BaseController {

    @Resource
    private MemberBalanceLogModel memberBalanceLogModel;

    @ApiOperation("资金明细列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName", value = "会员名", paramType = "query"),
            @ApiImplicitParam(name = "adminName", value = "操作管理员", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("/list")
    public JsonResult<PageVO<MemberBalanceLogVO>> getList(HttpServletRequest request,
                                                          @RequestParam(value = "memberName", required = false) String memberName,
                                                          @RequestParam(value = "adminName", required = false) String adminName,
                                                          @RequestParam(value = "startTime", required = false) Date startTime,
                                                          @RequestParam(value = "endTime", required = false) Date endTime) {

        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据条件查询集合
        MemberBalanceLogExample example = new MemberBalanceLogExample();
        example.setMemberNameLike(memberName);
        example.setAdminNameLike(adminName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);

        //响应
        List<MemberBalanceLog> list = memberBalanceLogModel.getMemberBalanceLogList(example, pager);
        ArrayList<MemberBalanceLogVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(memberBalanceLog -> {
                MemberBalanceLogVO vo = new MemberBalanceLogVO(memberBalanceLog);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("导出Excel")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName", value = "会员名称"),
            @ApiImplicitParam(name = "adminName", value = "操作人名称"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间")
    })
    @GetMapping("memberBalanceLogExport")
    public JsonResult memberBalanceLogExport(HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam(value = "memberName", required = false) String memberName,
                                             @RequestParam(value = "adminName", required = false) String adminName,
                                             @RequestParam(value = "startTime", required = false) Date startTime,
                                             @RequestParam(value = "endTime", required = false) Date endTime) {

        //查询资金明细列表
        MemberBalanceLogExample example = new MemberBalanceLogExample();
        example.setMemberNameLike(memberName);
        example.setAdminNameLike(adminName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);
        memberBalanceLogModel.balanceLogListExport(example, request, response);
        return null;
    }

}
