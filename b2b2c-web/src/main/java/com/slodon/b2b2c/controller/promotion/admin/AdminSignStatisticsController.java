package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.core.constant.SignConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.CouponModel;
import com.slodon.b2b2c.model.promotion.SignActivityModel;
import com.slodon.b2b2c.model.promotion.SignLogModel;
import com.slodon.b2b2c.promotion.example.SignActivityExample;
import com.slodon.b2b2c.promotion.example.SignLogExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.SignActivity;
import com.slodon.b2b2c.promotion.pojo.SignLog;
import com.slodon.b2b2c.vo.promotion.MemberSignListVO;
import com.slodon.b2b2c.vo.promotion.SignActivityStatisticsVO;
import com.slodon.b2b2c.vo.promotion.SignLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-签到统计")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/sign/statistics")
public class AdminSignStatisticsController extends BaseController {

    @Resource
    private SignActivityModel signActivityModel;
    @Resource
    private SignLogModel signLogModel;
    @Resource
    private CouponModel couponModel;

    @ApiOperation("活动签到统计列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("actList")
    public JsonResult<PageVO<SignActivityStatisticsVO>> actList(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SignActivityExample example = new SignActivityExample();
        example.setStartTimeBefore(new Date());
        example.setStateNotEquals(SignConst.SIGN_STATE_2);
        example.setOrderBy("create_time desc");
        List<SignActivity> list = signActivityModel.getSignActivityList(example, pager);
        List<SignActivityStatisticsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(signActivity -> {
                vos.add(signActivityModel.getStatisticsVO(signActivity));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("活动签到统计详情")
    @GetMapping("actDetail")
    public JsonResult<SignActivityStatisticsVO> actDetail(HttpServletRequest request, Integer signActivityId) {
        AssertUtil.notNullOrZero(signActivityId, "签到活动id不能为空");
        SignActivity signActivity = signActivityModel.getSignActivityBySignActivityId(signActivityId);
        AssertUtil.notNull(signActivity, "查询的签到活动信息为空");
        return SldResponse.success(signActivityModel.getStatisticsVO(signActivity));
    }

    @ApiOperation("查看奖励发放列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signActivityId", value = "签到活动id", paramType = "query"),
            @ApiImplicitParam(name = "memberId", value = "会员id(查看用户签到明细必传)", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "signType", value = "签到类型（0-每日签到，1-连续签到）", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "签到开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "签到结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("bonusList")
    public JsonResult<PageVO<SignLogVO>> bonusList(HttpServletRequest request, Integer signActivityId, Integer memberId,
                                                   String memberName, Integer signType, Date startTime, Date endTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SignLogExample example = new SignLogExample();
        example.setSignActivityId(signActivityId);
        example.setMemberId(memberId);
        example.setMemberNameLike(memberName);
        example.setSignType(signType);
        example.setSignTimeAfter(startTime);
        example.setSignTimeBefore(endTime);
        List<SignLog> list = signLogModel.getSignLogList(example, pager);
        List<SignLogVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(signLog -> {
                SignLogVO vo = new SignLogVO(signLog);

                //奖励类型
                boolean integral = StringUtil.isNullOrZero(vo.getBonusIntegral());
                boolean voucher = StringUtil.isNullOrZero(vo.getBonusVoucher());
                if (integral && voucher) {
                    //没有奖励
                    vo.setBonusType(3);
                    vo.setBonusTypeValue("无");
                } else if (!voucher) {
                    //有优惠券奖励
                    vo.setBonusType(2);
                    vo.setBonusTypeValue("积分+优惠券");
                    Coupon coupon = couponModel.getCouponByCouponId(signLog.getBonusVoucher());
                    AssertUtil.notNull(coupon, "获取优惠券信息为空，请重试");
                    vo.setBonusVoucherName(coupon.getCouponName());
                } else {
                    //只有积分奖励
                    vo.setBonusType(1);
                    vo.setBonusTypeValue("积分");
                }
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("用户签到统计列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("memberList")
    public JsonResult<PageVO<MemberSignListVO>> memberList(HttpServletRequest request, String memberName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SignLogExample example = new SignLogExample();
        example.setMemberNameLike(memberName);
        List<MemberSignListVO> list = signLogModel.getMemberSignList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

}
