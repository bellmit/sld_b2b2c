package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsParameterGroupReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsParameterReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsParameterGroupWriteMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsParameterWriteMapper;
import com.slodon.b2b2c.goods.pojo.GoodsParameterGroup;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.dto.GoodsParameterGroupAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsParameterGroupUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsParameterExample;
import com.slodon.b2b2c.goods.example.GoodsParameterGroupExample;
import com.slodon.b2b2c.goods.pojo.GoodsParameter;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class GoodsParameterGroupModel {
    @Resource
    private GoodsParameterGroupReadMapper goodsParameterGroupReadMapper;

    @Resource
    private GoodsParameterGroupWriteMapper goodsParameterGroupWriteMapper;

    @Resource
    private GoodsParameterReadMapper goodsParameterReadMapper;

    @Resource
    private GoodsParameterWriteMapper goodsParameterWriteMapper;

    /**
     * 新增属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用
     *
     * @param goodsParameterGroup
     * @return
     */
    public Integer saveGoodsParameterGroup(GoodsParameterGroup goodsParameterGroup) {
        int count = goodsParameterGroupWriteMapper.insert(goodsParameterGroup);
        if (count == 0) {
            throw new MallException("添加属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败，请重试");
        }
        return count;
    }

    /**
     * 新增属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用
     *
     * @param goodsParameterGroupAddDTO,vendor
     * @return
     */
    @Transactional
    public Integer saveGoodsParameterGroup(GoodsParameterGroupAddDTO goodsParameterGroupAddDTO, Vendor vendor) throws Exception {
        //验证参数是否为空
        if (goodsParameterGroupAddDTO == null || "".equals(goodsParameterGroupAddDTO)) {
            throw new MallException("属性分组不能为空,请重试!");
        } else if (goodsParameterGroupAddDTO.getGroupName() == null || "".equals(goodsParameterGroupAddDTO.getGroupName())) {
            throw new MallException("分组名称不能为空,请重试!");
        } else if (goodsParameterGroupAddDTO.getSort() == null || "".equals(goodsParameterGroupAddDTO.getSort())) {
            throw new MallException("排序不能为空,请重试!");
        }

        //查重
        GoodsParameterGroupExample example = new GoodsParameterGroupExample();
        example.setGroupName(goodsParameterGroupAddDTO.getGroupName());
        List<GoodsParameterGroup> list = goodsParameterGroupReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("属性分组名称重复,请重新填写");
        }

        //新增
        GoodsParameterGroup insertOne = new GoodsParameterGroup();
        PropertyUtils.copyProperties(insertOne, goodsParameterGroupAddDTO);
        insertOne.setStoreId(vendor.getStoreId());
        insertOne.setCreateVendorId(vendor.getVendorId());
        insertOne.setCreateTime(new Date());

        int count = goodsParameterGroupWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败，请重试");
        }
        return count;
    }

    /**
     * 根据groupId删除属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用
     *
     * @param groupId groupId
     * @return
     */
    public Integer deleteGoodsParameterGroup(Integer groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsParameterGroupWriteMapper.deleteByPrimaryKey(groupId);
        if (count == 0) {
            log.error("根据groupId：" + groupId + "删除属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败");
            throw new MallException("删除属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败,请重试");
        }
        return count;
    }

    /**
     * 根据groupIds删除属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用
     *
     * @param groupIds
     * @return
     */
    @Transactional
    public Integer deleteGoodsParameterGroup(String groupIds,Long storeId) {

        //先遍历得到每一个groupId,逐个删除属性,再删除自定义属性组
        String[] strings = groupIds.split(",");
        for (String groupId : strings) {
            GoodsParameterExample goodsParameterExample = new GoodsParameterExample();
            goodsParameterExample.setStoreId(storeId);
            goodsParameterExample.setGroupId(Integer.valueOf(groupId));
            goodsParameterWriteMapper.deleteByExample(goodsParameterExample);
        }

        //组装要删除的条件
        GoodsParameterGroupExample example = new GoodsParameterGroupExample();
        example.setStoreId(storeId);
        example.setGroupIdIn(groupIds);
        int count = goodsParameterGroupWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据groupIds：" + groupIds + "删除属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败");
            throw new MallException("删除属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败,请重试");
        }
        return 1;
    }

    /**
     * 根据groupId更新属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用
     *
     * @param goodsParameterGroup
     * @return
     */
    public Integer updateGoodsParameterGroup(GoodsParameterGroup goodsParameterGroup) {
        if (StringUtils.isEmpty(goodsParameterGroup.getGroupId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsParameterGroupWriteMapper.updateByPrimaryKeySelective(goodsParameterGroup);
        if (count == 0) {
            log.error("根据groupId：" + goodsParameterGroup.getGroupId() + "更新属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败");
            throw new MallException("更新属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败,请重试");
        }
        return count;
    }

    /**
     * 根据groupId更新属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用
     *
     * @param goodsParameterGroupUpdateDTO
     * @return
     */
    @Transactional
    public Integer updateGoodsParameterGroup(GoodsParameterGroupUpdateDTO goodsParameterGroupUpdateDTO,Long stored) throws Exception {

        //查重
        if (!StringUtils.isEmpty(goodsParameterGroupUpdateDTO.getGroupName())){
            GoodsParameterGroupExample example = new GoodsParameterGroupExample();
            example.setGroupName(goodsParameterGroupUpdateDTO.getGroupName());
            example.setGroupIdNotEquals(goodsParameterGroupUpdateDTO.getGroupId());
            List<GoodsParameterGroup> list = goodsParameterGroupReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(list)) {
                throw new MallException("属性分组名称重复,请重新填写");
            }
        }

        //修改
        GoodsParameterGroup updateOne = new GoodsParameterGroup();
        PropertyUtils.copyProperties(updateOne, goodsParameterGroupUpdateDTO);

        GoodsParameterGroupExample example = new GoodsParameterGroupExample();
        example.setStoreId(stored);
        example.setGroupIdIn(goodsParameterGroupUpdateDTO.getGroupId()+"");
        int count = goodsParameterGroupWriteMapper.updateByExampleSelective(updateOne,example);
        if (count == 0) {
            log.error("根据groupId：" + goodsParameterGroupUpdateDTO.getGroupId() + "更新属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败");
            throw new MallException("更新属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用失败,请重试");
        }
        return count;
    }

    /**
     * 根据groupId获取属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用详情
     *
     * @param groupId groupId
     * @return
     */
    public GoodsParameterGroup getGoodsParameterGroupByGroupId(Integer groupId) {
        return goodsParameterGroupReadMapper.getByPrimaryKey(groupId);
    }

    /**
     * 根据条件获取属性-店铺自定义商品组参数表，仅作详情顶部的展示使用详情
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     */
    public List<GoodsParameter> getGoodsParameterByGroupId(GoodsParameterExample example, PagerInfo pager) {
        List<GoodsParameter> list;
        if (pager != null) {
            pager.setRowsCount(goodsParameterReadMapper.countByExample(example));
            list = goodsParameterReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            list = goodsParameterReadMapper.listByExample(example);
        }
        return list;
    }

    /**
     * 根据条件获取属性分组-店铺自定义商品组参数表，仅作详情顶部的展示使用列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsParameterGroup> getGoodsParameterGroupList(GoodsParameterGroupExample example, PagerInfo pager) {
        List<GoodsParameterGroup> goodsParameterGroupList;
        if (pager != null) {
            pager.setRowsCount(goodsParameterGroupReadMapper.countByExample(example));
            goodsParameterGroupList = goodsParameterGroupReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsParameterGroupList = goodsParameterGroupReadMapper.listByExample(example);
        }
        return goodsParameterGroupList;
    }


}