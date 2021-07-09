package com.slodon.b2b2c.controller.seller.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreBindCategoryModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreBindCategory;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.seller.AuditCateListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "admin-平台管理经营类目")
@RestController
@RequestMapping("v3/seller/admin/cateAudit")
public class AdminCateAuditController {

    @Resource
    private StoreBindCategoryModel storeBindCategoryModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private VendorModel vendorModel;

    @ApiOperation("经营类目列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "审核状态:1-待审核;2-审核通过;3-审核失败", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<AuditCateListVO>> getList(HttpServletRequest request,
                                                       @RequestParam(value = "storeName", required = false) String storeName,
                                                       @RequestParam(value = "state", required = false) Integer state) {

        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
        storeBindCategoryExample.setStoreNameLike(storeName);
        storeBindCategoryExample.setState(state);
        storeBindCategoryExample.setStoreIdNotEquals(0);
        storeBindCategoryExample.setOrderBy("create_time desc");
        storeBindCategoryExample.setPager(pager);
        List<StoreBindCategory> storeBindCategoryList = storeBindCategoryModel.getStoreBindCategoryList(storeBindCategoryExample, pager);
        List<AuditCateListVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeBindCategoryList)) {
            for (StoreBindCategory storeBindCategory : storeBindCategoryList) {
                AuditCateListVO vo = new AuditCateListVO(storeBindCategory);
                //获取店铺名称
                Store store = storeModel.getStoreByStoreId(storeBindCategory.getStoreId());
                vo.setStoreName(store.getStoreName());
                //获取店主账号
                VendorExample vendorExample = new VendorExample();
                vendorExample.setStoreId(store.getStoreId());
                vendorExample.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
                Vendor vendor = vendorModel.getVendorList(vendorExample, null).get(0);
                vo.setVendorName(vendor.getVendorName());
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, storeBindCategoryExample.getPager()));
    }

    @ApiOperation("批量审核通过/审核拒绝")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bindIds", value = "绑定id串，以逗号隔开", required = true),
            @ApiImplicitParam(name = "isPass", value = "是否通过[true==通过,false==拒绝]", required = true),
            @ApiImplicitParam(name = "refuseReason", value = "审核拒绝理由，拒绝时必填")
    })
    @OperationLogger(option = "批量审核通过/审核拒绝")
    @PostMapping("audit")
    public JsonResult<Integer> auditBindCate(HttpServletRequest request,
                                             @RequestParam("bindIds") String bindIds,
                                             @RequestParam("isPass") Boolean isPass,
                                             @RequestParam(value = "refuseReason", required = false) String refuseReason) {
        //参数校验
        AssertUtil.notEmpty(bindIds,"绑定id不能为空");
        AssertUtil.notFormatFrontIds(bindIds,"bindIds格式错误,请重试");
        Admin admin = UserUtil.getUser(request, Admin.class);

        AssertUtil.isTrue(!isPass && StringUtils.isEmpty(refuseReason), "请填写审核拒绝理由");
        storeBindCategoryModel.auditBindCate(bindIds, isPass, refuseReason, admin);
        String msg = isPass ? "审核通过" : "审核拒绝";
        return SldResponse.success(msg);
    }
}
