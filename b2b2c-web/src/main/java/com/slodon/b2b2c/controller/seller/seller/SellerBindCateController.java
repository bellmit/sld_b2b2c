package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreBindCategoryModel;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.pojo.StoreBindCategory;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.seller.StoreBindCateVO;
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

@Api(tags = "seller-商户经营类目相关接口")
@RestController
@RequestMapping("v3/seller/seller/bindCate")
public class SellerBindCateController extends BaseController {

    @Resource
    private StoreBindCategoryModel storeBindCategoryModel;

    @ApiOperation("商户获取经营类目列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "state", value = "申请状态：1-提交审核;2-审核通过;3-审核失败;", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreBindCateVO>> getList(HttpServletRequest request, @RequestParam(value = "state", required = false) Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
        storeBindCategoryExample.setStoreId(vendor.getStoreId());
        storeBindCategoryExample.setState(state);
        storeBindCategoryExample.setOrderBy("create_time desc,bind_id desc");
        List<StoreBindCategory> storeBindCategoryList = storeBindCategoryModel.getStoreBindCategoryList(storeBindCategoryExample, pager);
        List<StoreBindCateVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeBindCategoryList)) {
            for (StoreBindCategory storeBindCategory : storeBindCategoryList) {
                StoreBindCateVO vo = new StoreBindCateVO(storeBindCategory);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("申请经营类目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsCateIds", value = "申请分类id字符串,例1级-2级-3级,1级-2级-3级", required = true)
    })
    @VendorLogger(option = "申请经营类目")
    @PostMapping("apply")
    public JsonResult<Integer> applyStoreBindCate(HttpServletRequest request, @RequestParam("goodsCateIds") String goodsCateIds) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        storeBindCategoryModel.commitApply(goodsCateIds, vendor);
        return SldResponse.success("申请已提交");
    }

    @ApiOperation("删除经营类目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bindId", value = "绑定id", required = true)
    })
    @VendorLogger(option = "删除经营类目")
    @PostMapping("delBindCate")
    public JsonResult<Integer> delBindCate(HttpServletRequest request, @RequestParam("bindId") Integer bindId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //通过bindId查询store_bind_category表
        StoreBindCategory storeBindCategory = storeBindCategoryModel.getStoreBindCategoryByBindId(bindId);
        AssertUtil.isTrue(StringUtils.isEmpty(storeBindCategory), "请输入有效的bindId");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeBindCategory.getStoreId()), "不能删除其他店铺的经营类目");
        AssertUtil.isTrue(storeBindCategory.getState() != StoreConst.STORE_CATEGORY_STATE_SEND, "只能删除状态为待审核的经营类目");
        storeBindCategoryModel.deleteStoreBindCategory(bindId);
        return SldResponse.success("删除成功");
    }
}
