package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.constant.StoreTplConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.model.seller.StoreApplyModel;
import com.slodon.b2b2c.model.seller.StoreCertificateModel;
import com.slodon.b2b2c.model.system.BillAccountModel;
import com.slodon.b2b2c.model.system.BillLogModel;
import com.slodon.b2b2c.model.system.BillModel;
import com.slodon.b2b2c.model.system.BillOrderBindModel;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import com.slodon.b2b2c.system.example.BillAccountExample;
import com.slodon.b2b2c.system.example.BillExample;
import com.slodon.b2b2c.system.example.BillLogExample;
import com.slodon.b2b2c.system.example.BillOrderBindExample;
import com.slodon.b2b2c.system.pojo.*;
import com.slodon.b2b2c.vo.system.BillDetailVO;
import com.slodon.b2b2c.vo.system.BillVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_SELLER_MSG;

@Api(tags = "admin-结算管理")
@RestController
@RequestMapping("v3/system/admin/bill")
public class AdminBillController extends BaseController {

    @Resource
    private BillModel billModel;
    @Resource
    private BillAccountModel billAccountModel;
    @Resource
    private BillOrderBindModel billOrderBindModel;
    @Resource
    private BillLogModel billLogModel;
    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("结算列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billSn", value = "结算单号", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "结算状态：1、待确认；2、待审核；3、待结算；4、结算完成", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "结算开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结算结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<BillVO>> list(HttpServletRequest request, String storeName, Integer state,
                                           Date startTime, Date endTime, String billSn) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        BillExample example = new BillExample();
        example.setStoreNameLike(storeName);
        example.setBillSnLike(billSn);
        example.setState(state);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        List<Bill> list = billModel.getBillList(example, pager);
        List<BillVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(bill -> {
                vos.add(new BillVO(bill));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("结算详情")
    @GetMapping("detail")
    public JsonResult<BillDetailVO> detail(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "结算id不能为空");

        Bill bill = billModel.getBillByBillId(billId);
        AssertUtil.notNull(bill, "获取结算信息为空，请重试！");

        BillDetailVO detailVO = null;
        if (bill.getState() == BillConst.STATE_1) {
            detailVO = new BillDetailVO(bill);
        } else {
            //查询结算账号信息
            BillAccountExample example = new BillAccountExample();
            example.setStoreId(bill.getStoreId());
            example.setIsDefault(BillConst.IS_DEFAULT_YES);
            List<BillAccount> accountList = billAccountModel.getBillAccountList(example, null);
            detailVO = new BillDetailVO(bill, accountList.get(0));
        }
        //查询结算订单信息
        BillOrderBindExample bindExample = new BillOrderBindExample();
        bindExample.setBillSn(bill.getBillSn());
        List<BillOrderBind> orderBindList = billOrderBindModel.getBillOrderBindList(bindExample, null);
        AssertUtil.notEmpty(orderBindList, "获取结算订单信息为空，请重试");
        detailVO.setOrderList(orderBindList);
        //查询结算日志信息
        BillLogExample logExample = new BillLogExample();
        logExample.setBillSn(bill.getBillSn());
        logExample.setOrderBy("create_time asc");
        List<BillLog> billLogList = billLogModel.getBillLogList(logExample, null);
        AssertUtil.notEmpty(billLogList, "获取结算日志记录为空，请重试");
        List<BillDetailVO.BillLogVO> logVOS = new ArrayList<>();
        billLogList.forEach(billLog -> {
            logVOS.add(new BillDetailVO.BillLogVO(billLog));
        });
        detailVO.setLogList(logVOS);
        //根据storeId查询vendorId信息
        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setStoreId(bill.getStoreId());
        List<StoreApply> storeApplyList = storeApplyModel.getStoreApplyList(storeApplyExample, null);
        AssertUtil.notEmpty(storeApplyList, "获取店铺申请信息为空，请重试");
        //根据vendorId查询店铺联系信息
        StoreCertificateExample certificateExample = new StoreCertificateExample();
        certificateExample.setVendorId(storeApplyList.get(0).getVendorId());
        List<StoreCertificate> certificateList = storeCertificateModel.getStoreCertificateList(certificateExample, null);
        AssertUtil.notEmpty(certificateList, "获取店铺资质信息为空，请重试");
        detailVO.setContactName(certificateList.get(0).getContactName());
        detailVO.setContactPhone(CommonUtil.dealMobile(certificateList.get(0).getContactPhone()));
        return SldResponse.success(detailVO);
    }

    @ApiOperation("审核通过")
    @OperationLogger(option = "审核通过")
    @PostMapping("approved")
    public JsonResult approved(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "结算单id不能为空");

        Admin admin = UserUtil.getUser(request, Admin.class);

        Bill billDb = billModel.getBillByBillId(billId);
        AssertUtil.notNull(billDb, "获取结算信息为空，请重试");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_2, "未到平台审核阶段");

        Bill bill = new Bill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_3);
        bill.setUpdateTime(new Date());
        billModel.updateBill(bill);
        //记录操作日志
        BillLog billLog = new BillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(admin.getAdminId().longValue());
        billLog.setOperatorName(admin.getAdminName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_3);
        billLog.setContent("平台审核");
        billLog.setCreateTime(new Date());
        billLogModel.saveBillLog(billLog);
        return SldResponse.success("审核成功");
    }

    @ApiOperation("确认打款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billId", value = "结算id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "paymentRemark", value = "打款备注", paramType = "query"),
            @ApiImplicitParam(name = "paymentEvidence", value = "打款凭证", paramType = "query")
    })
    @PostMapping("confirmPayment")
    public JsonResult confirmPayment(HttpServletRequest request, Integer billId,
                                     String paymentRemark, String paymentEvidence) {
        AssertUtil.notNullOrZero(billId, "结算单id不能为空");

        Admin admin = UserUtil.getUser(request, Admin.class);

        Bill billDb = billModel.getBillByBillId(billId);
        AssertUtil.notNull(billDb, "获取结算信息为空，请重试");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_3, "未到平台打款阶段");

        Bill bill = new Bill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_4);
        bill.setPaymentRemark(paymentRemark);
        bill.setPaymentEvidence(paymentEvidence);
        bill.setUpdateTime(new Date());
        billModel.updateBill(bill);
        //记录操作日志
        BillLog billLog = new BillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(admin.getAdminId().longValue());
        billLog.setOperatorName(admin.getAdminName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_4);
        billLog.setContent("结算完成");
        billLog.setCreateTime(new Date());
        billLogModel.saveBillLog(billLog);

        //消息通知
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("billSn", billDb.getBillSn()));
        String msgLinkInfo = "{\"billSn\":\"" + billDb.getBillSn() + "\",\"type\":\"bill_news\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, billDb.getStoreId(), StoreTplConst.BILL_PAID_REMINDER, msgLinkInfo);
        //发送到mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
        return SldResponse.success("确认成功");
    }

    @ApiOperation("结算导出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billSns", value = "结算单号集合，用逗号隔开", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "结算状态：1、待确认；2、待审核；3、待结算；4、结算完成", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "结算开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结算结束时间", paramType = "query")
    })
    @GetMapping("export")
    public JsonResult export(HttpServletRequest request, HttpServletResponse response, String billSns, String storeName,
                             Integer state, Date startTime, Date endTime) {

        BillExample example = new BillExample();
        if (!StringUtil.isEmpty(billSns)) {
            //参数校验
            AssertUtil.notFormatFrontIds(billSns, "格式错误,请重试");
            example.setBillSnIn("'" + billSns.replace(",", "','") + "'");
        }
        example.setStoreNameLike(storeName);
        example.setState(state);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        billModel.billExport(request, response, example);
        return null;
    }
}
