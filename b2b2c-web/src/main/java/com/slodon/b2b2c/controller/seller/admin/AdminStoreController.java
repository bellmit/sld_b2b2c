package com.slodon.b2b2c.controller.seller.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.model.seller.*;
import com.slodon.b2b2c.seller.dto.StoreUpdateDTO;
import com.slodon.b2b2c.seller.example.*;
import com.slodon.b2b2c.seller.pojo.*;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.seller.StoreDetailVO;
import com.slodon.b2b2c.vo.seller.StoreGoodsCateVO;
import com.slodon.b2b2c.vo.seller.StoreVO;
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
import java.util.*;

@Api(tags = "admin-入驻成功的店铺管理")
@RestController
@RequestMapping("v3/seller/admin/store")
public class AdminStoreController extends BaseController {

    @Resource
    private StoreModel storeModel;
    @Resource
    private VendorModel vendorModel;
    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;
    @Resource
    private StoreBindCategoryModel storeBindCategoryModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("入驻店铺列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "vendorName", value = "店主账号", paramType = "query"),
            @ApiImplicitParam(name = "storeGradeId", value = "店铺等级id", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "店铺状态 1、开启；2、关闭", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreVO>> getList(HttpServletRequest request,
                                               @RequestParam(value = "storeName", required = false) String storeName,
                                               @RequestParam(value = "vendorName", required = false) String vendorName,
                                               @RequestParam(value = "storeGradeId", required = false) Integer storeGradeId,
                                               @RequestParam(value = "state", required = false) Integer state) {

        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreExample storeExample = new StoreExample();
        storeExample.setStoreNameLike(storeName);
        storeExample.setVendorNameLike(vendorName);
        storeExample.setStoreGradeId(storeGradeId);
        storeExample.setState(state);
        storeExample.setStateNotEquals(StoreConst.STORE_STATE_DELETE);
        storeExample.setIsOwnStore(StoreConst.NO_OWN_STORE);
        storeExample.setPager(pager);
        List<Store> storeList = storeModel.getStoreList(storeExample, pager);
        List<StoreVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeList)) {
            storeList.forEach(store -> {
                StoreVO vo = new StoreVO(store);
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

    @ApiOperation("查看店铺信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<StoreDetailVO> getDetail(HttpServletRequest request, @RequestParam("storeId") Long storeId) {
        String logMsg = "店铺id" + storeId;

        //根据storeId获取店铺信息
        Store store = storeModel.getStoreByStoreId(storeId);

        //获取申请表信息
        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setStoreId(storeId);
        StoreApply storeApply = storeApplyModel.getStoreApplyList(storeApplyExample, null).get(0);

        //获取店铺资质信息
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(storeApply.getVendorId());
        StoreCertificate storeCertificate = storeCertificateModel.getStoreCertificateList(storeCertificateExample, null).get(0);

        //获取店铺logo
        if (StringUtils.isEmpty(store.getStoreLogo())) {
            store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
        } else {
            store.setStoreLogo(store.getStoreLogo());
        }
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
        storeBindCategoryExample.setState(StoreConst.STORE_CATEGORY_STATE_PASS);
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
        return SldResponse.success(vo, logMsg);
    }

    @ApiOperation("编辑店铺详情信息")
    @OperationLogger(option = "编辑店铺详情信息")
    @PostMapping("edit")
    public JsonResult editStore(HttpServletRequest request, StoreUpdateDTO storeUpdateDTO) {
        String logMsg = "店铺id" + storeUpdateDTO.getStoreId();
        storeModel.editStoreInfo(storeUpdateDTO);
        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("临效期店铺列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "vendorName", value = "店主账号", paramType = "query"),
            @ApiImplicitParam(name = "storeGradeId", value = "店铺等级id", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "店铺状态 1、开启；2、关闭", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("expiryList")
    public JsonResult<PageVO<StoreVO>> getExpiryList(HttpServletRequest request,
                                                     @RequestParam(value = "storeName", required = false) String storeName,
                                                     @RequestParam(value = "vendorName", required = false) String vendorName,
                                                     @RequestParam(value = "storeGradeId", required = false) Integer storeGradeId,
                                                     @RequestParam(value = "state", required = false) Integer state) {

        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreExample storeExample = new StoreExample();
        storeExample.setStoreNameLike(storeName);
        storeExample.setVendorNameLike(vendorName);
        storeExample.setStoreGradeId(storeGradeId);
        storeExample.setState(state);
        storeExample.setStateNotEquals(StoreConst.STORE_STATE_DELETE);
        storeExample.setIsOwnStore(StoreConst.NO_OWN_STORE);
        storeExample.setPager(pager);
        //获取当前一个月内的日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        Date date = calendar.getTime();
        storeExample.setStoreExpireTimeAfter(new Date());
        storeExample.setStoreExpireTimeBefore(date);
        storeExample.setOrderBy("store_expire_time asc");
        List<Store> storeList = storeModel.getStoreList(storeExample, pager);
        List<StoreVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeList)) {
            storeList.forEach(store -> {
                StoreVO vo = new StoreVO(store);
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
                    vo.setContactPhone(storeCertificateList.get(0).getContactPhone());
                }
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, storeExample.getPager()));
    }

    @ApiOperation("设置结算周期")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true),
            @ApiImplicitParam(name = "billType", value = "结算方式：1-按月结算，2-按周结算", required = true),
            @ApiImplicitParam(name = "billDays", value = "结算日期字符串")
    })
    @OperationLogger(option = "设置结算周期")
    @PostMapping("setBillDate")
    public JsonResult<Integer> setBillDate(HttpServletRequest request,
                                           @RequestParam("storeId") Long storeId,
                                           @RequestParam("billType") Integer billType,
                                           @RequestParam(value = "billDays", required = false) String billDays) {
        String logMsg = "店铺id:" + storeId;
        //根据storeId查询店铺信息
        Store storeDb = storeModel.getStoreByStoreId(storeId);
        AssertUtil.notNull(storeDb, "未获取到店铺信息");

        Store storeEdit = new Store();
        storeEdit.setStoreId(storeDb.getStoreId());
        storeEdit.setBillType(billType);
        if (!StringUtils.isEmpty(billDays)) {
            //结算日期
            String[] billDayArr = billDays.split(",");

            Boolean billFlag = false;
            for (String billDay : billDayArr) {
                if (billType == StoreConst.BILL_TYPE_WEEK) {
                    for (int i = 1; i < 8; i++) {
                        if (Integer.parseInt(billDay) == i) {
                            billFlag = true;
                        }
                    }
                } else if (billType == StoreConst.BILL_TYPE_MONTH) {
                    for (int i = 1; i < 32; i++) {
                        if (Integer.parseInt(billDay) == i) {
                            billFlag = true;
                        }
                    }
                }
            }
            if (billFlag == false) {
                throw new MallException("请选择结算周期");
            }
            storeEdit.setBillDay(billDays);
        }
        storeModel.updateStore(storeEdit);
        return SldResponse.success("设置成功", logMsg);
    }

    @ApiOperation("获取开店时长列表")
    @GetMapping("openTime")
    public JsonResult openTime(HttpServletRequest request) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        return SldResponse.success(Arrays.asList(1, 2, 3));
    }
}
