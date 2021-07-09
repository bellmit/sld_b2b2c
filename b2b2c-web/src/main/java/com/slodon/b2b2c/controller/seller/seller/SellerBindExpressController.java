package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.ExpressConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreBindExpressModel;
import com.slodon.b2b2c.model.system.ExpressModel;
import com.slodon.b2b2c.seller.example.StoreBindExpressExample;
import com.slodon.b2b2c.seller.pojo.StoreBindExpress;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.example.ExpressExample;
import com.slodon.b2b2c.system.pojo.Express;
import com.slodon.b2b2c.vo.seller.ExpressVO;
import com.slodon.b2b2c.vo.seller.StoreBindExpressVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-物流公司管理")
@RestController
@RequestMapping("v3/seller/seller/express")
public class SellerBindExpressController extends BaseController {

    @Resource
    private StoreBindExpressModel storeBindExpressModel;
    @Resource
    private ExpressModel expressModel;

    @ApiOperation("默认物流公司列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expressName", value = "物流名称", paramType = "query"),
            @ApiImplicitParam(name = "expressState", value = "是否启用：1-启用，0-不启用", paramType = "query"),
            @ApiImplicitParam(name = "createTimeAfter", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "createTimeBefore", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreBindExpressVO>> getList(HttpServletRequest request, String expressName, String expressState,
                                                          Date createTimeAfter, Date createTimeBefore) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreBindExpressExample example = new StoreBindExpressExample();
        example.setExpressNameLike(expressName);
        example.setExpressState(expressState);
        example.setCreateTimeAfter(createTimeAfter);
        example.setCreateTimeBefore(createTimeBefore);
        example.setStoreId(vendor.getStoreId());
        example.setPager(pager);
        List<StoreBindExpress> storeBindExpressList = storeBindExpressModel.getStoreBindExpressList(example, pager);
        List<StoreBindExpressVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeBindExpressList)) {
            for (StoreBindExpress storeBindExpress : storeBindExpressList) {
                StoreBindExpressVO vo = new StoreBindExpressVO(storeBindExpress);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, example.getPager()));
    }

    @ApiOperation("添加物流公司")
    @VendorLogger(option = "添加物流公司")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "expressIds", value = "从平台获取的物流公司id串，以逗号隔开", required = true)
    })
    @PostMapping("add")
    public JsonResult<Integer> addExpress(HttpServletRequest request, @RequestParam("expressIds") String expressIds) {
        //参数校验
        AssertUtil.notEmpty(expressIds,"请选择要添加的物流公司id");
        AssertUtil.notFormatFrontIds(expressIds,"expressIds格式错误");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        String[] expressIdArr = expressIds.split(",");
        for (String expressId : expressIdArr) {
            //根据物流公司id获取物流公司信息
            Express express = expressModel.getExpressByExpressId(Integer.parseInt(expressId));
            StoreBindExpress storeBindExpress = new StoreBindExpress();
            storeBindExpress.setStoreId(vendor.getStoreId());
            storeBindExpress.setExpressId(express.getExpressId());
            storeBindExpress.setExpressName(express.getExpressName());
            storeBindExpress.setExpressCode(express.getExpressCode());
            storeBindExpress.setExpressWebsite(express.getWebsite());
            storeBindExpressModel.saveStoreBindExpress(storeBindExpress);
        }
        return SldResponse.success("添加成功");
    }

    @ApiOperation("开启/关闭物流公司")
    @VendorLogger(option = "开启/关闭物流公司")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bindId", value = "物流公司绑定id", required = true),
            @ApiImplicitParam(name = "expressState", value = "(string类型)1-启用，0-不启用", required = true),
    })
    @PostMapping("openOrCloseExpress")
    public JsonResult<Integer> openOrCloseExpress(HttpServletRequest request,
                                                  @RequestParam("bindId") Integer bindId,
                                                  @RequestParam("expressState") String expressState) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.isTrue(StringUtil.isNullOrZero(bindId), "请选择要开启/关闭的物流公司");
        //根据bindId获取物流公司信息
        StoreBindExpress storeBindExpress = storeBindExpressModel.getStoreBindExpressByBindId(bindId);
        AssertUtil.notNull(storeBindExpress, "快递公司为空");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeBindExpress.getStoreId()), "非法操作");
        storeBindExpressModel.openOrCloseExpress(bindId, expressState);

        if (expressState.equals(StoreConst.EXPRESS_STATE_CLOSE)) {
            return SldResponse.success("物流公司已关闭");
        } else {
            return SldResponse.success("物流公司已开启");
        }
    }

    @ApiOperation("删除快递公司")
    @VendorLogger(option = "删除快递公司")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bindId", value = "物流公司绑定id", required = true)
    })
    @PostMapping("del")
    public JsonResult<Integer> delExpress(HttpServletRequest request, @RequestParam("bindId") Integer bindId) {
        String logMsg = "店铺绑定快递公司id:" + bindId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据bindId获取物流公司信息
        StoreBindExpress storeBindExpress = storeBindExpressModel.getStoreBindExpressByBindId(bindId);
        AssertUtil.notNull(storeBindExpress, "快递公司为空");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeBindExpress.getStoreId()), "非法操作");

        storeBindExpressModel.deleteStoreBindExpress(bindId);
        return SldResponse.success("删除成功", logMsg);
    }

    @ApiOperation("获取平台物流公司列表")
    @GetMapping("expressList")
    public JsonResult<List<ExpressVO>> getExpressList(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        ExpressExample expressExample = new ExpressExample();
        expressExample.setExpressState(ExpressConst.EXPRESS_STATE_OPEN);

        StringBuilder stringBuilder = new StringBuilder();
        //获取店铺内的所有快递公司
        StoreBindExpressExample storeBindExpressExample = new StoreBindExpressExample();
        storeBindExpressExample.setStoreId(vendor.getStoreId());
        List<StoreBindExpress> storeBindExpressList = storeBindExpressModel.getStoreBindExpressList(storeBindExpressExample, null);
        if (!CollectionUtils.isEmpty(storeBindExpressList)) {
            for (StoreBindExpress storeBindExpress : storeBindExpressList) {
                stringBuilder.append(",");
                stringBuilder.append(storeBindExpress.getExpressId());
            }
            expressExample.setExpressIdNotIn(stringBuilder.toString().substring(1));
        }
        expressExample.setPager(null);
        List<Express> expressList = expressModel.getExpressList(expressExample, null);
        List<ExpressVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(expressList)) {
            for (Express express : expressList) {
                ExpressVO vo = new ExpressVO(express);
                vos.add(vo);
            }
        }
        return SldResponse.success(vos);
    }

}
