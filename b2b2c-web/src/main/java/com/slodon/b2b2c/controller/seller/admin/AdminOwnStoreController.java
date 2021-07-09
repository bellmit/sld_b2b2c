package com.slodon.b2b2c.controller.seller.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreCertificateModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.seller.dto.OwnStoreAddDTO;
import com.slodon.b2b2c.seller.dto.OwnStoreUpdateDTO;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.example.StoreExample;
import com.slodon.b2b2c.seller.example.VendorExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.seller.OwnStoreDetailVO;
import com.slodon.b2b2c.vo.seller.OwnStoreVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "admin-自营店铺相关接口")
@RestController
@RequestMapping("v3/seller/admin/ownStore")
public class AdminOwnStoreController extends BaseController {

    @Resource
    private StoreModel storeModel;
    @Resource
    private VendorModel vendorModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("自营店铺列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "店铺状态 1、开启；2、关闭", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<OwnStoreVO>> getList(HttpServletRequest request,
                                                  @RequestParam(value = "storeName", required = false) String storeName,
                                                  @RequestParam(value = "state", required = false) Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreExample storeExample = new StoreExample();
        storeExample.setStoreNameLike(storeName);
        storeExample.setState(state);
        storeExample.setStateNotEquals(StoreConst.STORE_STATE_DELETE);
        storeExample.setIsOwnStore(StoreConst.IS_OWN_STORE);
        storeExample.setPager(pager);
        List<Store> storeList = storeModel.getStoreList(storeExample, pager);
        List<OwnStoreVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeList)) {
            storeList.forEach(store -> {
                OwnStoreVO vo = new OwnStoreVO(store);
                //根据店铺id获取商户账号
                VendorExample vendorExample = new VendorExample();
                vendorExample.setStoreId(store.getStoreId());
                vendorExample.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
                List<Vendor> vendorList = vendorModel.getVendorList(vendorExample, null);
                vo.setVendorName(vendorList.get(0).getVendorName());

                //通过vendorId查询资质表获取联系人电话
                StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
                storeCertificateExample.setVendorId(vendorList.get(0).getVendorId());
                List<StoreCertificate> storeCertificateList = storeCertificateModel.getStoreCertificateList(storeCertificateExample, null);
                if (!CollectionUtils.isEmpty(storeCertificateList)) {
                    vo.setContactPhone(CommonUtil.dealMobile(storeCertificateList.get(0).getContactPhone()));
                }
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, storeExample.getPager()));
    }

    @ApiOperation("新增自营店铺")
    @OperationLogger(option = "新增自营店铺")
    @PostMapping("add")
    public JsonResult<Integer> addOwnStore(HttpServletRequest request, OwnStoreAddDTO ownStoreAddDTO) {
        String logMsg = "店铺名称" + ownStoreAddDTO.getStoreName();
        storeModel.saveStore(ownStoreAddDTO);
        return SldResponse.success("添加成功", logMsg);
    }

    @ApiOperation("获取自营店铺详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<OwnStoreDetailVO> getDetail(HttpServletRequest request,
                                                  @RequestParam("storeId") Long storeId) {
        //根据店铺id获取店铺信息
        Store store = storeModel.getStoreByStoreId(storeId);
        //根据店铺id获取商户账号
        VendorExample vendorExample = new VendorExample();
        vendorExample.setStoreId(store.getStoreId());
        vendorExample.setIsStoreAdmin(VendorConst.IS_STORE_ADMIN_1);
        List<Vendor> vendorList = vendorModel.getVendorList(vendorExample, null);
        //获取店铺资质信息
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(vendorList.get(0).getVendorId());
        StoreCertificate storeCertificate = storeCertificateModel.getStoreCertificateList(storeCertificateExample, null).get(0);
        OwnStoreDetailVO vo = new OwnStoreDetailVO(store, storeCertificate);
        return SldResponse.success(vo);
    }

    @ApiOperation("编辑自营店铺")
    @OperationLogger(option = "编辑自营店铺")
    @PostMapping("edit")
    public JsonResult editOwnStore(HttpServletRequest request, OwnStoreUpdateDTO ownStoreUpdateDTO) throws Exception {
        String logMsg = "店铺id" + ownStoreUpdateDTO.getStoreId();
        storeModel.updateStore(ownStoreUpdateDTO);
        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("关闭/开启店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true),
            @ApiImplicitParam(name = "state", value = "店铺状态 1、开启；2、关闭", required = true),
    })
    @OperationLogger(option = "关闭/开启店铺")
    @PostMapping("lockUp")
    public JsonResult<Integer> lockUpStore(HttpServletRequest request,
                                           @RequestParam("storeId") Long storeId,
                                           @RequestParam("state") Integer state) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        AssertUtil.notNullOrZero(storeId, "请选择店铺id");
        storeModel.lockUpStore(storeId, state);
        if (state == 2) {
            //删除redis存储数据
            VendorExample example = new VendorExample();
            example.setStoreId(storeId);
            List<Vendor> list = vendorModel.getVendorList(example, null);
            for (Vendor vendor : list) {
                stringRedisTemplate.delete("seller-" + vendor.getVendorId());
            }
            return SldResponse.success("店铺已关闭");
        } else {
            return SldResponse.success("店铺已开启");
        }
    }

    @ApiOperation("删除店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true)
    })
    @OperationLogger(option = "删除店铺")
    @PostMapping("del")
    public JsonResult deleteStore(HttpServletRequest request, @RequestParam("storeId") Long storeId) {
        String logMsg = "店铺id" + storeId;
        Admin admin = UserUtil.getUser(request, Admin.class);

        AssertUtil.notNullOrZero(storeId, "请选择要删除的店铺id");
        storeModel.deleteStore(storeId);
        return SldResponse.success("删除店铺成功");
    }
}
