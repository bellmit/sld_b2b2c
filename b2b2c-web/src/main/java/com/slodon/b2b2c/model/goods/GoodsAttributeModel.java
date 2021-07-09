package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsAttributeReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsAttributeWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.write.goods.GoodsCategoryBindAttributeWriteMapper;
import com.slodon.b2b2c.goods.dto.GoodsAttributeAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsAttributeUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsAttributeExample;
import com.slodon.b2b2c.goods.example.GoodsCategoryBindAttributeExample;
import com.slodon.b2b2c.goods.pojo.GoodsAttribute;
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
public class GoodsAttributeModel {

    @Resource
    private GoodsAttributeReadMapper goodsAttributeReadMapper;
    @Resource
    private GoodsAttributeWriteMapper goodsAttributeWriteMapper;
    @Resource
    private GoodsAttributeValueModel goodsAttributeValueModel;
    @Resource
    private GoodsCategoryBindAttributeWriteMapper goodsCategoryBindAttributeWriteMapper;

    /**
     * 新增平台检索属性，系统创建，和商品分类绑定
     *
     * @param goodsAttribute
     * @return
     */
    public Integer saveGoodsAttribute(GoodsAttribute goodsAttribute) {
        int count = goodsAttributeWriteMapper.insert(goodsAttribute);
        if (count == 0) {
            throw new MallException("添加平台检索属性，系统创建，和商品分类绑定失败，请重试");
        }
        return count;
    }

    /**
     * 新增平台检索属性，系统创建，和商品分类绑定
     *
     * @param adminId
     * @param goodsAttributeAddDTO
     * @return
     */
    public Integer saveGoodsAttribute(Integer adminId, GoodsAttributeAddDTO goodsAttributeAddDTO) {
        //判断属性名称是否重复
        GoodsAttributeExample goodsAttributeExample = new GoodsAttributeExample();
        goodsAttributeExample.setAttributeName(goodsAttributeAddDTO.getAttributeName());
        List<GoodsAttribute> goodsAttributes = goodsAttributeReadMapper.listByExample(goodsAttributeExample);
        if (!CollectionUtils.isEmpty(goodsAttributes)) {
            throw new MallException("属性名称已存在，请重新填写");
        }
        //插入属性表
        GoodsAttribute goodsAttributeInsert = new GoodsAttribute();
        goodsAttributeInsert.setAttributeName(goodsAttributeAddDTO.getAttributeName());
        goodsAttributeInsert.setSort(goodsAttributeAddDTO.getSort());
        goodsAttributeInsert.setIsShow(goodsAttributeAddDTO.getIsShow());
        goodsAttributeInsert.setCreateTime(new Date());
        goodsAttributeInsert.setCreateAdminId(adminId);
        int count;
        count = goodsAttributeWriteMapper.insert(goodsAttributeInsert);
        if (count == 0) {
            throw new MallException("添加属性表失败，请重试");
        }
        return goodsAttributeInsert.getAttributeId();
    }

    /**
     * 保存平台检索属性和属性值,
     *
     * @param adminId
     * @param goodsAttributeAddDTO
     * @return
     */
    @Transactional
    public void saveGoodsAttributeAndValue(Integer adminId, GoodsAttributeAddDTO goodsAttributeAddDTO) {
        Integer attributeId = saveGoodsAttribute(adminId, goodsAttributeAddDTO);
        goodsAttributeValueModel.saveGoodsAttributeValue(adminId, attributeId, goodsAttributeAddDTO.getAttributeValues());
    }

    /**
     * 根据attributeId删除平台检索属性，系统创建，和商品分类绑定
     *
     * @param attributeId attributeId
     * @return
     */
    @Transactional
    public Integer deleteGoodsAttribute(Integer attributeId) {
        //删除分类绑定属性表
        int count;
        GoodsCategoryBindAttributeExample categoryBindAttributeExample = new GoodsCategoryBindAttributeExample();
        categoryBindAttributeExample.setAttributeId(attributeId);
        count = goodsCategoryBindAttributeWriteMapper.deleteByExample(categoryBindAttributeExample);
        //删除属性表
        count = goodsAttributeWriteMapper.deleteByPrimaryKey(attributeId);
        if (count == 0) {
            log.error("根据attributeId：" + attributeId + "删除平台检索属性，商品分类绑定属性失败");
            throw new MallException("删除平台检索属性，商品分类绑定属性失败,请重试");
        }
        return count;
    }

    /**
     * 根据attributeId删除平台检索属性和属性值
     *
     * @param attributeId attributeId
     * @return
     */
    @Transactional
    public void deleteGoodsAttributeAndValue(Integer attributeId) {
        goodsAttributeValueModel.deleteGoodsAttributeValueByAttributeID(attributeId);
        deleteGoodsAttribute(attributeId);
    }

    /**
     * 根据attributeId更新平台检索属性，系统创建，和商品分类绑定
     *
     * @param goodsAttribute
     * @return
     */
    public Integer updateGoodsAttribute(GoodsAttribute goodsAttribute) {
        if (StringUtils.isEmpty(goodsAttribute.getAttributeId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsAttributeWriteMapper.updateByPrimaryKeySelective(goodsAttribute);
        if (count == 0) {
            log.error("根据attributeId：" + goodsAttribute.getAttributeId() + "更新平台检索属性，系统创建，和商品分类绑定失败");
            throw new MallException("更新平台检索属性，系统创建，和商品分类绑定失败,请重试");
        }
        return count;
    }

    /**
     * 根据attributeId更新平台检索属性，系统创建，和商品分类绑定
     *
     * @param adminId
     * @param goodsAttributeUpdateDTO
     * @return
     */
    public Integer updateGoodsAttribute(Integer adminId, GoodsAttributeUpdateDTO goodsAttributeUpdateDTO) {
        GoodsAttribute goodsAttributeUpdate = new GoodsAttribute();
        goodsAttributeUpdate.setAttributeId(goodsAttributeUpdateDTO.getAttributeId());
        if (!StringUtils.isEmpty(goodsAttributeUpdateDTO.getAttributeName())) {
            //判断属性名称是否重复
            GoodsAttributeExample goodsAttributeExample = new GoodsAttributeExample();
            goodsAttributeExample.setAttributeName(goodsAttributeUpdateDTO.getAttributeName());
            goodsAttributeExample.setAttributeIdNotEquals(goodsAttributeUpdateDTO.getAttributeId());
            List<GoodsAttribute> goodsAttributes = goodsAttributeReadMapper.listByExample(goodsAttributeExample);
            if (!CollectionUtils.isEmpty(goodsAttributes)) {
                throw new MallException("属性名称已存在，请重新填写");
            }
            goodsAttributeUpdate.setAttributeName(goodsAttributeUpdateDTO.getAttributeName());
        }
        //修改属性表
        goodsAttributeUpdate.setSort(goodsAttributeUpdateDTO.getSort());

        goodsAttributeUpdate.setIsShow(goodsAttributeUpdateDTO.getIsShow());

        int count;
        count = goodsAttributeWriteMapper.updateByPrimaryKeySelective(goodsAttributeUpdate);
        if (count == 0) {
            log.error("根据属性ID：" + goodsAttributeUpdate.getAttributeId() + "更新属性表失败");
            throw new MallException("更新属性表失败,请重试");
        }
        return count;
    }


    /**
     * 更新平台检索属性和属性值,
     *
     * @param adminId
     * @param goodsAttributeUpdateDTO
     * @return
     */
    @Transactional
    public void updateGoodsAttributeAndValue(Integer adminId, GoodsAttributeUpdateDTO goodsAttributeUpdateDTO) {
        updateGoodsAttribute(adminId, goodsAttributeUpdateDTO);
        if (!StringUtils.isEmpty(goodsAttributeUpdateDTO.getAttributeValues())) {
            goodsAttributeValueModel.deleteGoodsAttributeValueByAttributeID(goodsAttributeUpdateDTO.getAttributeId());
            goodsAttributeValueModel.saveGoodsAttributeValue(adminId, goodsAttributeUpdateDTO.getAttributeId(), goodsAttributeUpdateDTO.getAttributeValues());
        }
    }


    /**
     * 根据attributeId获取平台检索属性，系统创建，和商品分类绑定详情
     *
     * @param attributeId attributeId
     * @return
     */
    public GoodsAttribute getGoodsAttributeByAttributeId(Integer attributeId) {
        return goodsAttributeReadMapper.getByPrimaryKey(attributeId);
    }

    /**
     * 根据条件获取平台检索属性，系统创建，和商品分类绑定列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsAttribute> getGoodsAttributeList(GoodsAttributeExample example, PagerInfo pager) {
        List<GoodsAttribute> goodsAttributeList;
        if (pager != null) {
            pager.setRowsCount(goodsAttributeReadMapper.countByExample(example));
            goodsAttributeList = goodsAttributeReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsAttributeList = goodsAttributeReadMapper.listByExample(example);
        }
        return goodsAttributeList;
    }
}