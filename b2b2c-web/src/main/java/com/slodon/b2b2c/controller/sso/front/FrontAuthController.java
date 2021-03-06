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

@Api(tags = "????????????")
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

    @ApiOperation("????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "loginType", value = "???????????????1-?????????????????????2-?????????????????????", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "???????????????1????????????????????????2??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "password", value = "???????????????1?????????????????????2??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "cartInfo", value = "????????????????????? ??? [{\"productId\":1,\"buyNum\":1},{\"productId\":2,\"buyNum\":2}]", paramType = "query"),
            @ApiImplicitParam(name = "clientId", value = "???????????????ID app??????", paramType = "query"),
            @ApiImplicitParam(name = "alias", value = "????????????????????? app??????", paramType = "query")
    })
    @PostMapping("token")
    public JsonResult doLogin(HttpServletRequest request, @RequestParam(value = "loginType", required = false, defaultValue = "1") Integer loginType,
                              String username, String password, String cartInfo, String clientId, String alias, String refresh_token) throws ParseException {
        Member member = null;
        if (!StringUtils.isEmpty(refresh_token)) {
            //??????token
            String memberId = JWTRSA256Util.validToken(refresh_token);
            member = memberModel.getMemberByMemberId(Integer.parseInt(memberId));
        } else {
            // ????????????
            MemberExample memberExample = new MemberExample();
            memberExample.setMemberNameOrMemberMobile(username.replace(":", ""));
            List<Member> memberList = memberModel.getMemberList(memberExample, null);
            AssertUtil.notEmpty(memberList, "????????????????????????");
            member = memberList.get(0);
            if (loginType == 1) {
                AssertUtil.isTrue(!member.getLoginPwd().equals(Md5.getMd5String(password)), "???????????????????????????");
            } else {
                //?????????????????????
                String verifyCode = stringRedisTemplate.opsForValue().get(username);
                AssertUtil.isTrue(verifyCode == null || !verifyCode.equals(password), "????????????????????????????????????");
            }
            AssertUtil.isTrue(!member.getState().equals(MemberConst.STATE_1), "????????????????????????????????????????????????");
        }

        //????????????uuid???????????????redis???key??????????????????token???
        String uuid = UUID.randomUUID().toString();
        //??????access_token???refresh_token
        ModelMap modelMap = new ModelMap();
        modelMap.put("access_token", JWTRSA256Util.buildToken(member.getMemberId().toString(), uuid, WebConst.WEB_IDENTIFY_FRONT));
        modelMap.put("refresh_token", JWTRSA256Util.buildRefreshToken(member.getMemberId().toString(), uuid));
        //???????????????????????????
        if (!StringUtils.isEmpty(cartInfo)) {
            List<Cart> cartList = JSONObject.parseArray(cartInfo, Cart.class);
            cartModel.addCartByMemberId(cartList, member.getMemberId());
        }
        if (!StringUtils.isEmpty(clientId) || !StringUtils.isEmpty(alias)) {
            //app?????????????????????clientId,??????app????????????
            AppClientLogExample appClientLogExample = new AppClientLogExample();
            appClientLogExample.setClientId(clientId);
            appClientLogExample.setAlias(alias);
            List<AppClientLog> appClientLogList = appClientLogModel.getAppClientLogList(appClientLogExample, null);
            if (CollectionUtils.isEmpty(appClientLogList)) {
                //????????????????????????????????????
                AppClientLog appClientLog = new AppClientLog();
                appClientLog.setUserId(member.getMemberId().longValue());
                appClientLog.setClientId(clientId);
                appClientLog.setAlias(alias);
                appClientLog.setUpdateTime(new Date());
                appClientLogModel.saveAppClientLog(appClientLog);
            } else {
                //?????????????????????refreshToken???????????????????????????
                appClientLogModel.updateAppClientLogByParams(clientId, alias);
            }
        }
        //??????????????????
        UserAuthority<Member> userAuthority = new UserAuthority<>();
        userAuthority.setT(member);
        userAuthority.addAuthority("/v*/*/front/**");
        objectRedisTemplate.opsForHash().put("front-" + member.getMemberId().toString(), uuid, userAuthority);
        objectRedisTemplate.expire("front-" + member.getMemberId().toString(), ExpireTimeConst.EXPIRE_SECOND_1_HOUR + 5, TimeUnit.SECONDS);//???????????????access_token???5????????????token???????????????????????????

        //??????????????????
        String integralPresentLogin = stringRedisTemplate.opsForValue().get("integral_present_login");
        Integer loginIntegral = StringUtil.isEmpty(integralPresentLogin) ? 0 : Integer.parseInt(integralPresentLogin);
        if (loginIntegral > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date startTime = sdf.parse(TimeUtil.getToday() + " 00:00:00");
            Date endTime = sdf.parse(TimeUtil.getToday() + " 23:59:59");
            //??????????????????????????????????????????????????????
            MemberIntegralLogExample example = new MemberIntegralLogExample();
            example.setMemberId(member.getMemberId());
            example.setType(MemberIntegralLogConst.TYPE_2);
            example.setCreateTimeAfter(startTime);
            example.setCreateTimeBefore(endTime);
            List<MemberIntegralLog> list = memberIntegralLogModel.getMemberIntegralLogList(example, null);
            if (CollectionUtils.isEmpty(list)) {
                MemberIntegralVO memberIntegralVO = new MemberIntegralVO(member.getMemberId(), member.getMemberName(), loginIntegral,
                        MemberIntegralLogConst.TYPE_2, "????????????", "", 0, "");
                rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_INTEGRAL, memberIntegralVO);

                member.setMemberIntegral(member.getMemberIntegral() + loginIntegral);
                //??????????????????????????????
                this.sendMsgIntegralChange(member, "??????????????????");
            }
        }
        //??????????????????
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
     * ??????refreshToken????????????
     *
     * @param refreshToken
     * @return refreshToken????????????????????????uuid
     */
    private String checkRefreshToken(String refreshToken) {
        AssertUtil.notEmpty(refreshToken, "?????????");
        try {
            //?????? refreshToken
            JWTClaimsSet jwtClaimsSet = JWTParser.parse(refreshToken).getJWTClaimsSet();
            //???????????????uuid
            String uuid = jwtClaimsSet.getStringClaim("uuid");
            //???????????????user_id
            String userId = jwtClaimsSet.getStringClaim("user_id");
            //??????redis????????????????????????
            Object user = objectRedisTemplate.opsForHash().get(jwtClaimsSet.getStringClaim("webIdentify") + "-" + userId, uuid);
            return user != null ? uuid : null;
        } catch (ParseException e) {
            return null;
        }
    }

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refresh_token", value = "??????token", required = true, paramType = "query")
    })
    @PostMapping("logout")
    public JsonResult logout(HttpServletRequest request, String refresh_token) throws ParseException {
        //??????refresh_token,??????token????????????????????????uuid
        String uuid = this.checkRefreshToken(refresh_token);
        if (uuid == null) {
            //refresh_token????????????????????????
            return SldResponse.success("????????????");
        }
        //?????? refreshToken
        JWTClaimsSet jwtClaimsSet = JWTParser.parse(refresh_token).getJWTClaimsSet();
        //???????????????user_id
        String userId = jwtClaimsSet.getStringClaim("user_id");
        //??????redis??????????????????
        objectRedisTemplate.opsForHash().delete(jwtClaimsSet.getStringClaim("webIdentify") + "-" + userId, uuid);

        return SldResponse.success("????????????");
    }

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "?????????", required = true),
            @ApiImplicitParam(name = "code", value = "??????????????????", required = true),
            @ApiImplicitParam(name = "verifyCode", value = "???????????????", required = true),
            @ApiImplicitParam(name = "verifyKey", value = "???????????????key", required = true)

    })
    @PostMapping("register")
    public JsonResult registerMember(HttpServletRequest request, String phone, String code, String verifyCode, String verifyKey) {
        //?????????????????????????????????????????????
        MemberExample memberExample = new MemberExample();
        memberExample.setMemberMobile(phone);
        List<Member> memberList = memberModel.getMemberList(memberExample, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(memberList), "?????????????????????");
        if (!CollectionUtils.isEmpty(memberList)) {
            throw new MallException("?????????????????????");
        }

        //?????????????????????
        //???redis?????????
        String verifyNumber = stringRedisTemplate.opsForValue().get(verifyKey);
        AssertUtil.isTrue(verifyNumber == null
                || !verifyNumber.equalsIgnoreCase(verifyCode), "????????????????????????");

        //?????????????????????
        String confirmCode = stringRedisTemplate.opsForValue().get(phone);
        AssertUtil.isTrue(confirmCode == null || !confirmCode.equals(code), "????????????????????????");

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

        //??????????????????
        Integer registerIntegral = Integer.parseInt(stringRedisTemplate.opsForValue().get("integral_present_register"));
        if (registerIntegral > 0) {
            MemberIntegralVO memberIntegralVO = new MemberIntegralVO(memberId, member.getMemberName(), registerIntegral,
                    MemberIntegralLogConst.TYPE_1, "????????????", "", 0, "");
            rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_INTEGRAL, memberIntegralVO);

            member.setMemberId(memberId);
            member.setMemberIntegral(registerIntegral);
            //??????????????????????????????
            this.sendMsgIntegralChange(member, "??????????????????");
        }

        //??????
        //???????????????
        Map<String, String> headers = new HashedMap();
        headers.put("Authorization", "Basic ZnJvbnQ6ZnJvbnQ=");
        headers.put("name", null);

        //??????????????????
        UrlQuery urlQuery = new UrlQuery();
        urlQuery.add("loginType", 2);
        urlQuery.add("username", phone);
        urlQuery.add("password", confirmCode);//???redis??????????????????

        URI uri;
        try {
            uri = new URI(DomainUrlUtil.SLD_API_URL + "/v3/frontLogin/oauth/token");
        } catch (URISyntaxException e) {
            log.error("??????????????????", e);
            throw new MallException("????????????");
        }
        UrlBuilder urlBuilder = UrlBuilder.of(uri, Charset.defaultCharset()).setQuery(urlQuery);

        HttpResponse execute = new HttpRequest(urlBuilder).method(Method.POST).addHeaders(headers).execute();
        return JSONObject.parseObject(execute.body(), JsonResult.class);
    }

    /**
     * ??????????????????????????????
     *
     * @param member ????????????
     */
    public void sendMsgIntegralChange(Member member, String description) {
        //????????????
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("description", description));
        messageSendPropertyList.add(new MessageSendProperty("availableIntegral", member.getMemberIntegral().toString()));
        //??????????????????
        List<MessageSendProperty> messageSendPropertyList4Wx = new ArrayList<>();
        messageSendPropertyList4Wx.add(new MessageSendProperty("first", "????????????????????????????????????"));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword1", TimeUtil.getDateTimeString(new Date())));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword2", description));
        messageSendPropertyList4Wx.add(new MessageSendProperty("keyword3", member.getMemberIntegral().toString()));
        String msgLinkInfo = "{\"type\":\"integral_change\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, messageSendPropertyList4Wx, "changeTime", member.getMemberId(), MemberTplConst.INTEGRAL_CHANGE_REMINDER, msgLinkInfo);
        //?????????mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_MEMBER_MSG, messageSendVO);
    }
}
