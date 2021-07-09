package com.slodon.b2b2c.controller.business.front;

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
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.business.*;
import com.slodon.b2b2c.model.seller.StoreAddressModel;
import com.slodon.b2b2c.seller.pojo.StoreAddress;
import com.slodon.b2b2c.vo.business.ComplainDetailVO;
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
import java.util.List;

@Api(tags = "front-售后管理")
@RestController
@RequestMapping("v3/business/front/after/sale")
public class FrontAfterSaleController extends BaseController {

    @Resource
    private OrderReturnModel orderReturnModel;
    @Resource
    private OrderAfterServiceModel orderAfterServiceModel;
    @Resource
    private OrderAfterSaleLogModel orderAfterSaleLogModel;
    @Resource
    private OrderProductModel orderProductModel;
    @Resource
    private OrderModel orderModel;
    @Resource
    private ComplainModel complainModel;
    @Resource
    private ComplainTalkModel complainTalkModel;
    @Resource
    private StoreAddressModel storeAddressModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("售后列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "returnType", value = "退款方式：1-仅退款 2-退货退款", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<OrderReturnVO>> list(HttpServletRequest request, Integer returnType) {
        Member member = UserUtil.getUser(request, Member.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        OrderReturnExample example = new OrderReturnExample();
        example.setMemberId(member.getMemberId());
        example.setReturnType(returnType);
        List<OrderReturn> list = orderReturnModel.getOrderReturnList(example, pager);
        List<OrderReturnVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(orderReturn -> {
                //查询售后信息
                OrderAfterService orderAfterService = orderAfterServiceModel.getAfterServiceByAfsSn(orderReturn.getAfsSn());
                //查询订单货品
                OrderProduct orderProduct = orderProductModel.getOrderProductByOrderProductId(orderAfterService.getOrderProductId());
                AssertUtil.notNull(orderProduct, "获取订单货品信息为空，请重试");
                OrderReturnVO vo = new OrderReturnVO(orderReturn, orderProduct, "afterSale");
                //查询订单状态
                Order order = orderModel.getOrderByOrderSn(orderReturn.getOrderSn());
                vo.setOrderState(order.getOrderState());
                //封装vo
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("售后详情")
    @GetMapping("detail")
    public JsonResult<OrderReturnDetailVO> detail(HttpServletRequest request, String afsSn) {
        AssertUtil.notEmpty(afsSn, "退款编号不能为空");

        Member member = UserUtil.getUser(request, Member.class);

        OrderReturn orderReturn = orderReturnModel.getOrderReturnByAfsSn(afsSn);
        AssertUtil.notNull(orderReturn, "获取退货信息为空，请重试！");
        AssertUtil.isTrue(!orderReturn.getMemberId().equals(member.getMemberId()), "非法操作");

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
        if (!StringUtils.isEmpty(orderAfterService.getStoreAfsAddress())) {
            storeAddress = storeAddressModel.getStoreAddressByAddressId(Integer.valueOf(orderAfterService.getStoreAfsAddress()));
        }

        OrderReturnDetailVO detailVO = new OrderReturnDetailVO(orderReturn, orderAfterService, orderProduct, storeAddress);
        if (orderReturn.getState().equals(OrdersAfsConst.RETURN_STATE_100)
                || orderReturn.getState().equals(OrdersAfsConst.RETURN_STATE_101)) {
            //申请状态,商家处理截止时间
            int limitDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_afs_seller_audit"));
            detailVO.setDeadline(TimeUtil.getDayAgoDate(orderAfterService.getBuyerApplyTime(), limitDay));
        } else if (orderReturn.getState().equals(OrdersAfsConst.RETURN_STATE_201)) {
            //商家同意退货退款，待买家发货
            int limitDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_afs_member_send"));
            detailVO.setDeadline(TimeUtil.getDayAgoDate(orderAfterService.getStoreAuditTime(), limitDay));
        } else if (orderReturn.getState().equals(OrdersAfsConst.RETURN_STATE_102)) {
            //买家发货，待商家收货退款
            int limitDay = Integer.parseInt(stringRedisTemplate.opsForValue().get("time_limit_of_afs_seller_receive"));
            detailVO.setDeadline(TimeUtil.getDayAgoDate(orderAfterService.getBuyerDeliverTime(), limitDay));
        }
        //拨打电话
        String phone = stringRedisTemplate.opsForValue().get("basic_site_phone");
        if (!StringUtils.isEmpty(phone)) {
            detailVO.setPlatformPhone(CommonUtil.dealMobile(phone));
        }
        List<OrderReturnDetailVO.ReturnLogVO> returnLogList = new ArrayList<>();
        logList.forEach(orderAfterSaleLog -> {
            returnLogList.add(new OrderReturnDetailVO.ReturnLogVO(orderAfterSaleLog));
        });
        detailVO.setReturnLogList(returnLogList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("用户售后发货")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "afsSn", value = "售后服务单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "expressId", value = "物流公司ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "logisticsNumber", value = "快递单号", required = true, paramType = "query")
    })
    @PostMapping("deliverGoods")
    public JsonResult deliverGoods(HttpServletRequest request, String afsSn, Integer expressId, String logisticsNumber) {
        Member member = UserUtil.getUser(request, Member.class);

        orderAfterServiceModel.memberDeliverGoods(member.getMemberId(), afsSn, expressId, logisticsNumber);
        return SldResponse.success("发货成功");
    }

    @ApiOperation("投诉商家")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "afsSn", value = "售后服务单号", required = true, paramType = "query"),
            @ApiImplicitParam(name = "complainContent", value = "投诉内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "complainPic", value = "上传凭证,图片之间用逗号隔开", paramType = "query")
    })
    @PostMapping("complainStore")
    public JsonResult complainStore(HttpServletRequest request, String afsSn, String complainContent, String complainPic) {
        //校验
        AssertUtil.notEmpty(afsSn, "售后服务单号不能为空");
        AssertUtil.notEmpty(complainContent, "投诉内容不能为空");
        //投诉商家
        Member member = UserUtil.getUser(request, Member.class);
        Integer complainId = complainModel.saveComplain(afsSn, complainContent, complainPic, member.getMemberId(), member.getMemberName());
        return SldResponse.success("投诉商家成功", complainId);
    }

    @ApiOperation("投诉详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query")
    })
    @GetMapping("complainDetail")
    public JsonResult<ComplainDetailVO> complainDetail(HttpServletRequest request, Integer complainId) {
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        Member member = UserUtil.getUser(request, Member.class);

        //查询投诉信息
        Complain complain = complainModel.getComplainByComplainId(complainId);
        AssertUtil.notNull(complain, "查询的投诉信息为空");
        AssertUtil.isTrue(!complain.getComplainMemberId().equals(member.getMemberId()), "您无权操作");
        //查询售后信息
        OrderAfterService orderAfterService = orderAfterServiceModel.getAfterServiceByAfsSn(complain.getAfsSn());
        AssertUtil.isTrue(!orderAfterService.getMemberId().equals(member.getMemberId()), "您无权操作");
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

    @ApiOperation("撤销投诉")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query")
    })
    @PostMapping("cancelComplain")
    public JsonResult cancelComplain(HttpServletRequest request, Integer complainId) {
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        Member member = UserUtil.getUser(request, Member.class);
        //撤销投诉
        Complain updateOne = new Complain();
        updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_5);

        ComplainExample example = new ComplainExample();
        example.setComplainId(complainId);
        example.setComplainMemberId(member.getMemberId());
        Integer count = complainModel.updateByExampleSelective(updateOne, example);
        return SldResponse.success("撤销投诉成功");
    }

    @ApiOperation("发送对话")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "talkContent", value = "投诉对话内容", required = true, paramType = "query")
    })
    @PostMapping("addTalk")
    public JsonResult addTalk(HttpServletRequest request, Integer complainId, String talkContent) {
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        AssertUtil.notEmpty(talkContent, "投诉对话内容不能为空");
        Member member = UserUtil.getUser(request, Member.class);
        int talkUserType = ComplainConst.TALK_USER_TYPE_1;
        complainTalkModel.saveComplainTalk(complainId, talkContent, Long.valueOf(member.getMemberId()), member.getMemberName(), talkUserType);
        return SldResponse.success("发送对话成功");
    }

    @ApiOperation("提交仲裁")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "complainId", value = "投诉id", required = true, paramType = "query")
    })
    @PostMapping("handle")
    public JsonResult handle(HttpServletRequest request, Integer complainId) {
        //校验
        AssertUtil.notNullOrZero(complainId, "投诉id不能为空");
        //仲裁
        Member member = UserUtil.getUser(request, Member.class);
        Complain updateOne = new Complain();
        updateOne.setComplainState(ComplainConst.COMPLAIN_STATE_4);

        ComplainExample example = new ComplainExample();
        example.setComplainMemberId(member.getMemberId());
        example.setComplainId(complainId);
        complainModel.updateByExampleSelective(updateOne, example);
        return SldResponse.success("提交仲裁成功");
    }
}
