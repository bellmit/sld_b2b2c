package com.slodon.b2b2c.controller.sso.front;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.*;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.user.UserAuthority;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.member.example.MemberIntegralLogExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberIntegralLog;
import com.slodon.b2b2c.model.business.CartModel;
import com.slodon.b2b2c.model.member.MemberIntegralLogModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.msg.AppClientLogModel;
import com.slodon.b2b2c.msg.example.AppClientLogExample;
import com.slodon.b2b2c.msg.pojo.AppClientLog;
import com.slodon.b2b2c.starter.mq.entity.MemberIntegralVO;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.util.JWTRSA256Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.*;

@Api(tags = "会员登录")
@RestController
@RequestMapping("v3/frontLogin/oauth")
@Slf4j
public class FrontAuthController {

    @Resource
    private MemberModel memberModel;
    @Resource
    private CartModel cartModel;
    @Resource
    private AppClientLogModel appClientLogModel;
    @Resource
    private MemberIntegralLogModel memberIntegralLogModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("账号密码登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginType", value = "登陆类型：1-账号密码登陆，2-手机验证码登陆", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "登陆类型为1时：是用户名；为2时：是手机号", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "登陆类型为1时：是密码；为2时：是验证码", paramType = "query"),
            @ApiImplicitParam(name = "cartInfo", value = "离线购物车信息 如 [{\"productId\":1,\"buyNum\":1},{\"productId\":2,\"buyNum\":2}]", paramType = "query"),
            @ApiImplicitParam(name = "clientId", value = "客户端身份ID app传参", paramType = "query"),
            @ApiImplicitParam(name = "alias", value = "客户端身份别名 app传参", paramType = "query")
    })
    @PostMapping("token")
    public JsonResult doLogin(HttpServletRequest request, @RequestParam(value = "loginType", required = false, defaultValue = "1") Integer loginType,
                              String username, String password, String cartInfo, String clientId, String alias, String refresh_token) throws ParseException {
        Member member = null;
        if (!StringUtils.isEmpty(refresh_token)) {
            //校验token
            String memberId = JWTRSA256Util.validToken(refresh_token);
            member = memberModel.getMemberByMemberId(Integer.parseInt(memberId));
        } else {
            // 登录验证
            MemberExample memberExample = new MemberExample();
            memberExample.setMemberNameOrMemberMobile(username.replace(":", ""));
            List<Member> memberList = memberModel.getMemberList(memberExample, null);
            AssertUtil.notEmpty(memberList, "用户名或密码错误");
            member = memberList.get(0);
            if (loginType == 1) {
                AssertUtil.isTrue(!member.getLoginPwd().equals(Md5.getMd5String(password)), "用户名或密码错误！");
            } else {
                //验证手机验证码
                String verifyCode = stringRedisTemplate.opsForValue().get(username);
                AssertUtil.isTrue(verifyCode == null || !verifyCode.equals(password), "验证码输入错误，请重试！");
            }
            AssertUtil.isTrue(!member.getState().equals(MemberConst.STATE_1), "会员账户被禁用，请联系平台客服！");
        }

        //定义一个uuid，用做存储redis的key，并且设置到token中
        String uuid = UUID.randomUUID().toString();
        //生成access_token和refresh_token
        ModelMap modelMap = new ModelMap();
        modelMap.put("access_token", JWTRSA256Util.buildToken(member.getMemberId().toString(), uuid, WebConst.WEB_IDENTIFY_FRONT));
        modelMap.put("refresh_token", JWTRSA256Util.buildRefreshToken(member.getMemberId().toString(), uuid));
        //离线购物车信息入库
        if (!StringUtils.isEmpty(cartInfo)) {
            List<Cart> cartList = JSONObject.parseArray(cartInfo, Cart.class);
            cartModel.addCartByMemberId(cartList, member.getMemberId());
        }
        if (!StringUtils.isEmpty(clientId) || !StringUtils.isEmpty(alias)) {
            //app登陆，记录一条clientId,用于app消息提醒
            AppClientLogExample appClientLogExample = new AppClientLogExample();
            appClientLogExample.setClientId(clientId);
            appClientLogExample.setAlias(alias);
            List<AppClientLog> appClientLogList = appClientLogModel.getAppClientLogList(appClientLogExample, null);
            if (CollectionUtils.isEmpty(appClientLogList)) {
                //没有该记录，插入新的数据
                AppClientLog appClientLog = new AppClientLog();
                appClientLog.setUserId(member.getMemberId().longValue());
                appClientLog.setClientId(clientId);
                appClientLog.setAlias(alias);
                appClientLog.setUpdateTime(new Date());
                appClientLogModel.saveAppClientLog(appClientLog);
            } else {
                //有记录，可能是refreshToken过期后重新登陆更新
                appClientLogModel.updateAppClientLogByParams(clientId, alias);
            }
        }
        //构造存储对象
        UserAuthority<Member> userAuthority = new UserAuthority<>();
        userAuthority.setT(member);
        userAuthority.addAuthority("/v*/*/front/**");
        objectRedisTemplate.opsForHash().put("front-" + member.getMemberId().toString(), uuid, userAuthority);
        objectRedisTemplate.expire("front-" + member.getMemberId().toString(), ExpireTimeConst.EXPIRE_SECOND_1_HOUR + 5, TimeUnit.SECONDS);//用户信息比access_token多5秒，防止token未过期时提示未授权

        //登录赠送积分
        String integralPresentLogin = stringRedisTemplate.opsForValue().get("integral_present_login");
        Integer loginIntegral = StringUtil.isEmpty(integralPresentLogin) ? 0 : Integer.parseInt(integralPresentLogin);
        if (loginIntegral > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date startTime = sdf.parse(TimeUtil.getToday() + " 00:00:00");
            Date endTime = sdf.parse(TimeUtil.getToday() + " 23:59:59");
            //查询今天登录是否赠送积分，为空则插入
            MemberIntegralLogExample example = new MemberIntegralLogExample();
            example.setMemberId(member.getMemberId());
            example.setType(MemberIntegralLogConst.TYPE_2);
            example.setCreateTimeAfter(startTime);
            example.setCreateTimeBefore(endTime);
            List<MemberIntegralLog> list = memberIntegralLogModel.getMemberIntegralLogList(example, null);
            if (CollectionUtils.isEmpty(list)) {
                MemberIntegralVO memberIntegralVO = new MemberIntegralVO(member.getMemberId(), member.getMemberName(), loginIntegral,
                        MemberIntegralLogConst.TYPE_2, "会员登录", "", 0, "");
                rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_INTEGRAL, memberIntegralVO);

                member.setMemberIntegral(member.getMemberIntegral() + loginIntegral);
                //发送积分变动消息通知
                this.sendMsgIntegralChange(member, "登录赠送积分");
            }
        }
        //更新会员信息
        String ip = WebUtil.getRealIp(request);
        Member updateMember = new Member();
        updateMember.setMemberId(member.getMemberId());
        updateMember.setLastLoginIp(ip);
        updateMember.setLastLoginTime(new Date());
        updateMember.setLoginNumber(member.getLoginNumber() + 1);
        updateMember.setUpdateTime(new Date());
        memberModel.updateMember(updateMember);
        return SldResponse.success(modelMap);
    }

    /**
     * 检测refreshToken是否有效
     *
     * @param refreshToken
     * @return refreshToken中存放用户信息的uuid
     */
    private String checkRefreshToken(String refreshToken) {
        AssertUtil.notEmpty(refreshToken, "未登录");
        try {
            //解析 refreshToken
            JWTClaimsSet jwtClaimsSet = JWTParser.parse(refreshToken).getJWTClaimsSet();
            //获取其中的uuid
            String uuid = jwtClaimsSet.getStringClaim("uuid");
            //获取其中的user_id
            String userId = jwtClaimsSet.getStringClaim("user_id");
            //查询redis中是否有用户信息
            Object user = objectRedisTemplate.opsForHash().get(jwtClaimsSet.getStringClaim("webIdentify") + "-" + userId, uuid);
            return user != null ? uuid : null;
        } catch (ParseException e) {
            return null;
        }
    }

    @ApiOperation("用户登出接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refresh_token", value = "刷新token", required = true, paramType = "query")
    })
    @PostMapping("logout")
    public JsonResult logout(HttpServletRequest request, String refresh_token) throws ParseException {
        //检测refresh_token,获取token中存放用户信息的uuid
        String uuid = this.checkRefreshToken(refresh_token);
        if (uuid == null) {
            //refresh_token已失效，不做处理
            return SldResponse.success("退出成功");
        }
        //解析 refreshToken
        JWTClaimsSet jwtClaimsSet = JWTParser.parse(refresh_token).getJWTClaimsSet();
        //获取其中的user_id
        String userId = jwtClaimsSet.getStringClaim("user_id");
        //清除redis中的用户信息
        objectRedisTemplate.opsForHash().delete(jwtClaimsSet.getStringClaim("webIdentify") + "-" + userId, uuid);

        return SldResponse.success("退出成功");
    }

    @ApiOperation("注册会员账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", required = true),
            @ApiImplicitParam(name = "code", value = "手机号验证码", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "图形验证码", required = true),
            @ApiImplicitParam(name = "verifyKey", value = "图形验证码key", required = true)

    })
    @PostMapping("register")
    public JsonResult registerMember(HttpServletRequest request, String phone, String code, String verifyCode, String verifyKey) {
        //判断会员表中是否有重复的手机号
        MemberExample memberExample = new MemberExample();
        memberExample.setMemberMobile(phone);
        List<Member> memberList = memberModel.getMemberList(memberExample, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(memberList), "该手机号已注册");
        if (!CollectionUtils.isEmpty(memberList)) {
            throw new MallException("该手机号已注册");
        }

        //获取图像验证码
        //从redis中获取
        String verifyNumber = stringRedisTemplate.opsForValue().get(verifyKey);
        AssertUtil.isTrue(verifyNumber == null
                || !verifyNumber.equalsIgnoreCase(verifyCode), "图形验证码不正确");

        //验证手机验证码
        String confirmCode = stringRedisTemplate.opsForValue().get(phone);
        AssertUtil.isTrue(confirmCode == null || !confirmCode.equals(code), "手机验证码不正确");

        Member member = new Member();
        member.setMemberName(GoodsIdGenerator.getMemberName());
        member.setMemberMobile(phone);
        member.setLoginPwd("");
        member.setPayPwd("");
        member.setLastLoginIp("");
        member.setLoginNumber(0);
        member.setRegisterTime(new Date());
        member.setLastLoginTime(new Date());
        member.setRegisterChannel(MemberConst.MEMBER_FROM_PC);
        member.setIsMobileVerify(MemberConst.IS_MOBILE_VERIFY_1);
        Integer memberId = memberModel.saveMember(member);

        //注册赠送积分
        Integer registerIntegral = Integer.parseInt(stringRedisTemplate.opsForValue().get("integral_present_register"));
        if (registerIntegral > 0) {
            MemberIntegralVO memberIntegralVO = new MemberIntegralVO(memberId, member.getMemberName(), registerIntegral,
                    MemberIntegralLogConst.TYPE_1, "会员注册", "", 0, "");
            rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_INTEGRAL, memberIntegralVO);

            member.setMemberId(memberId);
            member.setMemberIntegral(registerIntegral);
            //发送积分变动消息通知
            this.sendMsgIntegralChange(member, "注册赠送积分");
        }

        //登陆
        //设置请求头
        Map<String, String> headers = new HashedMap();
        headers.put("Authorization", "Basic ZnJvbnQ6ZnJvbnQ=");
        headers.put("name", null);

        //设置请求参数
        UrlQuery urlQuery = new UrlQuery();
        urlQuery.add("loginType", 2);
        urlQuery.add("username", phone);
        urlQuery.add("password", confirmCode);//从redis中获取验证码

        URI uri;
        try {
            uri = new URI(DomainUrlUtil.SLD_API_URL + "/v3/frontLogin/oauth/token");
        } catch (URISyntaxException e) {
            log.error("登录地址有误", e);
            throw new MallException("登录失败");
        }
        UrlBuilder urlBuilder = UrlBuilder.of(uri, Charset.defaultCharset()).setQuery(urlQuery);

        HttpResponse execute = new HttpRequest(urlBuilder).method(Method.POST).addHeaders(headers).execute();
        return JSONObject.parseObject(execute.body(), JsonResult.class);
    }

    /**
     * 发送积分变动消息通知
     *
     * @param member 会员信息
     */
    public void sendMsgIntegralChange(Member member, String description) {
        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("description", description));
        messageSendPropertyList.add(new MessageSendProperty("availableIntegral", member.getMemberIntegral().toString()));
        //微信消息通知
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "您的账户发生了积分变动。"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", TimeUtil.getDateTimeString(new Date())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", description));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", member.getMemberIntegral().toString()));
        String msgLinkInfo = "{\"type\":\"integral_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", member.getMemberId(), MemberTplConst.INTEGRAL_CHANGE_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }
}
