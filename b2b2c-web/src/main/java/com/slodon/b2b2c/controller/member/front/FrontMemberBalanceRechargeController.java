package com.slodon.b2b2c.controller.member.front;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.member.example.MemberWxsignExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberBalanceRecharge;
import com.slodon.b2b2c.member.pojo.MemberWxsign;
import com.slodon.b2b2c.model.member.MemberBalanceRechargeModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.member.MemberWxsignModel;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.constant.MemberWxSignConst;
import com.slodon.b2b2c.core.constant.OrderPaymentConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.example.MemberBalanceRechargeExample;
import com.slodon.b2b2c.starter.entity.SlodonPayResponse;
import com.slodon.b2b2c.util.PayPropertiesUtil;
import com.slodon.b2b2c.vo.member.BalanceRechargeDetailVO;
import com.slodon.b2b2c.vo.member.BalanceRechargeVO;
import com.slodon.b2b2c.starter.entity.SlodonPayRequest;
import com.slodon.b2b2c.starter.pay.SlodonPay;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.slodon.b2b2c.core.constant.PayConst.*;

@Api(tags = "front-??????")
@Slf4j
@RestController
@RequestMapping("v3/member/front/balanceRecharge")
public class FrontMemberBalanceRechargeController extends BaseController {

    private final static String PAY_SUCCESS_URL = "/#/pages/recharge/list";
    private final static String PAY_SUCCESS_URL_PC = "/#/member/balance";

    @Resource
    private MemberBalanceRechargeModel memberBalanceRechargeModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberWxsignModel memberWxsignModel;
    @Resource
    private SlodonPay slodonPay;
    @Resource
    private PayPropertiesUtil payPropertiesUtil;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * ??????
     * ????????????,?????????
     *
     * @param request
     * @return
     */
    @ApiOperation("??????")
    @GetMapping("getMemberBalance")
    public JsonResult getMemberBalance(HttpServletRequest request) {

        Member member = UserUtil.getUser(request, Member.class);
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        //??????
        HashMap<String, Object> map = new HashMap<>();
        map.put("balanceAvailable", memberDb.getBalanceAvailable());
        map.put("rechargeSum", memberDb.getBalanceAvailable().add(memberDb.getBalanceFrozen()));

        return SldResponse.success(map);
    }


    /**
     * ????????????
     */
    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "payState", value = "???????????? 1-????????? 2-?????????", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<BalanceRechargeVO>> getList(HttpServletRequest request, @RequestParam(name = "payState", required = false) Integer payState) {

        Member member = UserUtil.getUser(request, Member.class);

        //??????
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //??????
        MemberBalanceRechargeExample example = new MemberBalanceRechargeExample();
        example.setMemberId(member.getMemberId());
        example.setPayState(payState);
        List<MemberBalanceRecharge> list = memberBalanceRechargeModel.getMemberBalanceRechargeList(example, pager);
        ArrayList<BalanceRechargeVO> vos = new ArrayList<>();
        list.forEach(memberBalanceRecharge -> {
            BalanceRechargeVO vo = new BalanceRechargeVO(memberBalanceRecharge);
            vos.add(vo);
        });

        return SldResponse.success(new PageVO<>(vos, pager));
    }

    /**
     * ????????????
     *
     * @param request
     * @param rechargeId
     * @return
     */
    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rechargeId", value = "??????id", paramType = "query", required = true)
    })
    @GetMapping("detail")
    public JsonResult<BalanceRechargeDetailVO> getBalanceRecharge(HttpServletRequest request, @RequestParam("rechargeId") Integer rechargeId) {

        //??????
        MemberBalanceRecharge detail = memberBalanceRechargeModel.getMemberBalanceRechargeByRechargeId(rechargeId);
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.isTrue(!member.getMemberId().equals(detail.getMemberId()), "?????????");

        MemberBalanceRecharge memberBalanceRecharge = memberBalanceRechargeModel.getMemberBalanceRechargeByRechargeId(rechargeId);
        BalanceRechargeDetailVO vo = new BalanceRechargeDetailVO(memberBalanceRecharge);

        return SldResponse.success(vo);
    }


    //region ????????????
    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "payMethod", value = "???????????????balance==???????????????wx==???????????????alipay==?????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "payType", required = true, paramType = "query",
                    value = "???????????????[BALANCE,NATIVE,APP,JSAPI,MWEB]"),
            @ApiImplicitParam(name = "amount", value = "????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "code", value = "??????code???JSAPI??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "codeSource", value = "??????code?????????JSAPI?????????????????????1==????????????2==?????????????????????", paramType = "query")
    })
    @PostMapping("recharge")
    public JsonResult recharge(HttpServletRequest request,
                               @RequestParam(value = "payType") String payType,
                               @RequestParam(value = "payMethod") String payMethod,
                               @RequestParam(value = "amount") String amount,
                               @RequestParam(value = "code", required = false) String code,
                               @RequestParam(value = "codeSource", required = false) Integer codeSource) throws Exception {
        Member memberToken = UserUtil.getUser(request, Member.class);

        //???????????????????????????
        Member memberDb = memberModel.getMemberByMemberId(memberToken.getMemberId());

        //???????????????????????????999999.00????????????????????????
        if (memberDb.getBalanceAvailable().add(memberDb.getBalanceFrozen()).compareTo(new BigDecimal(999999)) >= 0) {
            return SldResponse.fail("???????????????????????????999999.00?????????????????????");
        }

        BigDecimal fee = new BigDecimal(amount);
        if (fee.compareTo(new BigDecimal(999999)) > 0) {
            return SldResponse.fail("????????????????????????999999.00");
        }

        BigDecimal newBalance = memberDb.getBalanceAvailable().add(memberDb.getBalanceFrozen()).add(fee);
        if (newBalance.compareTo(new BigDecimal(999999)) > 0) {
            return SldResponse.fail("?????????????????????999999.00");
        }

        String rechargeSn = GoodsIdGenerator.getOrderSn();
        //??????????????????
        Integer rechargeId = this.saveRecharge(rechargeSn, payType, payMethod, new BigDecimal(amount), memberDb);


        //??????????????????????????????
        SlodonPayRequest payRequest = this.getPayRequest(memberToken.getMemberId(), payMethod, payType, rechargeSn, fee, code, codeSource, WebUtil.getRealIp(request));
        SlodonPayResponse slodonPayResponse = slodonPay.unitePay(payRequest);
        slodonPayResponse.setRechargeId(rechargeId);
        return SldResponse.success(slodonPayResponse);
    }

    /**
     * ??????????????????
     *
     * @param rechargeSn
     * @param payType
     * @param payMethod
     * @param amount
     * @param member
     */
    private Integer saveRecharge(String rechargeSn, String payType, String payMethod, BigDecimal amount, Member member) {
        MemberBalanceRecharge memberBalanceRecharge = new MemberBalanceRecharge();
        memberBalanceRecharge.setRechargeSn(rechargeSn);
        String paymentCode = OrderPaymentConst.PAYMENT_CODE_ONLINE;
        String paymentName = OrderPaymentConst.PAYMENT_NAME_ONLINE;
        if (payMethod.equals(METHOD_WX)) {
            switch (payType) {
                case WX_TYPE_APP:
                    paymentCode = OrderPaymentConst.PAYMENT_CODE_APPWXPAY;
                    paymentName = OrderPaymentConst.PAYMENT_NAME_APPWXPAY;
                    break;
                case WX_TYPE_JSAPI:
                    paymentCode = OrderPaymentConst.PAYMENT_CODE_MINIAPPWXPAY;
                    paymentName = OrderPaymentConst.PAYMENT_NAME_MINIAPPWXPAY;
                    break;
                case WX_TYPE_MWEB:
                    paymentCode = OrderPaymentConst.PAYMENT_CODE_H5WXPAY;
                    paymentName = OrderPaymentConst.PAYMENT_NAME_H5WXPAY;
                    break;
                case WX_TYPE_NATIVE:
                    paymentCode = OrderPaymentConst.PAYMENT_CODE_PCWXPAY;
                    paymentName = OrderPaymentConst.PAYMENT_NAME_PCWXPAY;
                    break;
            }
        } else {
            switch (payType) {
                case ALI_TYPE_APP:
                    paymentCode = OrderPaymentConst.PAYMENT_CODE_APPWXPAY;
                    paymentName = OrderPaymentConst.PAYMENT_NAME_APPWXPAY;
                    break;
                case ALI_TYPE_MWEB:
                    paymentCode = OrderPaymentConst.PAYMENT_CODE_H5ALIPAY;
                    paymentName = OrderPaymentConst.PAYMENT_NAME_H5ALIPAY;
                    break;
                case ALI_TYPE_NATIVE:
                    paymentCode = OrderPaymentConst.PAYMENT_CODE_PCALIPAY;
                    paymentName = OrderPaymentConst.PAYMENT_NAME_PCALIPAY;
                    break;
            }
        }

        memberBalanceRecharge.setPaymentCode(paymentCode);
        memberBalanceRecharge.setPaymentName(paymentName);
        memberBalanceRecharge.setPayAmount(amount);
        memberBalanceRecharge.setPayState(MemberConst.PAY_STATE_1);
        memberBalanceRecharge.setMemberId(member.getMemberId());
        memberBalanceRecharge.setMemberName(member.getMemberName());
        memberBalanceRecharge.setMemberMobile(member.getMemberMobile());
        memberBalanceRecharge.setCreateTime(new Date());

        memberBalanceRechargeModel.saveMemberBalanceRecharge(memberBalanceRecharge);
        return memberBalanceRecharge.getRechargeId();
    }


    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "payMethod", value = "???????????????balance==???????????????wx==???????????????alipay==?????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "payType", required = true, paramType = "query",
                    value = "???????????????[BALANCE,NATIVE,APP,JSAPI,MWEB]"),
            @ApiImplicitParam(name = "rechargeSn", value = "????????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "code", value = "??????code???JSAPI??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "codeSource", value = "??????code?????????JSAPI?????????????????????1==????????????2==?????????????????????", paramType = "query")
    })
    @PostMapping("rechargeContinue")
    public JsonResult rechargeContinue(HttpServletRequest request,
                                       @RequestParam(value = "payType") String payType,
                                       @RequestParam(value = "payMethod") String payMethod,
                                       @RequestParam(value = "rechargeSn") String rechargeSn,
                                       @RequestParam(value = "code", required = false) String code,
                                       @RequestParam(value = "codeSource", required = false) Integer codeSource) throws Exception {
        Member memberToken = UserUtil.getUser(request, Member.class);

        //??????????????????
        MemberBalanceRechargeExample rechargeExample = new MemberBalanceRechargeExample();
        rechargeExample.setRechargeSn(rechargeSn);
        rechargeExample.setMemberId(memberToken.getMemberId());
        List<MemberBalanceRecharge> memberBalanceRechargeList = memberBalanceRechargeModel.getMemberBalanceRechargeList(rechargeExample, null);
        AssertUtil.notEmpty(memberBalanceRechargeList, "??????????????????");

        MemberBalanceRecharge rechargeDb = memberBalanceRechargeList.get(0);

        if (rechargeDb.getPayState() == MemberConst.PAY_STATE_2) {
            return SldResponse.fail("???????????????");
        }

        //??????????????????????????????
        SlodonPayRequest payRequest = this.getPayRequest(memberToken.getMemberId(), payMethod, payType, rechargeSn, rechargeDb.getPayAmount(), code, codeSource, WebUtil.getRealIp(request));
        SlodonPayResponse slodonPayResponse = slodonPay.unitePay(payRequest);
        slodonPayResponse.setRechargeId(rechargeDb.getRechargeId());
        return SldResponse.success(slodonPayResponse);
    }


    /**
     * ????????????????????????
     *
     * @param memberId   ??????id
     * @param payMethod  ????????????
     * @param payType    ????????????
     * @param paySn      ????????????
     * @param payAmount  ????????????
     * @param code       ??????code???JSAPI??????????????????
     * @param codeSource ??????code?????????JSAPI?????????????????????1==????????????2==?????????????????????
     * @param ip         ip
     * @return ??????????????????
     */
    private SlodonPayRequest getPayRequest(Integer memberId, String payMethod, String payType, String paySn,
                                           BigDecimal payAmount, String code, Integer codeSource, String ip) {
        SlodonPayRequest request = new SlodonPayRequest();
        AssertUtil.isTrue(!payMethod.equals(METHOD_WX) && !payMethod.equals(METHOD_ALIPAY), "??????????????????");
        request.setPayMethod(payMethod);
        request.setPayType(payType);
        if (payType.equalsIgnoreCase(ALI_TYPE_NATIVE)){
            //pc????????????
            request.setReturnUrl(DomainUrlUtil.SLD_PC_URL + PAY_SUCCESS_URL_PC);
        }else {
            //????????????
            request.setReturnUrl(DomainUrlUtil.SLD_H5_URL + PAY_SUCCESS_URL);
        }
        if (payMethod.equals(METHOD_WX)) {
            request.setWxPayProperties(payPropertiesUtil.getWxPayProperties(payType, codeSource));
            request.setNotifyUrl(DomainUrlUtil.SLD_API_URL + "/v3/member/front/rechargeCallback/wx");
            request.setBody(stringRedisTemplate.opsForValue().get("wxpay_order_name"));
            if (payType.equals(WX_TYPE_JSAPI)) {
                //????????????????????????????????????????????????openid
                request.setOpenId(this.getOpenId(codeSource, memberId, code));
            }
        } else {
            request.setAlipayProperties(payPropertiesUtil.getAliPayProperties());
            request.setNotifyUrl(DomainUrlUtil.SLD_API_URL + "/v3/member/front/rechargeCallback/ali");
            request.setSubject(stringRedisTemplate.opsForValue().get("alipay_all_subject"));
            request.setBody(request.getSubject());
        }
        request.setPaySn(paySn);
        if ("1".equals(stringRedisTemplate.opsForValue().get("alipay_test_enable_h5"))) {
            request.setPayAmount(new BigDecimal("0.01"));
        } else {
            request.setPayAmount(payAmount);
        }
        request.setIp(ip);

        return request;
    }


    /**
     * ???????????????/????????????????????????openid
     *
     * @param codeSource 1.????????????2-?????????????????????
     * @param memberId   ??????id
     * @param code       ??????code
     * @return
     */
    private String getOpenId(Integer codeSource, Integer memberId, String code) {
        MemberWxsignExample memberWxsignExample = new MemberWxsignExample();
        memberWxsignExample.setMemberId(memberId);
        if (codeSource == 1) {
            //?????????
            memberWxsignExample.setResource(MemberWxSignConst.XCX);
        } else {
            //?????????????????????
            memberWxsignExample.setResource(MemberWxSignConst.GZH);
        }
        List<MemberWxsign> memberWxsignList = memberWxsignModel.getMemberWxsignList(memberWxsignExample, null);
        if (!CollectionUtils.isEmpty(memberWxsignList)) {
            //?????????
            return memberWxsignList.get(0).getOpenid();
        }

        //???????????????????????????code??????openid
        String token_url = "";//??????openid???url
        if (codeSource == 1) {
            //?????????
            token_url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + stringRedisTemplate.opsForValue().get("login_wx_mini_appid") +
                    "&secret=" + stringRedisTemplate.opsForValue().get("login_wx_mini_appsecret") + "&js_code=" + code + "&grant_type=authorization_code";
        } else {
            //?????????????????????
            token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + stringRedisTemplate.opsForValue().get("login_wx_dev_appid") +
                    "&secret=" + stringRedisTemplate.opsForValue().get("login_wx_dev_appsecret") + "&code=" + code + "&grant_type=authorization_code";
        }
        String resp = HttpUtil.get(token_url);
        if (0 == JSONObject.parseObject(resp).getIntValue("errcode")) {
            return JSONObject.parseObject(resp).getString("openid");
        } else {
            log.error("??????openid?????????" + resp);
            throw new MallException("??????openid?????????");
        }
    }

}
