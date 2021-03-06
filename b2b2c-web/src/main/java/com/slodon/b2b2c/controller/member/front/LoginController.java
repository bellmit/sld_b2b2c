package com.slodon.b2b2c.controller.member.front;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.business.pojo.Cart;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.HttpClientUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.example.MemberExample;
import com.slodon.b2b2c.member.example.MemberWxsignExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberWxsign;
import com.slodon.b2b2c.model.business.CartModel;
import com.slodon.b2b2c.model.member.MemberLoginLogModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.member.MemberWxsignModel;
import com.slodon.b2b2c.model.msg.AppClientLogModel;
import com.slodon.b2b2c.msg.example.AppClientLogExample;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Api(tags = "front-????????????")
@RestController
@RequestMapping("v3/member/front/login")
@Slf4j
public class LoginController extends BaseController {

    private static final String WX_AUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    private static final String WX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    private static final String WX_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo";
    private static final String WXXCX_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberWxsignModel memberWxsignModel;
    @Resource
    private MemberLoginLogModel memberLoginLogModel;
    @Resource
    private CartModel cartModel;
    @Resource
    private AppClientLogModel appClientLogModel;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "refreshKey", value = "??????????????????"),
            @ApiImplicitParam(name = "clientId", value = "???????????????ID app??????"),
            @ApiImplicitParam(name = "alias", value = "????????????????????? app??????")
    })
    @PostMapping("logout")
    public JsonResult logout(HttpServletRequest request, String clientId, String alias) {
        stringRedisTemplate.opsForValue().getOperations().delete(request.getParameter("refreshKey"));
        if (!StringUtils.isEmpty(clientId) || !StringUtils.isEmpty(alias)) {
//            app?????????????????????clientId
            AppClientLogExample appClientLogExample = new AppClientLogExample();
            appClientLogExample.setClientId(clientId);
            appClientLogExample.setAlias(alias);
            appClientLogModel.deleteAppClientLogByExample(appClientLogExample);
        }
        return SldResponse.success("??????????????????");
    }

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", value = "???????????? 1???H5(?????????????????????) 2???H5(???????????????)???3???app", required = true),
            @ApiImplicitParam(name = "code", value = "????????????code??????app?????????????????????"),
            @ApiImplicitParam(name = "openid", value = "openid???app?????????????????????"),
            @ApiImplicitParam(name = "unionid", value = "unionid???app?????????????????????"),
            @ApiImplicitParam(name = "cartInfo", value = "????????????????????? ??? [{\"productId\":1,\"buyNum\":1},{\"productId\":2,\"buyNum\":2}]"),
            @ApiImplicitParam(name = "userInfo", value = "????????????,????????????app????????? ???:{\" nickname\": \"ming\",\"sex\":\"1\",\"province\":\"PROVINCE\",\"city\":\"CITY\",\"country\":\"COUNTRY\",\"headimgurl\":\"http://thirdwx.qlogo.cn/mmopen\"}")
    })
    @PostMapping("wechat/login")
    public JsonResult wxGetAT(HttpServletRequest request, Integer source, String code, String openid,
                              String unionid, String cartInfo, String userInfo) {
        AssertUtil.isTrue(StringUtil.isNullOrZero(source) || source > 3, "????????????");

        String resp;
        JSONObject userInfoJson;//????????????
        String appId;
        String secret;

        switch (source) {
            case 1:
                appId = stringRedisTemplate.opsForValue().get("login_wx_dev_appid");
                secret = stringRedisTemplate.opsForValue().get("login_wx_dev_appsecret");
                //?????????????????????
                resp = HttpClientUtil.sendGet(WX_ACCESS_TOKEN_URL + "?appid=" + appId + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code");
                if (null != JSONObject.parseObject(resp).getInteger("errcode")) {
                    return SldResponse.fail(JSONObject.parseObject(resp).getString("errmsg"));
                }
                //??????openid???unionid
                String accessToken = JSONObject.parseObject(resp).getString("access_token");
                openid = JSONObject.parseObject(resp).getString("openid");
                //??????userInfo
                userInfoJson = JSONObject.parseObject(HttpClientUtil.sendGet(WX_USER_INFO_URL + "?access_token=" + accessToken + "&openid=" + openid));
                if (null != JSONObject.parseObject(HttpClientUtil.sendGet(WX_USER_INFO_URL + "?access_token=" + accessToken + "&openid=" + openid)).getInteger("errcode")) {
                    return SldResponse.fail(JSONObject.parseObject(HttpClientUtil.sendGet(WX_USER_INFO_URL + "?access_token=" + accessToken + "&openid=" + openid)).getString("errmsg"));
                }
                unionid = userInfoJson.getString("unionid");
                userInfoJson.put("openid", openid);
                break;
            case 2:
                appId = stringRedisTemplate.opsForValue().get("login_wx_mini_appid");
                secret = stringRedisTemplate.opsForValue().get("login_wx_mini_appsecret");
                //?????????
                resp = HttpClientUtil.sendGet(WXXCX_ACCESS_TOKEN_URL + "?appid=" + appId + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code");
                if (null != JSONObject.parseObject(resp).getInteger("errcode")) {
                    return SldResponse.fail(JSONObject.parseObject(resp).getString("errmsg"));
                }
                //??????openid???unionid
                openid = JSONObject.parseObject(resp).getString("openid");
                unionid = JSONObject.parseObject(resp).getString("unionid");
                userInfoJson = JSONObject.parseObject(userInfo);
                userInfoJson.put("openid", openid);
                userInfoJson.put("unionid", unionid);
                userInfoJson.put("session_key", JSONObject.parseObject(resp).getString("session_key"));
                break;
            case 3:
                //app
                AssertUtil.notEmpty(openid, "openid????????????");
                AssertUtil.notEmpty(unionid, "unionid????????????");
                userInfoJson = JSONObject.parseObject(userInfo);
                userInfoJson.put("openid", openid);
                userInfoJson.put("unionid", unionid);
                break;
            default:
                return SldResponse.fail("????????????");
        }

        //??????????????????????????????
        if (null != unionid) {
            MemberExample example = new MemberExample();
            example.setWxUnionid(unionid);
            List<Member> memberList = memberModel.getMemberList(example, null);
            if (!CollectionUtils.isEmpty(memberList)) {
                Member member = memberList.get(0);
                this.checkLoginMember(member);
                //???????????????????????????
                if (!StringUtils.isEmpty(cartInfo)) {
                    List<Cart> cartList = JSONObject.parseArray(cartInfo, Cart.class);
                    cartModel.addCartByMemberId(cartList, member.getMemberId());
                }
                return createToken(member);
            }
        } else {
            MemberWxsignExample example = new MemberWxsignExample();
            example.setOpenid(openid);
            List<MemberWxsign> memberWxsignList = memberWxsignModel.getMemberWxsignList(example, null);
            if (!CollectionUtils.isEmpty(memberWxsignList)) {
                MemberWxsign memberWxsign = memberWxsignList.get(0);
                Member member = memberModel.getMemberByMemberId(memberWxsign.getMemberId());
                if (null != member) {
                    this.checkLoginMember(member);
                    //???????????????????????????
                    if (!StringUtils.isEmpty(cartInfo)) {
                        List<Cart> cartList = JSONObject.parseArray(cartInfo, Cart.class);
                        cartModel.addCartByMemberId(cartList, member.getMemberId());
                    }

                    return createToken(member);
                }
            }
        }

        //???????????????????????????????????????
        HashMap<String, String> map = new HashMap<>(2);
        //true-?????????????????????????????????;false-???????????????????????????????????????
        map.put("redirect", "false");
        UUID uuid = UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(uuid.toString(), JSONObject.toJSONString(userInfoJson), 60 * 1000, TimeUnit.SECONDS);
        map.put("bindKey", uuid.toString());
        return SldResponse.success(map);
    }

    /**
     * ??????????????????????????????
     *
     * @param memberDb
     */
    private void checkLoginMember(Member memberDb) {
        if (memberDb.getState() != MemberConst.STATE_1) throw new MallException("???????????????");
    }

    @ApiOperation("???????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bindType", value = "????????????????????? 1==?????????????????????????????????????????????????????????2==??????????????????????????????????????????3==?????????????????????????????????????????????", defaultValue = "1"),
            @ApiImplicitParam(name = "mobile", value = "?????????????????????", required = true),
            @ApiImplicitParam(name = "smsCode", value = "??????????????????????????????", required = true),
            @ApiImplicitParam(name = "resource", value = "?????? 1???pc 2???app???3???????????????????????????????????????4????????????", required = true),
            @ApiImplicitParam(name = "bindKey", value = "????????????code", required = true),
            @ApiImplicitParam(name = "cartInfo", value = "????????????????????? ??? [{\"productId\":1,\"buyNum\":1},{\"productId\":2,\"buyNum\":2}]")
    })
    @PostMapping("wechat/bindMobile")
    public JsonResult bindWxMember(HttpServletRequest request, Integer bindType, String mobile, String smsCode,
                                   Integer resource, String bindKey, String cartInfo) {
        //???redis???????????????????????????
        String wxUserInfo = stringRedisTemplate.opsForValue().get(bindKey);
        AssertUtil.isTrue(StringUtils.isEmpty(wxUserInfo), "????????????????????????????????????");
        AssertUtil.isTrue(StringUtils.isEmpty(mobile), "????????????????????????");

        String sms_number = stringRedisTemplate.opsForValue().get(mobile);
        AssertUtil.isTrue(sms_number == null || !sms_number.equalsIgnoreCase(smsCode), "????????????????????????");

        JSONObject userInfoJson = JSONObject.parseObject(wxUserInfo);
        //???????????????????????????key?????????????????????????????????key
        if (resource == 3) {
            //?????????????????????
            userInfoJson.put("nickName", userInfoJson.getString("nickname"));//??????
            userInfoJson.put("gender", userInfoJson.getString("sex"));//??????
            userInfoJson.put("avatarUrl", userInfoJson.getString("headimgurl"));//??????
        }
        //??????????????????
        MemberExample example = new MemberExample();
        example.setMemberMobile(mobile);
        List<Member> memberList = memberModel.getMemberList(example, null);
        if (bindType == 1) {
            //??????????????????????????????????????????
            if (!CollectionUtils.isEmpty(memberList)) {
                //?????????????????????
                JsonResult<Object> special = SldResponse.failSpecial("?????????????????????????????????");
                special.setData(memberList.get(0).getMemberName());
                return special;
            } else {
                //?????????????????????????????????
                this.wxSignAddMember(userInfoJson, mobile, WebUtil.getRealIp(request), resource);
            }
        } else if (bindType == 2) {
            //???????????????????????????????????????????????????????????????????????????
            AssertUtil.notEmpty(memberList, "??????????????????");

            Member updateMember = new Member();
            updateMember.setMemberId(memberList.get(0).getMemberId());
            updateMember.setMemberMobile("");
            updateMember.setIsMobileVerify(MemberConst.IS_MOBILE_VERIFY_0);
            updateMember.setState(MemberConst.STATE_0);
            updateMember.setUpdateTime(new Date());
            memberModel.updateMember(updateMember);

            // ?????????????????????
            this.wxSignAddMember(userInfoJson, mobile, WebUtil.getRealIp(request), resource);
        } else {
            //?????????????????????????????????????????????
            AssertUtil.notEmpty(memberList, "??????????????????");
            //????????????????????????????????????
            this.checkLoginMember(memberList.get(0));

            Member updateMember = new Member();
            updateMember.setMemberId(memberList.get(0).getMemberId());
            updateMember.setMemberNickName(userInfoJson.getString("nickName"));
            updateMember.setGender(userInfoJson.getIntValue("gender"));
            updateMember.setWxUnionid(userInfoJson.getString("unionid"));
            updateMember.setWxAvatarImg(userInfoJson.getString("avatarUrl"));
            updateMember.setUpdateTime(new Date());
            memberModel.updateMember(updateMember);

            //??????????????? openId ??????
            MemberWxsign wxsignNewOrUpdate = new MemberWxsign();
            wxsignNewOrUpdate.setMemberId(memberList.get(0).getMemberId());
            wxsignNewOrUpdate.setOpenid(userInfoJson.getString("openid"));
            wxsignNewOrUpdate.setResource(resource);
            memberWxsignModel.saveMemberWxsign(wxsignNewOrUpdate);
        }

        //????????????
        Member member = memberLoginLogModel.memberLoginByMobile(mobile, WebUtil.getRealIp(request), resource);
        AssertUtil.notNull(member, "??????????????????");

        //???????????????????????????
        if (!StringUtils.isEmpty(cartInfo)) {
            List<Cart> cartList = JSONObject.parseArray(cartInfo, Cart.class);
            cartModel.addCartByMemberId(cartList, member.getMemberId());
        }
        stringRedisTemplate.opsForValue().getOperations().delete(bindKey);
        return createToken(member);
    }


    @ApiOperation("???????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mobile", value = "?????????", required = true),
            @ApiImplicitParam(name = "smsCode", value = "???????????????", required = true),
            @ApiImplicitParam(name = "cartInfo", value = "????????????????????? ??? [{\"productId\":1,\"buyNum\":1},{\"productId\":2,\"buyNum\":2}]")
    })
    @PostMapping("freeSecretLogin")
    public JsonResult freeSecretLogin(HttpServletRequest request, String mobile, String smsCode, String cartInfo) {

        AssertUtil.notEmpty(mobile, "?????????????????????");
        String sms_number = stringRedisTemplate.opsForValue().get(mobile);
        AssertUtil.isTrue(sms_number == null || !sms_number.equalsIgnoreCase(smsCode), "????????????????????????");

        String ip = WebUtil.getRealIp(request);
        // ????????????
        Member member = memberLoginLogModel.memberLoginByMobile(mobile, ip, MemberConst.MEMBER_FROM_H5);
        AssertUtil.notNull(member, "??????????????????");

        //???????????????????????????
        if (!StringUtils.isEmpty(cartInfo)) {
            List<Cart> cartList = JSONObject.parseArray(cartInfo, Cart.class);
            cartModel.addCartByMemberId(cartList, member.getMemberId());
        }
        return createToken(member);
    }

    /**
     * ??????????????????????????????
     *
     * @param userInfoJson
     * @param mobile
     * @param ip
     * @param resource
     * @return memberId
     */
    private void wxSignAddMember(JSONObject userInfoJson, String mobile, String ip, Integer resource) {
        // ?????????????????????
        Member memberNew = new Member();
        memberNew.setMemberName(GoodsIdGenerator.getMemberName());
        memberNew.setMemberNickName(userInfoJson.getString("nickName"));
        memberNew.setGender(userInfoJson.getIntValue("gender"));
        memberNew.setWxUnionid(userInfoJson.getString("unionid"));
        memberNew.setWxAvatarImg(userInfoJson.getString("avatarUrl"));
        memberNew.setLoginPwd("");

        memberNew.setGrade(0);
        memberNew.setRegisterTime(new Date());
        memberNew.setExperienceValue(0);
        memberNew.setMemberMobile(mobile);
        memberNew.setMemberIntegral(0);
        memberNew.setLastLoginIp(ip);
        memberNew.setLastLoginTime(new Date());
        memberNew.setLoginNumber(1);
        memberNew.setLoginErrorCount(0);
        memberNew.setPayErrorCount(0);
        memberNew.setRegisterChannel(resource);
        memberNew.setBalanceAvailable(new BigDecimal(0.00));
        memberNew.setBalanceFrozen(new BigDecimal(0.00));
        memberNew.setPayPwd("");
        memberNew.setIsEmailVerify(MemberConst.IS_EMAIL_VERIFY_0);
        memberNew.setIsMobileVerify(MemberConst.IS_MOBILE_VERIFY_1);
        memberNew.setIsReceiveSms(1);
        memberNew.setIsReceiveEmail(1);
        memberNew.setState(MemberConst.STATE_1);
        memberNew.setIsAllowBuy(MemberConst.IS_ALLOW_BUY_1);
        memberNew.setIsAllowAsk(MemberConst.IS_ALLOW_ASK_1);
        memberNew.setIsAllowComment(MemberConst.IS_ALLOW_COMMENT_1);
        memberNew.setMemberEmail("");
        memberNew.setUpdateTime(new Date());

        //??????????????????????????????
        MemberExample example = new MemberExample();
        example.setMemberName(memberNew.getMemberName());
        List<Member> byNameList = memberModel.getMemberList(example, null);
        if (byNameList != null && byNameList.size() > 0) {
            List<Member> byName;
            String name = memberNew.getMemberName();
            boolean flag = true;
            int i = 0;
            while (flag) {
                example.setMemberName(name + i);
                byName = memberModel.getMemberList(example, null);
                if (byName.isEmpty()) {
                    flag = false;
                    memberNew.setMemberName(name + i);
                }
                i++;
            }
        }

        memberModel.saveMember(memberNew);

        //??????openId
        MemberWxsign wxsign = new MemberWxsign();
        wxsign.setMemberId(memberNew.getMemberId());
        wxsign.setOpenid(userInfoJson.getString("openid"));
        wxsign.setResource(resource);
        memberWxsignModel.saveMemberWxsign(wxsign);
    }

    /**
     * ??????token
     *
     * @param member
     * @return
     */
    public JsonResult createToken(Member member) {
        //??????
        //???????????????
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic ZnJvbnQ6ZnJvbnQ=");
        headers.put("name", null);

        UUID uuid = UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(member.getMemberMobile(), uuid.toString(), 60 * 1000, TimeUnit.SECONDS);
        //??????????????????
        UrlQuery urlQuery = new UrlQuery();
        urlQuery.add("loginType", 2);
        urlQuery.add("username", member.getMemberMobile());
        urlQuery.add("password", uuid.toString());
        urlQuery.add("grant_type", "password");

        URI uri;
        try {
            uri = new URI(DomainUrlUtil.SLD_H5_URL + "/v3/frontLogin/oauth/token");
        } catch (URISyntaxException e) {
            log.error("??????????????????", e);
            throw new MallException("????????????");
        }
        UrlBuilder urlBuilder = UrlBuilder.of(uri, Charset.defaultCharset()).setQuery(urlQuery);

        HttpResponse execute = new HttpRequest(urlBuilder).method(Method.POST).addHeaders(headers).execute();
        return JSONObject.parseObject(execute.body(), JsonResult.class);
    }

    @ApiOperation("??????wxjsapi????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "source", value = "source ?????? 1???H5(?????????????????????+?????????) 2???H5(???????????????)???3???app", required = true),
            @ApiImplicitParam(name = "url", value = "????????????url")
    })
    @GetMapping("wxjsConf")
    public JsonResult wxjsConf(HttpServletRequest request, Integer source, String url) {

        //??????access_token??????
        final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
        //??????ticket??????
        final String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
        final String TICKET_REDIS_KEY = "wx_share_ticket";//reids?????????ticket
        String appsecret;//????????????
        String appid;//????????????
        String nonceStr = UUID.randomUUID().toString();//?????????????????????
        Long timestamp = System.currentTimeMillis() / 1000;//????????????????????????
        String ticket = stringRedisTemplate.opsForValue().get(TICKET_REDIS_KEY);//????????????ticket??????

        switch (source) {
            case 1:
                appid = stringRedisTemplate.opsForValue().get("login_wx_dev_appid");
                appsecret = stringRedisTemplate.opsForValue().get("login_wx_dev_appsecret");
                break;
            case 2:
                appid = stringRedisTemplate.opsForValue().get("login_wx_mini_appid");
                appsecret = stringRedisTemplate.opsForValue().get("login_wx_mini_appsecret");
                break;
            case 3:
                appid = stringRedisTemplate.opsForValue().get("login_wx_app_appid");
                appsecret = stringRedisTemplate.opsForValue().get("login_wx_app_appsecret");
                break;
            default:
                appid = "";
                appsecret = "";
        }


        if (ticket == null) {
            //?????????????????????????????? ticket
            //??????accessToken
            String accessTokenResp = HttpUtil.get(ACCESS_TOKEN_URL.replace("APPID", appid).replace("APPSECRET", appsecret));
            JSONObject accessTokenJsonObject = JSONObject.parseObject(accessTokenResp);
            if (null != accessTokenJsonObject.getInteger("errcode")) {
                //??????accessToken??????
                return SldResponse.fail(accessTokenJsonObject.getString("errmsg"));
            }
            String accessToken = accessTokenJsonObject.getString("access_token");
            int accessTokenExpiresIn = accessTokenJsonObject.getIntValue("expires_in");//accessToken???????????????

            //??????ticket
            String ticketResp = HttpUtil.get(JSAPI_TICKET_URL.replace("ACCESS_TOKEN", accessToken));
            JSONObject ticketJsonObject = JSONObject.parseObject(ticketResp);
            if (!ticketJsonObject.getInteger("errcode").equals(0)) {
                return SldResponse.fail(ticketJsonObject.getString("errmsg"));
            }
            ticket = ticketJsonObject.getString("ticket");
            int ticketExpiresIn = ticketJsonObject.getIntValue("expires_in");//ticket???????????????

            //??????ticket,???????????????accessToken???ticket??????????????????????????????
            stringRedisTemplate.opsForValue().set(TICKET_REDIS_KEY, ticket, Long.min(accessTokenExpiresIn, ticketExpiresIn), TimeUnit.SECONDS);
        }

        String signature = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url;
        log.debug("-------------------------------------------------------");
        log.debug(signature);
        log.debug("-------------------------------------------------------");
        signature = DigestUtils.sha1Hex(signature);//sha1??????

        HashMap<String, Object> data = new HashMap<>();
        data.put("appId", appid);//appid
        data.put("signature", signature);//??????
        data.put("nonceStr", nonceStr);//????????????????????????
        data.put("timestamp", timestamp);//????????????????????????

        return SldResponse.success(data);
    }
}
