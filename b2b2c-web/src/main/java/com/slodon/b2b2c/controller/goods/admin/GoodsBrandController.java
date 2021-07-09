package com.slodon.b2b2c.controller.goods.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.BrandAuditDTO;
import com.slodon.b2b2c.goods.dto.GoodsBrandAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsBrandUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsBrandExample;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.goods.pojo.GoodsCategoryBindBrand;
import com.slodon.b2b2c.model.goods.GoodsBrandModel;
import com.slodon.b2b2c.model.goods.GoodsCategoryBindBrandModel;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.goods.GoodsBrandApplyVO;
import com.slodon.b2b2c.vo.goods.GoodsBrandVO;
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

@Api(tags = "admin-品牌管理")
@RestController
@RequestMapping("v3/goods/admin/goodsBrand")
public class GoodsBrandController {

    @Resource
    private GoodsBrandModel goodsBrandModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private GoodsCategoryBindBrandModel goodsCategoryBindBrandModel;
    @Resource
    private GoodsModel goodsModel;

    @ApiOperation("品牌列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandName", value = "品牌名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsBrandVO>> getList(HttpServletRequest request,
                                                    @RequestParam(value = "brandName", required = false) String brandName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setBrandNameLike(brandName);
        goodsBrandExample.setState(GoodsConst.BRAND_STATE_1); //只查询审核通过的（因为待审核和审核失败的有单独接口--applyList）
        List<GoodsBrand> brandList = goodsBrandModel.getGoodsBrandList(goodsBrandExample, pager);
        List<GoodsBrandVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(brandList)) {
            brandList.forEach(brand -> {
                GoodsBrandVO vo = new GoodsBrandVO(brand);
                GoodsExample example = new GoodsExample();
                example.setBrandId(brand.getBrandId());
                vo.setTotalGoodsNum(goodsModel.getGoodsCount(example));
                //查询在售商品数
                example.setState(GoodsConst.GOODS_STATE_UPPER);
                vo.setOnSaleGoodsNum(goodsModel.getGoodsCount(example));
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增品牌")
    @OperationLogger(option = "新增品牌")
    @PostMapping("add")
    public JsonResult<Integer> addBrand(HttpServletRequest request, GoodsBrandAddDTO goodSBrandAddDTO) {
        String logMsg = "品牌名称" + goodSBrandAddDTO.getBrandName();
        // 验证参数是否为空
        AssertUtil.notEmpty(goodSBrandAddDTO.getBrandName(), "品牌名称不能为空，请重试！");
        AssertUtil.notEmpty(goodSBrandAddDTO.getImage(), "品牌图片不能为空，请重试！");

        Admin admin = UserUtil.getUser(request, Admin.class);
        goodsBrandModel.saveGoodsBrand(admin.getAdminId(), goodSBrandAddDTO);
        return SldResponse.success("添加成功", logMsg);
    }

    @ApiOperation("编辑品牌")
    @OperationLogger(option = "编辑品牌")
    @PostMapping("edit")
    public JsonResult editBrand(HttpServletRequest request, GoodsBrandUpdateDTO goodsBrandUpdateDTO) throws Exception {
        String logMsg = "品牌id" + goodsBrandUpdateDTO.getBrandId();
        // 验证参数是否为空
        AssertUtil.notNullOrZero(goodsBrandUpdateDTO.getBrandId(), "请选择要修改的数据");
        AssertUtil.notEmpty(goodsBrandUpdateDTO.getBrandName(), "品牌名称不能为空，请重试！");
        AssertUtil.notEmpty(goodsBrandUpdateDTO.getImage(), "品牌图片不能为空，请重试！");

        Admin admin = UserUtil.getUser(request, Admin.class);
        goodsBrandModel.updateGoodsBrand(admin.getAdminId(), goodsBrandUpdateDTO);
        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("删除品牌")
    @OperationLogger(option = "删除品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandIds", value = "品牌id串,以逗号隔开", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteBrand(HttpServletRequest request, String brandIds) {
        String logMsg = "品牌id串" + brandIds;
        //参数校验
        AssertUtil.notEmpty(brandIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(brandIds, "brandIds格式错误,请重试");

        String[] brandIdArr = brandIds.split(",");
        for (String brandId : brandIdArr) {
            if (StringUtil.isEmpty(brandId)) continue;
            GoodsExample example = new GoodsExample();
            example.setBrandId(Integer.parseInt(brandId));
            example.setStateNotEquals(GoodsConst.GOODS_STATE_DELETE);
            Integer count = goodsModel.getGoodsCount(example);
            AssertUtil.isTrue(count > 0, "该品牌正在使用，不能删除");
        }

        goodsBrandModel.deleteBrand(brandIds);
        return SldResponse.success("删除品牌成功", logMsg);
    }

    @ApiOperation("品牌审核")
    @OperationLogger(option = "品牌审核")
    @PostMapping("Audit")
    public JsonResult<Integer> Audit(HttpServletRequest request, BrandAuditDTO brandAuditDTO) {
        String brandIds = brandAuditDTO.getBrandIds();
        String logMsg = "审核品牌ID" + brandIds;
        //参数校验
        AssertUtil.notEmpty(brandIds, "请选择要审核的品牌brandIds");
        AssertUtil.notFormatFrontIds(brandIds, "品牌brandIds格式错误,请重试");
        //审核拒绝参数判断
        if (brandAuditDTO.getState().equals(GoodsConst.BRAND_AUDIT_REJECT)) {
            AssertUtil.notEmpty(brandAuditDTO.getAuditReason(), "审核拒绝原因不能为空，请重试！");
        }

        int number = 0;
        String[] brandIdArr = brandIds.split(",");
        AssertUtil.isTrue(brandIdArr.length < 1, "品牌Id不能为空,请重试");
        for (String brandId : brandIdArr) {
            AssertUtil.notEmpty(brandId, "品牌Id不能为空,请重试");
            GoodsBrand goodsBrandUpdate = goodsBrandModel.getGoodsBrandByBrandId(Integer.valueOf(brandId));
            AssertUtil.notNull(brandId, "未查询到id为" + brandId + "品牌,请重试");
            if (goodsBrandUpdate.getState() == GoodsConst.BRAND_STATE_2) {
                //审核拒绝参数判断
                if (brandAuditDTO.getState() == GoodsConst.BRAND_AUDIT_AGREE) {
                    goodsBrandUpdate.setState(GoodsConst.BRAND_STATE_1);
                    //添加品牌绑定分类表
                    GoodsCategoryBindBrand goodsCategoryBindBrandInsert = new GoodsCategoryBindBrand();
                    goodsCategoryBindBrandInsert.setBrandId(Integer.parseInt(brandId));
                    goodsCategoryBindBrandInsert.setCategoryId(goodsBrandUpdate.getGoodsCategoryId3());
                    goodsCategoryBindBrandModel.saveGoodsCategoryBindBrand(goodsCategoryBindBrandInsert);
                } else {
                    goodsBrandUpdate.setState(GoodsConst.BRAND_STATE_3);
                    goodsBrandUpdate.setFailReason(brandAuditDTO.getAuditReason());
                }
                number += goodsBrandModel.updateGoodsBrand(goodsBrandUpdate);
            } else {
                throw new MallException("只有待审核品牌可以审核！");
            }
        }
        return SldResponse.success(number + "个品牌审核完成", logMsg);
    }

    @ApiOperation("获取待审核的品牌列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandName", value = "品牌名称", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "审核状态：2-待审核，3-审核失败", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("applyList")
    public JsonResult<PageVO<GoodsBrandApplyVO>> getApplyList(HttpServletRequest request,
                                                              @RequestParam(value = "brandName", required = false) String brandName,
                                                              @RequestParam(value = "storeName", required = false) String storeName,
                                                              @RequestParam(value = "state", required = false) Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setBrandNameLike(brandName);
        goodsBrandExample.setState(state);
        goodsBrandExample.setStoreNameLike(storeName);
        goodsBrandExample.setStateNotIn(GoodsConst.BRAND_STATE_1 + "," + GoodsConst.BRAND_STATE_4);
        goodsBrandExample.setPager(pager);
        List<GoodsBrand> brandList = goodsBrandModel.getGoodsBrandList(goodsBrandExample, pager);
        List<GoodsBrandApplyVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(brandList)) {
            brandList.forEach(brand -> {
                GoodsBrandApplyVO vo = new GoodsBrandApplyVO(brand);
                //根据storeId获取store
                Store store = storeModel.getStoreByStoreId(brand.getApplyStoreId());
                vo.setStoreName(store.getStoreName());
                //获取一级,二级分类id
                List<GoodsCategory> list = goodsCategoryModel.getGoodsCategoryListByCategoryId3(brand.getGoodsCategoryId3());
                vo.setGoodsCategoryId1(list.get(0).getCategoryId());
                vo.setGoodsCategoryId2(list.get(1).getCategoryId());
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, goodsBrandExample.getPager()));
    }
}
