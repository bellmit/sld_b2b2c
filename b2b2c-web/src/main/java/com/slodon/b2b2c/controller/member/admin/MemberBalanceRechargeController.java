package com.slodon.b2b2c.controller.member.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.constant.OrderPaymentConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.dto.MemberBalanceRechargeUpdateDTO;
import com.slodon.b2b2c.member.example.MemberBalanceRechargeExample;
import com.slodon.b2b2c.member.pojo.MemberBalanceRecharge;
import com.slodon.b2b2c.model.member.MemberBalanceRechargeModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.member.MemberBalanceRechargeDetailVO;
import com.slodon.b2b2c.vo.member.MemberBalanceRechargeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Api(tags = "admin-充值管理")
@RestController
@RequestMapping("v3/member/admin/balanceRecharge")
public class MemberBalanceRechargeController extends BaseController {

    @Resource
    private MemberBalanceRechargeModel memberBalanceRechargeModel;

    @ApiOperation("删除充值管理")
    @OperationLogger(option = "删除充值管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rechargeIds", value = "充值管理id集合,用逗号隔开", paramType = "query", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteMemberBalanceRecharge(HttpServletRequest request, String rechargeIds) {
        //参数校验
        AssertUtil.notEmpty(rechargeIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(rechargeIds, "rechargeIds格式错误,请重试");

        memberBalanceRechargeModel.batchDeleteMemberBalanceRecharge(rechargeIds);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("充值管理列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberName", value = "充值管理名称", paramType = "query"),
            @ApiImplicitParam(name = "memberMobile", value = "手机号", paramType = "query"),
            @ApiImplicitParam(name = "payState", value = "充值状态(支付状态 1-未支付 2-已支付)", paramType = "query"),
            @ApiImplicitParam(name = "paymentCode", value = "充值方式(支付方式code：WXPAY-微信支付；ALIPAY-支付宝支付)", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "创建开始时间", paramType = "query"),
            @ApiImplicitParam(name = "payStartTime", value = "支付开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "创建结束时间", paramType = "query"),
            @ApiImplicitParam(name = "payEndTime", value = "支付结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<MemberBalanceRechargeVO>> getList(HttpServletRequest request, String memberName, String memberMobile,
                                                               Integer payState, String paymentCode, Date startTime,
                                                               Date payStartTime, Date endTime, Date payEndTime) {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据条件查询集合
        MemberBalanceRechargeExample example = new MemberBalanceRechargeExample();
        example.setMemberNameLike(memberName);
        example.setMemberMobileLike(memberMobile);
        example.setPayState(payState);
        if (!StringUtil.isEmpty(paymentCode)) {
            if (paymentCode.equals(OrderPaymentConst.PAYMENT_CODE_WXPAY)) {
                example.setPaymentCodeLike(PayConst.METHOD_WX.toUpperCase());
            } else if (paymentCode.equals(OrderPaymentConst.PAYMENT_CODE_ALIPAY)) {
                example.setPaymentCodeLike(PayConst.METHOD_ALIPAY.toUpperCase());
            }
        }
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);
        example.setPayTimeAfter(payStartTime);
        example.setPayTimeBefore(payEndTime);
        List<MemberBalanceRecharge> list = memberBalanceRechargeModel.getMemberBalanceRechargeList(example, pager);
        List<MemberBalanceRechargeVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(memberbalancerecharge -> {
                MemberBalanceRechargeVO vo = new MemberBalanceRechargeVO(memberbalancerecharge);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("充值管理详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "rechargeId", value = "充值管理id", paramType = "query", required = true)
    })
    @GetMapping("detail")
    public JsonResult<MemberBalanceRechargeDetailVO> getMemberBalanceRecharge(HttpServletRequest request, Integer rechargeId) {

        //查询详情
        MemberBalanceRecharge memberBalanceRecharge = memberBalanceRechargeModel.getMemberBalanceRechargeByRechargeId(rechargeId);
        MemberBalanceRechargeDetailVO vo = new MemberBalanceRechargeDetailVO(memberBalanceRecharge);
        return SldResponse.success(vo);
    }

    @ApiOperation("累计充值")
    @GetMapping("getSum")
    public JsonResult getMemberBalanceRechargeSum(HttpServletRequest request) {
        //根据条件查询充值明细集合
        MemberBalanceRechargeExample example = new MemberBalanceRechargeExample();
        example.setPayState(MemberConst.PAY_STATE_2);   //充值成功

        List<MemberBalanceRecharge> list = memberBalanceRechargeModel.getMemberBalanceRechargeList(example, null);
        //累计充值人次
        int rechargeNum = memberBalanceRechargeModel.getRechargeNumber(example);

        //累计充值金额
        BigDecimal rechargeSum = BigDecimal.ZERO;
        for (MemberBalanceRecharge memberBalanceRecharge : list) {
            rechargeSum = rechargeSum.add(memberBalanceRecharge.getPayAmount());
        }
        //响应
        HashMap<String, Object> map = new HashMap<>();
        map.put("rechargeNum", rechargeNum);
        map.put("rechargeSum", rechargeSum);

        return SldResponse.success(map);
    }

    @ApiOperation("去付款")
    @OperationLogger(option = "去付款")
    @PostMapping("toPay")
    public JsonResult toPay(HttpServletRequest request, MemberBalanceRechargeUpdateDTO memberBalanceRechargeUpdateDTO) throws Exception {

        AssertUtil.notNullOrZero(memberBalanceRechargeUpdateDTO.getRechargeId(), "请选择要修改的数据");
        Admin admin = UserUtil.getUser(request, Admin.class);
        memberBalanceRechargeModel.updateMemberBalanceRecharge(memberBalanceRechargeUpdateDTO, admin);
        return SldResponse.success("付款成功", memberBalanceRechargeUpdateDTO.getRechargeId());
    }
}
