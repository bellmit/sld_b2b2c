package com.slodon.b2b2c.controller.integral.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.integral.example.IntegralBillExample;
import com.slodon.b2b2c.integral.example.IntegralBillLogExample;
import com.slodon.b2b2c.integral.example.IntegralBillOrderBindExample;
import com.slodon.b2b2c.integral.pojo.IntegralBill;
import com.slodon.b2b2c.integral.pojo.IntegralBillLog;
import com.slodon.b2b2c.integral.pojo.IntegralBillOrderBind;
import com.slodon.b2b2c.model.integral.IntegralBillLogModel;
import com.slodon.b2b2c.model.integral.IntegralBillModel;
import com.slodon.b2b2c.model.integral.IntegralBillOrderBindModel;
import com.slodon.b2b2c.model.seller.StoreApplyModel;
import com.slodon.b2b2c.model.seller.StoreCertificateModel;
import com.slodon.b2b2c.model.system.BillAccountModel;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import com.slodon.b2b2c.system.example.BillAccountExample;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.BillAccount;
import com.slodon.b2b2c.vo.integral.AdminIntegralBillDetailVO;
import com.slodon.b2b2c.vo.integral.AdminIntegralBillVO;
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
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-????????????")
@RestController
@RequestMapping("v3/integral/admin/integralBill")
public class AdminIntegralBillController extends BaseController {

    @Resource
    private IntegralBillModel integralBillModel;
    @Resource
    private IntegralBillLogModel integralBillLogModel;
    @Resource
    private IntegralBillOrderBindModel integralBillOrderBindModel;
    @Resource
    private BillAccountModel billAccountModel;
    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;

    @GetMapping("list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billSn", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "???????????????1???????????????2???????????????3???????????????4??????????????? ;??????(?????????)?????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @ApiOperation("????????????")
    public JsonResult<PageVO<AdminIntegralBillVO>> list(HttpServletRequest request, String billSn, String storeName,
                                                        Integer state, Date startTime, Date endTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //??????????????????
        IntegralBillExample example = new IntegralBillExample();
        example.setBillSnLike(billSn);
        example.setStoreNameLike(storeName);
        example.setState(state);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        List<AdminIntegralBillVO> vos = new ArrayList<>();
        List<IntegralBill> list = integralBillModel.getIntegralBillList(example, pager);
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(integralBill -> {
                AdminIntegralBillVO vo = new AdminIntegralBillVO(integralBill);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billId", value = "??????id", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<AdminIntegralBillDetailVO> detail(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "??????billId????????????");

        IntegralBill bill = integralBillModel.getIntegralBillByBillId(billId);
        AssertUtil.notNull(bill, "???????????????????????????????????????");

        AdminIntegralBillDetailVO detailVO = null;
        if (bill.getState() == BillConst.STATE_1) {
            detailVO = new AdminIntegralBillDetailVO(bill);
        } else {
            //????????????????????????
            BillAccountExample example = new BillAccountExample();
            example.setStoreId(bill.getStoreId());
            example.setIsDefault(BillConst.IS_DEFAULT_YES);
            List<BillAccount> accountList = billAccountModel.getBillAccountList(example, null);
            AssertUtil.notEmpty(accountList, "????????????????????????????????????????????????????????????");
            detailVO = new AdminIntegralBillDetailVO(bill, accountList.get(0));
        }
        //????????????????????????
        IntegralBillOrderBindExample bindExample = new IntegralBillOrderBindExample();
        bindExample.setBillSn(bill.getBillSn());
        List<IntegralBillOrderBind> orderBindList = integralBillOrderBindModel.getIntegralBillOrderBindList(bindExample, null);
        AssertUtil.notEmpty(orderBindList, "??????????????????????????????????????????");
        detailVO.setOrderList(orderBindList);
        //????????????????????????
        IntegralBillLogExample logExample = new IntegralBillLogExample();
        logExample.setBillSn(bill.getBillSn());
        logExample.setOrderBy("create_time asc");
        List<IntegralBillLog> billLogList = integralBillLogModel.getIntegralBillLogList(logExample, null);
        AssertUtil.notEmpty(billLogList, "??????????????????????????????????????????");
        List<AdminIntegralBillDetailVO.BillLogVO> logVOS = new ArrayList<>();
        billLogList.forEach(billLog -> {
            logVOS.add(new AdminIntegralBillDetailVO.BillLogVO(billLog));
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

        IntegralBill billDb = integralBillModel.getIntegralBillByBillId(billId);
        AssertUtil.notNull(billDb, "????????????????????????????????????");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_2, "????????????????????????");

        IntegralBill bill = new IntegralBill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_3);
        bill.setUpdateTime(new Date());
        integralBillModel.updateIntegralBill(bill);
        //??????????????????
        IntegralBillLog billLog = new IntegralBillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(admin.getAdminId().longValue());
        billLog.setOperatorName(admin.getAdminName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_3);
        billLog.setContent("????????????");
        billLog.setCreateTime(new Date());
        integralBillLogModel.saveIntegralBillLog(billLog);
        return SldResponse.success("????????????");
    }

    @ApiOperation("????????????")
    @OperationLogger(option = "????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billId", value = "??????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "paymentRemark", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "paymentEvidence", value = "????????????", paramType = "query")
    })
    @PostMapping("confirmPayment")
    public JsonResult confirmPayment(HttpServletRequest request, Integer billId, String paymentRemark, String paymentEvidence) {
        AssertUtil.notNullOrZero(billId, "?????????id????????????");

        Admin admin = UserUtil.getUser(request, Admin.class);

        IntegralBill billDb = integralBillModel.getIntegralBillByBillId(billId);
        AssertUtil.notNull(billDb, "????????????????????????????????????");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_3, "????????????????????????");

        IntegralBill bill = new IntegralBill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_4);
        bill.setPaymentRemark(paymentRemark);
        bill.setPaymentEvidence(paymentEvidence);
        bill.setUpdateTime(new Date());
        integralBillModel.updateIntegralBill(bill);
        //??????????????????
        IntegralBillLog billLog = new IntegralBillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(admin.getAdminId().longValue());
        billLog.setOperatorName(admin.getAdminName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_4);
        billLog.setContent("????????????");
        billLog.setCreateTime(new Date());
        integralBillLogModel.saveIntegralBillLog(billLog);
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

        IntegralBillExample example = new IntegralBillExample();
        if (!StringUtil.isEmpty(billSns)) {
            //????????????
            AssertUtil.notFormatFrontIds(billSns, "????????????,?????????");
            example.setBillSnIn("'" + billSns.replace(",", "','") + "'");
        }
        example.setStoreNameLike(storeName);
        example.setState(state);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        integralBillModel.billExport(request, response, example);
        return null;
    }

}
