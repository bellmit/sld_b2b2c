package com.slodon.b2b2c.controller.integral.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
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
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.example.BillAccountExample;
import com.slodon.b2b2c.system.pojo.BillAccount;
import com.slodon.b2b2c.vo.integral.SellerIntegralBillDetailVO;
import com.slodon.b2b2c.vo.integral.SellerIntegralBillVO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-????????????")
@RestController
@RequestMapping("v3/integral/seller/integralBill")
public class SellerIntegralBillController extends BaseController {

    @Resource
    private IntegralBillModel integralBillModel;
    @Resource
    private IntegralBillOrderBindModel integralBillOrderBindModel;
    @Resource
    private IntegralBillLogModel integralBillLogModel;
    @Resource
    private BillAccountModel billAccountModel;
    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "billSn", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "???????????????1???????????????2???????????????3???????????????4???????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "??????????????????", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SellerIntegralBillVO>> list(HttpServletRequest request, String billSn, Integer state, Date startTime, Date endTime) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralBillExample example = new IntegralBillExample();
        example.setBillSnLike(billSn);
        example.setStoreId(vendor.getStoreId());
        example.setState(state);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        List<IntegralBill> list = integralBillModel.getIntegralBillList(example, pager);
        List<SellerIntegralBillVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(bill -> {
                vos.add(new SellerIntegralBillVO(bill));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("????????????")
    @GetMapping("detail")
    public JsonResult<SellerIntegralBillDetailVO> detail(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "??????id????????????");

        IntegralBill bill = integralBillModel.getIntegralBillByBillId(billId);
        AssertUtil.notNull(bill, "???????????????????????????????????????");
        SellerIntegralBillDetailVO detailVO = null;
        if (bill.getState() == BillConst.STATE_1) {
            detailVO = new SellerIntegralBillDetailVO(bill);
        } else {
            //????????????????????????
            BillAccountExample example = new BillAccountExample();
            example.setStoreId(bill.getStoreId());
            example.setIsDefault(BillConst.IS_DEFAULT_YES);
            List<BillAccount> accountList = billAccountModel.getBillAccountList(example, null);
            AssertUtil.notEmpty(accountList, "????????????????????????????????????????????????????????????");
            detailVO = new SellerIntegralBillDetailVO(bill, accountList.get(0));
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
        List<SellerIntegralBillDetailVO.BillLogVO> logVOS = new ArrayList<>();
        billLogList.forEach(billLog -> {
            logVOS.add(new SellerIntegralBillDetailVO.BillLogVO(billLog));
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

    @ApiOperation("???????????????")
    @VendorLogger(option = "???????????????")
    @PostMapping("confirm")
    public JsonResult confirmBill(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "?????????id????????????");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        IntegralBill billDb = integralBillModel.getIntegralBillByBillId(billId);
        AssertUtil.notNull(billDb, "????????????????????????????????????");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_1, "????????????");

        //????????????????????????
        BillAccountExample example = new BillAccountExample();
        example.setStoreId(vendor.getStoreId());
        example.setIsDefault(BillConst.IS_DEFAULT_YES);
        List<BillAccount> accountList = billAccountModel.getBillAccountList(example, null);
        AssertUtil.notEmpty(accountList, "????????????????????????????????????????????????????????????");

        IntegralBill bill = new IntegralBill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_2);
        bill.setUpdateTime(new Date());
        integralBillModel.updateIntegralBill(bill);
        //??????????????????
        IntegralBillLog billLog = new IntegralBillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(vendor.getVendorId());
        billLog.setOperatorName(vendor.getVendorName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_2);
        billLog.setState(BillConst.STATE_2);
        billLog.setContent("????????????");
        billLog.setCreateTime(new Date());
        integralBillLogModel.saveIntegralBillLog(billLog);

        return SldResponse.success("????????????");
    }

}