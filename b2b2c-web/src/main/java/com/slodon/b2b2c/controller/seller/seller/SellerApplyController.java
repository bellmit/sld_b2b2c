package com.slodon.b2b2c.controller.seller.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.model.seller.StoreApplyModel;
import com.slodon.b2b2c.model.seller.StoreBindCategoryModel;
import com.slodon.b2b2c.model.seller.StoreCertificateModel;
import com.slodon.b2b2c.model.seller.StoreGradeModel;
import com.slodon.b2b2c.seller.dto.StoreApplyDTO;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.pojo.*;
import com.slodon.b2b2c.vo.seller.ProcessVO;
import com.slodon.b2b2c.vo.seller.StoreApplyDetailVO;
import com.slodon.b2b2c.vo.seller.StoreGoodsCateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Api(tags = "seller-商户入驻申请相关接口")
@RestController
@RequestMapping("v3/seller/seller/apply")
public class SellerApplyController extends BaseController {

    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;
    @Resource
    private StoreGradeModel storeGradeModel;
    @Resource
    private StoreBindCategoryModel storeBindCategoryModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;

    @ApiOperation("保存入驻信息")
    @VendorLogger(option = "保存入驻信息")
    @PostMapping("saveApply")
    public JsonResult saveApply(HttpServletRequest request, StoreApplyDTO storeApplyDTO) throws Exception {
        String logMsg = "店铺名称" + storeApplyDTO.getStoreName();
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.isTrue(storeApplyDTO.getEnterType() == StoreConst.APPLY_TYPE_COMPANY
                && StringUtils.isEmpty(storeApplyDTO.getCompanyName()), "请填写公司名称");

        //先根据vendorId查询数据库中的申请，判断是否是第一次申请
        StoreApplyExample example = new StoreApplyExample();
        example.setVendorId(vendor.getVendorId());
        List<StoreApply> storeApplies = storeApplyModel.getStoreApplyList(example, null);
        if (!CollectionUtils.isEmpty(storeApplies)) {
            //数据库里已存在该商家的申请，说明是审核失败，重新提交申请
            StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
            storeCertificateExample.setVendorId(vendor.getVendorId());
            List<StoreCertificate> storeCertificateList = storeCertificateModel.getStoreCertificateList(storeCertificateExample, null);
            storeApplyDTO.setCertificateId(storeCertificateList.get(0).getCertificateId());
            storeApplyDTO.setApplyId(storeApplies.get(0).getApplyId());
            //更新申请信息
            storeApplyModel.updateStoreApplyInfo(storeApplyDTO, vendor);
        } else {
            //数据库里没有改用户的申请，插入新的数据
            storeApplyModel.saveStoreApplyInfo(storeApplyDTO, vendor);
        }
        return SldResponse.success("申请成功提交", logMsg);
    }

    @ApiOperation("获取开店时长列表")
    @GetMapping("openTime")
    public JsonResult openTime(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        return SldResponse.success(Arrays.asList(1, 2, 3));
    }

    @ApiOperation("获取审核进程接口")
    @GetMapping("process")
    public JsonResult<ProcessVO> process(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setVendorId(vendor.getVendorId());
        List<StoreApply> storeApplyList = storeApplyModel.getStoreApplyList(storeApplyExample, null);

        ProcessVO vo = new ProcessVO();
        if (CollectionUtils.isEmpty(storeApplyList)) {
            //用户未入驻过
            vo.setApplyStep(0);
            vo.setStateValue("未入驻");
            return SldResponse.success(vo);
        }
        //根据等级id获取等级名称
        StoreGrade storeGrade = storeGradeModel.getStoreGradeByGradeId(storeApplyList.get(0).getStoreGradeId());
        vo = new ProcessVO(storeGrade, storeApplyList.get(0));
        vo.setPrice(storeGrade.getPrice() + "元/年");
        vo.setApplyYear(storeApplyList.get(0).getApplyYear() + "年");
        vo.setPayAmount("￥" + new BigDecimal(storeGrade.getPrice()).multiply(new BigDecimal(storeApplyList.get(0).getApplyYear())));
        //审核状态信息
        if (storeApplyList.get(0).getState() == StoreConst.STATE_1_SEND_APPLY || storeApplyList.get(0).getState() == StoreConst.STATE_2_DONE_APPLY) {
            //待审核或审核通过，支付凭证信息
            //获取经营类目列表
            StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
            storeBindCategoryExample.setCreateVendorId(storeApplyList.get(0).getVendorId());
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
            if (storeApplyList.get(0).getState() == StoreConst.STATE_1_SEND_APPLY) {
                vo.setStateValue("申请已经提交，请等待管理员审核");
            } else {
                vo.setStateValue("您的审核已通过，请选择支付方式并缴纳费用");
            }
        } else if (storeApplyList.get(0).getState() == StoreConst.STATE_3_FAIL_APPLY) {
            vo.setStateValue("审核失败");
        } else {
            vo.setStateValue("入驻成功");
        }
        return SldResponse.success(vo);
    }

    @ApiOperation("获取入驻详情接口")
    @GetMapping("applyDetail")
    public JsonResult<StoreApplyDetailVO> getDetail(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //获取商家申请信息
        StoreApplyExample storeApplyExample = new StoreApplyExample();
        storeApplyExample.setVendorId(vendor.getVendorId());
        StoreApply storeApply = storeApplyModel.getStoreApplyList(storeApplyExample, null).get(0);

        //获取申请资质信息
        StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
        storeCertificateExample.setVendorId(storeApply.getVendorId());
        List<StoreCertificate> storeCertificateList = storeCertificateModel.getStoreCertificateList(storeCertificateExample, null);
        StoreApplyDetailVO vo = new StoreApplyDetailVO(storeApply, storeCertificateList.get(0));

        //根据等级id获取等级名称
        StoreGrade storeGrade = storeGradeModel.getStoreGradeByGradeId(storeApply.getStoreGradeId());
        if (storeGrade != null) {
            vo.setStoreGradeName(storeGrade.getGradeName());
        }

        //获取经营类目信息
        StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
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
        return SldResponse.success(vo);
    }
}
