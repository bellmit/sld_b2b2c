package com.slodon.b2b2c.controller.seller.seller;

import cn.hutool.core.bean.BeanUtil;
import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreAddressModel;
import com.slodon.b2b2c.seller.dto.StoreAddressAddDTO;
import com.slodon.b2b2c.seller.dto.StoreAddressUpdateDTO;
import com.slodon.b2b2c.seller.example.StoreAddressExample;
import com.slodon.b2b2c.seller.pojo.StoreAddress;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.seller.StoreAddressVO;
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

@Api(tags = "店铺收发货地址管理")
@RestController
@RequestMapping("v3/seller/seller/address")
public class SellerAddressController extends BaseController {

    @Resource
    private StoreAddressModel storeAddressModel;

    @ApiOperation("收发货地址列表接口")
    @VendorLogger(option = "收发货地址列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "地址类型：1-发货地址；2-收货地址", required = true),
            @ApiImplicitParam(name = "contactName", value = "联系人", paramType = "query"),
            @ApiImplicitParam(name = "telphone", value = "联系电话", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreAddressVO>> getList(HttpServletRequest request,
                                                      @RequestParam("type") Integer type,
                                                      @RequestParam(value = "contactName", required = false) String contactName,
                                                      @RequestParam(value = "telphone", required = false) String telphone) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.notNullOrZero(type, "请选择地址类型");
        StoreAddressExample example = new StoreAddressExample();
        example.setContactNameLike(contactName);
        example.setTelphoneLike(telphone);
        example.setStoreId(vendor.getStoreId());
        example.setType(type);
        example.setPager(pager);
        List<StoreAddress> storeAddressList = storeAddressModel.getStoreAddressList(example, pager);
        List<StoreAddressVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeAddressList)) {
            for (StoreAddress storeAddress : storeAddressList) {
                StoreAddressVO vo = new StoreAddressVO(storeAddress);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, example.getPager()));
    }

    @ApiOperation("获取地址详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<StoreAddressVO> getDetail(HttpServletRequest request, @RequestParam("addressId") Integer addressId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据地址id查询地址详情
        StoreAddress storeAddress = storeAddressModel.getStoreAddressByAddressId(addressId);
        AssertUtil.notNull(storeAddress, "店铺地址为空");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeAddress.getStoreId()), "非法操作");

        StoreAddressVO vo = new StoreAddressVO(storeAddress);
        return SldResponse.success(vo);
    }

    @ApiOperation("新增店铺地址接口")
    @VendorLogger(option = "新增店铺地址接口")
    @PostMapping("add")
    public JsonResult<Integer> addStoreAddress(HttpServletRequest request, StoreAddressAddDTO storeAddressAddDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreAddress storeAddressInsert = new StoreAddress();
        BeanUtil.copyProperties(storeAddressAddDTO, storeAddressInsert);
        storeAddressInsert.setStoreId(vendor.getStoreId());
        storeAddressInsert.setCreatedTime(new Date());
        storeAddressModel.saveStoreAddress(storeAddressInsert);
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑店铺地址接口")
    @VendorLogger(option = "编辑店铺地址接口")
    @PostMapping("edit")
    public JsonResult<Integer> editStoreAddress(HttpServletRequest request, StoreAddressUpdateDTO storeAddressUpdateDTO) {
        String logMsg = "修改地址id:" + storeAddressUpdateDTO.getAddressId();
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据地址id获取地址
        StoreAddress storeAddress = storeAddressModel.getStoreAddressByAddressId(storeAddressUpdateDTO.getAddressId());
        AssertUtil.notNull(storeAddress, "店铺地址为空");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeAddress.getStoreId()), "非法操作");

        BeanUtil.copyProperties(storeAddressUpdateDTO, storeAddress);
        storeAddress.setUpdateTime(new Date());
        storeAddressModel.updateStoreAddress(storeAddress);
        return SldResponse.success("修改成功", logMsg);
    }

    @ApiOperation("是否设为默认地址接口")
    @VendorLogger(option = "是否设为默认地址接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true),
            @ApiImplicitParam(name = "isDefault", value = "是否默认：1-是;0-否", required = true)
    })
    @PostMapping("isDefault")
    public JsonResult<Integer> isDefault(HttpServletRequest request,
                                         @RequestParam("addressId") Integer addressId,
                                         @RequestParam("isDefault") Integer isDefault) {
        String logMsg = "地址id:" + addressId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //根据地址id获取地址
        StoreAddress storeAddress = storeAddressModel.getStoreAddressByAddressId(addressId);
        AssertUtil.notNull(storeAddress, "店铺地址为空");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeAddress.getStoreId()), "非法操作");
        storeAddress.setIsDefault(isDefault);
        storeAddress.setUpdateTime(new Date());
        storeAddressModel.updateStoreAddress(storeAddress);
        return SldResponse.success("设置成功", logMsg);
    }

    @ApiOperation("删除地址接口")
    @VendorLogger(option = "删除地址接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true)
    })
    @PostMapping("del")
    public JsonResult<Integer> delStoreAddress(HttpServletRequest request, @RequestParam("addressId") Integer addressId) {
        String logMsg = "地址id:" + addressId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //根据addressId查询地址信息
        StoreAddress storeAddress = storeAddressModel.getStoreAddressByAddressId(addressId);
        AssertUtil.notNull(storeAddress, "未获取到店铺地址信息");
        AssertUtil.isTrue(!vendor.getStoreId().equals(storeAddress.getStoreId()), "非法操作");
        storeAddressModel.deleteStoreAddress(addressId);
        return SldResponse.success("删除成功", logMsg);
    }


}
