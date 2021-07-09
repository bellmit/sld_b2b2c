package com.slodon.b2b2c.controller.msg.seller;

import cn.hutool.extra.mail.MailUtil;
import com.slodon.b2b2c.core.constant.SMSConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.random.RandomUtil;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.sms.YunPianJsonResult;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.msg.MsgSendModel;
import com.slodon.b2b2c.model.msg.SmsCodeModel;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.msg.pojo.SmsCode;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.pojo.Vendor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "seller-手机验证码")
@RestController
@Slf4j
@RequestMapping("v3/msg/seller/commons")
public class SellerVerifyController extends BaseController {

    @Resource
    private VendorModel vendorModel;
    @Resource
    private SmsCodeModel smsCodeModel;
    @Resource
    private MsgSendModel msgSendModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("获取验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "手机号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "注册账号-register;找回密码-retrieve", required = true, paramType = "query"),
            @ApiImplicitParam(name = "verifyCode", value = "图形验证码，类型为注册时必传", paramType = "query"),
            @ApiImplicitParam(name = "verifyKey", value = "图形验证码key，类型为注册时必传", paramType = "query"),
    })
    @PostMapping("smsCode")
    public JsonResult smsCode(HttpServletRequest request,
                              @RequestParam("mobile") String mobile,
                              @RequestParam("type") String type,
                              @RequestParam(value = "verifyCode", required = false) String verifyCode,
                              @RequestParam(value = "verifyKey", required = false) String verifyKey) {
        // 记录手机短信验证码
        SmsCode code = new SmsCode();
        code.setMobile(mobile);
        code.setRequestIp(WebUtil.getRealIp(request));
        code.setCreateTime(new Date());
        code.setMemberId(0);
        code.setMemberName("");

        VendorExample example = new VendorExample();
        example.setVendorMobile(mobile);
        List<Vendor> vendorList = vendorModel.getVendorList(example, null);

        if ("register".equals(type)) {
            AssertUtil.isTrue(!CollectionUtils.isEmpty(vendorList), "该手机号已注册");
            //注册账号
            //验证图形验证码
            String verifyNumber = stringRedisTemplate.opsForValue().get(verifyKey);
            AssertUtil.isTrue(verifyNumber == null
                    || !verifyNumber.equalsIgnoreCase(verifyCode), "图形验证码不正确");

            code.setSmsType(SMSConst.SMS_TYPE_1);
        } else if ("retrieve".equals(type)) {
            //找回密码
            AssertUtil.notEmpty(vendorList, "对不起，该手机号码未注册");

            code.setSmsType(SMSConst.SMS_TYPE_3);
        }
        return sendSms(mobile, code, 60L * 10);
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
        MailUtil.send(email, "邮箱验证码", content.toString(), false);
        //将随机数存在redis中
        stringRedisTemplate.opsForValue().set(email, verif, expireTime, TimeUnit.SECONDS);
        return SldResponse.success("发送成功");
    }
}
