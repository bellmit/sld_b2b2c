package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsAttributeValueReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsAttributeValueWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsBindAttributeValueWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsAttributeValueExample;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.example.GoodsBindAttributeValueExample;
import com.slodon.b2b2c.goods.pojo.GoodsAttributeValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class GoodsAttributeValueModel {

    @Resource
    private GoodsAttributeValueReadMapper goodsAttributeValueReadMapper;
    @Resource
    private GoodsAttributeValueWriteMapper goodsAttributeValueWriteMapper;
    @Resource
    private GoodsBindAttributeValueWriteMapper goodsBindAttributeValueWriteMapper;

    /**
     * 新增检索属性表，系统创建，和类型绑定
     *
     * @param goodsAttributeValue
     * @return
     */
    public Integer saveGoodsAttributeValue(GoodsAttributeValue goodsAttributeValue) {
        int count = goodsAttributeValueWriteMapper.insert(goodsAttributeValue);
        if (count == 0) {
            throw new MallException("添加检索属性表，系统创建，和类型绑定失败，请重试");
        }
        return count;
    }

    /**
     * 新增平台检索属性值
     *
     * @param adminId
     * @param attributeId
     * @param AttributeValues
     * @return
     */
    @Transactional
    public Integer saveGoodsAttributeValue(Integer adminId, Integer attributeId, String AttributeValues) {
        //判断属性ID是否重复
        GoodsAttributeValueExample goodsAttributeValueExample = new GoodsAttributeValueExample();
        goodsAttributeValueExample.setAttributeId(attributeId);
        List<GoodsAttributeValue> goodsAttributeValues = goodsAttributeValueReadMapper.listByExample(goodsAttributeValueExample);
        if (!CollectionUtils.isEmpty(goodsAttributeValues)) {
            throw new MallException("属性ID已存在，请重新填写");
        }
        //插入属性值表
        String[] AttributeValueArray = AttributeValues.split(",");
        int count = 0;
        for (int i = 0; i < AttributeValueArray.length; i++) {
            GoodsAttributeValue goodsAttributeValueInsert = new GoodsAttributeValue();
            goodsAttributeValueInsert.setAttributeId(attributeId);
            goodsAttributeValueInsert.setAttributeValue(AttributeValueArray[i]);
            goodsAttributeValueInsert.setCreateTime(new Date());
            goodsAttributeValueInsert.setCreateAdminId(adminId);
            count += goodsAttributeValueWriteMapper.insert(goodsAttributeValueInsert);
            if (count == 0) {
                throw new MallException("添加属性值表失败，请重试");
            }
        }
        return count;
    }

    /**
     * 根据valueId删除检索属性表，系统创建，和类型绑定
     *
     * @param valueId valueId
     * @return
     */
    @Transactional
    public Integer deleteGoodsAttributeValue(Integer valueId) {
        if (StringUtils.isEmpty(valueId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsAttributeValueWriteMapper.deleteByPrimaryKey(valueId);
        if (count == 0) {
            log.error("根据valueId：" + valueId + "删除检索属性表，系统创建，和类型绑定失败");
            throw new MallException("删除检索属性表，系统创建，和类型绑定失败,请重试");
        }
        return count;
    }

    /**
     * 根据属性Id删除属性值表中的记录
     *
     * @param attributeID attributeID
     * @return
     */
    @Transactional
    public Integer deleteGoodsAttributeValueByAttributeID(Integer attributeID) {
        if (StringUtils.isEmpty(attributeID)) {
            throw new MallException("请选择要删除的数据");
        }
        GoodsAttributeValueExample GoodsAttributeValueExample = new GoodsAttributeValueExample();
        GoodsAttributeValueExample.setAttributeId(attributeID);
        List<GoodsAttributeValue> goodsAttributeValues = goodsAttributeValueReadMapper.listByExample(GoodsAttributeValueExample);
        if (!CollectionUtils.isEmpty(goodsAttributeValues)) {
            for (GoodsAttributeValue goodsAttributeValue : goodsAttributeValues) {
                //删除商品属性绑定表
                GoodsBindAttributeValueExample goodsBindAttributeValueExample = new GoodsBindAttributeValueExample();
                goodsBindAttributeValueExample.setAttributeValueId(goodsAttributeValue.getValueId());
                goodsBindAttributeValueExample.setAttributeId(attributeID);
                goodsBindAttributeValueWriteMapper.deleteByExample(goodsBindAttributeValueExample);
            }
        }
        int count = goodsAttributeValueWriteMapper.deleteByExample(GoodsAttributeValueExample);
        return count;
    }


    /**
     * 根据valueId更新检索属性表，系统创建，和类型绑定
     *
     * @param goodsAttributeValue
     * @return
     */
    public Integer updateGoodsAttributeValue(GoodsAttributeValue goodsAttributeValue) {
        if (StringUtils.isEmpty(goodsAttributeValue.getValueId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsAttributeValueWriteMapper.updateByPrimaryKeySelective(goodsAttributeValue);
        if (count == 0) {
            log.error("根据valueId：" + goodsAttributeValue.getValueId() + "更新检索属性表，系统创建，和类型绑定失败");
            throw new MallException("更新检索属性表，系统创建，和类型绑定失败,请重试");
        }
        return count;
    }

    /**
     * 根据valueId获取检索属性表，系统创建，和类型绑定详情
     *
     * @param valueId valueId
     * @return
     */
    public GoodsAttributeValue getGoodsAttributeValueByValueId(Integer valueId) {
        return goodsAttributeValueReadMapper.getByPrimaryKey(valueId);
    }

    /**
     * 根据条件获取检索属性表，系统创建，和类型绑定列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsAttributeValue> getGoodsAttributeValueList(GoodsAttributeValueExample example, PagerInfo pager) {
        List<GoodsAttributeValue> goodsAttributeValueList;
        if (pager != null) {
            pager.setRowsCount(goodsAttributeValueReadMapper.countByExample(example));
            goodsAttributeValueList = goodsAttributeValueReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsAttributeValueList = goodsAttributeValueReadMapper.listByExample(example);
        }
        return goodsAttributeValueList;
    }
}