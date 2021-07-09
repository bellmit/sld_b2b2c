package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.SignConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.CouponModel;
import com.slodon.b2b2c.model.promotion.SignActivityModel;
import com.slodon.b2b2c.promotion.dto.SignActivityAddDTO;
import com.slodon.b2b2c.promotion.dto.SignActivityUpdateDTO;
import com.slodon.b2b2c.promotion.example.SignActivityExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.SignActivity;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.promotion.SignActivityDetailVO;
import com.slodon.b2b2c.vo.promotion.SignActivityVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-签到活动")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/sign/activity")
public class AdminSignActivityController extends BaseController {

    @Resource
    private SignActivityModel signActivityModel;
    @Resource
    private CouponModel couponModel;

    @ApiOperation("签到活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SignActivityVO>> list(HttpServletRequest request, Date startTime, Date endTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SignActivityExample example = new SignActivityExample();
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        example.setStateNotEquals(SignConst.SIGN_STATE_2);
        List<SignActivity> list = signActivityModel.getSignActivityList(example, pager);
        List<SignActivityVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(signActivity -> {
                SignActivityVO signActivityVO = new SignActivityVO(signActivity);
                if (!StringUtil.isNullOrZero(signActivity.getBonusVoucher())) {
                    //设置优惠券名称
                    Coupon coupon = couponModel.getCouponByCouponId(signActivity.getBonusVoucher());
                    AssertUtil.notNull(coupon, "获取优惠券信息为空，请重试");
                    signActivityVO.setBonusVoucherName(coupon.getCouponName());
                }
                vos.add(signActivityVO);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("签到活动详情")
    @GetMapping("detail")
    public JsonResult<SignActivityDetailVO> detail(HttpServletRequest request, Integer signActivityId) {
        SignActivity signActivity = signActivityModel.getSignActivityBySignActivityId(signActivityId);
        SignActivityDetailVO vo = new SignActivityDetailVO(signActivity);
        if (!StringUtil.isNullOrZero(signActivity.getBonusVoucher())) {
            //设置优惠券名称
            Coupon coupon = couponModel.getCouponByCouponId(signActivity.getBonusVoucher());
            AssertUtil.notNull(coupon, "获取优惠券信息为空，请重试");
            vo.setBonusVoucherName(coupon.getCouponName());
        }
        return SldResponse.success(vo);
    }

    @ApiOperation("新建签到活动")
    @OperationLogger(option = "新建签到活动")
    @PostMapping("add")
    public JsonResult addSign(HttpServletRequest request, SignActivityAddDTO signActivityDTO) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        SignActivity signActivity = new SignActivity();
        BeanUtils.copyProperties(signActivityDTO, signActivity);
        signActivity.setCreateAdminId(admin.getAdminId());
        signActivity.setCreateAdminName(admin.getAdminName());
        signActivityModel.saveSignActivity(signActivity);
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑签到活动")
    @OperationLogger(option = "编辑签到活动")
    @PostMapping("update")
    public JsonResult updateSign(HttpServletRequest request, SignActivityUpdateDTO signActivityUpdateDTO) {
        //查询签到活动
        SignActivity signDb = signActivityModel.getSignActivityBySignActivityId(signActivityUpdateDTO.getSignActivityId());
        AssertUtil.notNull(signDb, "签到活动不存在");
        AssertUtil.isTrue(signDb.getSignState() == 2, "进行中的活动不允许编辑");
        //判断活动时间是否重叠
        AssertUtil.isTrue(!signActivityModel.checkActivityTime(signActivityUpdateDTO.getSignActivityId(), signActivityUpdateDTO.getStartTime(),
                signActivityUpdateDTO.getEndTime()), "当前选择的时间段内已有活动，请选择其他时间段");

        SignActivity signActivity = new SignActivity();
        BeanUtils.copyProperties(signActivityUpdateDTO, signActivity);
        if (StringUtil.isNullOrZero(signActivity.getContinueNum())) {
            signActivity.setContinueNum(0);
            signActivity.setBonusIntegral(0);
            signActivity.setBonusVoucher(0);
        }
        signActivityModel.updateSignActivity(signActivity);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("是否提醒签到活动")
    @OperationLogger(option = "是否提醒签到活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signActivityId", value = "签到活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isRemind", value = "是否提醒，0-否，1-是", required = true, paramType = "query")
    })
    @PostMapping("isRemind")
    public JsonResult isRemind(HttpServletRequest request, Integer signActivityId, Integer isRemind) {
        //查询签到活动
        SignActivity signDb = signActivityModel.getSignActivityBySignActivityId(signActivityId);
        AssertUtil.notNull(signDb, "签到活动不存在");
        AssertUtil.isTrue(signDb.getState() != SignConst.SIGN_STATE_1, "该签到活动尚未开启，请确认开启后再提醒");
        //判断活动时间是否重叠
        Date date = new Date();
        if (SignConst.SIGN_STATE_1 == isRemind) {
            AssertUtil.isTrue(!signActivityModel.checkActivityTime(signActivityId, date, date), "当前选择的时间段内已有活动，请选择其他时间段");
        }

        SignActivity signActivity = new SignActivity();
        signActivity.setSignActivityId(signActivityId);
        signActivity.setIsRemind(isRemind);
        signActivityModel.updateSignActivity(signActivity);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("是否开启签到活动")
    @OperationLogger(option = "是否开启签到活动")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signActivityId", value = "签到活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isOpen", value = "是否开启，0-关闭，1-开启", required = true, paramType = "query")
    })
    @PostMapping("isOpen")
    public JsonResult isOpen(HttpServletRequest request, Integer signActivityId, Integer isOpen) {

        //判断活动时间是否重叠
        Date date = new Date();
        if (SignConst.SIGN_STATE_1 == isOpen) {
            AssertUtil.isTrue(!signActivityModel.checkActivityTime(signActivityId, date, date), "当前选择的时间段内已有活动，请选择其他时间段");
        }
        SignActivity signActivity = new SignActivity();
        signActivity.setSignActivityId(signActivityId);
        signActivity.setState(isOpen);
        signActivityModel.updateSignActivity(signActivity);
        return SldResponse.success(isOpen == 0 ? "关闭成功" : "开启成功");
    }

    @ApiOperation("删除签到活动")
    @OperationLogger(option = "删除签到活动")
    @PostMapping("del")
    public JsonResult delSignActivity(HttpServletRequest request, Integer signActivityId) {
        //查询签到活动
        SignActivity signDb = signActivityModel.getSignActivityBySignActivityId(signActivityId);
        AssertUtil.notNull(signDb, "签到活动不存在");
        AssertUtil.isTrue(signDb.getSignState() == 2 && signDb.getState() == SignConst.SIGN_STATE_1, "正在进行的活动无法删除");
        AssertUtil.isTrue(signDb.getState() == SignConst.SIGN_STATE_2, "活动已经删除，请勿重复操作");

        //伪删除，将state状态设置为删除
        SignActivity signActivity = new SignActivity();
        signActivity.setSignActivityId(signActivityId);
        signActivity.setState(SignConst.SIGN_STATE_2);
        signActivityModel.updateSignActivity(signActivity);
        return SldResponse.success("删除成功");
    }


}
