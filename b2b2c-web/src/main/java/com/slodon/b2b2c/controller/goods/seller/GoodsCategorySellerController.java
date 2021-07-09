package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.core.constant.GoodsCategoryConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.example.GoodsCategoryExample;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.model.goods.GoodsCategoryModel;
import com.slodon.b2b2c.model.seller.StoreBindCategoryModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.example.StoreBindCategoryExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreBindCategory;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.GoodsCategoryListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(tags = "seller商品分类")
@RestController
@RequestMapping("v3/goods/seller/goodsCategory")
public class GoodsCategorySellerController {

    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private StoreBindCategoryModel storeBindCategoryModel;
    @Resource
    private StoreModel storeModel;

    @ApiOperation("分类列表,获取当前分类的下级分类，0代表获取所有1级分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类Id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsCategoryListVO>> getList(HttpServletRequest request,
                                                           @RequestParam(value = "categoryId", required = true) Integer categoryId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
        goodsCategoryExample.setPid(categoryId);
        goodsCategoryExample.setPager(pager);
        goodsCategoryExample.setOrderBy("sort asc");
        List<GoodsCategory> goodsCategoryList = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample, pager);
        List<GoodsCategoryListVO> vos = new ArrayList<>();
        AssertUtil.notNull(goodsCategoryList, "未查询到分类");
        for (GoodsCategory goodsCategory : goodsCategoryList) {
            vos.add(new GoodsCategoryListVO(goodsCategory));
        }
        return SldResponse.success(new PageVO<>(vos, goodsCategoryExample.getPager()));
    }

    @ApiOperation("逐级获取分类列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类Id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("listByPId")
    public JsonResult<PageVO<GoodsCategoryListVO>> listByPId(HttpServletRequest request,
                                                             @RequestParam(value = "categoryId", required = true) Integer categoryId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
        goodsCategoryExample.setPid(categoryId);
        goodsCategoryExample.setPager(pager);
        List<GoodsCategory> goodsCategoryList = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample, pager);
        List<GoodsCategoryListVO> vos = new ArrayList<>();
        AssertUtil.notNull(goodsCategoryList, "未查询到分类");
        for (GoodsCategory goodsCategory : goodsCategoryList) {
            GoodsCategoryListVO vo = new GoodsCategoryListVO(goodsCategory);
            //查询是否有子分类
            goodsCategoryExample.setPid(goodsCategory.getCategoryId());
            vo.setChildren(goodsCategoryModel.getGoodsCategoryListCount(goodsCategoryExample) > 0 ? new ArrayList<>() : null);
            vos.add(vo);
        }
        return SldResponse.success(new PageVO<>(vos, goodsCategoryExample.getPager()));
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
        goodsCategoryExample.setState(GoodsCategoryConst.CATEGORY_STATE_1);
        goodsCategoryExample.setOrderBy("sort asc");
        List<GoodsCategory> goodsCategoryList = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample, null);
        if (CollectionUtils.isEmpty(goodsCategoryList)) {
            return new ArrayList<>();
        }
        List<GoodsCategoryListVO> vos = new ArrayList<>();
        goodsCategoryList.forEach(goodsCategory -> {
            GoodsCategoryListVO vo = new GoodsCategoryListVO(goodsCategory);
            vo.setChildren(getGoodsCategoryTree(goodsCategory.getCategoryId(), grade - 1));
            vos.add(vo);
        });

        return vos;
    }

    @ApiOperation("获取分类列表接口")
    @GetMapping("getCateList")
    public JsonResult<List<GoodsCategoryListVO>> getCateList(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        List<GoodsCategoryListVO> vos = new ArrayList<>();
        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
        goodsCategoryExample.setState(GoodsCategoryConst.CATEGORY_STATE_1);
        goodsCategoryExample.setGrade(3);
        List<GoodsCategory> thirdList = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample, null);
        if (!CollectionUtils.isEmpty(thirdList)) {
            Map<Integer, List<GoodsCategoryListVO>> secondMap = new HashedMap();
            thirdList.forEach(goodsCategory -> {
                StoreBindCategoryExample bindCategoryExample = new StoreBindCategoryExample();
                bindCategoryExample.setGoodsCategoryId3(goodsCategory.getCategoryId());
                bindCategoryExample.setStoreId(vendor.getStoreId());
                bindCategoryExample.setState(StoreConst.STORE_CATEGORY_STATE_PASS);
                List<StoreBindCategory> storeBindCategoryList = storeBindCategoryModel.getStoreBindCategoryList(bindCategoryExample, null);
                if (CollectionUtils.isEmpty(storeBindCategoryList)) {
                    if (secondMap.containsKey(goodsCategory.getPid())) {
                        secondMap.get(goodsCategory.getPid()).add(new GoodsCategoryListVO(goodsCategory));
                    } else {
                        List<GoodsCategoryListVO> thirdVos = new ArrayList<>();
                        thirdVos.add(new GoodsCategoryListVO(goodsCategory));
                        secondMap.put(goodsCategory.getPid(), thirdVos);
                    }
                }

            });
            List<GoodsCategoryListVO> secondVos = new ArrayList<>();
            secondMap.forEach((cateId2, cate3List) -> {
                GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(cateId2);
                GoodsCategoryListVO vo = new GoodsCategoryListVO(goodsCategory2);
                vo.setChildren(cate3List);
                secondVos.add(vo);
            });
            //一级id和二级列表
            Map<Integer, List<GoodsCategoryListVO>> thirdMap = new HashedMap();
            secondVos.forEach(categoryListVO -> {
                if (thirdMap.containsKey(categoryListVO.getPid())) {
                    thirdMap.get(categoryListVO.getPid()).add(categoryListVO);
                } else {
                    ArrayList<GoodsCategoryListVO> listVOS = new ArrayList<>();
                    listVOS.add(categoryListVO);
                    thirdMap.put(categoryListVO.getPid(), listVOS);
                }
            });
            thirdMap.forEach((cateId1, cate2List) -> {
                GoodsCategory goodCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(cateId1);
                GoodsCategoryListVO vo = new GoodsCategoryListVO(goodCategory1);
                vo.setChildren(cate2List);
                vos.add(vo);
            });
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("获取发布商品分类列表")
    @GetMapping("cateList")
    public JsonResult<List<GoodsCategoryListVO>> cateList(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //获取店铺信息
        Store storeDb = storeModel.getStoreByStoreId(vendor.getStoreId());
        AssertUtil.notNull(storeDb, "未获取到店铺信息");
        //根据店铺类型获取分类
        List<GoodsCategoryListVO> vos = new ArrayList<>();
        List<GoodsCategory> thirdList = new ArrayList<>();
        if (storeDb.getIsOwnStore().equals(StoreConst.IS_OWN_STORE)) {
            //自营店铺，获取所有可用的分类
            //获取一级分类列表
            GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
            goodsCategoryExample.setState(GoodsCategoryConst.CATEGORY_STATE_1);
            goodsCategoryExample.setGrade(1);
            goodsCategoryExample.setOrderBy("sort asc");
            List<GoodsCategory> cates1 = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample, null);
            //遍历一级分类列表，根据pid获取二级分类列表
            if (!CollectionUtils.isEmpty(cates1)) {
                for (GoodsCategory goodsCategory : cates1) {
                    GoodsCategoryListVO vo = new GoodsCategoryListVO(goodsCategory);
                    GoodsCategoryExample goodsCategoryExample2 = new GoodsCategoryExample();
                    goodsCategoryExample2.setPid(goodsCategory.getCategoryId());
                    goodsCategoryExample2.setGrade(2);
                    goodsCategoryExample2.setState(GoodsCategoryConst.CATEGORY_STATE_1);
                    goodsCategoryExample2.setOrderBy("sort asc");
                    List<GoodsCategory> cates2 = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample2, null);
                    List<GoodsCategoryListVO> secondvos = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(cates2)) {
                        for (GoodsCategory category : cates2) {
                            GoodsCategoryListVO secondvo = new GoodsCategoryListVO(category);
                            GoodsCategoryExample goodsCategoryExample3 = new GoodsCategoryExample();
                            goodsCategoryExample3.setPid(category.getCategoryId());
                            goodsCategoryExample3.setGrade(3);
                            goodsCategoryExample3.setState(GoodsCategoryConst.CATEGORY_STATE_1);
                            goodsCategoryExample3.setOrderBy("sort asc");
                            List<GoodsCategory> cates3 = goodsCategoryModel.getGoodsCategoryList(goodsCategoryExample3, null);
                            List<GoodsCategoryListVO> thirdvos = new ArrayList<>();
                            if (!CollectionUtils.isEmpty(cates3)) {
                                for (GoodsCategory category3 : cates3) {
                                    GoodsCategoryListVO thirdvo = new GoodsCategoryListVO(category3);
                                    thirdvos.add(thirdvo);
                                }
                            }
                            secondvo.setChildren(thirdvos);
                            secondvos.add(secondvo);
                        }
                    }
                    vo.setChildren(secondvos);
                    vos.add(vo);
                }
            }
        } else {
            //入驻店铺，获取审核通过的分类
            StoreBindCategoryExample example = new StoreBindCategoryExample();
            example.setStoreId(storeDb.getStoreId());
            example.setState(StoreConst.STORE_CATEGORY_STATE_PASS);
            List<StoreBindCategory> storeBindCategoryList = storeBindCategoryModel.getStoreBindCategoryList(example, null);
            if (!CollectionUtils.isEmpty(storeBindCategoryList)) {
                for (StoreBindCategory storeBindCategory : storeBindCategoryList) {
                    GoodsCategory category3 = goodsCategoryModel.getGoodsCategoryByCategoryId(storeBindCategory.getGoodsCategoryId3());
                    thirdList.add(category3);
                }
            }
            if (!CollectionUtils.isEmpty(thirdList)) {
                Map<Integer, List<GoodsCategoryListVO>> secondMap = new HashedMap();
                thirdList.forEach(goodsCategory -> {
                    if (secondMap.containsKey(goodsCategory.getPid())) {
                        secondMap.get(goodsCategory.getPid()).add(new GoodsCategoryListVO(goodsCategory));
                    } else {
                        List<GoodsCategoryListVO> thirdVos = new ArrayList<>();
                        thirdVos.add(new GoodsCategoryListVO(goodsCategory));
                        secondMap.put(goodsCategory.getPid(), thirdVos);
                    }
                });
                List<GoodsCategoryListVO> secondVos = new ArrayList<>();
                secondMap.forEach((cateId2, cate3List) -> {
                    GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(cateId2);
                    GoodsCategoryListVO vo = new GoodsCategoryListVO(goodsCategory2);
                    vo.setChildren(cate3List);
                    secondVos.add(vo);
                });
                //一级id和二级列表
                Map<Integer, List<GoodsCategoryListVO>> thirdMap = new HashedMap();
                secondVos.forEach(categoryListVO -> {
                    if (thirdMap.containsKey(categoryListVO.getPid())) {
                        thirdMap.get(categoryListVO.getPid()).add(categoryListVO);
                    } else {
                        ArrayList<GoodsCategoryListVO> listVOS = new ArrayList<>();
                        listVOS.add(categoryListVO);
                        thirdMap.put(categoryListVO.getPid(), listVOS);
                    }
                });
                thirdMap.forEach((cateId1, cate2List) -> {
                    GoodsCategory goodCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(cateId1);
                    GoodsCategoryListVO vo = new GoodsCategoryListVO(goodCategory1);
                    vo.setChildren(cate2List);
                    vos.add(vo);
                });
            }
        }
        return SldResponse.success(vos);
    }
}
