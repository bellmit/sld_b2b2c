package com.slodon.b2b2c.controller.seller.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreGradeModel;
import com.slodon.b2b2c.model.seller.StoreRenewModel;
import com.slodon.b2b2c.seller.example.StoreRenewExample;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import com.slodon.b2b2c.seller.pojo.StoreRenew;
import com.slodon.b2b2c.vo.seller.StoreRenewListVO;
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

@Api(tags = "admin-平台店铺续签管理")
@RestController
@RequestMapping("v3/seller/admin/storeRenew")
public class AdminStoreRenewController {

    @Resource
    private StoreRenewModel storeRenewModel;
    @Resource
    private StoreGradeModel storeGradeModel;

    @ApiOperation("续签列表管理")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "vendorName", value = "店主账号", paramType = "query"),
            @ApiImplicitParam(name = "contactName", value = "联系人", paramType = "query"),
            @ApiImplicitParam(name = "contactPhone", value = "联系电话", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态，1：待付款；2已付款", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreRenewListVO>> getList(HttpServletRequest request,
                                                        @RequestParam(value = "storeName", required = false) String storeName,
                                                        @RequestParam(value = "vendorName", required = false) String vendorName,
                                                        @RequestParam(value = "contactName", required = false) String contactName,
                                                        @RequestParam(value = "contactPhone", required = false) String contactPhone,
                                                        @RequestParam(value = "state", required = false) Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreRenewExample storeRenewExample = new StoreRenewExample();
        storeRenewExample.setStoreNameLike(storeName);
        storeRenewExample.setVendorNameLike(vendorName);
        storeRenewExample.setContactNameLike(contactName);
        storeRenewExample.setContactPhoneLike(contactPhone);
        storeRenewExample.setState(state);
        storeRenewExample.setPager(pager);
        storeRenewExample.setOrderBy("apply_time desc");
        List<StoreRenew> storeRenewList = storeRenewModel.getStoreRenewList(storeRenewExample, pager);
        List<StoreRenewListVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeRenewList)) {
            storeRenewList.forEach(storeRenew -> {
                StoreRenewListVO vo = new StoreRenewListVO(storeRenew);
                //根据gradeId查询店铺等级表
                StoreGrade storeGrade = storeGradeModel.getStoreGradeByGradeId(storeRenew.getGradeId());
                vo.setGradeName(storeGrade.getGradeName());
                vo.setPrice(storeGrade.getPrice());
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, storeRenewExample.getPager()));
    }

    @ApiOperation("平台删除店铺续签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "renewId", value = "续签id", required = true)
    })
    @OperationLogger(option = "平台删除店铺续签")
    @PostMapping("delRenew")
    public JsonResult<Integer> delRenew(HttpServletRequest request, @RequestParam("renewId") Integer renewId) {
        String logMsg = "续签id" + renewId;

        //通过renewId查询数据库store_renew表
        StoreRenew storeRenew = storeRenewModel.getStoreRenewByRenewId(renewId);
        AssertUtil.isTrue(storeRenew.getState() == StoreConst.STORE_RENEW_STATE_SUCCESS, "只能删除状态为待付款的续签");
        storeRenewModel.deleteStoreRenew(renewId);
        return SldResponse.success("删除成功", logMsg);
    }
}
