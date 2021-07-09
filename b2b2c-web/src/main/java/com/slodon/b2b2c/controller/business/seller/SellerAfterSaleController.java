package com.slodon.b2b2c.controller.business.seller;

import com.slodon.b2b2c.aop.VendorLogger;
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
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.business.*;
import com.slodon.b2b2c.model.seller.StoreAddressModel;
import com.slodon.b2b2c.seller.pojo.StoreAddress;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.business.ComplainDetailVO;
import com.slodon.b2b2c.vo.business.ComplainVO;
import com.slodon.b2b2c.vo.business.OrderReturnDetailVO;
import com.slodon.b2b2c.vo.business.OrderReturnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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

@Api(tags = "seller-售后管理")
@RestController
@RequestMapping("v3/business/seller/after/sale")
public class SellerAfterSaleController extends BaseController {

    @Resource
    private OrderReturnModel orderReturnModel;
    @Resource
    private OrderAfterServiceModel orderAfterServiceModel;
    @Resource
    private OrderAfterSaleLogModel orderAfterSaleLogModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private ComplainModel complainModel;
    @Resource
    private ComplainTalkModel complainTalkModel;
    @Resource
    private StoreAddressModel storeAddressModel;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("售后列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "returnType", value = "退款方式：1-仅退款 2-退货退款", required = true, paramType = "query"),
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "afsSn", value = "退款编号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "售后状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家同意退款申请；201-商家同意退货退款申请；202-商家拒绝退款申请(退款关闭/拒收关闭)；203-商家确认收货；300-平台确认退款(已完成)", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<OrderReturnVO>> list(HttpServletRequest request, Integer returnType, String orderSn, String afsSn,
                                                  String memberName, Integer state, Date startTime, Date endTime) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        OrderReturnExample example = new OrderReturnExample();
        example.setStoreId(vendor.getStoreId());
        example.setReturnType(returnType);
        example.setApplyTimeAfter(startTime);
        example.setApplyTimeBefore(endTime);
        example.setState(state);
        example.setOrderSnLike(orderSn);
        example.setAfsSnLike(afsSn);
        example.setMemberNameLike(memberName);
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
                vos.add(new OrderReturnVO(orderReturn, orderProduct, "afterSale"));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("售后详情")
    @GetMapping("detail")
    public JsonResult<OrderReturnDetailVO> detail(HttpServletRequest request, String afsSn) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notEmpty(afsSn, "退款编号不能为空");

        OrderReturn orderReturn = orderReturnModel.getOrderReturnByAfsSn(afsSn);
        AssertUtil.notNull(orderReturn, "获取退货信息为空，请重试！");
        AssertUtil.isTrue(!vendor.getStoreId().equals(orderReturn.getStoreId()), "无权限");

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
        //查询退款设置方式
        Integer settingSwitch = Integer.parseInt(stringRedisTemplate.opsForValue().get("refund_setting_switch"));
        if (settingSwitch == OrdersAfsConst.MONEY_RETURN_TYPE_TO_BALANCE) {
            detailVO.setReturnMethod("同意退款后，退款将退回到会员的余额");
        } else {
            detailVO.setReturnMethod("同意退款后，退款将按支付方式原路退回");
        }
        return SldResponse.success(detailVO);
    }

    @ApiOperation("审核退款申请")
    @VendorLogger(option = "审核退款申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "afsSn", value = "售后单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isPass", value = "是否审核通过[true==是,false==否]", required = true, paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注(拒绝必填)", paramType = "query"),
            @ApiImplicitParam(name = "storeAddressId", value = "商家地址id (同意退货退款时必填)", paramType = "query")
    })
    @PostMapping("audit")
    public JsonResult audit(HttpServletRequest request, String afsSn, boolean isPass, String remark, Integer storeAddressId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        orderReturnModel.afsStoreAudit(vendor, afsSn, isPass, remark, storeAddressId);

        return SldResponse.success("审核成功");
    }

    @ApiOperation("确认收货")
    @VendorLogger(option = "确认收货")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "afsSn", value = "售后单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isReceive", value = "是否收货：true-收货，false-拒收", required = true, paramType = "query"),
            @ApiImplicitParam(name = "remark", value = "备注(拒绝必填)", paramType = "query")
    })
    @PostMapping("confirmReceive")
    public JsonResult confirmReceive(HttpServletRequest request, String afsSn, boolean isReceive, String remark) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        orderReturnModel.afsStoreReceive(vendor, afsSn, isReceive, remark);

        return SldResponse.success("审核成功");
    }

    @ApiOperation("投诉列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "afsSn", value = "退款编号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "投诉人", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "投诉商品名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "投诉状态，1-待平台处理/2-待商家申诉/3-待双方对话/4-待平台仲裁/5-会员撤销投诉/6-会员胜诉/7-商家胜诉", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "投诉开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "投诉结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("complainList")
    public JsonResult<PageVO<ComplainVO>> complainList(HttpServletRequest request, String orderSn, String afsSn, String memberName,
                                                       String goodsName, Integer state, Date startTime, Date endTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //查询投诉列表
        ComplainExample example = new ComplainExample();
        example.setOrderSnLike(orderSn);
        example.setAfsSnLike(afsSn);
        example.setComplainMemberNameLike(memberName);
        example.setGoodsNameLike(goodsName);
        example.setComplainState(state);
        example.setComplainTimeAfter(startTime);
        example.setComplainTimeBefore(endTime);
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

    @ApiOperation("提交申诉")
    @VendorLogger(option = "提交申诉")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "appealContent", value = "申诉内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "appealImage", value = "上传凭证,图片之间用逗号隔开", paramType = "query")
    })
    @PostMapping("appeal")
    public JsonResult appeal(HttpServletRequest request, Integer complainId, String appealContent, String appealImage) {
        //校验
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        AssertUtil.notEmpty(appealContent, "申诉内容不能为空");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //申诉
        Complain updateOne = new Complain();
        updateOne.setAppealContent(appealContent);
        updateOne.setAppealImage(appealImage);
        updateOne.setAppealTime(new Date());
        updateOne.setAppealVendorId(vendor.getVendorId());
        updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_4);

        ComplainExample example = new ComplainExample();
        example.setStoreId(vendor.getStoreId());
        example.setComplainId(complainId);
        complainModel.updateByExampleSelective(updateOne, example);

        return SldResponse.success("提交申诉成功");
    }

    @ApiOperation("放弃申诉")
    @VendorLogger(option = "放弃申诉")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query")
    })
    @PostMapping("cancelAppeal")
    public JsonResult cancelAppeal(HttpServletRequest request, Integer complainId) {
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //放弃申诉
        Complain updateOne = new Complain();
        updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_6);

        ComplainExample example = new ComplainExample();
        example.setComplainId(complainId);
        example.setStoreId(vendor.getStoreId());
        Integer count = complainModel.updateByExampleSelective(updateOne, example);

        return SldResponse.success("放弃申诉成功");
    }

    @ApiOperation("发送对话")
    @VendorLogger(option = "发送对话")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "talkContent", value = "投诉对话内容", required = true, paramType = "query")
    })
    @PostMapping("addTalk")
    public JsonResult addTalk(HttpServletRequest request, Integer complainId, String talkContent) {
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        AssertUtil.notEmpty(talkContent, "投诉对话内容不能为空");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        int talkUserType = ComplainConst.TALK_USER_TYPE_2;
        complainTalkModel.saveComplainTalk(complainId, talkContent, vendor.getStoreId(), vendor.getVendorName(), talkUserType);

        return SldResponse.success("发送对话成功");
    }

    @ApiOperation("提交仲裁")
    @VendorLogger(option = "提交仲裁")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query")
    })
    @PostMapping("handle")
    public JsonResult handle(HttpServletRequest request, Integer complainId) {
        //校验
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        //仲裁
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        Complain updateOne = new Complain();
        updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_4);

        ComplainExample example = new ComplainExample();
        example.setStoreId(vendor.getStoreId());
        example.setComplainId(complainId);
        complainModel.updateByExampleSelective(updateOne, example);

        return SldResponse.success("提交仲裁成功");
    }
}
