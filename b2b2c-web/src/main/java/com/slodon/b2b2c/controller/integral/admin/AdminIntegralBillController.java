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

@Api(tags = "admin-结算管理")
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
            @ApiImplicitParam(name = "billSn", value = "结算单号", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "结算状态：1、待确认；2、待审核；3、待结算；4、结算完成 ;默认(不传值)为全部", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "结算开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结算结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @ApiOperation("结算列表")
    public JsonResult<PageVO<AdminIntegralBillVO>> list(HttpServletRequest request, String billSn, String storeName,
                                                        Integer state, Date startTime, Date endTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //查询结算列表
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

    @ApiOperation("结算详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billId", value = "结算id", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<AdminIntegralBillDetailVO> detail(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "结算billId不能为空");

        IntegralBill bill = integralBillModel.getIntegralBillByBillId(billId);
        AssertUtil.notNull(bill, "获取结算信息为空，请重试！");

        AdminIntegralBillDetailVO detailVO = null;
        if (bill.getState() == BillConst.STATE_1) {
            detailVO = new AdminIntegralBillDetailVO(bill);
        } else {
            //查询结算账号信息
            BillAccountExample example = new BillAccountExample();
            example.setStoreId(bill.getStoreId());
            example.setIsDefault(BillConst.IS_DEFAULT_YES);
            List<BillAccount> accountList = billAccountModel.getBillAccountList(example, null);
            AssertUtil.notEmpty(accountList, "您尚未设置结算信息，请先完成结算信息设置");
            detailVO = new AdminIntegralBillDetailVO(bill, accountList.get(0));
        }
        //查询结算订单信息
        IntegralBillOrderBindExample bindExample = new IntegralBillOrderBindExample();
        bindExample.setBillSn(bill.getBillSn());
        List<IntegralBillOrderBind> orderBindList = integralBillOrderBindModel.getIntegralBillOrderBindList(bindExample, null);
        AssertUtil.notEmpty(orderBindList, "获取结算订单信息为空，请重试");
        detailVO.setOrderList(orderBindList);
        //查询结算日志信息
        IntegralBillLogExample logExample = new IntegralBillLogExample();
        logExample.setBillSn(bill.getBillSn());
        logExample.setOrderBy("create_time asc");
        List<IntegralBillLog> billLogList = integralBillLogModel.getIntegralBillLogList(logExample, null);
        AssertUtil.notEmpty(billLogList, "获取结算日志记录为空，请重试");
        List<AdminIntegralBillDetailVO.BillLogVO> logVOS = new ArrayList<>();
        billLogList.forEach(billLog -> {
            logVOS.add(new AdminIntegralBillDetailVO.BillLogVO(billLog));
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

        IntegralBill billDb = integralBillModel.getIntegralBillByBillId(billId);
        AssertUtil.notNull(billDb, "获取结算信息为空，请重试");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_2, "未到平台审核阶段");

        IntegralBill bill = new IntegralBill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_3);
        bill.setUpdateTime(new Date());
        integralBillModel.updateIntegralBill(bill);
        //记录操作日志
        IntegralBillLog billLog = new IntegralBillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(admin.getAdminId().longValue());
        billLog.setOperatorName(admin.getAdminName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_3);
        billLog.setContent("平台审核");
        billLog.setCreateTime(new Date());
        integralBillLogModel.saveIntegralBillLog(billLog);
        return SldResponse.success("审核成功");
    }

    @ApiOperation("确认打款")
    @OperationLogger(option = "确认打款")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billId", value = "结算id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "paymentRemark", value = "打款备注", paramType = "query"),
            @ApiImplicitParam(name = "paymentEvidence", value = "打款凭证", paramType = "query")
    })
    @PostMapping("confirmPayment")
    public JsonResult confirmPayment(HttpServletRequest request, Integer billId, String paymentRemark, String paymentEvidence) {
        AssertUtil.notNullOrZero(billId, "结算单id不能为空");

        Admin admin = UserUtil.getUser(request, Admin.class);

        IntegralBill billDb = integralBillModel.getIntegralBillByBillId(billId);
        AssertUtil.notNull(billDb, "获取结算信息为空，请重试");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_3, "未到平台打款阶段");

        IntegralBill bill = new IntegralBill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_4);
        bill.setPaymentRemark(paymentRemark);
        bill.setPaymentEvidence(paymentEvidence);
        bill.setUpdateTime(new Date());
        integralBillModel.updateIntegralBill(bill);
        //记录操作日志
        IntegralBillLog billLog = new IntegralBillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(admin.getAdminId().longValue());
        billLog.setOperatorName(admin.getAdminName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_1);
        billLog.setState(BillConst.STATE_4);
        billLog.setContent("结算完成");
        billLog.setCreateTime(new Date());
        integralBillLogModel.saveIntegralBillLog(billLog);
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

        IntegralBillExample example = new IntegralBillExample();
        if (!StringUtil.isEmpty(billSns)) {
            //参数校验
            AssertUtil.notFormatFrontIds(billSns, "格式错误,请重试");
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
