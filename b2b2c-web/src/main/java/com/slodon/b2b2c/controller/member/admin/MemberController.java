package com.slodon.b2b2c.controller.member.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.MemberIntegralLogConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.dto.MemberAddDTO;
import com.slodon.b2b2c.member.dto.MemberIntegralLogUpdateDTO;
import com.slodon.b2b2c.member.dto.MemberUpdateDTO;
import com.slodon.b2b2c.member.dto.MemberUpdatePwdDTO;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.member.MemberIntegralLogModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.member.MemberDetailVO;
import com.slodon.b2b2c.vo.member.MemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-会员列表")
@RestController
@RequestMapping("v3/member/admin/member")
public class MemberController extends BaseController {

    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberIntegralLogModel memberIntegralLogModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("新增会员")
    @OperationLogger(option = "新增会员")
    @PostMapping("add")
    public JsonResult<Integer> addMember(HttpServletRequest request, MemberAddDTO memberAddDTO) throws Exception {
        //参数校验
        AssertUtil.isTrue(!memberAddDTO.getLoginPwd().equals(memberAddDTO.getConfirmPwd()), "密码输入不一致,请重试!");
        AssertUtil.isTrue(memberAddDTO.getMemberName().length() > 20, "会员名最多输入20个字");
        //保存
        memberModel.saveMember(memberAddDTO);
        return SldResponse.success("添加成功", memberAddDTO.getMemberName());
    }

    @ApiOperation("删除会员")
    @OperationLogger(option = "删除会员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberIds", value = "会员id集合,用逗号隔开", required = true, paramType = "query")
    })
    @PostMapping("del")
    public JsonResult deleteMember(HttpServletRequest request, String memberIds) {

        //参数校验
        AssertUtil.notEmpty(memberIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(memberIds, "memberIds格式错误,请重试");

        memberModel.batchDeleteMember(memberIds);

        return SldResponse.success("删除成功");
    }

    @ApiOperation("编辑会员")
    @OperationLogger(option = "编辑会员")
    @PostMapping("edit")
    public JsonResult editMember(HttpServletRequest request, MemberUpdateDTO memberUpdateDTO) throws Exception {

        //编辑
        memberModel.updateMember(memberUpdateDTO);
        return SldResponse.success("编辑成功", memberUpdateDTO.getMemberName());
    }

    @ApiOperation("会员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "memberMobile", value = "mobile", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "会员状态：0-禁用，1-启用", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<MemberVO>> getList(HttpServletRequest request,
                                                @RequestParam(value = "memberName", required = false) String memberName,
                                                @RequestParam(value = "memberMobile", required = false) String memberMobile,
                                                @RequestParam(value = "state", required = false) Integer state,
                                                @RequestParam(value = "startTime", required = false) Date startTime,
                                                @RequestParam(value = "endTime", required = false) Date endTime) {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据条件查询集合
        MemberExample example = new MemberExample();
        example.setMemberNameLike(memberName);
        example.setMemberMobileLike(memberMobile);
        example.setState(state);
        example.setRegisterTimeAfter(startTime);
        example.setRegisterTimeBefore(endTime);
        List<Member> list = memberModel.getMemberList(example, pager);

        //响应
        List<MemberVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(member -> {
                if (StringUtil.isEmpty(member.getMemberAvatar()) && StringUtil.isEmpty(member.getWxAvatarImg())) {
                    member.setMemberAvatar(stringRedisTemplate.opsForValue().get("default_image_user_portrait"));
                }
                MemberVO vo = new MemberVO(member);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("会员详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "会员id", paramType = "query", required = true)
    })
    @GetMapping("detail")
    public JsonResult<MemberDetailVO> getMember(HttpServletRequest request, @RequestParam("memberId") Integer memberId) {

        //查询详情
        MemberDetailVO vo = memberModel.getMember(memberId);

        return SldResponse.success(vo);
    }

    @ApiOperation("编辑会员状态")
    @OperationLogger(option = "编辑会员状态")
    @ApiImplicitParams({
//            @ApiImplicitParam(name = "state",value = "会员状态：0-禁用，1-启用",paramType = "query",required = true)
            @ApiImplicitParam(name = "memberId", value = "会员id", paramType = "query", required = true)
    })
    @PostMapping("editState")
    public JsonResult editMemberState(HttpServletRequest request, @RequestParam("memberId") Integer memberId) throws Exception {

        //编辑
        memberModel.updateMemberState(memberId);
        return SldResponse.success("编辑会员状态成功");
    }

    @ApiOperation("修改密码")
    @OperationLogger(option = "修改密码")
    @PostMapping("editPwd")
    public JsonResult editPwd(HttpServletRequest request, MemberUpdatePwdDTO memberUpdatePwdDTO) throws Exception {
        //参数校验
        AssertUtil.isTrue(!memberUpdatePwdDTO.getLoginPwd().equals(memberUpdatePwdDTO.getConfirmPwd()), "密码输入不一致,请重试!");
        memberModel.updateMember(memberUpdatePwdDTO);
        return SldResponse.success("密码修改成功");
    }


    @ApiOperation("会员积分设置")
    @OperationLogger(option = "会员积分设置")
    @PostMapping("editMemberIntegral")
    public JsonResult editMemberIntegral(HttpServletRequest request, MemberIntegralLogUpdateDTO memberIntegralLogUpdateDTO) throws Exception {
        AssertUtil.notNull(memberIntegralLogUpdateDTO, "积分设置信息不能为空");
        AssertUtil.notNullOrZero(memberIntegralLogUpdateDTO.getMemberId(), "会员Id不能为空");
        AssertUtil.notNullOrZero(memberIntegralLogUpdateDTO.getType(), "增减类型不能空或0值");
        AssertUtil.isTrue(memberIntegralLogUpdateDTO.getValue() < 0, "积分变化值输入有误");
        Admin admin = UserUtil.getUser(request, Admin.class);
        memberIntegralLogModel.editMemberIntegral(memberIntegralLogUpdateDTO, admin.getAdminId(), admin.getAdminName());
        return SldResponse.success(memberIntegralLogUpdateDTO.getType() == MemberIntegralLogConst.ADMIN_TYPE_1 ? "添加成功" : "减少成功");
    }

}
