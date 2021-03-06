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

@Api(tags = "admin-????????????")
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

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billSn", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "???????????????1???????????????2???????????????3???????????????4???????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
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

    @ApiOperation("????????????")
    @GetMapping("detail")
    public JsonResult<BillDetailVO> detail(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "??????id????????????");

        Bill bill = billModel.getBillByBillId(billId);
        AssertUtil.notNull(bill, "???????????????????????????????????????");

        BillDetailVO detailVO = null;
        if (bill.getState() == BillConst.STATE_1) {
            detailVO = new BillDetailVO(bill);
        } else {
            //????????????????????????
            BillAccountExample example = new BillAccountExample();
            example.setStoreId(bill.getStoreId());
            example.setIsDefault(BillConst.IS_DEFAULT_YES);
            List<BillAccount> accountList = billAccountModel.getBillAccountList(example, null);
            detailVO = new BillDetailVO(bill, accountList.get(0));
        }
        //????????????????????????
        BillOrderBindExample bindExample = new BillOrderBindExample();
        bindExample.setBillSn(bill.getBillSn());
        List<BillOrderBind> orderBindList = billOrderBindModel.getBillOrderBindList(bindExample, null);
        AssertUtil.notEmpty(orderBindList, "??????????????????????????????????????????");
        detailVO.setOrderList(orderBindList);
        //????????????????????????
        BillLogExample logExample = new BillLogExample();
        logExample.setBillSn(bill.getBillSn());
        logExample.setOrderBy("create_time asc");
        List<BillLog> billLogList = billLogModel.getBillLogList(logExample, null);
        AssertUtil.notEmpty(billLogList, "??????????????????????????????????????????");
        List<BillDetailVO.BillLogVO> logVOS = new ArrayList<>();
        billLogList.forEach(billLog -> {
            logVOS.add(new BillDetailVO.BillLogVO(billLog));
        });
        detailVO.setLogList(logVOS);
        //??????storeId??????vendorId??????
        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setStoreId(bill.getStoreId());
        List<StoreApply> storeApplyList = storeApplyModel.getStoreApplyList(storeApplyExample, null);
        AssertUtil.notEmpty(storeApplyList, "??????????????????????????????????????????");
        //??????vendorId????????????????????????
        StoreCertificateExample certificateExample = new StoreCertificateExample();
        certificateExample.setVendorId(storeApplyList.get(0).getVendorId());
        List<StoreCertificate> certificateList = storeCertificateModel.getStoreCertificateList(certificateExample, null);
        AssertUtil.notEmpty(certificateList, "??????????????????????????????????????????");
        detailVO.setContactName(certificateList.get(0).getContactName());
        detailVO.setContactPhone(CommonUtil.dealMobile(certificateList.get(0).getContactPhone()));
        return SldResponse.success(detailVO);
    }

    @ApiOperation("????????????")
    @OperationLogger(option = "????????????")
    @PostMapping("approved")
    public JsonResult approved(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "?????????id????????????");

        Admin admin = UserUtil.getUser(request, Admin.class);

        Bill billDb = billModel.getBillByBillId(billId);
        AssertUtil.notNull(billDb, "????????????????????????????????????");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_2, "????????????????????????");

        Bill bill = new Bill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_3);
        bill.setUpdateTime(new Date());
        billModel.updateBill(bill);
        //??????????????????
        BillLog billLog = new BillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(admin.getAdminId().longValue());
        billLog.setOperatorName(admin.getAdminName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_3);
        billLog.setContent("????????????");
        billLog.setCreateTime(new Date());
        billLogModel.saveBillLog(billLog);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "paymentRemark", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "paymentEvidence", value = "????????????", paramType = "query")
    })
    @PostMapping("confirmPayment")
    public JsonResult confirmPayment(HttpServletRequest request, Integer billId,
                                     String paymentRemark, String paymentEvidence) {
        AssertUtil.notNullOrZero(billId, "?????????id????????????");

        Admin admin = UserUtil.getUser(request, Admin.class);

        Bill billDb = billModel.getBillByBillId(billId);
        AssertUtil.notNull(billDb, "????????????????????????????????????");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_3, "????????????????????????");

        Bill bill = new Bill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_4);
        bill.setPaymentRemark(paymentRemark);
        bill.setPaymentEvidence(paymentEvidence);
        bill.setUpdateTime(new Date());
        billModel.updateBill(bill);
        //??????????????????
        BillLog billLog = new BillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(admin.getAdminId().longValue());
        billLog.setOperatorName(admin.getAdminName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_4);
        billLog.setContent("????????????");
        billLog.setCreateTime(new Date());
        billLogModel.saveBillLog(billLog);

        //????????????
        List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
        messageSendPropertyList.add(new MessageSendProperty("billSn", billDb.getBillSn()));
        String msgLinkInfo = "{\"billSn\":\"" + billDb.getBillSn() + "\",\"type\":\"bill_news\"}";
        MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, billDb.getStoreId(), StoreTplConst.BILL_PAID_REMINDER, msgLinkInfo);
        //?????????mq
        rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billSns", value = "????????????????????????????????????", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "???????????????1???????????????2???????????????3???????????????4???????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query")
    })
    @GetMapping("export")
    public JsonResult export(HttpServletRequest request, HttpServletResponse response, String billSns, String storeName,
                             Integer state, Date startTime, Date endTime) {

        BillExample example = new BillExample();
        if (!StringUtil.isEmpty(billSns)) {
            //????????????
            AssertUtil.notFormatFrontIds(billSns, "????????????,?????????");
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
