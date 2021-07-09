package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.model.seller.*;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.pojo.*;
import com.slodon.b2b2c.vo.seller.StoreDetailVO;
import com.slodon.b2b2c.vo.seller.StoreGoodsCateVO;
import com.slodon.b2b2c.vo.seller.StoreIndexInformationVO;
import com.slodon.b2b2c.vo.seller.StoreSettingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "seller-商户后台店铺信息")
@RestController
@RequestMapping("v3/seller/seller/store")
public class SellerStoreController extends BaseController {

    @Resource
    private StoreModel storeModel;
    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;
    @Resource
    private StoreBindCategoryModel storeBindCategoryModel;
    @Resource
    private VendorRolesModel vendorRolesModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("查看店铺信息")
    @GetMapping("detail")
    public JsonResult<StoreDetailVO> getDetail(HttpServletRequest request) {

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //根据vendor获取店铺信息
        Store store = storeModel.getStoreByStoreId(vendor.getStoreId());
        AssertUtil.notNull(store,"未获取到店铺信息");
        AssertUtil.isTrue(!vendor.getStoreId().equals(store.getStoreId()),"非法操作");

        //获取店铺logo
        if (StringUtils.isEmpty(store.getStoreLogo())) {
            store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
        } else {
            store.setStoreLogo(store.getStoreLogo());
        }

        //获取申请表信息
        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setStoreId(store.getStoreId());
        StoreApply storeApply = storeApplyModel.getStoreApplyList(storeApplyExample, null).get(0);

        //获取店铺资质信息
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(storeApply.getVendorId());
        StoreCertificate storeCertificate = storeCertificateModel.getStoreCertificateList(storeCertificateExample, null).get(0);

        StoreDetailVO vo = new StoreDetailVO(store, storeCertificate);
        //获取应付金额
        vo.setPayAmount(storeApply.getPayAmount());
        //获取支付方式
        vo.setPaymentCode(storeApply.getPaymentCode());
        //获取支付名称
        vo.setPaymentName(storeApply.getPaymentName());

        //获取经营类目信息
        StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
        storeBindCategoryExample.setStoreId(storeApply.getStoreId());
        storeBindCategoryExample.setCreateVendorId(storeApply.getVendorId());
        List<StoreBindCategory> storeBindCategoryList = storeBindCategoryModel.getStoreBindCategoryList(storeBindCategoryExample, null);
        List<StoreGoodsCateVO> storeGoodsCateVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeBindCategoryList)) {
            storeBindCategoryList.forEach(storeBindCategory -> {
                StoreGoodsCateVO storeGoodsCateVO = new StoreGoodsCateVO(storeBindCategory);
                GoodsCategory goodsCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId1());
                storeGoodsCateVO.setGoodsCateName1(goodsCategory1.getCategoryName());
                GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId2());
                storeGoodsCateVO.setGoodsCateName2(goodsCategory2.getCategoryName());
                GoodsCategory goodsCategory3 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId3());
                storeGoodsCateVO.setGoodsCateName3(goodsCategory3.getCategoryName());
                storeGoodsCateVOList.add(storeGoodsCateVO);
            });
            vo.setStoreGoodsCateVOList(storeGoodsCateVOList);
        }

        //获取结算周期
        if (!StringUtils.isEmpty(store.getBillType()) && !StringUtils.isEmpty(store.getBillDay())) {
            if (store.getBillType() == StoreConst.BILL_TYPE_MONTH) {
                vo.setBillCycle("每月" + store.getBillDay() + "号");
            } else {
                //阿拉伯数字转化为星期
                StringBuilder builder = new StringBuilder();
                String[] dayArr = store.getBillDay().split(",");
                for (String dayStr : dayArr) {
                    AssertUtil.isTrue(Integer.parseInt(dayStr) < 0 && Integer.parseInt(dayStr) > 7, "获取的星期有误");
                    switch (Integer.parseInt(dayStr)) {
                        case 1:
                            builder.append("周一");
                            builder.append(",");
                            break;
                        case 2:
                            builder.append("周二");
                            builder.append(",");
                            break;
                        case 3:
                            builder.append("周三");
                            builder.append(",");
                            break;
                        case 4:
                            builder.append("周四");
                            builder.append(",");
                            break;
                        case 5:
                            builder.append("周五");
                            builder.append(",");
                            break;
                        case 6:
                            builder.append("周六");
                            builder.append(",");
                            break;
                        case 7:
                            builder.append("周日");
                            builder.append(",");
                            break;
                    }
                }
                vo.setBillCycle("每" + builder.substring(0, builder.length() - 1));
            }
        }
        return SldResponse.success(vo);
    }

    @ApiOperation("获取店铺设置信息接口")
    @GetMapping("settingDetail")
    public JsonResult<StoreSettingVO> settingDetail(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Store store = storeModel.getStoreByStoreId(vendor.getStoreId());
        StoreSettingVO vo = new StoreSettingVO(store);
        return SldResponse.success(vo);
    }

    @ApiOperation("编辑店铺设置接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "mainBusiness", value = "店铺主营商品名称，用','分隔，例如'男装,女装,童装'"),
            @ApiImplicitParam(name = "storeLogo", value = "店铺logo"),
            @ApiImplicitParam(name = "servicePhone", value = "店铺客服电话"),
            @ApiImplicitParam(name = "storeBannerPc", value = "pc端店铺横幅"),
            @ApiImplicitParam(name = "storeBannerMobile", value = "移动端店铺横幅"),
            @ApiImplicitParam(name = "storeBackdrop", value = "店铺背景"),
            @ApiImplicitParam(name = "storeSeoKeyword", value = "店铺seo keyword"),
            @ApiImplicitParam(name = "storeSeoDesc", value = "店铺SEO描述")
    })
    @VendorLogger(option = "编辑店铺设置接口")
    @PostMapping("updateSetting")
    public JsonResult<Integer> updateStoreSetting(HttpServletRequest request,
                                                  @RequestParam(value = "mainBusiness", required = false) String mainBusiness,
                                                  @RequestParam(value = "storeLogo", required = false) String storeLogo,
                                                  @RequestParam(value = "servicePhone", required = false) String servicePhone,
                                                  @RequestParam(value = "storeBannerPc", required = false) String storeBannerPc,
                                                  @RequestParam(value = "storeBannerMobile", required = false) String storeBannerMobile,
                                                  @RequestParam(value = "storeBackdrop", required = false) String storeBackdrop,
                                                  @RequestParam(value = "storeSeoKeyword", required = false) String storeSeoKeyword,
                                                  @RequestParam(value = "storeSeoDesc", required = false) String storeSeoDesc) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        String logMsg = "店铺id:" + vendor.getStoreId();

        Store store = new Store();
        store.setStoreId(vendor.getStoreId());
        store.setMainBusiness(mainBusiness);
        store.setStoreLogo(storeLogo);
        store.setServicePhone(servicePhone);
        store.setStoreBannerPc(storeBannerPc);
        store.setStoreBannerMobile(storeBannerMobile);
        store.setStoreBackdrop(storeBackdrop);
        store.setStoreSeoKeyword(storeSeoKeyword);
        store.setStoreSeoDesc(storeSeoDesc);
        storeModel.updateStore(store);
        return SldResponse.success("设置成功", logMsg);
    }

    @ApiOperation("获取商户首页店铺信息")
    @GetMapping("indexStoreInfor")
    public JsonResult<StoreIndexInformationVO> getIndexStoreInfor(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        Store store = storeModel.getStoreByStoreId(vendor.getStoreId());
        //店铺默认logo
        if (StringUtils.isEmpty(store.getStoreLogo())){
            store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
        }
        StoreIndexInformationVO vo = new StoreIndexInformationVO(store, vendor);
        //获取角色名称
        VendorRoles vendorRoles = vendorRolesModel.getVendorRolesByRolesId(vendor.getRolesId());
        vo.setRolesName(vendorRoles.getRolesName());
        vo.setAdminLogoUrl(FileUrlUtil.getFileUrl(stringRedisTemplate.opsForValue().get("main_admin_top_logo"), null));
        return SldResponse.success(vo);
    }
}
