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
import com.slodon.b2b2c.model.seller.StoreGradeModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.seller.StoreRenewModel;
import com.slodon.b2b2c.seller.example.StoreRenewExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import com.slodon.b2b2c.seller.pojo.StoreRenew;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.seller.RenewPayVO;
import com.slodon.b2b2c.vo.seller.RenewStateVO;
import com.slodon.b2b2c.vo.seller.StoreExpireTimeVO;
import com.slodon.b2b2c.vo.seller.StoreRenewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "seller-店铺进行续签")
@RestController
@RequestMapping("v3/seller/seller/renew")
public class SellerRenewController extends BaseController {

    @Resource
    private StoreRenewModel storeRenewModel;
    @Resource
    private StoreGradeModel storeGradeModel;
    @Resource
    private StoreModel storeModel;

    @ApiOperation("店铺续签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreRenewVO>> getList(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreRenewExample storeRenewExample = new StoreRenewExample();
        storeRenewExample.setStoreId(vendor.getStoreId());
        storeRenewExample.setPager(pager);
        storeRenewExample.setOrderBy("apply_time desc");
        List<StoreRenew> storeRenewList = storeRenewModel.getStoreRenewList(storeRenewExample, pager);
        List<StoreRenewVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeRenewList)) {
            storeRenewList.forEach(storeRenew -> {
                StoreRenewVO vo = new StoreRenewVO(storeRenew);
                //根据gradeId查询店铺等级表
                StoreGrade storeGrade = storeGradeModel.getStoreGradeByGradeId(storeRenew.getGradeId());
                vo.setGradeName(storeGrade.getGradeName());
                vo.setPrice(storeGrade.getPrice());
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, storeRenewExample.getPager()));
    }

    @ApiOperation("获取续签详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "renewId", value = "续签id", required = true)
    })
    @GetMapping("getDetail")
    public JsonResult<RenewStateVO> getDetail(HttpServletRequest request, @RequestParam("renewId") Integer renewId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreRenew storeRenew = storeRenewModel.getStoreRenewByRenewId(renewId);
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeRenew.getStoreId()), "非法操作");
        RenewStateVO vo = new RenewStateVO(storeRenew);
        return SldResponse.success(vo);

    }

    @ApiOperation("发起续签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gradeId", value = "店铺等级id", required = true),
            @ApiImplicitParam(name = "duration", value = "续签开店时长", required = true)
    })
    @VendorLogger(option = "发起续签")
    @PostMapping("doRenew")
    public JsonResult<RenewPayVO> doRenew(HttpServletRequest request,
                                          @RequestParam("gradeId") Integer gradeId,
                                          @RequestParam("duration") Integer duration) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //如果该店铺有未付款的续签，则不能再次发起续签
        StoreRenewExample storeRenewExample = new StoreRenewExample();
        storeRenewExample.setStoreId(vendor.getStoreId());
        storeRenewExample.setState(StoreConst.STORE_RENEW_STATE_WAITPAY);
        List<StoreRenew> storeRenewList = storeRenewModel.getStoreRenewList(storeRenewExample, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(storeRenewList), "已有未付款续签");

        StoreRenew storeRenew = new StoreRenew();
        storeRenew.setGradeId(gradeId);
        storeRenew.setDuration(duration);
        Integer renewId = storeRenewModel.doStoreRenew(storeRenew, vendor);
        //根据gradeId获取店铺等级
        StoreGrade storeGrade = storeGradeModel.getStoreGradeByGradeId(gradeId);
        RenewPayVO vo = new RenewPayVO(storeGrade);
        vo.setDuration(duration);
        vo.setPayAmount(new BigDecimal(storeGrade.getPrice()).multiply(new BigDecimal(duration)));
        //获取续签支付单号
        StoreRenew renew = storeRenewModel.getStoreRenewByRenewId(renewId);
        vo.setPaySn(renew.getPaySn());
        vo.setRenewId(renewId);
        return SldResponse.success("发起续签成功", vo);
    }

    @ApiOperation("删除店铺续签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "renewId", value = "续签id", required = true)
    })
    @VendorLogger(option = "删除店铺续签")
    @PostMapping("delRenew")
    public JsonResult<Integer> delRenew(HttpServletRequest request, @RequestParam("renewId") Integer renewId) {
        String logMsg = "续签id" + renewId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //通过renewId查询数据库store_renew表
        StoreRenew storeRenew = storeRenewModel.getStoreRenewByRenewId(renewId);
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeRenew.getStoreId()), "不能删除其他店铺的续签");
        AssertUtil.isTrue(storeRenew.getState() == StoreConst.STORE_RENEW_STATE_SUCCESS, "只能删除状态为待付款的续签");
        storeRenewModel.deleteStoreRenew(renewId);
        return SldResponse.success("删除成功", logMsg);
    }

    @ApiOperation("获取店铺到期时间")
    @GetMapping("getExpireTime")
    public JsonResult<StoreExpireTimeVO> getExpireTime(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Store storeDb = storeModel.getStoreByStoreId(vendor.getStoreId());
        StoreExpireTimeVO vo = new StoreExpireTimeVO(storeDb);
        return SldResponse.success(vo);

    }
}
