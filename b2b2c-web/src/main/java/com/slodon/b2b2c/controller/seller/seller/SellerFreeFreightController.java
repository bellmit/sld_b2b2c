package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.Vendor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Api(tags = "seller-免运费设置")
@RestController
@RequestMapping("v3/seller/seller/logistics")
public class SellerFreeFreightController extends BaseController {

    @Resource
    private StoreModel storeModel;

    @ApiOperation("免运费设置")
    @VendorLogger(option = "免运费设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "freeFreightLimit", value = "免运费额度，输出范围0～999999的阿拉伯数字", required = true)
    })
    @PostMapping("setFreeFreight")
    public JsonResult<Integer> setFreeFreight(HttpServletRequest request,
                                              @RequestParam("freeFreightLimit") Integer freeFreightLimit) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //获取店铺信息
        Store store = storeModel.getStoreByStoreId(vendor.getStoreId());
        //设置免运费额度
        store.setFreeFreightLimit(freeFreightLimit);
        storeModel.updateStore(store);
        return SldResponse.success("设置成功");
    }

}
