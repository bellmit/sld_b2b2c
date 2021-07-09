package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsParameterReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsParameterWriteMapper;
import com.slodon.b2b2c.goods.dto.GoodsParameterAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsParameterUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsParameterExample;
import com.slodon.b2b2c.goods.pojo.GoodsParameter;
import com.slodon.b2b2c.seller.pojo.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class GoodsParameterModel {
    @Resource
    private GoodsParameterReadMapper goodsParameterReadMapper;

    @Resource
    private GoodsParameterWriteMapper goodsParameterWriteMapper;

    /**
     * 新增店铺自定义商品参数表，仅作详情顶部的展示使用
     *
     * @param goodsParameter
     * @return
     */
    public Integer saveGoodsParameter(GoodsParameter goodsParameter) {
        int count = goodsParameterWriteMapper.insert(goodsParameter);
        if (count == 0) {
            throw new MallException("添加店铺自定义商品参数表，仅作详情顶部的展示使用失败，请重试");
        }
        return count;
    }

    /**
     * 新增店铺自定义商品参数表，仅作详情顶部的展示使用
     *
     * @param goodsParameterAddDTO,vendor
     * @return
     */
    @Transactional
    public Integer saveGoodsParameter(GoodsParameterAddDTO goodsParameterAddDTO, Vendor vendor) throws Exception{
        //验证参数是否为空
        if (goodsParameterAddDTO == null || "".equals(goodsParameterAddDTO)) {
            throw new MallException("自定义属性不能为空,请重试!");
        }else if (goodsParameterAddDTO.getParameterName() == null || "".equals(goodsParameterAddDTO.getParameterName())){
            throw new MallException("自定义属性名称不能为空,请重试!");
        }else if (goodsParameterAddDTO.getParameterValue() == null || "".equals(goodsParameterAddDTO.getParameterValue())){
            throw new MallException("自定义属性值不能为空,请重试!");
        }else if (goodsParameterAddDTO.getSort() == null || "".equals(goodsParameterAddDTO.getSort())){
            throw new MallException("排序不能为空,请重试!");
        }

        //查重
        GoodsParameterExample example = new GoodsParameterExample();
        example.setParameterName(goodsParameterAddDTO.getParameterName());
        List<GoodsParameter> list = goodsParameterReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("自定义属性名称重复,请重新填写");
        }

        //新增
        GoodsParameter insertOne = new GoodsParameter();
        PropertyUtils.copyProperties(insertOne, goodsParameterAddDTO);
        insertOne.setStoreId(vendor.getStoreId());
        insertOne.setStoreName(vendor.getStore().getStoreName());
        insertOne.setCreateVendorId(vendor.getVendorId());
        insertOne.setCreateTime(new Date());

        int count = goodsParameterWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加店铺自定义商品参数表，仅作详情顶部的展示使用失败，请重试");
        }
        return count;
    }

    /**
     * 根据parameterId删除店铺自定义商品参数表，仅作详情顶部的展示使用
     *
     * @param parameterId parameterId
     * @return
     */
    public Integer deleteGoodsParameter(Integer parameterId) {
        if (StringUtils.isEmpty(parameterId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsParameterWriteMapper.deleteByPrimaryKey(parameterId);
        if (count == 0) {
            log.error("根据parameterId：" + parameterId + "删除店铺自定义商品参数表，仅作详情顶部的展示使用失败");
            throw new MallException("删除店铺自定义商品参数表，仅作详情顶部的展示使用失败,请重试");
        }
        return count;
    }
    /**
     * 根据parameterIds删除店铺自定义商品参数表，仅作详情顶部的展示使用
     *
     * @param parameterIds parameterIds
     * @return
     */
    @Transactional
    public Integer deleteGoodsParameter(String parameterIds,Long storeId) {

        //组装要删除的条件
        GoodsParameterExample example = new GoodsParameterExample();
        example.setStoreId(storeId);
        example.setParameterIdIn(parameterIds);
        int count = goodsParameterWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据parameterIds：" + parameterIds + "删除店铺自定义商品参数表，仅作详情顶部的展示使用失败");
            throw new MallException("删除店铺自定义商品参数表，仅作详情顶部的展示使用失败,请重试");
        }
        return count;
    }

    /**
     * 根据parameterId更新店铺自定义商品参数表，仅作详情顶部的展示使用
     *
     * @param goodsParameter
     * @return
     */
    public Integer updateGoodsParameter(GoodsParameter goodsParameter) {
        if (StringUtils.isEmpty(goodsParameter.getParameterId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsParameterWriteMapper.updateByPrimaryKeySelective(goodsParameter);
        if (count == 0) {
            log.error("根据parameterId：" + goodsParameter.getParameterId() + "更新店铺自定义商品参数表，仅作详情顶部的展示使用失败");
            throw new MallException("更新店铺自定义商品参数表，仅作详情顶部的展示使用失败,请重试");
        }
        return count;
    }

    /**
     * 根据parameterId更新店铺自定义商品参数表，仅作详情顶部的展示使用
     *
     * @param goodsParameterUpdateDTO
     * @return
     */
    @Transactional
    public Integer updateGoodsParameter(GoodsParameterUpdateDTO goodsParameterUpdateDTO,Long storeId) throws Exception{
        GoodsParameter goodsParameter = goodsParameterReadMapper.getByPrimaryKey(goodsParameterUpdateDTO.getParameterId());
        AssertUtil.isTrue(!goodsParameter.getStoreId().equals(storeId),"您无权限操作");

        //查重
        if (!StringUtils.isEmpty(goodsParameterUpdateDTO.getParameterName())){
            GoodsParameterExample example = new GoodsParameterExample();
            example.setParameterName(goodsParameterUpdateDTO.getParameterName());
            example.setParameterIdNotEquals(goodsParameterUpdateDTO.getParameterId());
            List<GoodsParameter> list = goodsParameterReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(list)) {
                throw new MallException("自定义属性名称重复,请重新填写");
            }
        }

        //修改
        GoodsParameter updateOne = new GoodsParameter();
        PropertyUtils.copyProperties(updateOne, goodsParameterUpdateDTO);

        int count = goodsParameterWriteMapper.updateByPrimaryKeySelective(updateOne);
        
        if (count == 0) {
            log.error("根据parameterId：" + goodsParameterUpdateDTO.getParameterId() + "更新店铺自定义商品参数表，仅作详情顶部的展示使用失败");
            throw new MallException("更新店铺自定义商品参数表，仅作详情顶部的展示使用失败,请重试");
        }
        return count;
    }

    /**
     * 根据parameterId获取店铺自定义商品参数表，仅作详情顶部的展示使用详情
     *
     * @param parameterId parameterId
     * @return
     */
    public GoodsParameter getGoodsParameterByParameterId(Integer parameterId) {
        return goodsParameterReadMapper.getByPrimaryKey(parameterId);
    }

    /**
     * 根据条件获取店铺自定义商品参数表，仅作详情顶部的展示使用列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsParameter> getGoodsParameterList(GoodsParameterExample example, PagerInfo pager) {
        List<GoodsParameter> goodsParameterList;
        if (pager != null) {
            pager.setRowsCount(goodsParameterReadMapper.countByExample(example));
            goodsParameterList = goodsParameterReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsParameterList = goodsParameterReadMapper.listByExample(example);
        }
        return goodsParameterList;
    }
}