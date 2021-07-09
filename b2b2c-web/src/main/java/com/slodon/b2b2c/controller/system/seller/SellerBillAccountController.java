package com.slodon.b2b2c.controller.system.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.system.BillAccountModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.dto.BillAccountAddDTO;
import com.slodon.b2b2c.system.dto.BillAccountUpdateDTO;
import com.slodon.b2b2c.system.example.BillAccountExample;
import com.slodon.b2b2c.system.pojo.BillAccount;
import com.slodon.b2b2c.vo.system.BillAccountVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "seller-结算信息")
@RestController
@RequestMapping("v3/system/seller/bill/account")
public class SellerBillAccountController extends BaseController {

    @Resource
    private BillAccountModel billAccountModel;

    @ApiOperation("结算账号列表")
    @GetMapping("list")
    public JsonResult<PageVO<BillAccountVO>> list(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        BillAccountExample example = new BillAccountExample();
        example.setStoreId(vendor.getStoreId());
        List<BillAccount> list = billAccountModel.getBillAccountList(example, pager);
        List<BillAccountVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(billAccount -> {
                vos.add(new BillAccountVO(billAccount));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增结算账号")
    @VendorLogger(option = "新增结算账号")
    @PostMapping("add")
    public JsonResult addBillAccount(HttpServletRequest request, BillAccountAddDTO billAccountAddDTO) {
        AssertUtil.isTrue(StringUtil.isNullOrZero(billAccountAddDTO.getAccountType())
                || billAccountAddDTO.getAccountType() > 2, "请选择正确的账户类型");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //结算账号不可重复
        BillAccountExample example = new BillAccountExample();
        if (billAccountAddDTO.getAccountType() == BillConst.ACCOUNT_TYPE_1) {
            example.setBankAccountNumber(billAccountAddDTO.getBankAccountNumber());
        } else {
            example.setAlipayAccount(billAccountAddDTO.getAlipayAccount());
        }
        List<BillAccount> list = billAccountModel.getBillAccountList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "该结算账号已存在");

        BillAccount billAccount = new BillAccount();
        BeanUtils.copyProperties(billAccountAddDTO, billAccount);
        billAccount.setStoreId(vendor.getStoreId());
        billAccount.setStoreName(vendor.getVendorName());
        billAccount.setCreateVendorId(vendor.getVendorId());
        billAccountModel.saveBillAccount(billAccount);
        return SldResponse.success("新增成功");
    }

    @ApiOperation("编辑结算账号")
    @VendorLogger(option = "编辑结算账号")
    @PostMapping("update")
    public JsonResult updateBillAccount(HttpServletRequest request, BillAccountUpdateDTO billAccountUpdateDTO) {
        AssertUtil.isTrue(StringUtil.isNullOrZero(billAccountUpdateDTO.getAccountType())
                || billAccountUpdateDTO.getAccountType() > 2, "请选择正确的账户类型");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //结算账号不可重复
        BillAccountExample example = new BillAccountExample();
        if (billAccountUpdateDTO.getAccountType() == BillConst.ACCOUNT_TYPE_1) {
            example.setBankAccountNumber(billAccountUpdateDTO.getBankAccountNumber());
        } else {
            example.setAlipayAccount(billAccountUpdateDTO.getAlipayAccount());
        }
        example.setAccountIdNotEquals(billAccountUpdateDTO.getAccountId());
        List<BillAccount> list = billAccountModel.getBillAccountList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "该结算账号已存在");

        BillAccount billAccount = new BillAccount();
        BeanUtils.copyProperties(billAccountUpdateDTO, billAccount);
        billAccount.setStoreId(vendor.getStoreId());
        billAccountModel.updateBillAccount(billAccount);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("删除结算账号")
    @VendorLogger(option = "删除结算账号")
    @PostMapping("del")
    public JsonResult delBillAccount(HttpServletRequest request, Integer accountId) {
        billAccountModel.deleteBillAccount(accountId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("设置默认结算账号")
    @VendorLogger(option = "设置默认结算账号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "accountId", value = "账号id", required = true),
            @ApiImplicitParam(name = "isDefault", value = "是否默认账号：1-默认账号，0-非默认账号", required = true)
    })
    @PostMapping("setDefault")
    public JsonResult setDefault(HttpServletRequest request, Integer accountId, Integer isDefault) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        BillAccount billAccount = new BillAccount();
        billAccount.setAccountId(accountId);
        billAccount.setIsDefault(isDefault);
        billAccount.setStoreId(vendor.getStoreId());
        billAccountModel.updateBillAccount(billAccount);
        return SldResponse.success("设置成功");
    }
}
