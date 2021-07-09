package com.slodon.b2b2c.controller.business.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.business.example.ComplainExample;
import com.slodon.b2b2c.business.example.ComplainTalkExample;
import com.slodon.b2b2c.business.example.OrderAfterSaleLogExample;
import com.slodon.b2b2c.business.example.OrderReturnExample;
import com.slodon.b2b2c.business.pojo.*;
import com.slodon.b2b2c.core.constant.ComplainConst;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.business.*;
import com.slodon.b2b2c.model.seller.StoreAddressModel;
import com.slodon.b2b2c.seller.pojo.StoreAddress;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.business.ComplainDetailVO;
import com.slodon.b2b2c.vo.business.ComplainVO;
import com.slodon.b2b2c.vo.business.OrderReturnDetailVO;
import com.slodon.b2b2c.vo.business.OrderReturnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-售后管理")
@RestController
@RequestMapping("v3/business/admin/after/sale")
public class AdminAfterSaleController extends BaseController {

    @Resource
    private OrderReturnModel orderReturnModel;
    @Resource
    private OrderAfterServiceModel orderAfterServiceModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderAfterSaleLogModel orderAfterSaleLogModel;
    @Resource
    private StoreAddressModel storeAddressModel;
    @Resource
    private ComplainModel complainModel;
    @Resource
    private ComplainTalkModel complainTalkModel;

    @ApiOperation("售后列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "类型：退款审核必传audit，默认不传", paramType = "query"),
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "afsSn", value = "退款编号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "returnType", value = "退款方式：1-仅退款 2-退货退款", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "售后状态：100-待商家审核；102-待商家收货；201-待买家发货（商家同意退货）；202-售后关闭(拒绝退款/商家拒收)；203-待平台处理（商家同意仅退款以及商家确认收货）；300-退款完成", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<OrderReturnVO>> list(HttpServletRequest request, String type, String orderSn, String afsSn, String memberName,
                                                  String storeName, Integer returnType, Integer state, Date startTime, Date endTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        OrderReturnExample example = new OrderReturnExample();
        example.setOrderSnLike(orderSn);
        example.setAfsSnLike(afsSn);
        example.setMemberNameLike(memberName);
        example.setStoreNameLike(storeName);
        example.setReturnType(returnType);
        if (!StringUtil.isEmpty(type) && "audit".equals(type)) {
            if (!StringUtil.isNullOrZero(state)) {
                if (state.equals(OrdersAfsConst.RETURN_STATE_203)) {
                    //待处理
                    example.setStateIn(OrdersAfsConst.RETURN_STATE_200 + "," + OrdersAfsConst.RETURN_STATE_203);
                } else {
                    //已完成
                    example.setState(OrdersAfsConst.RETURN_STATE_300);
                }
            } else {
                example.setStateIn(OrdersAfsConst.RETURN_STATE_200 + "," + OrdersAfsConst.RETURN_STATE_203 + "," + OrdersAfsConst.RETURN_STATE_300);
            }
        } else {
            if (!StringUtil.isNullOrZero(state)) {
                if (state.equals(OrdersAfsConst.RETURN_STATE_100)) {
                    example.setStateIn(OrdersAfsConst.RETURN_STATE_100 + "," + OrdersAfsConst.RETURN_STATE_101);
                } else if (state.equals(OrdersAfsConst.RETURN_STATE_203)) {
                    example.setStateIn(OrdersAfsConst.RETURN_STATE_200 + "," + OrdersAfsConst.RETURN_STATE_203);
                } else {
                    example.setState(state);
                }
            }
        }
        example.setApplyTimeAfter(startTime);
        example.setApplyTimeBefore(endTime);
        List<OrderReturn> list = orderReturnModel.getOrderReturnList(example, pager);
        List<OrderReturnVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(orderReturn -> {
                //查询售后信息
                OrderAfterService orderAfterService = orderAfterServiceModel.getAfterServiceByAfsSn(orderReturn.getAfsSn());
                //查询订单货品
                OrderProduct orderProduct = orderProductModel.getOrderProductByOrderProductId(orderAfterService.getOrderProductId());
                AssertUtil.notNull(orderProduct, "获取订单货品信息为空，请重试");

                //封装vo
                vos.add(new OrderReturnVO(orderReturn, orderProduct, type));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("售后详情")
    @GetMapping("detail")
    public JsonResult<OrderReturnDetailVO> detail(HttpServletRequest request, String afsSn) {
        AssertUtil.notEmpty(afsSn, "退款编号不能为空");

        OrderReturn orderReturn = orderReturnModel.getOrderReturnByAfsSn(afsSn);
        AssertUtil.notNull(orderReturn, "获取退货信息为空，请重试！");

        //查询售后信息
        OrderAfterService orderAfterService = orderAfterServiceModel.getAfterServiceByAfsSn(orderReturn.getAfsSn());
        //查询订单货品
        OrderProduct orderProduct = orderProductModel.getOrderProductByOrderProductId(orderAfterService.getOrderProductId());
        AssertUtil.notNull(orderProduct, "获取订单货品信息为空，请重试");
        //查询售后日志
        OrderAfterSaleLogExample example = new OrderAfterSaleLogExample();
        example.setAfsSn(afsSn);
        example.setOrderBy("create_time asc");
        List<OrderAfterSaleLog> logList = orderAfterSaleLogModel.getOrderAfterSaleLogList(example, null);
        AssertUtil.notEmpty(logList, "获取售后记录为空，请重试");
        StoreAddress storeAddress = null;
        if (!StringUtils.isEmpty(orderAfterService.getStoreAfsAddress())){
            storeAddress = storeAddressModel.getStoreAddressByAddressId(Integer.valueOf(orderAfterService.getStoreAfsAddress()));
        }

        OrderReturnDetailVO detailVO = new OrderReturnDetailVO(orderReturn, orderAfterService, orderProduct,storeAddress);
        List<OrderReturnDetailVO.ReturnLogVO> returnLogList = new ArrayList<>();
        logList.forEach(orderAfterSaleLog -> {
            returnLogList.add(new OrderReturnDetailVO.ReturnLogVO(orderAfterSaleLog));
        });
        detailVO.setReturnLogList(returnLogList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("确认退款")
    @OperationLogger(option = "确认退款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "afsSns", value = "售后单号集合，用逗号隔开", required = true, paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注", paramType = "query")
    })
    @PostMapping("confirmRefund")
    public JsonResult confirmRefund(HttpServletRequest request, String afsSns, String remark) {
        Admin admin = UserUtil.getUser(request, Admin.class);
        //参数校验
        AssertUtil.notEmpty(afsSns,"售后单号afsSns不能为空");
        AssertUtil.notFormatFrontIds(afsSns,"格式错误,请重试");
        orderReturnModel.adminConfirmRefund(admin, afsSns, remark);

        return SldResponse.success("确认成功");
    }
    @ApiOperation("投诉列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "afsSn", value = "退款编号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "投诉人", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "投诉店铺", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "投诉状态，1-待平台处理/2-待商家申诉/3-待双方对话/4-待平台仲裁/5-会员撤销投诉/6-会员胜诉/7-商家胜诉", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("complainList")
    public JsonResult<PageVO<ComplainVO>> complainList(HttpServletRequest request, String orderSn, String afsSn, String memberName,
                                                       String storeName, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //查询投诉列表
        ComplainExample example = new ComplainExample();
        example.setOrderSnLike(orderSn);
        example.setAfsSnLike(afsSn);
        example.setComplainMemberNameLike(memberName);
        example.setStoreNameLike(storeName);
        example.setComplainState(state);
        List<Complain> list = complainModel.getComplainList(example, pager);
        List<ComplainVO> vos = new ArrayList<>();

        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(complain -> {
                //查询售后信息
                OrderAfterService orderAfterService = orderAfterServiceModel.getAfterServiceByAfsSn(complain.getAfsSn());
                //查询订单货品
                OrderProduct orderProduct = orderProductModel.getOrderProductByOrderProductId(orderAfterService.getOrderProductId());
                AssertUtil.notNull(orderProduct, "获取订单货品信息为空，请重试");
                vos.add(new ComplainVO(complain, orderAfterService, orderProduct));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("投诉详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query")
    })
    @GetMapping("complainDetail")
    public JsonResult<ComplainDetailVO> complainDetail(HttpServletRequest request, Integer complainId) {
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        //查询投诉信息
        Complain complain = complainModel.getComplainByComplainId(complainId);
        AssertUtil.notNull(complain, "查询的投诉信息为空");
        //查询售后信息
        OrderAfterService orderAfterService = orderAfterServiceModel.getAfterServiceByAfsSn(complain.getAfsSn());
        //查询订单货品
        OrderProduct orderProduct = orderProductModel.getOrderProductByOrderProductId(orderAfterService.getOrderProductId());
        AssertUtil.notNull(orderProduct, "获取订单货品信息为空，请重试");
        //查询对话信息列表
        ComplainTalkExample example = new ComplainTalkExample();
        example.setComplainId(complainId);
        example.setOrderBy("complain_talk_id ASC");
        List<ComplainTalk> list = complainTalkModel.getComplainTalkList(example, null);

        ComplainDetailVO vo = new ComplainDetailVO(complain, orderAfterService, orderProduct);

        if (!CollectionUtils.isEmpty(list)) {
            List<ComplainDetailVO.ComplainTalkInfo> complainTalkInfoList = new ArrayList<>();
            list.forEach(complainTalk -> {
                ComplainDetailVO.ComplainTalkInfo complainTalkInfo = new ComplainDetailVO.ComplainTalkInfo(complainTalk);
                complainTalkInfoList.add(complainTalkInfo);
            });
            vo.setComplainTalkInfoList(complainTalkInfoList);
        }

        return SldResponse.success(vo);
    }

    @ApiOperation("投诉审核")
    @OperationLogger(option = "投诉审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainIds", value = "投诉id集合，用逗号隔开", required = true, paramType = "query"),
            @ApiImplicitParam(name = "auditType", value = "投诉审核:1 通过, 2 拒绝", required = true, paramType = "query"),
            @ApiImplicitParam(name = "adminHandleContent", value = "拒绝理由,当auditType为2 拒绝时,必填", paramType = "query")
    })
    @PostMapping("complainAudit")
    public JsonResult complainAudit(HttpServletRequest request, String complainIds, Integer auditType, String adminHandleContent) {
        Admin admin = UserUtil.getUser(request, Admin.class);
        //参数校验
        AssertUtil.notEmpty(complainIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(complainIds, "afsSns格式错误,请重试");
        AssertUtil.notNullOrZero(auditType, "投诉审核不能为空");
        AssertUtil.isTrue(auditType == ComplainConst.AUDIT_TYPE_NO && StringUtil.isEmpty(adminHandleContent), "当审核拒绝时,拒绝理由不能为空");

        complainModel.batchUpdateComplain(admin.getAdminId(), complainIds, auditType, adminHandleContent);

        return SldResponse.success(auditType == ComplainConst.AUDIT_TYPE_YES ? "投诉审核通过成功" : "投诉审核拒绝成功");
    }

    @ApiOperation("仲裁")
    @OperationLogger(option = "仲裁")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "会员是否胜诉:true 会员胜诉, false 商家胜诉", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "仲裁意见", paramType = "query")
    })
    @PostMapping("handle")
    public JsonResult addHandle(HttpServletRequest request, Integer complainId, Boolean state, String content) {
        //参数校验
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        AssertUtil.notNull(state, "投诉id不能为空");
        Admin admin = UserUtil.getUser(request, Admin.class);
        //仲裁
        Complain updateOne = new Complain();
        updateOne.setAdminHandleContent(content);
        updateOne.setAdminHandleTime(new Date());
        updateOne.setAdminHandleId(admin.getAdminId());
        if (state){
            updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_6);
        }else {
            updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_7);
        }

        ComplainExample example = new ComplainExample();
        example.setComplainId(complainId);
        complainModel.updateByExampleSelective(updateOne,example);

        return SldResponse.success(state ? "会员胜诉" : "商家胜诉");
    }

    @ApiOperation("发送对话")
    @OperationLogger(option = "发送对话")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "talkContent", value = "投诉对话内容", required = true, paramType = "query")
    })
    @PostMapping("addTalk")
    public JsonResult addTalk(HttpServletRequest request, Integer complainId, String talkContent) {
        AssertUtil.notNullOrZero(complainId,"投诉id不能为空");
        AssertUtil.notEmpty(talkContent,"投诉对话内容不能为空");
        Admin admin = UserUtil.getUser(request, Admin.class);
        int talkUserType = ComplainConst.TALK_USER_TYPE_3;
        complainTalkModel.saveComplainTalk(complainId,talkContent, Long.valueOf(admin.getAdminId()),admin.getAdminName(),talkUserType);

        return SldResponse.success("发送对话成功");
    }

}
