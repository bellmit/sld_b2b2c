package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.Md5;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.member.dto.MemberAddLoginPwdDTO;
import com.slodon.b2b2c.member.dto.MemberAddPayPwdDTO;
import com.slodon.b2b2c.member.dto.MemberUpdateLoginPwdDTO;
import com.slodon.b2b2c.member.dto.MemberUpdatePayPwdDTO;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.member.MemberModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "front-账号安全")
@RestController
@RequestMapping("v3/member/front/memberPassword")
public class MemberPasswordController extends BaseController {

    @Resource
    private MemberModel memberModel;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("会员修改手机号，效验原手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberMobile", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "smsCode", value = "sms验证码", required = true, paramType = "query")
    })
    @PostMapping("verifyOldMobile")
    public JsonResult verifyOldMobile(HttpServletRequest request, String memberMobile, String smsCode) {
        AssertUtil.mobileCheck(memberMobile);

        Member member = UserUtil.getUser(request, Member.class);

        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.isTrue(!memberMobile.equalsIgnoreCase(memberDb.getMemberMobile()), "手机号有误,请重新输入");
        String verify_sms = stringRedisTemplate.opsForValue().get(memberMobile);
        AssertUtil.isTrue(!smsCode.equals(verify_sms), "验证码输入错误，请重试");

        return SldResponse.success("验证成功");
    }

    @ApiOperation("修改手机号码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberMobile", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "smsCode", value = "sms验证码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isUnbound", value = "如果手机号已被其他账号绑定，是否解绑，1-解绑，0-不解绑", paramType = "query")
    })
    @PostMapping("editMobile")
    public JsonResult editMobile(HttpServletRequest request, String memberMobile, String smsCode, Integer isUnbound) {
        AssertUtil.mobileCheck(memberMobile);

        String verify_sms = stringRedisTemplate.opsForValue().get(memberMobile);
        AssertUtil.isTrue(!smsCode.equals(verify_sms), "验证码输入错误，请重试");

        Member member = UserUtil.getUser(request, Member.class);
        //查询最新会员信息
        Member member1 = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.isTrue(!StringUtil.isEmpty(member1.getMemberMobile()) && member1.getMemberMobile().equals(memberMobile), "请输入新的手机号");

        // 查询该手机号是否被其它账号绑定(若有解除绑定)
        MemberExample example = new MemberExample();
        example.setMemberMobile(memberMobile);
        example.setMemberIdNotEquals(member.getMemberId());
        List<Member> memberList = memberModel.getMemberList(example, null);
        if (!CollectionUtils.isEmpty(memberList)) {
            Member memberDb = memberList.get(0);
            if (isUnbound != null && isUnbound == 1) {
                //解绑
                Member memberOld = new Member();
                memberOld.setMemberId(memberDb.getMemberId());
                memberOld.setIsMobileVerify(MemberConst.IS_MOBILE_VERIFY_0);
                memberOld.setMemberMobile("");
                memberOld.setUpdateTime(new Date());
                memberModel.updateMember(memberOld);
            } else {
                return SldResponse.failSpecial("该手机号已被绑定，继续操作，将解除与<" + memberDb.getMemberName() + ">账号的绑定关系，是否继续");
            }
        }
        // 绑定新手机号
        Member memberNew = new Member();
        memberNew.setMemberId(member.getMemberId());
        memberNew.setIsMobileVerify(MemberConst.IS_MOBILE_VERIFY_1);
        memberNew.setMemberMobile(memberMobile);
        memberNew.setUpdateTime(new Date());
        memberModel.updateMember(memberNew);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("会员修改邮箱，效验原邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberEmail", value = "邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(name = "emailCode", value = "邮箱验证码", required = true, paramType = "query")
    })
    @PostMapping("verifyOldEmail")
    public JsonResult verifyOldEmail(HttpServletRequest request, String memberEmail, String emailCode) {
        AssertUtil.emailCheck(memberEmail);

        Member member = UserUtil.getUser(request, Member.class);

        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.isTrue(!memberEmail.equalsIgnoreCase(memberDb.getMemberEmail()), "邮箱有误,请重新输入");
        String verify_email = stringRedisTemplate.opsForValue().get(memberEmail);
        AssertUtil.isTrue(!emailCode.equals(verify_email), "验证码输入错误，请重试");

        return SldResponse.success("验证成功");
    }

    @ApiOperation("修改/设置电子邮箱")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberEmail", value = "邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(name = "emailCode", value = "邮箱验证码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isUnbound", value = "如果邮箱已被其他账号绑定，是否解绑，1-解绑，0-不解绑", paramType = "query")
    })
    @PostMapping("editEmail")
    public JsonResult editEmail(HttpServletRequest request, String memberEmail, String emailCode, Integer isUnbound) {
        AssertUtil.emailCheck(memberEmail);

        String verify_email = stringRedisTemplate.opsForValue().get(memberEmail);
        AssertUtil.isTrue(!emailCode.equals(verify_email), "验证码输入错误，请重试");

        Member member = UserUtil.getUser(request, Member.class);
        //查询最新会员信息
        Member member1 = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.isTrue(!StringUtil.isEmpty(member1.getMemberEmail()) && member1.getMemberEmail().equals(memberEmail), "请输入新的邮箱");

        //查询邮箱是否被绑定
        MemberExample example = new MemberExample();
        example.setMemberIdNotEquals(member.getMemberId());
        example.setMemberEmail(memberEmail);
        List<Member> memberList = memberModel.getMemberList(example, null);
        if (!CollectionUtils.isEmpty(memberList)) {
            //邮箱已被其他账号绑定
            Member memberDb = memberList.get(0);
            if (isUnbound != null && isUnbound == 1) {
                //解绑
                Member memberOld = new Member();
                memberOld.setMemberId(memberDb.getMemberId());
                memberOld.setMemberEmail("");
                memberOld.setIsEmailVerify(MemberConst.IS_EMAIL_VERIFY_0);
                memberOld.setUpdateTime(new Date());
                memberModel.updateMember(memberOld);
            } else {
                //提示被绑定
                return SldResponse.failSpecial("该邮箱已被绑定，继续操作，将解除与<" + memberDb.getMemberName() + ">账号的绑定关系，是否继续");
            }
        }

        // 绑定新邮箱
        Member memberNew = new Member();
        memberNew.setMemberId(member.getMemberId());
        memberNew.setIsEmailVerify(MemberConst.IS_EMAIL_VERIFY_1);
        memberNew.setMemberEmail(memberEmail);
        memberNew.setUpdateTime(new Date());
        memberModel.updateMember(memberNew);
        return SldResponse.success("修改/设置电子邮箱成功");
    }

    @ApiOperation("修改登录密码")
    @PostMapping("editLoginPwd")
    public JsonResult editLoginPwd(HttpServletRequest request, MemberUpdateLoginPwdDTO memberUpdateLoginPwdDTO) {
        Member member = UserUtil.getUser(request, Member.class);
        //参数校验
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.isTrue(memberUpdateLoginPwdDTO.getOldLoginPwd().equals(memberUpdateLoginPwdDTO.getLoginPwd()), "新密码与原密码不能一样");
        AssertUtil.isTrue(!Md5.getMd5String(memberUpdateLoginPwdDTO.getOldLoginPwd()).equalsIgnoreCase(memberDb.getLoginPwd()), "原密码不对,请重新输入");

        memberModel.editLoginPwd(memberUpdateLoginPwdDTO, member.getMemberId());
        return SldResponse.success("修改登录密码成功");
    }

    @ApiOperation("设置登录密码")
    @PostMapping("addLoginPwd")
    public JsonResult addLoginPwd(HttpServletRequest request, MemberAddLoginPwdDTO memberAddLoginPwdDTO) {
        //参数校验
        Member member = UserUtil.getUser(request, Member.class);
        //验证验证码
        String smsCode = stringRedisTemplate.opsForValue().get(memberAddLoginPwdDTO.getMemberMobile());
        AssertUtil.isTrue(StringUtil.isEmpty(smsCode) || !smsCode.equalsIgnoreCase(memberAddLoginPwdDTO.getVerifyCode()), "您输入的手机验证码有误,请重新输入");

        memberModel.addLoginPwd(memberAddLoginPwdDTO, member.getMemberId());
        return SldResponse.success("设置登录密码成功");
    }

    @ApiOperation("重置登录密码")
    @PostMapping("resetLoginPwd")
    public JsonResult resetLoginPwd(HttpServletRequest request, MemberAddLoginPwdDTO memberAddLoginPwdDTO) {
        //根据手机号查找会员
        MemberExample example = new MemberExample();
        example.setMemberMobile(memberAddLoginPwdDTO.getMemberMobile());
        example.setIsMobileVerify(MemberConst.IS_MOBILE_VERIFY_1);
        List<Member> list = memberModel.getMemberList(example, null);
        AssertUtil.notEmpty(list, "您输入的手机号未验证,请重新输入");
        //一个手机号只能绑定一个会员
        Member memberDb = list.get(0);

        //验证验证码
        String smsCode = stringRedisTemplate.opsForValue().get(memberAddLoginPwdDTO.getMemberMobile());
        AssertUtil.isTrue(StringUtil.isEmpty(smsCode) || !smsCode.equalsIgnoreCase(memberAddLoginPwdDTO.getVerifyCode()), "您输入的手机验证码有误,请重新输入");

        memberModel.addLoginPwd(memberAddLoginPwdDTO, memberDb.getMemberId());
        return SldResponse.success("重置登录密码成功");
    }

    @ApiOperation("设置/重置支付密码")
    @PostMapping("addPayPwd")
    public JsonResult addPayPwd(HttpServletRequest request, MemberAddPayPwdDTO memberAddPayPwdDTO) {
        //参数校验
        //验证验证码
        Member member = UserUtil.getUser(request, Member.class);
        Member memberDB = memberModel.getMemberByMemberId(member.getMemberId());
        String smsCode = stringRedisTemplate.opsForValue().get(memberAddPayPwdDTO.getMemberMobile());
        AssertUtil.isTrue(StringUtil.isEmpty(smsCode) || !smsCode.equalsIgnoreCase(memberAddPayPwdDTO.getVerifyCode()), "您输入的手机验证码有误,请重新输入");

        memberModel.addPayPwd(memberAddPayPwdDTO, member.getMemberId());
        return SldResponse.success("设置/重置支付密码成功");
    }

    @ApiOperation("修改支付密码")
    @PostMapping("editPayPwd")
    public JsonResult editPayPwd(HttpServletRequest request, MemberUpdatePayPwdDTO memberUpdatePayPwdDTO) {
        Member member = UserUtil.getUser(request, Member.class);
        //参数校验
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.isTrue(memberUpdatePayPwdDTO.getOldPayPwd().equals(memberUpdatePayPwdDTO.getPayPwd()), "新密码与原密码不能一样");
        AssertUtil.isTrue(!Md5.getMd5String(memberUpdatePayPwdDTO.getOldPayPwd()).equalsIgnoreCase(memberDb.getPayPwd()), "原密码不对,请重新输入");

        memberModel.editPayPwd(memberUpdatePayPwdDTO, member.getMemberId());
        return SldResponse.success("修改支付密码成功");
    }

}
