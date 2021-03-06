package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.business.example.OrderExample;
import com.slodon.b2b2c.business.example.OrderReturnExample;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.member.dto.MemberInfoUpdateDTO;
import com.slodon.b2b2c.member.example.MemberFollowProductExample;
import com.slodon.b2b2c.member.example.MemberFollowStoreExample;
import com.slodon.b2b2c.member.example.MemberProductLookLogExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderReturnModel;
import com.slodon.b2b2c.model.member.MemberFollowProductModel;
import com.slodon.b2b2c.model.member.MemberFollowStoreModel;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.member.MemberProductLookLogModel;
import com.slodon.b2b2c.model.msg.MemberReceiveModel;
import com.slodon.b2b2c.model.promotion.CouponMemberModel;
import com.slodon.b2b2c.msg.example.MemberReceiveExample;
import com.slodon.b2b2c.promotion.example.CouponMemberExample;
import com.slodon.b2b2c.vo.member.MemberInfoVO;
import com.slodon.b2b2c.vo.member.MemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Api(tags = "front-????????????")
@RestController
@RequestMapping("v3/member/front/member")
public class MemberInfoController extends BaseController {

    @Resource
    private MemberModel memberModel;
    @Resource
    private MemberFollowProductModel memberFollowProductModel;
    @Resource
    private MemberFollowStoreModel memberFollowStoreModel;
    @Resource
    private MemberProductLookLogModel memberProductLookLogModel;
    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderReturnModel orderReturnModel;
    @Resource
    private CouponMemberModel couponMemberModel;
    @Resource
    private MemberReceiveModel memberReceiveModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("????????????????????????")
    @GetMapping("getInfo")
    public JsonResult<MemberInfoVO> getInfo(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);

        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        //????????????
        if (StringUtil.isEmpty(memberDb.getMemberAvatar()) && StringUtil.isEmpty(memberDb.getWxAvatarImg())) {
            memberDb.setMemberAvatar(stringRedisTemplate.opsForValue().get("default_image_user_portrait"));
        }
        //????????????
        MemberInfoVO vo = new MemberInfoVO(memberDb);

        //??????????????????
        OrderExample example = new OrderExample();
        example.setMemberId(member.getMemberId());
        example.setOrderState(OrderConst.ORDER_STATE_10);
        vo.setToPaidOrder(orderModel.getOrderCount(example));
        //??????????????????
        example.setOrderState(OrderConst.ORDER_STATE_20);
        vo.setToDeliverOrder(orderModel.getOrderCount(example));
        //??????????????????
        example.setOrderState(OrderConst.ORDER_STATE_30);
        vo.setToReceivedOrder(orderModel.getOrderCount(example));
        //??????????????????
        example.setEvaluateStateNotEquals(OrderConst.EVALUATE_STATE_3);
        example.setOrderState(OrderConst.ORDER_STATE_40);
        vo.setToEvaluateOrder(orderModel.getOrderCount(example));
        //???????????????
        OrderReturnExample returnExample = new OrderReturnExample();
        returnExample.setMemberId(member.getMemberId());
        returnExample.setStateNotIn(OrdersAfsConst.RETURN_STATE_202 + "," + OrdersAfsConst.RETURN_STATE_300);
        vo.setAfterSaleNum(orderReturnModel.getOrderReturnCount(returnExample));
        //????????????
        CouponMemberExample memberExample = new CouponMemberExample();
        memberExample.setMemberId(member.getMemberId());
        memberExample.setUseState(CouponConst.USE_STATE_1);
        memberExample.setEffectiveEndAfter(new Date());
        vo.setCouponNum(couponMemberModel.getCouponMemberList(memberExample, null).size());
        //???????????????
        MemberFollowProductExample followProductExample = new MemberFollowProductExample();
        followProductExample.setMemberId(member.getMemberId());
        vo.setFollowProductNum(memberFollowProductModel.getMemberFollowProductCount(followProductExample));
        //???????????????
        MemberFollowStoreExample followStoreExample = new MemberFollowStoreExample();
        followStoreExample.setMemberId(member.getMemberId());
        vo.setFollowStoreNum(memberFollowStoreModel.getMemberFollowStoreCount(followStoreExample));
        //???????????????
        MemberProductLookLogExample productLookLogExample = new MemberProductLookLogExample();
        productLookLogExample.setMemberId(member.getMemberId());
        vo.setLookLogNum(memberProductLookLogModel.getMemberProductLookLogCount(productLookLogExample));
        //???????????????
        MemberReceiveExample receiveExample = new MemberReceiveExample();
        receiveExample.setMemberId(member.getMemberId());
        receiveExample.setMsgState(MsgConst.MSG_STATE_UNREAD);
        vo.setMsgNum(memberReceiveModel.getMemberReceiveList(receiveExample, null).size());
        String sellerSwitch = stringRedisTemplate.opsForValue().get("seller_center_entrance_is_enable");
        vo.setSellerSwitch(StringUtil.isEmpty(sellerSwitch) ? "0" : sellerSwitch);
        vo.setSellerUrl(DomainUrlUtil.SLD_SELLER_URL);
        return SldResponse.success(vo);
    }

    @ApiOperation("??????????????????")
    @GetMapping("memberInfo")
    public JsonResult<MemberVO> getMemberInfo(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);

        //????????????????????????
        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        if (StringUtil.isEmpty(memberDb.getMemberAvatar()) && StringUtil.isEmpty(memberDb.getWxAvatarImg())) {
            memberDb.setMemberAvatar(stringRedisTemplate.opsForValue().get("default_image_user_portrait"));
        }
        MemberVO vo = new MemberVO(memberDb);
        return SldResponse.success(vo);
    }

    @ApiOperation("??????????????????")
    @PostMapping("updateInfo")
    public JsonResult updateInfo(HttpServletRequest request, MemberInfoUpdateDTO memberInfoUpdateDTO) {
        Member member = UserUtil.getUser(request, Member.class);

        Member memberNew = new Member();
        BeanUtils.copyProperties(memberInfoUpdateDTO, memberNew);
        memberNew.setMemberId(member.getMemberId());
        memberNew.setUpdateTime(new Date());
        memberModel.updateMember(memberNew);
        return SldResponse.success("????????????");
    }

    @ApiOperation("???????????????????????????")
    @GetMapping("isBindMobile")
    public JsonResult isBindMobile(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);
        //??????memberId??????????????????
        Member memberDB = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.notNull(member, "????????????????????????????????????");

        if (StringUtils.isEmpty(memberDB.getMemberMobile())) {
            return SldResponse.failSpecial("??????????????????");
        } else {
            return SldResponse.success(memberDB.getMemberMobile());
        }
    }

    @ApiOperation("??????????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberMobile", value = "?????????", required = true, paramType = "query"),
            @ApiImplicitParam(name = "smsCode", value = "sms?????????", required = true, paramType = "query")
    })
    @GetMapping("verifyOldMobile")
    public JsonResult verifyOldMobile(HttpServletRequest request, String smsCode, String memberMobile) {
        Member member = UserUtil.getUser(request, Member.class);

        Member memberDb = memberModel.getMemberByMemberId(member.getMemberId());
        AssertUtil.isTrue(!memberDb.getMemberMobile().equals(memberMobile), "???????????????");

        String verify_sms = stringRedisTemplate.opsForValue().get(memberMobile);
        AssertUtil.isTrue(!smsCode.equals(verify_sms), "?????????????????????????????????");

        return SldResponse.success();
    }

}
