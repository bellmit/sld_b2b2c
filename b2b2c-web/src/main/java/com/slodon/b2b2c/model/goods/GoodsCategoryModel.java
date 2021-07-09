package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.constant.GoodsCategoryConst;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsCategoryReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsCategoryWriteMapper;
import com.slodon.b2b2c.goods.dto.GoodsCategoryAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsCategoryUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsCategoryExample;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.goods.GoodsCategoryListVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class GoodsCategoryModel {

    @Resource
    private GoodsCategoryReadMapper goodsCategoryReadMapper;
    @Resource
    private GoodsCategoryWriteMapper goodsCategoryWriteMapper;

    @Resource
    private GoodsCategoryBindAttributeModel goodsCategoryBindAttributeModel;

    @Resource
    private GoodsCategoryBindBrandModel goodsCategoryBindBrandModel;

    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private GoodsBrandModel goodsBrandModel;
    /**
     * 新增商品分类
     *
     * @param goodsCategory
     * @return
     */
    public Integer saveGoodsCategory(GoodsCategory goodsCategory) {
        int count = goodsCategoryWriteMapper.insert(goodsCategory);
        if (count == 0) {
            throw new MallException("添加商品分类失败，请重试");
        }
        return count;
    }

    /**
     * 查询商品分类数
     *
     * @return
     */
    public Integer getGoodsCategoryListCount(GoodsCategoryExample example) {
        return goodsCategoryReadMapper.countByExample(example);
    }

    /**
     * 新增商品分类
     *
     * @param goodsCategoryAddDTO
     * @param admin
     * @return
     */
    private Integer saveGoodsCategory(Admin admin, GoodsCategoryAddDTO goodsCategoryAddDTO) {
        // 验证参数是否为空
        if (goodsCategoryAddDTO == null) {
            throw new MallException("分类信息不能为空，请重试！");
        } else if (goodsCategoryAddDTO.getCategoryName() == null) {
            throw new MallException("分类名称不能为空，请重试！");
        } else if (goodsCategoryAddDTO.getPid() == null) {
            throw new MallException("上级分类不能为空，请重试！");
        } else if (goodsCategoryAddDTO.getSort() == null) {
            throw new MallException("分类排序不能为空，请重试！");
        }
        //判断该pid下的子分类中有没有分类名称重复的
        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
        goodsCategoryExample.setPid(goodsCategoryAddDTO.getPid());
        goodsCategoryExample.setCategoryAlias(goodsCategoryAddDTO.getCategoryAlias());
        goodsCategoryExample.setCategoryName(goodsCategoryAddDTO.getCategoryName());
        List<GoodsCategory> goodsCategoryList = goodsCategoryReadMapper.listByExample(goodsCategoryExample);
        if (!CollectionUtils.isEmpty(goodsCategoryList)) {
            throw new MallException("分类名称已存在，请重新填写");
        }

        //获取上级分类
        GoodsCategory parentGoodsCategory = getGoodsCategoryByCategoryId(goodsCategoryAddDTO.getPid());

        //插入分类表
        GoodsCategory goodsCategoryInsert = new GoodsCategory();
        goodsCategoryInsert.setCategoryName(goodsCategoryAddDTO.getCategoryName());
        goodsCategoryInsert.setPid(goodsCategoryAddDTO.getPid());
        String path;
        Integer grade;
        if (parentGoodsCategory == null) {
            path = "/";
            grade = 1;
        } else {
            path = parentGoodsCategory.getPath();
            if (parentGoodsCategory.getPid() != 0) {
                path += "/";
            }
            path += goodsCategoryAddDTO.getPid();
            grade = parentGoodsCategory.getGrade() + 1;
        }
        goodsCategoryInsert.setPath(path);
        goodsCategoryInsert.setScaling(goodsCategoryAddDTO.getScaling());
        goodsCategoryInsert.setCreateAdminId(admin.getAdminId());
        goodsCategoryInsert.setUpdateAdminId(admin.getAdminId());
        goodsCategoryInsert.setCreateTime(new Date());
        goodsCategoryInsert.setUpdateTime(new Date());
        goodsCategoryInsert.setSort(goodsCategoryAddDTO.getSort());
        goodsCategoryInsert.setState(1);
        goodsCategoryInsert.setGrade(grade);
        goodsCategoryInsert.setMobileImage(goodsCategoryAddDTO.getMobileImage());
        goodsCategoryInsert.setCategoryImage(goodsCategoryAddDTO.getCategoryImage());
        goodsCategoryInsert.setRecommendPicture(goodsCategoryAddDTO.getRecommendPicture());
        int count;
        count = goodsCategoryWriteMapper.insert(goodsCategoryInsert);
        if (count == 0) {
            throw new MallException("添加分类表失败，请重试");
        }

        return goodsCategoryInsert.getCategoryId();
    }

    /**
     * 新增商品分类 并绑定品牌和属性
     *
     * @param goodsCategoryAddDTO
     * @param admin
     * @return
     */
    @Transactional
    public void saveGoodsCategoryBindAttributeBrand(Admin admin, GoodsCategoryAddDTO goodsCategoryAddDTO) {

        Integer categoryId = this.saveGoodsCategory(admin, goodsCategoryAddDTO);

        //保存绑定的属性
        goodsCategoryBindAttributeModel.saveGoodsCategoryBindAttributeByCategory(categoryId, goodsCategoryAddDTO.getBindAttributes());
        //保存绑定的品牌
        goodsCategoryBindBrandModel.saveGoodsCategoryBindAttributeByCategory(categoryId, goodsCategoryAddDTO.getBindBrands());
    }

    /**
     * 根据categoryId删除商品分类
     *
     * @param categoryId categoryId
     * @return
     */
    public Integer deleteGoodsCategory(Integer categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsCategoryWriteMapper.deleteByPrimaryKey(categoryId);
        if (count == 0) {
            log.error("根据categoryId：" + categoryId + "删除商品分类失败");
            throw new MallException("删除商品分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据categoryId删除商品分类 同时删除属性，品牌绑定关系
     *
     * @param categoryId categoryId
     * @return
     */
    @Transactional
    public JsonResult deleteGoodsCategoryBindAttributeBrand(Integer categoryId) {
        //判断该分类下是否绑定商品
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setStateNotEquals(GoodsConst.GOODS_STATE_DELETE);
        //先查询该分类分类级别
        GoodsCategory goodsCategoryDB = goodsCategoryReadMapper.getByPrimaryKey(categoryId);
        Integer grade = goodsCategoryDB.getGrade();
        if (grade == GoodsCategoryConst.CATEGORY_GRADE_1) {
            goodsExample.setCategoryId1(goodsCategoryDB.getCategoryId());
        } else if (grade == GoodsCategoryConst.CATEGORY_GRADE_2) {
            goodsExample.setCategoryId2(goodsCategoryDB.getCategoryId());
        } else {
            goodsExample.setCategoryId3(goodsCategoryDB.getCategoryId());
        }
        List<Goods> goodsList = goodsReadMapper.listByExample(goodsExample);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(goodsList), "已绑定商品的分类不能删除");

        GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
        goodsCategoryExample.setPid(categoryId);
        List<GoodsCategory> goodsCategoryList = getGoodsCategoryList(goodsCategoryExample, null);
        List<GoodsCategoryListVO> vos = new ArrayList<>();
        generateTree(vos, goodsCategoryList, 0);
        deleteCategoryList(vos);
        deleteGoodsCategory(categoryId);
        //删除旧的属性绑定关系
        goodsCategoryBindAttributeModel.deleteGoodsCategoryBindAttributeByCategory(categoryId);
        //删除旧的品牌绑定关系
        goodsCategoryBindBrandModel.deleteGoodsCategoryBindBrandByCategory(categoryId);
        //删除该分类绑定的品牌
        goodsBrandModel.delGoodsBrandByCategoryId(categoryId);
        return SldResponse.success("删除分类成功");
    }

    private void deleteCategoryList(List<GoodsCategoryListVO> vos) {
        for (GoodsCategoryListVO vo : vos) {
            deleteGoodsCategory(vo.getCategoryId());
            //删除旧的属性绑定关系
            goodsCategoryBindAttributeModel.deleteGoodsCategoryBindAttributeByCategory(vo.getCategoryId());
            //删除旧的品牌绑定关系
            goodsCategoryBindBrandModel.deleteGoodsCategoryBindBrandByCategory(vo.getCategoryId());
            if (vo.getChildren().size() > 0) {
                deleteCategoryList(vo.getChildren());
            }
        }
    }

    /**
     * 根据categoryId更新商品分类
     *
     * @param goodsCategory
     * @return
     */
    public Integer updateGoodsCategory(GoodsCategory goodsCategory) {
        if (StringUtils.isEmpty(goodsCategory.getCategoryId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsCategoryWriteMapper.updateByPrimaryKeySelective(goodsCategory);
        if (count == 0) {
            log.error("根据categoryId：" + goodsCategory.getCategoryId() + "更新商品分类失败");
            throw new MallException("更新商品分类失败,请重试");
        }
        return count;
    }

    /**
     * 根据categoryId更新商品分类
     *
     * @param goodsCategoryUpdateDTO
     * @param admin
     */
    private void updateGoodsCategory(Admin admin, GoodsCategoryUpdateDTO goodsCategoryUpdateDTO) {

        if (!StringUtils.isEmpty(goodsCategoryUpdateDTO.getCategoryName())) {
            AssertUtil.notNull(goodsCategoryUpdateDTO.getPid(), "请选择父分类");
            //判断该pid下的子分类中有没有分类名称重复的
            GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
            goodsCategoryExample.setPid(goodsCategoryUpdateDTO.getPid());
            goodsCategoryExample.setCategoryIdNotEquals(goodsCategoryUpdateDTO.getCategoryId());
            goodsCategoryExample.setCategoryName(goodsCategoryUpdateDTO.getCategoryName());
            List<GoodsCategory> goodsCategoryList = goodsCategoryReadMapper.listByExample(goodsCategoryExample);
            if (!CollectionUtils.isEmpty(goodsCategoryList)) {
                throw new MallException("分类名称已存在，请重新填写");
            }
        }

        GoodsCategory goodsCategoryUpdate = new GoodsCategory();
        goodsCategoryUpdate.setCategoryId(goodsCategoryUpdateDTO.getCategoryId());
        goodsCategoryUpdate.setCategoryName(goodsCategoryUpdateDTO.getCategoryName());
        goodsCategoryUpdate.setScaling(goodsCategoryUpdateDTO.getScaling());
        goodsCategoryUpdate.setUpdateAdminId(admin.getAdminId());
        goodsCategoryUpdate.setUpdateTime(new Date());
        goodsCategoryUpdate.setSort(goodsCategoryUpdateDTO.getSort());
        goodsCategoryUpdate.setMobileImage(goodsCategoryUpdateDTO.getMobileImage());
        goodsCategoryUpdate.setCategoryImage(goodsCategoryUpdateDTO.getCategoryImage());
        goodsCategoryUpdate.setRecommendPicture(goodsCategoryUpdateDTO.getRecommendPicture());
        int count;
        count = goodsCategoryWriteMapper.updateByPrimaryKeySelective(goodsCategoryUpdate);
        if (count == 0) {
            throw new MallException("编辑分类表失败，请重试");
        }
    }


    /**
     * 根据categoryId更新商品分类 并更新绑定品牌和属性
     *
     * @param goodsCategoryUpdateDTO
     * @param admin
     * @return
     */
    @Transactional
    public void updateGoodsCategoryBindAttributeBrand(Admin admin, GoodsCategoryUpdateDTO goodsCategoryUpdateDTO) {
        updateGoodsCategory(admin, goodsCategoryUpdateDTO);
        //删除旧的属性绑定关系
        goodsCategoryBindAttributeModel.deleteGoodsCategoryBindAttributeByCategory(goodsCategoryUpdateDTO.getCategoryId());

        //保存新绑定的属性
        goodsCategoryBindAttributeModel.saveGoodsCategoryBindAttributeByCategory(goodsCategoryUpdateDTO.getCategoryId(), goodsCategoryUpdateDTO.getBindAttributes());

        //删除旧的品牌绑定关系
        goodsCategoryBindBrandModel.deleteGoodsCategoryBindBrandByCategory(goodsCategoryUpdateDTO.getCategoryId());

        //保存绑定的品牌
        goodsCategoryBindBrandModel.saveGoodsCategoryBindAttributeByCategory(goodsCategoryUpdateDTO.getCategoryId(), goodsCategoryUpdateDTO.getBindBrands());
    }

    /**
     * 根据categoryId获取商品分类详情
     *
     * @param categoryId categoryId
     * @return
     */
    public GoodsCategory getGoodsCategoryByCategoryId(Integer categoryId) {
        return goodsCategoryReadMapper.getByPrimaryKey(categoryId);
    }

    /**
     * 根据条件获取商品分类列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsCategory> getGoodsCategoryList(GoodsCategoryExample example, PagerInfo pager) {
        List<GoodsCategory> goodsCategoryList;
        if (pager != null) {
            pager.setRowsCount(goodsCategoryReadMapper.countByExample(example));
            goodsCategoryList = goodsCategoryReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsCategoryList = goodsCategoryReadMapper.listByExample(example);
        }
        return goodsCategoryList;
    }

    /**
     * 根据 父类ID PID获取商品分类列表
     *
     * @param Pid 查询Pid
     * @return
     */
    public List<GoodsCategory> getGoodsCategoryListByPid(Integer Pid) {
        {
            GoodsCategoryExample goodsCategoryExample = new GoodsCategoryExample();
            goodsCategoryExample.setPid(Pid);
            List<GoodsCategory> goodsCategoryList = goodsCategoryReadMapper.listByExample(goodsCategoryExample);
            return goodsCategoryList;
        }
    }

    /**
     * //根据三级分类ID获取上级分类
     *
     * @param categoryId3 三级分类ID
     * @return {一级分类，二级分类，三级分类}
     */

    public List<GoodsCategory> getGoodsCategoryListByCategoryId3(Integer categoryId3) {
        GoodsCategory goodsCategory3 = getGoodsCategoryByCategoryId(categoryId3);
        if (goodsCategory3 == null) {
            throw new MallException("商品分类信息有误，请重试！");
        }
        List<GoodsCategory> goodsCategoryList = new ArrayList<>();
        //二级分类
        Integer categoryId2 = goodsCategory3.getPid();
        GoodsCategory goodsCategory2 = getGoodsCategoryByCategoryId(categoryId2);
        //一级分类
        Integer categoryId1 = goodsCategory2.getPid();
        GoodsCategory goodsCategory1 = getGoodsCategoryByCategoryId(categoryId1);

        goodsCategoryList.add(goodsCategory1);
        goodsCategoryList.add(goodsCategory2);
        goodsCategoryList.add(goodsCategory3);
        return goodsCategoryList;
    }

    /**
     *分类列表生成树形结构
     */
    /**
     * 递归生成树
     *
     * @param treeList
     * @param data
     * @return
     */
    public List<GoodsCategoryListVO> generateTree(List<GoodsCategoryListVO> treeList, List<GoodsCategory> data, Integer grade) {
        if (grade < 3) {
            for (GoodsCategory goodsCategory : data) {
                GoodsCategoryListVO tree = new GoodsCategoryListVO(goodsCategory);
                tree.setCategoryId(goodsCategory.getCategoryId());
                tree.setPid(goodsCategory.getPid());
                tree.setCategoryName(goodsCategory.getCategoryName());
                tree.setSort(goodsCategory.getSort());
                tree.setScaling(goodsCategory.getScaling());
                tree.setGrade(goodsCategory.getGrade());
                tree.setChildren(generateTree(new ArrayList<>(), this.getGoodsCategoryListByPid(goodsCategory.getCategoryId()), goodsCategory.getGrade()));
                treeList.add(tree);
            }
        }
        return treeList;
    }
}