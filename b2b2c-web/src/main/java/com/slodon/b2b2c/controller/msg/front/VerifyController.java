package com.slodon.b2b2c.controller.msg.front;

import com.slodon.b2b2c.core.constant.SMSConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.random.RandomUtil;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.sms.YunPianJsonResult;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.msg.MsgSendModel;
import com.slodon.b2b2c.model.msg.SmsCodeModel;
import com.slodon.b2b2c.msg.pojo.SmsCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "front-手机验证码")
@RestController
@Slf4j
@RequestMapping("v3/msg/front/commons")
public class VerifyController extends BaseController {

    @Resource
    private MemberModel memberModel;
    @Resource
    private SmsCodeModel smsCodeModel;
    @Resource
    private MsgSendModel msgSendModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("获取验证码(免登录注册使用)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "verifyCode", value = " 图形验证码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "verifyKey", value = " 图形验证码key", required = true, paramType = "query")
    })
    @GetMapping("getCaptcha")
    public JsonResult getCaptcha(HttpServletRequest request, String mobile, String verifyKey, String verifyCode) {
        AssertUtil.notEmpty(mobile, "请输入手机号码");

        // 记录手机短信验证码
        SmsCode code = new SmsCode();
        code.setMobile(mobile);
        code.setRequestIp(WebUtil.getRealIp(request));
        code.setCreateTime(new Date());

        //获得redis中的验证码
        String verify_number = stringRedisTemplate.opsForValue().get(verifyKey);
        AssertUtil.isTrue(verify_number == null || !verify_number.equalsIgnoreCase(verifyCode), "验证码不正确");

        //查询是否已注册
        MemberExample example = new MemberExample();
        example.setMemberMobile(mobile);
        List<Member> memberList = memberModel.getMemberList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(memberList), "该手机号码已注册");

        code.setMemberId(0);
        code.setMemberName("");
        code.setSmsType(SMSConst.SMS_TYPE_1);
        return sendSms(mobile, code, 60 * 10);
    }

    @ApiOperation("获取验证码(免密登录或微信授权使用)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "sms验证码类型，免密登录时必填,值为:free，微信授权时必填,值为:wxAuth", required = true, paramType = "query")
    })
    @GetMapping("smsCode")
    public JsonResult smsCode(HttpServletRequest request, String mobile, String type) {
        // 记录手机短信验证码
        SmsCode code = new SmsCode();
        code.setMobile(mobile);
        code.setRequestIp(WebUtil.getRealIp(request));
        code.setCreateTime(new Date());

        MemberExample example = new MemberExample();
        example.setMemberMobile(mobile);
        List<Member> memberList = memberModel.getMemberList(example, null);

        if ("free".equals(type)) {
            AssertUtil.notEmpty(memberList, "对不起，该手机号码未注册");
//
            code.setMemberId(memberList.get(0).getMemberId());
            code.setMemberName(memberList.get(0).getMemberName());
            code.setSmsType(SMSConst.SMS_TYPE_2);
        } else if ("wxAuth".equals(type)) {
            code.setMemberId(0);
            code.setMemberName("");
            code.setSmsType(SMSConst.SMS_TYPE_4);
        }
        return sendSms(mobile, code, 60L * 10);
    }

    @ApiOperation("获取个人中心验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "verifyType", value = "验证码接收类型 1==邮箱，2==手机", required = true, paramType = "query"),
            @ApiImplicitParam(name = "verifyAddr", value = "验证码接收地址（邮箱或手机）", required = true, paramType = "query"),
            @ApiImplicitParam(name = "changeType", value = "会员修改手机/邮箱时必传，old==原账号获取验证码；new==新账号获取验证码", paramType = "query")
    })
    @GetMapping("sendVerifyCode")
    public JsonResult sendVerifyCode(HttpServletRequest request, String verifyAddr, Integer verifyType, String changeType) {
        Member member = UserUtil.getUser(request, Member.class);

        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.isTrue(StringUtil.isNullOrZero(verifyType) || verifyType > 2, "请选择接收地址类型");
        AssertUtil.isTrue(StringUtil.isEmpty(verifyAddr), "请输入手机或邮箱");

        JsonResult result;
        //验证码过期时间,单位秒
        long expireTime;
        if (!StringUtils.isEmpty(changeType) && changeType.equalsIgnoreCase("old")) {
            //修改手机号或邮箱时，旧账号获取验证码时，验证码保存时间加长
            expireTime = 60L * 5;
        } else {
            expireTime = 60L * 10;
        }

        if (!StringUtils.isEmpty(changeType) && changeType.equalsIgnoreCase("new")) {
            //安全性验证 && 防刷短信
            //修改手机号或邮箱时，新账号获取验证码时，验证redis中是否存了旧账号的验证码
            String oldAddr = verifyType == 1 ? memberDb.getMemberEmail() : memberDb.getMemberMobile();
            if (StringUtils.isEmpty(oldAddr)) {
                //未绑定手机号或邮箱，不能修改
                return SldResponse.fail("系统异常");
            }
            String oldVerify = stringRedisTemplate.opsForValue().get(oldAddr);
            if (StringUtils.isEmpty(oldVerify)) {
                //未生成验证码或验证码过期
                return SldResponse.failSpecial("验证失效，请重新验证");
            }
        }
        switch (verifyType) {
            case 2:
                // 记录手机短信验证码
                SmsCode code = new SmsCode();
                code.setMemberId(memberDb.getMemberId());
                code.setMemberName(memberDb.getMemberName());
                code.setMobile(verifyAddr);
                code.setSmsType(SMSConst.SMS_TYPE_4);
                code.setRequestIp(WebUtil.getRealIp(request));
                code.setCreateTime(new Date());
                result = sendSms(verifyAddr, code, expireTime);
                break;
            case 1:
                result = sendEmail(verifyAddr, expireTime);
                break;
            default:
                result = SldResponse.fail("不支持的验证方式");
        }
        return result;
    }

    /**
     * 发送短信
     *
     * @param mobile
     * @param code
     * @return
     */
    private JsonResult sendSms(String mobile, SmsCode code, long expireTime) {
        Map<String, Object> param = new HashMap<>();
        param.put("mobile", mobile);
        param.put("apiKey", stringRedisTemplate.opsForValue().get("notification_sms_key"));
        param.put("tplContent", stringRedisTemplate.opsForValue().get("notification_sms_tpl_content_vc"));
        param.put("app", stringRedisTemplate.opsForValue().get("basic_site_name"));
        YunPianJsonResult verifyCodeResult = msgSendModel.sendVerifyCode(param);

        code.setVerifyCode(verifyCodeResult.getVerifyCode());
        smsCodeModel.saveSmsCode(code);

        //将随机数存在redis中
        stringRedisTemplate.opsForValue().set(mobile, verifyCodeResult.getVerifyCode(), expireTime, TimeUnit.SECONDS);
        return SldResponse.success("发送成功");
    }

    /**
     * 发送邮件
     *
     * @param email
     * @return
     */
    private JsonResult sendEmail(String email, long expireTime) {
        String verif = RandomUtil.randomNumber(4);
        // 邮件发送服务
        StringBuffer content = new StringBuffer("本次操作的验证码：");
        content.append(verif);
        msgSendModel.sendMail(email, "邮箱验证码", content.toString());
        //将随机数存在redis中
        stringRedisTemplate.opsForValue().set(email, verif, expireTime, TimeUnit.SECONDS);
        return SldResponse.success("发送成功");
    }
}
