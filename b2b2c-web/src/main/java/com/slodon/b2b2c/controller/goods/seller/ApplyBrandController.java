package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.ApplyBrandAddDTO;
import com.slodon.b2b2c.goods.dto.ApplyBrandUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsBrandExample;
import com.slodon.b2b2c.goods.example.GoodsCategoryExample;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.model.goods.GoodsBrandModel;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.model.seller.VendorModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.BrandAndCateVO;
import com.slodon.b2b2c.vo.goods.SellerBrandDetailVO;
import com.slodon.b2b2c.vo.goods.SellerBrandVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Api(tags = "seller品牌管理")
@RestController
@RequestMapping("v3/goods/seller/Brand")
public class ApplyBrandController {

    @Resource
    private GoodsBrandModel goodsBrandModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private VendorModel vendorModel;

    @ApiOperation("品牌列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandName", value = "品牌名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SellerBrandVO>> getList(HttpServletRequest request,
                                                     @RequestParam(value = "brandName", required = false) String brandName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setBrandNameLike(brandName);
        goodsBrandExample.setApplyStoreId(vendor.getStoreId());
        goodsBrandExample.setPager(pager);
        List<GoodsBrand> brandList = goodsBrandModel.getGoodsBrandList(goodsBrandExample, pager);
        List<SellerBrandVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(brandList)) {
            brandList.forEach(brand -> {
                SellerBrandVO vo = new SellerBrandVO(brand);
                //获取一级,二级分类id
                List<GoodsCategory> list = goodsCategoryModel.getGoodsCategoryListByCategoryId3(brand.getGoodsCategoryId3());
                vo.setGoodsCategoryId1(list.get(0).getCategoryId());
                vo.setGoodsCategoryId2(list.get(1).getCategoryId());
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, goodsBrandExample.getPager()));
    }

    @ApiOperation("申请品牌")
    @VendorLogger(option = "申请品牌")
    @PostMapping("apply")
    public JsonResult<Integer> applyBrand(HttpServletRequest request, ApplyBrandAddDTO applyBrandAddDTO) {
        String logMsg = "品牌名称" + applyBrandAddDTO.getBrandName();
        // 验证参数是否为空
        AssertUtil.notNull(applyBrandAddDTO, "品牌信息不能为空，请重试！");
        AssertUtil.notEmpty(applyBrandAddDTO.getBrandName(), "品牌名称不能为空，请重试！");
        AssertUtil.notEmpty(applyBrandAddDTO.getImage(), "品牌图片不能为空，请重试！");
        AssertUtil.notNullOrZero(applyBrandAddDTO.getCategoryId(), "分类不能为空，请重试！");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //根据三级分类获取上级分类
        List<GoodsCategory> goodsCategoryList = goodsCategoryModel.getGoodsCategoryListByCategoryId3(applyBrandAddDTO.getCategoryId());
        AssertUtil.isTrue(goodsCategoryList == null || goodsCategoryList.size() == 0, "商品分类信息有误，请重试！");

        goodsBrandModel.saveSellersBrand(applyBrandAddDTO, vendor, goodsCategoryList.get(0).getCategoryName() + "/" + goodsCategoryList.get(1).getCategoryName() + "/" + goodsCategoryList.get(2).getCategoryName());
        return SldResponse.success("添加成功", logMsg);
    }

    @ApiOperation("编辑品牌")
    @VendorLogger(option = "编辑品牌")
    @PostMapping("edit")
    public JsonResult editBrand(HttpServletRequest request, ApplyBrandUpdateDTO applyBrandUpdateDTO) throws Exception {
        AssertUtil.notNull(applyBrandUpdateDTO, "品牌信息不能为空，请重试！");
        AssertUtil.notNullOrZero(applyBrandUpdateDTO.getBrandId(), "品牌ID不能为空，请重试！");
        AssertUtil.notEmpty(applyBrandUpdateDTO.getBrandName(), "品牌名称不能为空，请重试！");
        AssertUtil.notEmpty(applyBrandUpdateDTO.getImage(), "品牌图片不能为空，请重试！");
        AssertUtil.notNullOrZero(applyBrandUpdateDTO.getCategoryId(), "分类不能为空，请重试！");

        String logMsg = "品牌id" + applyBrandUpdateDTO.getBrandId();
        GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(applyBrandUpdateDTO.getBrandId());
        AssertUtil.notNull(goodsBrand, "未查询到该品牌，请重试！");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.isTrue(!goodsBrand.getApplyVendorId().equals(vendor.getVendorId()), "无操作权限，请重试！");

        //根据三级分类获取上级分类
        List<GoodsCategory> goodsCategoryList = goodsCategoryModel.getGoodsCategoryListByCategoryId3(applyBrandUpdateDTO.getCategoryId());
        AssertUtil.isTrue(goodsCategoryList == null || goodsCategoryList.size() == 0, "商品分类信息有误，请重试！");

        goodsBrandModel.updateSellersBrand(applyBrandUpdateDTO, vendor, goodsCategoryList.get(0).getCategoryName() + "/" + goodsCategoryList.get(1).getCategoryName() + "/" + goodsCategoryList.get(2).getCategoryName());

        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("删除品牌")
    @VendorLogger(option = "删除品牌")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌id", required = true)
    })
    @PostMapping("delete")
    public JsonResult deleteBrand(HttpServletRequest request, @RequestParam("brandId") Integer brandId) {

        GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(brandId);
        AssertUtil.notNull(goodsBrand, "未查询到该品牌");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.isTrue(!goodsBrand.getApplyVendorId().equals(vendor.getVendorId()), "无操作权限，请重试！");

        goodsBrandModel.deleteGoodsBrand(brandId);
        return SldResponse.success("删除品牌成功");
    }

    @ApiOperation("查看品牌详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brandId", value = "品牌id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<SellerBrandDetailVO> detail(HttpServletRequest request, @RequestParam("brandId") Integer brandId) {
        String logMsg = "品牌id" + brandId;
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(brandId);
        AssertUtil.notNull(goodsBrand, "未获取到品牌信息");
        Vendor vendorDb = vendorModel.getVendorByVendorId(goodsBrand.getApplyVendorId());
        AssertUtil.isTrue(!vendor.getStoreId().equals(vendorDb.getStoreId()), "无操作权限，请重试！");

        SellerBrandDetailVO vo = new SellerBrandDetailVO(goodsBrand);
        //获取一级,二级分类id
        List<GoodsCategory> list = goodsCategoryModel.getGoodsCategoryListByCategoryId3(goodsBrand.getGoodsCategoryId3());
        vo.setGoodsCateId1(list.get(0).getCategoryId());
        vo.setGoodsCateId2(list.get(1).getCategoryId());
        //获取品牌绑定的分类列表
        List<BrandAndCateVO> brandAndCateVOList = cateList(goodsBrand.getBrandId());
        vo.setBrandAndCateVOList(brandAndCateVOList);
        return SldResponse.success(vo, logMsg);
    }

    private List<BrandAndCateVO> cateList(Integer brandId) {
        List<BrandAndCateVO> list = new LinkedList<>();
        //根据品牌id查询品牌分类
        GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(brandId);
        //根据分类id查询分类
        GoodsCategory goodsCategory3 = goodsCategoryModel.getGoodsCategoryByCategoryId(goodsBrand.getGoodsCategoryId3());
        //获取三级分类id
        Integer categoryId3 = goodsCategory3.getCategoryId();

        //获取二级分类id
        Integer categoryId2 = goodsCategory3.getPid();
        GoodsCategory goodsCategoryByCategoryId = goodsCategoryModel.getGoodsCategoryByCategoryId(categoryId2);
        //获取一级分类id
        Integer categoryId1 = goodsCategoryByCategoryId.getPid();

        //获取所有一级分类
        GoodsCategoryExample goodsCategoryExample1 = new GoodsCategoryExample();
        goodsCategoryExample1.setGrade(1);
        List<GoodsCategory> goodsCategoryList = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample1, null);
        for (GoodsCategory goodsCategory1 : goodsCategoryList) {
            BrandAndCateVO vo = new BrandAndCateVO();
            vo.setCategoryId(goodsCategory1.getCategoryId());
            vo.setCategoryName(goodsCategory1.getCategoryName());
            if (goodsCategory1.getCategoryId().equals(categoryId1)) {

                GoodsCategoryExample goodsCategoryExample2 = new GoodsCategoryExample();
                goodsCategoryExample2.setPid(goodsCategory1.getCategoryId());
                List<GoodsCategory> goodsCategoryList2 = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample2, null);
                List<BrandAndCateVO> vos2 = new LinkedList<>();
                for (GoodsCategory category2 : goodsCategoryList2) {
                    BrandAndCateVO vo2 = new BrandAndCateVO();
                    vo2.setCategoryId(category2.getCategoryId());
                    vo2.setCategoryName(category2.getCategoryName());
                    if (category2.getCategoryId().equals(categoryId2)) {
                        GoodsCategoryExample goodsCategoryExample3 = new GoodsCategoryExample();
                        goodsCategoryExample3.setPid(category2.getCategoryId());
                        List<GoodsCategory> goodsCategoryList3 = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample3, null);
                        List<BrandAndCateVO> vos3 = new LinkedList<>();
                        for (GoodsCategory category3 : goodsCategoryList3) {
                            BrandAndCateVO vo3 = new BrandAndCateVO();
                            vo3.setCategoryId(category3.getCategoryId());
                            vo3.setCategoryName(category3.getCategoryName());
                            vos3.add(vo3);
                        }
                        vo2.setChildren(vos3);
                    }
                    vos2.add(vo2);
                }
                vo.setChildren(vos2);
            }
            list.add(vo);
        }
        return list;
    }
}
