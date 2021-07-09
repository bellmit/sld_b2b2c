package com.slodon.b2b2c.controller.goods.front;

import com.slodon.b2b2c.core.constant.GoodsCategoryConst;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.*;
import com.slodon.b2b2c.goods.dto.SearchConditionDTO;
import com.slodon.b2b2c.goods.example.*;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.goods.*;
import com.slodon.b2b2c.vo.goods.GoodsCategoryTreeVO;
import com.slodon.b2b2c.vo.goods.GoodsListVO;
import com.slodon.b2b2c.vo.goods.GoodsScreenListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-分类列表")
@RestController
@RequestMapping("v3/goods/front/goods/category")
public class FrontGoodsCategoryController extends BaseController {

    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private GoodsBrandModel goodsBrandModel;
    @Resource
    private GoodsAttributeModel goodsAttributeModel;
    @Resource
    private GoodsAttributeValueModel goodsAttributeValueModel;
    @Resource
    private GoodsCategoryBindBrandModel goodsCategoryBindBrandModel;
    @Resource
    private GoodsCategoryBindAttributeModel goodsCategoryBindAttributeModel;
    @Resource
    private ESGoodsModel esGoodsModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId1", value = "一级分类Id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsCategoryTreeVO>> list(HttpServletRequest request, Integer categoryId1) {
        Member member = UserUtil.getUser(request, Member.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsCategoryExample example = new GoodsCategoryExample();
        example.setPid(0);
        example.setState(GoodsCategoryConst.CATEGORY_STATE_1);
        example.setOrderBy("sort asc, create_time desc");
        List<GoodsCategory> list = goodsCategoryModel.getGoodsCategoryList(example, pager);
        if (StringUtil.isNullOrZero(categoryId1) && !CollectionUtils.isEmpty(list)) {
            categoryId1 = list.get(0).getCategoryId();
        }
        List<GoodsCategoryTreeVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (GoodsCategory goodsCategory : list) {
                GoodsCategoryTreeVO tree = new GoodsCategoryTreeVO(goodsCategory);
                if (goodsCategory.getCategoryId().equals(categoryId1)) {
                    //当前选中分类，查询其二级
                    tree = categoryTree(tree, categoryId1);

                    //查询当前一级分类下的商品列表
                    SearchConditionDTO qc = new SearchConditionDTO();
                    qc.setCategoryIds(categoryId1.toString());
                    List<GoodsListVO> goodsList = esGoodsModel.searchGoodsByES(qc, pager, member);
                    tree.setGoodsList(goodsList);
                } else {
                    //其他一级分类,只展示分类树,不展示其下商品
                    tree = categoryTree(tree, goodsCategory.getCategoryId());
                }
                if (CollectionUtils.isEmpty(tree.getChildren())) {
                    continue;
                }
                vos.add(tree);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("筛选")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "二级分类Id或三级分类Id", required = true, paramType = "query")
    })
    @GetMapping("screenList")
    public JsonResult<GoodsScreenListVO> screenList(HttpServletRequest request, Integer categoryId) {
        GoodsCategory goodsCategory = goodsCategoryModel.getGoodsCategoryByCategoryId(categoryId);
        AssertUtil.notNull(goodsCategory, "获取分类信息为空");
        GoodsScreenListVO vo = new GoodsScreenListVO();
        if (goodsCategory.getGrade() == GoodsCategoryConst.CATEGORY_GRADE_1) {
            return SldResponse.success(vo);
        } else {
            GoodsCategoryExample example = new GoodsCategoryExample();
            example.setState(GoodsCategoryConst.CATEGORY_STATE_1);
            example.setOrderBy("sort asc, create_time desc");
            List<GoodsScreenListVO.CategoryVO> categoryVOS = new ArrayList<>();
            List<GoodsCategory> categoryList;
            if (goodsCategory.getGrade() == GoodsCategoryConst.CATEGORY_GRADE_2) {
                //查询二级下的三级分类
                example.setPid(goodsCategory.getCategoryId());
                categoryList = goodsCategoryModel.getGoodsCategoryList(example, null);
                if (!CollectionUtils.isEmpty(categoryList)) {
                    categoryList.forEach(category -> {
                        categoryVOS.add(new GoodsScreenListVO.CategoryVO(category.getCategoryId(), category.getCategoryName()));
                    });
                }
            } else if (goodsCategory.getGrade() == GoodsCategoryConst.CATEGORY_GRADE_3) {
                //三级查询同级分类
                example.setPid(goodsCategory.getPid());
                categoryList = goodsCategoryModel.getGoodsCategoryList(example, null);
                if (!CollectionUtils.isEmpty(categoryList)) {
                    categoryList.forEach(category -> {
                        categoryVOS.add(new GoodsScreenListVO.CategoryVO(category.getCategoryId(), category.getCategoryName()));
                    });
                }
            }
            vo.setCategoryList(categoryVOS);
        }
        //查询分类绑定的品牌
        GoodsCategoryBindBrandExample example = new GoodsCategoryBindBrandExample();
        example.setCategoryId(goodsCategory.getCategoryId());
        List<GoodsCategoryBindBrand> categoryBindBrandList = goodsCategoryBindBrandModel.getGoodsCategoryBindBrandList(example, null);
        List<GoodsScreenListVO.BrandVO> brandVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryBindBrandList)) {
            categoryBindBrandList.forEach(categoryBindBrand -> {
                GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(categoryBindBrand.getBrandId());
                AssertUtil.notNull(goodsBrand, "品牌不存在");
                brandVOS.add(new GoodsScreenListVO.BrandVO(goodsBrand));
            });
        }
        vo.setBrandList(brandVOS);
        //查询分类绑定的属性
        GoodsCategoryBindAttributeExample attributeExample = new GoodsCategoryBindAttributeExample();
        attributeExample.setCategoryId(goodsCategory.getCategoryId());
        List<GoodsCategoryBindAttribute> categoryBindAttributeList = goodsCategoryBindAttributeModel.getGoodsCategoryBindAttributeList(attributeExample, null);
        List<GoodsScreenListVO.AttributeVO> attributeVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryBindAttributeList)) {
            StringBuffer attributeIds = new StringBuffer();
            categoryBindAttributeList.forEach(goodsCategoryBindAttribute -> {
                attributeIds.append(",").append(goodsCategoryBindAttribute.getAttributeId());
            });
            //查属性表中的对应属性
            GoodsAttributeExample goodsAttributeExample = new GoodsAttributeExample();
            goodsAttributeExample.setAttributeIdIn(attributeIds.toString().substring(1));
            goodsAttributeExample.setIsShow(GoodsConst.IS_ATTRIBUTE_YES);
            goodsAttributeExample.setOrderBy("sort asc, create_time desc");
            List<GoodsAttribute> goodsAttributeList = goodsAttributeModel.getGoodsAttributeList(goodsAttributeExample, null);
            goodsAttributeList.forEach(goodsAttribute -> {
                GoodsScreenListVO.AttributeVO attributeVO = new GoodsScreenListVO.AttributeVO(goodsAttribute.getAttributeId(), goodsAttribute.getAttributeName());
                //查询属性值
                GoodsAttributeValueExample valueExample = new GoodsAttributeValueExample();
                valueExample.setAttributeId(goodsAttribute.getAttributeId());
                List<GoodsAttributeValue> attributeValueList = goodsAttributeValueModel.getGoodsAttributeValueList(valueExample, null);
                AssertUtil.notEmpty(attributeValueList, "属性值不存在");
                List<GoodsScreenListVO.AttributeVO.AttributeValueVO> valueVOS = new ArrayList<>();
                attributeValueList.forEach(attributeValue -> {
                    valueVOS.add(new GoodsScreenListVO.AttributeVO.AttributeValueVO(attributeValue.getValueId(), attributeValue.getAttributeValue()));
                });
                attributeVO.setAttributeValueList(valueVOS);
                attributeVOS.add(attributeVO);
            });
        }
        vo.setAttributeList(attributeVOS);
        return SldResponse.success(vo);
    }

    /**
     * 构造分类树
     *
     * @param tree
     * @param categoryId
     * @return
     */
    public GoodsCategoryTreeVO categoryTree(GoodsCategoryTreeVO tree, Integer categoryId) {
        //其他一级分类,只展示分类树,不展示其下商品
        //查询其二级
        GoodsCategoryExample example2 = new GoodsCategoryExample();
        example2.setPid(categoryId);
        example2.setState(GoodsCategoryConst.CATEGORY_STATE_1);
        example2.setOrderBy("sort asc, create_time desc");
        List<GoodsCategory> list2 = goodsCategoryModel.getGoodsCategoryList(example2, null);
        //构造二级列表
        List<GoodsCategoryTreeVO> vos2 = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list2)) {
            for (GoodsCategory goodsCategory2 : list2) {
                GoodsCategoryTreeVO tree2 = new GoodsCategoryTreeVO(goodsCategory2);
                //没有图片就用默认图片
                if (StringUtils.isEmpty(goodsCategory2.getCategoryImage())) {
                    tree2.setCategoryImage(FileUrlUtil.getFileUrl(stringRedisTemplate.opsForValue().get("default_goods_cate_grade2_image"), null));
                }
                //查询三级分类列表
                GoodsCategoryExample example3 = new GoodsCategoryExample();
                example3.setPid(goodsCategory2.getCategoryId());
                example3.setState(GoodsCategoryConst.CATEGORY_STATE_1);
                example3.setOrderBy("sort asc, create_time desc");
                List<GoodsCategory> list3 = goodsCategoryModel.getGoodsCategoryList(example3, null);
                //构造每个二级的三级列表
                List<GoodsCategoryTreeVO> vos3 = new ArrayList<>();
                if (!CollectionUtils.isEmpty(list3)) {
                    list3.forEach(goodsCategory3 -> {
                        GoodsCategoryTreeVO tree3 = new GoodsCategoryTreeVO(goodsCategory3);
                        //没有图片就用默认图片
                        if (StringUtils.isEmpty(goodsCategory3.getCategoryImage())) {
                            tree3.setCategoryImage(FileUrlUtil.getFileUrl(stringRedisTemplate.opsForValue().get("default_goods_cate_grade3_image"), null));
                        }
                        vos3.add(tree3);
                    });
                }
                if (CollectionUtils.isEmpty(vos3)) {
                    continue;
                }
                tree2.setChildren(vos3);
                vos2.add(tree2);
            }
        }
        tree.setChildren(vos2);
        return tree;
    }
}
