package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsCategoryBindBrandReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsCategoryBindBrandWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.example.GoodsCategoryBindBrandExample;
import com.slodon.b2b2c.goods.pojo.GoodsCategoryBindBrand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsCategoryBindBrandModel {
    @Resource
    private GoodsCategoryBindBrandReadMapper goodsCategoryBindBrandReadMapper;

    @Resource
    private GoodsCategoryBindBrandWriteMapper goodsCategoryBindBrandWriteMapper;

    /**
     * 新增商品分类与品牌定关系表
     *
     * @param goodsCategoryBindBrand
     * @return
     */
    public Integer saveGoodsCategoryBindBrand(GoodsCategoryBindBrand goodsCategoryBindBrand) {
        int count = goodsCategoryBindBrandWriteMapper.insert(goodsCategoryBindBrand);
        if (count == 0) {
            throw new MallException("添加商品分类与品牌定关系表失败，请重试");
        }
        return count;
    }


    /**
     * 新增与CategoryID绑定的多个类型与品牌绑定关系表
     *
     * @param categoryId
     * @param brands 多个以，分割的品牌
     * @return
     */
    public Integer saveGoodsCategoryBindAttributeByCategory(Integer categoryId,String brands) {
        int count=0;
        if(!StringUtils.isEmpty(brands)) {
            String[] bindBrandsArray = brands.split(",");
            for (int i = 0; i < bindBrandsArray.length; i++) {
                GoodsCategoryBindBrand goodsCategoryBindBrand = new GoodsCategoryBindBrand();
                goodsCategoryBindBrand.setBrandId(Integer.valueOf(bindBrandsArray[i]));
                goodsCategoryBindBrand.setCategoryId(categoryId);
                count += goodsCategoryBindBrandWriteMapper.insert(goodsCategoryBindBrand);
            }
        }
        return count;
    }

    /**
     * 根据bindId删除商品分类与品牌定关系表
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteGoodsCategoryBindBrand(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsCategoryBindBrandWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除商品分类与品牌定关系表失败");
            throw new MallException("删除商品分类与品牌定关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据 CategoryId 删除商品分类与品牌定关系表
     *
     * @param categoryId categoryId
     * @return
     */
    public Integer deleteGoodsCategoryBindBrandByCategory(Integer categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new MallException("请选择要删除的数据");
        }
        GoodsCategoryBindBrandExample goodsCategoryBindBrandExample=new GoodsCategoryBindBrandExample();
        goodsCategoryBindBrandExample.setCategoryId(categoryId);
        int count = goodsCategoryBindBrandWriteMapper.deleteByExample(goodsCategoryBindBrandExample);
        return count;
    }

    /**
     * 根据bindId更新商品分类与品牌定关系表
     *
     * @param goodsCategoryBindBrand
     * @return
     */
    public Integer updateGoodsCategoryBindBrand(GoodsCategoryBindBrand goodsCategoryBindBrand) {
        if (StringUtils.isEmpty(goodsCategoryBindBrand.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsCategoryBindBrandWriteMapper.updateByPrimaryKeySelective(goodsCategoryBindBrand);
        if (count == 0) {
            log.error("根据bindId：" + goodsCategoryBindBrand.getBindId() + "更新商品分类与品牌定关系表失败");
            throw new MallException("更新商品分类与品牌定关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取商品分类与品牌定关系表详情
     *
     * @param bindId bindId
     * @return
     */
    public GoodsCategoryBindBrand getGoodsCategoryBindBrandByBindId(Integer bindId) {
        return goodsCategoryBindBrandReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取商品分类与品牌定关系表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsCategoryBindBrand> getGoodsCategoryBindBrandList(GoodsCategoryBindBrandExample example, PagerInfo pager) {
        List<GoodsCategoryBindBrand> goodsCategoryBindBrandList;
        if (pager != null) {
            pager.setRowsCount(goodsCategoryBindBrandReadMapper.countByExample(example));
            goodsCategoryBindBrandList = goodsCategoryBindBrandReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsCategoryBindBrandList = goodsCategoryBindBrandReadMapper.listByExample(example);
        }
        return goodsCategoryBindBrandList;
    }
}