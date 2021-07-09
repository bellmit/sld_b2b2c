package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreInnerLabelModel;
import com.slodon.b2b2c.seller.example.StoreInnerLabelExample;
import com.slodon.b2b2c.seller.pojo.StoreInnerLabel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.seller.StoreCategoryTreeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "seller-店铺分类管理")
@RestController
@RequestMapping("v3/seller/seller/storeCategory")
public class SellerCategoryController extends BaseController {

    @Resource
    private StoreInnerLabelModel storeInnerLabelModel;

    @ApiOperation("获取店铺分类列表")
    @GetMapping("list")
    public JsonResult getList(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        List<StoreCategoryTreeVO> tree = new ArrayList<>();
        StoreInnerLabelExample storeInnerLabelExample = new StoreInnerLabelExample();
        storeInnerLabelExample.setParentInnerLabelId(0);
        storeInnerLabelExample.setStoreId(vendor.getStoreId());
        List<StoreInnerLabel> storeInnerLabelList = storeInnerLabelModel.getStoreInnerLabelList(storeInnerLabelExample, pager);
        generateTree(tree, storeInnerLabelList, 2);

        return SldResponse.success(tree);
    }

    @ApiOperation("新增店铺分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "innerLabelName", value = "分类名称", required = true),
            @ApiImplicitParam(name = "parentInnerLabelId", value = "父分类ID,一级分类==0", required = true),
            @ApiImplicitParam(name = "innerLabelSort", value = "店铺内分类排序", required = true),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0-不显示，1-显示")
    })
    @VendorLogger(option = "新增店铺分类")
    @PostMapping("add")
    public JsonResult<Integer> addStoreCategory(HttpServletRequest request,
                                                @RequestParam("innerLabelName") String innerLabelName,
                                                @RequestParam("parentInnerLabelId") Integer parentInnerLabelId,
                                                @RequestParam("innerLabelSort") Integer innerLabelSort,
                                                @RequestParam(value = "isShow", required = false) Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreInnerLabel storeInnerLabel = new StoreInnerLabel();
        storeInnerLabel.setInnerLabelName(innerLabelName);
        storeInnerLabel.setInnerLabelSort(innerLabelSort);
        storeInnerLabel.setIsShow(isShow);
        storeInnerLabel.setParentInnerLabelId(parentInnerLabelId);
        storeInnerLabel.setStoreId(vendor.getStoreId());
        storeInnerLabel.setCreateVendorId(vendor.getVendorId());
        storeInnerLabel.setCreateVendorName(vendor.getVendorName());
        storeInnerLabelModel.saveStoreInnerLabel(storeInnerLabel);
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑店铺分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "innerLabelId", value = "分类id", required = true),
            @ApiImplicitParam(name = "innerLabelName", value = "分类名称", required = true),
            @ApiImplicitParam(name = "innerLabelSort", value = "排序", required = true),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0-不显示，1-显示"),
    })
    @VendorLogger(option = "编辑店铺分类")
    @PostMapping("edit")
    public JsonResult<Integer> editStoreCategory(HttpServletRequest request,
                                                 @RequestParam("innerLabelId") Integer innerLabelId,
                                                 @RequestParam("innerLabelName") String innerLabelName,
                                                 @RequestParam("innerLabelSort") Integer innerLabelSort,
                                                 @RequestParam(value = "isShow", required = false) Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据innerLabelId获取店铺内部分类信息
        StoreInnerLabel storeInnerLabel = storeInnerLabelModel.getStoreInnerLabelByInnerLabelId(innerLabelId);
        AssertUtil.notNull(storeInnerLabel, "未获取到店铺内部分类信息");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeInnerLabel.getStoreId()), "非法操作");

        StoreInnerLabel storeInnerLabelUpdate = new StoreInnerLabel();
        storeInnerLabelUpdate.setInnerLabelId(innerLabelId);
        storeInnerLabelUpdate.setInnerLabelName(innerLabelName);
        storeInnerLabelUpdate.setInnerLabelSort(innerLabelSort);
        storeInnerLabelUpdate.setIsShow(isShow);
        storeInnerLabelUpdate.setUpdateVendorId(vendor.getVendorId());
        storeInnerLabelUpdate.setUpdateVendorName(vendor.getVendorName());
        storeInnerLabelModel.editStoreCategory(storeInnerLabelUpdate);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("是否显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "innerLabelId", value = "分类id", required = true),
            @ApiImplicitParam(name = "isShow", value = "是否显示，0-不显示，1-显示")
    })
    @VendorLogger(option = "是否显示")
    @PostMapping("isShow")
    public JsonResult<Integer> updateIsShow(HttpServletRequest request,
                                            @RequestParam("innerLabelId") Integer innerLabelId,
                                            @RequestParam(value = "isShow", defaultValue = "1") Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据innerLabelId获取店铺内部分类信息
        StoreInnerLabel storeInnerLabelDb = storeInnerLabelModel.getStoreInnerLabelByInnerLabelId(innerLabelId);
        AssertUtil.notNull(storeInnerLabelDb, "未获取到店铺内部分类信息");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeInnerLabelDb.getStoreId()), "非法操作");

        StoreInnerLabel storeInnerLabel = new StoreInnerLabel();
        storeInnerLabel.setInnerLabelId(innerLabelId);
        storeInnerLabel.setIsShow(isShow);
        storeInnerLabel.setUpdateVendorId(vendor.getVendorId());
        storeInnerLabel.setUpdateVendorName(vendor.getVendorName());
        storeInnerLabelModel.updateStoreInnerLabel(storeInnerLabel);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("删除店铺分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "innerLabelId", value = "分类id", required = true)
    })
    @VendorLogger(option = "删除店铺分类")
    @PostMapping("del")
    public JsonResult<Integer> delStoreCategory(HttpServletRequest request,
                                                @RequestParam("innerLabelId") Integer innerLabelId) {
        String logMsg = "分类id" + innerLabelId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据innerLabelId获取店铺内部分类信息
        StoreInnerLabel storeInnerLabelDb = storeInnerLabelModel.getStoreInnerLabelByInnerLabelId(innerLabelId);
        AssertUtil.notNull(storeInnerLabelDb, "未获取到店铺内部分类信息");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeInnerLabelDb.getStoreId()), "非法操作");

        storeInnerLabelModel.deleteStoreInnerLabel(innerLabelId);
        return SldResponse.success("删除成功", logMsg);
    }

    /**
     * 递归生成树
     *
     * @param treeList
     * @param data
     * @return
     */
    private List<StoreCategoryTreeVO> generateTree(List<StoreCategoryTreeVO> treeList, List<StoreInnerLabel> data, Integer grade) {
        if (grade > 0) {
            for (StoreInnerLabel storeInnerLabel : data) {
                StoreCategoryTreeVO tree = new StoreCategoryTreeVO();
                tree.setInnerLabelId(storeInnerLabel.getInnerLabelId());
                tree.setInnerLabelName(storeInnerLabel.getInnerLabelName());
                tree.setInnerLabelSort(storeInnerLabel.getInnerLabelSort());
                tree.setIsShow(storeInnerLabel.getIsShow());
                tree.setParentInnerLabelId(storeInnerLabel.getParentInnerLabelId());
                tree.setCreateTime(storeInnerLabel.getCreateTime());
                tree.setUpdateTime(storeInnerLabel.getUpdateTime());

                StoreInnerLabelExample storeInnerLabelExample = new StoreInnerLabelExample();
                storeInnerLabelExample.setParentInnerLabelId(storeInnerLabel.getInnerLabelId());
                List<StoreCategoryTreeVO> vos = generateTree(new ArrayList<>(),
                        storeInnerLabelModel.getStoreInnerLabelList(storeInnerLabelExample, null), grade - 1);
                if (!CollectionUtils.isEmpty(vos)) {
                    tree.setChildren(vos);
                } else {
                    tree.setChildren(null);
                }
                treeList.add(tree);
            }
        }
        return treeList;
    }
}
