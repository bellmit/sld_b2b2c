package com.slodon.b2b2c.controller.system.seller;

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
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.example.BillAccountExample;
import com.slodon.b2b2c.system.example.BillExample;
import com.slodon.b2b2c.system.example.BillLogExample;
import com.slodon.b2b2c.system.example.BillOrderBindExample;
import com.slodon.b2b2c.system.pojo.Bill;
import com.slodon.b2b2c.system.pojo.BillAccount;
import com.slodon.b2b2c.system.pojo.BillLog;
import com.slodon.b2b2c.system.pojo.BillOrderBind;
import com.slodon.b2b2c.vo.system.BillDetailVO;
import com.slodon.b2b2c.vo.system.SellerBillVO;
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
@RequestMapping("v3/system/seller/bill")
public class SellerBillController extends BaseController {

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
    public JsonResult<PageVO<SellerBillVO>> list(HttpServletRequest request, String billSn, Integer state, Date startTime, Date endTime) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        BillExample example = new BillExample();
        example.setStoreId(vendor.getStoreId());
        example.setBillSnLike(billSn);
        example.setState(state);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        List<Bill> list = billModel.getBillList(example, pager);
        List<SellerBillVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(bill -> {
                vos.add(new SellerBillVO(bill));
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

    @ApiOperation("???????????????")
    @VendorLogger(option = "???????????????")
    @PostMapping("confirm")
    public JsonResult confirmBill(HttpServletRequest request, Integer billId) {
        AssertUtil.notNullOrZero(billId, "?????????id????????????");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Bill billDb = billModel.getBillByBillId(billId);
        AssertUtil.notNull(billDb, "????????????????????????????????????");
        AssertUtil.isTrue(billDb.getState() != BillConst.STATE_1, "????????????");

        //????????????????????????
        BillAccountExample example = new BillAccountExample();
        example.setStoreId(vendor.getStoreId());
        example.setIsDefault(BillConst.IS_DEFAULT_YES);
        List<BillAccount> accountList = billAccountModel.getBillAccountList(example, null);
        if (CollectionUtils.isEmpty(accountList)) {
            return SldResponse.failSpecial("????????????????????????????????????????????????????????????");
        }

        Bill bill = new Bill();
        bill.setBillId(billId);
        bill.setState(BillConst.STATE_2);
        bill.setUpdateTime(new Date());
        billModel.updateBill(bill);
        //??????????????????
        BillLog billLog = new BillLog();
        billLog.setBillSn(billDb.getBillSn());
        billLog.setOperatorId(vendor.getVendorId());
        billLog.setOperatorName(vendor.getVendorName());
        billLog.setOperatorRole(BillConst.OPERATOR_ROLE_2);
        billLog.setState(BillConst.STATE_2);
        billLog.setContent("????????????");
        billLog.setCreateTime(new Date());
        billLogModel.saveBillLog(billLog);
        return SldResponse.success("????????????");
    }
}
