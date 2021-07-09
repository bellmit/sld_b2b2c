package com.slodon.b2b2c.controller.goods.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.GoodsCategoryConst;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.GoodsCategoryAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsCategoryUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsCategoryBindAttributeExample;
import com.slodon.b2b2c.goods.example.GoodsCategoryBindBrandExample;
import com.slodon.b2b2c.goods.example.GoodsCategoryExample;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.model.goods.*;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.goods.GoodsCategoryListVO;
import com.slodon.b2b2c.vo.goods.GoodsCategoryVO;
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

@Api(tags = "admin-商品分类管理")
@RestController
@RequestMapping("v3/goods/admin/goodsCategory")
public class GoodsCategoryController {

    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private GoodsCategoryBindAttributeModel goodsCategoryBindAttributeModel;
    @Resource
    private GoodsCategoryBindBrandModel goodsCategoryBindBrandModel;
    @Resource
    private GoodsAttributeModel goodsAttributeModel;
    @Resource
    private GoodsBrandModel goodsBrandModel;
    @Resource
    private GoodsModel goodsModel;

    @ApiOperation("分类列表,获取当前分类的下级分类，0代表获取所有1级分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类Id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsCategoryListVO>> getList(HttpServletRequest request,
                                                           @RequestParam(value = "categoryId") Integer categoryId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
        goodsCategoryExample.setPid(categoryId);
        List<GoodsCategory> goodsCategoryList = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample, pager);
        List<GoodsCategoryListVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsCategoryList)) {
            goodsCategoryList.forEach(goodsCategory -> {
                GoodsCategoryListVO vo = new GoodsCategoryListVO(goodsCategory);
                //查询全部商品数
                GoodsExample example = new GoodsExample();
                if (goodsCategory.getGrade() == GoodsCategoryConst.CATEGORY_GRADE_1) {
                    //查询一级分类
                    example.setCategoryId1(goodsCategory.getCategoryId());
                } else if (goodsCategory.getGrade() == GoodsCategoryConst.CATEGORY_GRADE_2) {
                    //查询二级分类
                    example.setCategoryId2(goodsCategory.getCategoryId());
                } else {
                    //查询三级分类
                    example.setCategoryId3(goodsCategory.getCategoryId());
                }
                vo.setTotalGoodsNum(goodsModel.getGoodsCount(example));
                //查询在售商品数
                example.setState(GoodsConst.GOODS_STATE_UPPER);
                vo.setOnSaleGoodsNum(goodsModel.getGoodsCount(example));
                //查询是否有子分类
                goodsCategoryExample.setPid(goodsCategory.getCategoryId());
                vo.setChildren(goodsCategoryModel.getGoodsCategoryListCount(goodsCategoryExample) > 0 ? new ArrayList<>() : null);
                this.dealGoodsCategoryBindBrandAndAttribute(vo);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取商品分类树接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pId", value = "父分类id", paramType = "query", required = true),
            @ApiImplicitParam(name = "grade", value = "查询层数", paramType = "query"),
    })
    @GetMapping("getCateTree")
    public JsonResult<List<GoodsCategoryListVO>> getCateTree(HttpServletRequest request,
                                                             @RequestParam(value = "pId") Integer pId,
                                                             @RequestParam(value = "grade") Integer grade) {

        return SldResponse.success(this.getGoodsCategoryTree(pId, grade));
    }


    /**
     * 获取商品分类树
     *
     * @param pId   父id
     * @param grade 获取级别，比如 pid=0,grade=3时，获取1、2、3级分类；pid=0,grade=2时，获取1、2级分类；pid=0,grade=1时，获取1级分类；
     * @return
     */
    private List<GoodsCategoryListVO> getGoodsCategoryTree(Integer pId, Integer grade) {
        if (grade == 0) {
            return null;
        }
        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
        goodsCategoryExample.setPid(pId);
        List<GoodsCategory> goodsCategoryList = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample, null);
        if (CollectionUtils.isEmpty(goodsCategoryList)) {
            return new ArrayList<>();
        }
        List<GoodsCategoryListVO> vos = new ArrayList<>();
        goodsCategoryList.forEach(goodsCategory -> {
            GoodsCategoryListVO vo = new GoodsCategoryListVO(goodsCategory);
            //查询全部商品数
            GoodsExample example = new GoodsExample();
            if (goodsCategory.getGrade() == GoodsCategoryConst.CATEGORY_GRADE_1) {
                //查询一级分类
                example.setCategoryId1(goodsCategory.getCategoryId());
            } else if (goodsCategory.getGrade() == GoodsCategoryConst.CATEGORY_GRADE_2) {
                //查询二级分类
                example.setCategoryId2(goodsCategory.getCategoryId());
            } else {
                //查询三级分类
                example.setCategoryId3(goodsCategory.getCategoryId());
            }
            vo.setTotalGoodsNum(goodsModel.getGoodsCount(example));
            //查询在售商品数
            example.setState(GoodsConst.GOODS_STATE_UPPER);
            vo.setOnSaleGoodsNum(goodsModel.getGoodsCount(example));
            vo.setChildren(getGoodsCategoryTree(goodsCategory.getCategoryId(), grade - 1));
            this.dealGoodsCategoryBindBrandAndAttribute(vo);
            vos.add(vo);
        });

        return vos;
    }

    /**
     * 处理三级分类绑定的品牌和属性
     *
     * @param vo
     */
    private void dealGoodsCategoryBindBrandAndAttribute(GoodsCategoryListVO vo) {
        if (vo.getGrade().equals(GoodsCategoryConst.CATEGORY_GRADE_3)) {
            //三级分类，查询绑定的品牌和属性
            GoodsCategoryBindBrandExample bindBrandExample = new GoodsCategoryBindBrandExample();
            bindBrandExample.setCategoryId(vo.getCategoryId());
            bindBrandExample.setOrderBy("bind_id asc");
            List<GoodsCategoryBindBrand> goodsCategoryBindBrandList = goodsCategoryBindBrandModel.getGoodsCategoryBindBrandList(bindBrandExample, null);
            List<GoodsCategoryListVO.GoodsBrandListVO> brandListVOS = new ArrayList<>();
            if (!CollectionUtils.isEmpty(goodsCategoryBindBrandList)) {
                goodsCategoryBindBrandList.forEach(goodsCategoryBindBrand -> {
                    GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(goodsCategoryBindBrand.getBrandId());
                    brandListVOS.add(new GoodsCategoryListVO.GoodsBrandListVO(goodsBrand));
                });
            }
            vo.setGoodsBrandList(brandListVOS);

            //属性
            GoodsCategoryBindAttributeExample bindAttributeExample = new GoodsCategoryBindAttributeExample();
            bindAttributeExample.setCategoryId(vo.getCategoryId());
            bindAttributeExample.setOrderBy("bind_id asc");
            List<GoodsCategoryBindAttribute> goodsCategoryBindAttributeList = goodsCategoryBindAttributeModel.getGoodsCategoryBindAttributeList(bindAttributeExample, null);
            List<GoodsCategoryListVO.GoodsAttributeListVO> attributeListVOS = new ArrayList<>();
            if (!CollectionUtils.isEmpty(goodsCategoryBindAttributeList)) {
                goodsCategoryBindAttributeList.forEach(goodsCategoryBindAttribute -> {
                    GoodsAttribute goodsAttribute = goodsAttributeModel.getGoodsAttributeByAttributeId(goodsCategoryBindAttribute.getAttributeId());
                    attributeListVOS.add(new GoodsCategoryListVO.GoodsAttributeListVO(goodsAttribute));
                });
            }
            vo.setGoodsAttributeList(attributeListVOS);
        }
    }


    @ApiOperation("获取分类信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类Id", paramType = "query"),
    })
    @GetMapping("getCategory")
    public JsonResult<GoodsCategoryVO> getCategory(HttpServletRequest request,
                                                   @RequestParam(value = "categoryId", required = true) Integer categoryId) {

        GoodsCategory goodsCategory = goodsCategoryModel.getGoodsCategoryByCategoryId(categoryId);
        GoodsCategoryVO goodsCategoryVO = new GoodsCategoryVO(goodsCategory);

        //查绑定的属性表
        GoodsCategoryBindAttributeExample goodsCategoryBindAttributeExample = new GoodsCategoryBindAttributeExample();
        goodsCategoryBindAttributeExample.setCategoryId(categoryId);
        List<GoodsCategoryBindAttribute> goodsCategoryBindAttributeList = goodsCategoryBindAttributeModel.getGoodsCategoryBindAttributeList(goodsCategoryBindAttributeExample, null);

        //根据属性ID 查属性名称
        List<GoodsAttribute> goodsAttributeList = new ArrayList<>();
        for (GoodsCategoryBindAttribute goodsCategoryBindAttribute : goodsCategoryBindAttributeList) {
            GoodsAttribute goodsAttribute = goodsAttributeModel.getGoodsAttributeByAttributeId(goodsCategoryBindAttribute.getAttributeId());
            if (goodsAttribute != null) {
                goodsAttributeList.add(goodsAttribute);
            }
        }
        goodsCategoryVO.setAttributeList(goodsAttributeList);

        //查绑定的品牌表
        GoodsCategoryBindBrandExample goodsCategoryBindBrandExample = new GoodsCategoryBindBrandExample();
        goodsCategoryBindBrandExample.setCategoryId(categoryId);
        List<GoodsCategoryBindBrand> goodsCategoryBindBrandList = goodsCategoryBindBrandModel.getGoodsCategoryBindBrandList(goodsCategoryBindBrandExample, null);

        //根据品牌ID 查品牌名称
        List<GoodsBrand> goodsBrandList = new ArrayList<>();
        for (GoodsCategoryBindBrand goodsCategoryBindBrand : goodsCategoryBindBrandList) {
            GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(goodsCategoryBindBrand.getBrandId());
            if (goodsBrand != null) {
                goodsBrandList.add(goodsBrand);
            }
        }
        goodsCategoryVO.setBrandList(goodsBrandList);
        return SldResponse.success(goodsCategoryVO);
    }

    @ApiOperation("新增分类")
    @OperationLogger(option = "新增分类")
    @PostMapping("add")
    public JsonResult<Integer> addCategory(HttpServletRequest request, GoodsCategoryAddDTO goodsCategoryAddDTO) {
        String logMsg = "分类名称" + goodsCategoryAddDTO.getCategoryName();
        // 验证参数是否为空
        AssertUtil.notEmpty(goodsCategoryAddDTO.getCategoryName(), "分类名称不能为空，请重试！");
        AssertUtil.notNull(goodsCategoryAddDTO.getPid(), "上级分类不能为空，请重试！");
        AssertUtil.notNull(goodsCategoryAddDTO.getSort(), "分类排序不能为空，请重试！");

        Admin admin = UserUtil.getUser(request, Admin.class);
        goodsCategoryModel.saveGoodsCategoryBindAttributeBrand(admin, goodsCategoryAddDTO);

        return SldResponse.success("添加成功", logMsg);
    }

    @ApiOperation("编辑分类")
    @OperationLogger(option = "编辑分类")
    @PostMapping("edit")
    public JsonResult editCategory(HttpServletRequest request, GoodsCategoryUpdateDTO goodsCategoryUpdateDTO) throws Exception {
        String logMsg = "分类id" + goodsCategoryUpdateDTO.getCategoryId();
        // 验证参数是否为空
        AssertUtil.notNullOrZero(goodsCategoryUpdateDTO.getCategoryId(), "分类Id不能为空，请重试！");

        Admin admin = UserUtil.getUser(request, Admin.class);
        goodsCategoryModel.updateGoodsCategoryBindAttributeBrand(admin, goodsCategoryUpdateDTO);

        return SldResponse.success("编辑成功", logMsg);
    }

    @ApiOperation("删除分类")
    @OperationLogger(option = "删除分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类Id", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteCategory(HttpServletRequest request, @RequestParam("categoryId") Integer categoryId) {

        AssertUtil.notNullOrZero(categoryId, "分类Id不能为空，请重试！");

        goodsCategoryModel.deleteGoodsCategoryBindAttributeBrand(categoryId);
        return SldResponse.success("删除分类成功");
    }
}
