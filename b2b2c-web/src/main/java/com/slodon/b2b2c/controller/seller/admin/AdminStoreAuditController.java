package com.slodon.b2b2c.controller.seller.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.StoreConst;
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
import com.slodon.b2b2c.model.seller.StoreApplyModel;
import com.slodon.b2b2c.model.seller.StoreBindCategoryModel;
import com.slodon.b2b2c.model.seller.StoreCertificateModel;
import com.slodon.b2b2c.model.seller.StoreGradeModel;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.example.StoreCertificateExample;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.StoreBindCategory;
import com.slodon.b2b2c.seller.pojo.StoreCertificate;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.seller.*;
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

@Api(tags = "admin-入驻店铺相关接口")
@RestController
@RequestMapping("v3/seller/admin/storeAudit")
public class AdminStoreAuditController {

    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StoreGradeModel storeGradeModel;
    @Resource
    private StoreCertificateModel storeCertificateModel;
    @Resource
    private StoreBindCategoryModel storeBindCategoryModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;

    @ApiOperation("入驻审核列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "vendorName", value = "店主账号", paramType = "query"),
            @ApiImplicitParam(name = "storeGradeId", value = "店铺等级", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "审核状态", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreApplyVO>> getList(HttpServletRequest request,
                                                    @RequestParam(value = "storeName", required = false) String storeName,
                                                    @RequestParam(value = "vendorName", required = false) String vendorName,
                                                    @RequestParam(value = "storeGradeId", required = false) Integer storeGradeId,
                                                    @RequestParam(value = "state", required = false) Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreApplyExample example = new StoreApplyExample();
        example.setStoreNameLike(storeName);
        example.setVendorNameLike(vendorName);
        example.setStoreGradeId(storeGradeId);
        example.setStoreType(StoreConst.NO_OWN_STORE);
        example.setPager(pager);
        example.setOrderBy("submit_time desc");
        if (state != null) {
            example.setState(state);
        } else {
            example.setStateNotEquals(StoreConst.STATE_4_STORE_OPEN);
        }
        List<StoreApply> storeApplyList = storeApplyModel.getStoreApplyList(example, pager);
        List<StoreApplyVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeApplyList)) {
            storeApplyList.forEach(storeApply -> {
                StoreApplyVO vo = new StoreApplyVO(storeApply);
                //根据等级id获取等级名称
                StoreGrade storeGrade = storeGradeModel.getStoreGradeByGradeId(storeApply.getStoreGradeId());
                if (storeGrade != null) {
                    vo.setStoreGradeName(storeGrade.getGradeName());
                }
                //获取联系人名称和电话
                StoreCertificateExample storeCertificateExample = new StoreCertificateExample();
                storeCertificateExample.setVendorId(storeApply.getVendorId());
                List<StoreCertificate> storeCertificateList = storeCertificateModel.getStoreCertificateList(storeCertificateExample, null);
                if (!CollectionUtils.isEmpty(storeCertificateList)) {
                    vo.setContactName(storeCertificateList.get(0).getContactName());
                    vo.setContactPhone(CommonUtil.dealMobile(storeCertificateList.get(0).getContactPhone()));
                }
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, example.getPager()));
    }

    @ApiOperation("查看入驻详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyId", value = "申请id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<StoreApplyDetailVO> getDetail(HttpServletRequest request, @RequestParam("applyId") Integer applyId) {
        String logMsg = "申请id" + applyId;
        Admin admin = UserUtil.getUser(request, Admin.class);

        //获取商家申请信息
        StoreApply storeApply = storeApplyModel.getStoreApplyByApplyId(applyId);

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
            for (StoreBindCategory storeBindCategory : storeBindCategoryList) {
                StoreGoodsCateVO storeGoodsCateVO = new StoreGoodsCateVO(storeBindCategory);
                GoodsCategory goodsCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId1());
                if (goodsCategory1 == null) {
                    continue;
                }
                storeGoodsCateVO.setGoodsCateName1(goodsCategory1.getCategoryName());
                GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId2());
                if (goodsCategory2 == null) {
                    continue;
                }
                storeGoodsCateVO.setGoodsCateName2(goodsCategory2.getCategoryName());
                GoodsCategory goodsCategory3 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId3());
                if (goodsCategory3 == null) {
                    continue;
                }
                storeGoodsCateVO.setGoodsCateName3(goodsCategory3.getCategoryName());
                storeGoodsCateVOList.add(storeGoodsCateVO);
            }
            vo.setStoreGoodsCateVOList(storeGoodsCateVOList);
        }
        return SldResponse.success(vo, logMsg);
    }

    @ApiOperation("审核入驻申请")
    @OperationLogger(option = "审核入驻申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "applyId", value = "申请id", required = true),
            @ApiImplicitParam(name = "isPass", value = "是否通过[true==通过,false==驳回]", required = true),
            @ApiImplicitParam(name = "refuseReason", value = "审核原因，驳回时必填"),
            @ApiImplicitParam(name = "remark", value = "备注，拒绝时选填"),
            @ApiImplicitParam(name = "scalingBindIds", value = "分佣比例，审核通过时必填,例：bindId1-scaling1,bindId2-scaling2,...")
    })
    @PostMapping("audit")
    public JsonResult audit(HttpServletRequest request,
                            @RequestParam("applyId") Integer applyId,
                            @RequestParam("isPass") Boolean isPass,
                            @RequestParam(value = "refuseReason", required = false) String refuseReason,
                            @RequestParam(value = "remark", required = false) String remark,
                            @RequestParam(value = "scalingBindIds", required = false) String scalingBindIds) {

        Admin admin = UserUtil.getUser(request, Admin.class);

        AssertUtil.isTrue(!isPass && StringUtils.isEmpty(refuseReason), "请填写审核拒绝原因");
        AssertUtil.isTrue(isPass && StringUtils.isEmpty(scalingBindIds), "请填写分佣比例");

        storeApplyModel.audit(applyId, isPass, refuseReason, remark, scalingBindIds, admin);
        //根据applyId查询数据库
        StoreApply storeApply = storeApplyModel.getStoreApplyByApplyId(applyId);
        //根据gradeId查询store_grade表
        StoreGrade storeGrade = storeGradeModel.getStoreGradeByGradeId(storeApply.getStoreGradeId());

        RegisterPayVO vo = new RegisterPayVO(storeGrade, storeApply);
        //获取经营类目信息
        StoreBindCategoryExample storeBindCategoryExample = new StoreBindCategoryExample();
        storeBindCategoryExample.setCreateVendorId(storeApply.getVendorId());
        List<StoreBindCategory> storeBindCategoryList = storeBindCategoryModel.getStoreBindCategoryList(storeBindCategoryExample, null);
        List<StoreGoodsCateVO> storeGoodsCateVOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeBindCategoryList)) {
            for (StoreBindCategory storeBindCategory : storeBindCategoryList) {
                StoreGoodsCateVO storeGoodsCateVO = new StoreGoodsCateVO(storeBindCategory);
                GoodsCategory goodsCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId1());
                if (goodsCategory1 == null) {
                    continue;
                }
                storeGoodsCateVO.setGoodsCateName1(goodsCategory1.getCategoryName());
                GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId2());
                if (goodsCategory2 == null) {
                    continue;
                }
                storeGoodsCateVO.setGoodsCateName2(goodsCategory2.getCategoryName());
                GoodsCategory goodsCategory3 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId3());
                if (goodsCategory3 == null) {
                    continue;
                }
                storeGoodsCateVO.setGoodsCateName3(goodsCategory3.getCategoryName());
                storeGoodsCateVOList.add(storeGoodsCateVO);
            }
            vo.setStoreGoodsCateVOList(storeGoodsCateVOList);
        }
        if (isPass) {
            return SldResponse.success("审核通过", vo);
        } else {
            ApplyRefuseVO applyRefuseVO = new ApplyRefuseVO(storeApply);
            return SldResponse.success("审核拒绝", applyRefuseVO);
        }
    }
}
