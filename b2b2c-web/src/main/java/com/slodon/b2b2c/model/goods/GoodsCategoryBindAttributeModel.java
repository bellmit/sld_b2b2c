package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsCategoryBindAttributeReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsCategoryBindAttributeWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.example.GoodsCategoryBindAttributeExample;
import com.slodon.b2b2c.goods.pojo.GoodsCategoryBindAttribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsCategoryBindAttributeModel {
    @Resource
    private GoodsCategoryBindAttributeReadMapper goodsCategoryBindAttributeReadMapper;

    @Resource
    private GoodsCategoryBindAttributeWriteMapper goodsCategoryBindAttributeWriteMapper;

    /**
     * 新增类型与检索属性绑定关系表
     *
     * @param goodsCategoryBindAttribute
     * @return
     */
    public Integer saveGoodsCategoryBindAttribute(GoodsCategoryBindAttribute goodsCategoryBindAttribute) {
        int count = goodsCategoryBindAttributeWriteMapper.insert(goodsCategoryBindAttribute);
        if (count == 0) {
            throw new MallException("添加类型与检索属性绑定关系表失败，请重试");
        }
        return count;
    }

    /**
     * 新增与CategoryID绑定的多个类型与检索属性绑定关系表
     *
     * @param categoryId
     * @param attributes 多个以，分割的属性
     * @return
     */
    public Integer saveGoodsCategoryBindAttributeByCategory(Integer categoryId,String attributes) {
        int count=0;
        if(!StringUtils.isEmpty(attributes)) {
            String[] bindAttributeArray = attributes.split(",");
            for (int i = 0; i < bindAttributeArray.length; i++) {
                GoodsCategoryBindAttribute goodsCategoryBindAttribute = new GoodsCategoryBindAttribute();
                goodsCategoryBindAttribute.setAttributeId(Integer.valueOf(bindAttributeArray[i]));
                goodsCategoryBindAttribute.setCategoryId(categoryId);
                count += goodsCategoryBindAttributeWriteMapper.insert(goodsCategoryBindAttribute);
            }
        }
        return count;
    }

    /**
     * 根据bindId删除类型与检索属性绑定关系表
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteGoodsCategoryBindAttribute(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsCategoryBindAttributeWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除类型与检索属性绑定关系表失败");
            throw new MallException("删除类型与检索属性绑定关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据 CategoryId 删除类型与检索属性绑定关系表
     *
     * @param categoryId categoryId
     * @return
     */
    public Integer deleteGoodsCategoryBindAttributeByCategory(Integer categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            throw new MallException("请选择要删除的数据");
        }
        GoodsCategoryBindAttributeExample goodsCategoryBindAttributeExample=new GoodsCategoryBindAttributeExample();
        goodsCategoryBindAttributeExample.setCategoryId(categoryId);
        int count = goodsCategoryBindAttributeWriteMapper.deleteByExample(goodsCategoryBindAttributeExample);
        return count;
    }

    /**
     * 根据bindId更新类型与检索属性绑定关系表
     *
     * @param goodsCategoryBindAttribute
     * @return
     */
    public Integer updateGoodsCategoryBindAttribute(GoodsCategoryBindAttribute goodsCategoryBindAttribute) {
        if (StringUtils.isEmpty(goodsCategoryBindAttribute.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsCategoryBindAttributeWriteMapper.updateByPrimaryKeySelective(goodsCategoryBindAttribute);
        if (count == 0) {
            log.error("根据bindId：" + goodsCategoryBindAttribute.getBindId() + "更新类型与检索属性绑定关系表失败");
            throw new MallException("更新类型与检索属性绑定关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取类型与检索属性绑定关系表详情
     *
     * @param bindId bindId
     * @return
     */
    public GoodsCategoryBindAttribute getGoodsCategoryBindAttributeByBindId(Integer bindId) {
        return goodsCategoryBindAttributeReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取类型与检索属性绑定关系表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsCategoryBindAttribute> getGoodsCategoryBindAttributeList(GoodsCategoryBindAttributeExample example, PagerInfo pager) {
        List<GoodsCategoryBindAttribute> goodsCategoryBindAttributeList;
        if (pager != null) {
            pager.setRowsCount(goodsCategoryBindAttributeReadMapper.countByExample(example));
            goodsCategoryBindAttributeList = goodsCategoryBindAttributeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsCategoryBindAttributeList = goodsCategoryBindAttributeReadMapper.listByExample(example);
        }
        return goodsCategoryBindAttributeList;
    }
}