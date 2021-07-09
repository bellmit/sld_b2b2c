package com.slodon.b2b2c.model.integral;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsSpecReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsSpecValueReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsSpecValueWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsSpecWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsSpecValueExample;
import com.slodon.b2b2c.goods.pojo.GoodsSpec;
import com.slodon.b2b2c.goods.pojo.GoodsSpecValue;
import com.slodon.b2b2c.integral.example.IntegralGoodsSpecExample;
import com.slodon.b2b2c.integral.example.IntegralGoodsSpecValueExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsSpec;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsSpecValue;
import com.slodon.b2b2c.model.goods.GoodsSpecModel;
import com.slodon.b2b2c.model.goods.GoodsSpecValueModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.integral.SpecInfoVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 积分商品规格值表model
 */
@Component
@Slf4j
public class IntegralGoodsSpecValueModel {

    @Resource
    private IntegralGoodsSpecValueReadMapper integralGoodsSpecValueReadMapper;
    @Resource
    private IntegralGoodsSpecValueWriteMapper integralGoodsSpecValueWriteMapper;
    @Resource
    private IntegralGoodsSpecReadMapper integralGoodsSpecReadMapper;
    @Resource
    private IntegralGoodsSpecWriteMapper integralGoodsSpecWriteMapper;
    @Resource
    private GoodsSpecModel goodsSpecModel;
    @Resource
    private GoodsSpecValueModel goodsSpecValueModel;

    /**
     * 新增积分商品规格值表
     *
     * @param integralGoodsSpecValue
     * @return
     */
    public Integer saveIntegralGoodsSpecValue(IntegralGoodsSpecValue integralGoodsSpecValue) {
        int count = integralGoodsSpecValueWriteMapper.insert(integralGoodsSpecValue);
        if (count == 0) {
            throw new MallException("添加积分商品规格值表失败，请重试");
        }
        return integralGoodsSpecValue.getSpecValueId();
    }

    /**
     * 根据specValueId删除积分商品规格值表
     *
     * @param specValueId specValueId
     * @return
     */
    public Integer deleteIntegralGoodsSpecValue(Integer specValueId) {
        if (StringUtils.isEmpty(specValueId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralGoodsSpecValueWriteMapper.deleteByPrimaryKey(specValueId);
        if (count == 0) {
            log.error("根据specValueId：" + specValueId + "删除积分商品规格值表失败");
            throw new MallException("删除积分商品规格值表失败,请重试");
        }
        return count;
    }

    /**
     * 根据specValueId更新积分商品规格值表
     *
     * @param integralGoodsSpecValue
     * @return
     */
    public Integer updateIntegralGoodsSpecValue(IntegralGoodsSpecValue integralGoodsSpecValue) {
        if (StringUtils.isEmpty(integralGoodsSpecValue.getSpecValueId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralGoodsSpecValueWriteMapper.updateByPrimaryKeySelective(integralGoodsSpecValue);
        if (count == 0) {
            log.error("根据specValueId：" + integralGoodsSpecValue.getSpecValueId() + "更新积分商品规格值表失败");
            throw new MallException("更新积分商品规格值表失败,请重试");
        }
        return count;
    }

    /**
     * 根据specValueId获取积分商品规格值表详情
     *
     * @param specValueId specValueId
     * @return
     */
    public IntegralGoodsSpecValue getIntegralGoodsSpecValueBySpecValueId(Integer specValueId) {
        return integralGoodsSpecValueReadMapper.getByPrimaryKey(specValueId);
    }

    /**
     * 根据条件获取积分商品规格值表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralGoodsSpecValue> getIntegralGoodsSpecValueList(IntegralGoodsSpecValueExample example, PagerInfo pager) {
        List<IntegralGoodsSpecValue> integralGoodsSpecValueList;
        if (pager != null) {
            pager.setRowsCount(integralGoodsSpecValueReadMapper.countByExample(example));
            integralGoodsSpecValueList = integralGoodsSpecValueReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralGoodsSpecValueList = integralGoodsSpecValueReadMapper.listByExample(example);
        }
        return integralGoodsSpecValueList;
    }

    /**
     * 把商城的规格信息替换为积分商城的最新信息
     *
     * @param specJson
     * @param vendor
     * @return
     */
    @SneakyThrows
    public String dealSpecJson(String specJson, Vendor vendor) {
        if (StringUtil.isEmpty(specJson)) {
            return null;
        }
        List<SpecInfoVO> specJsonList = new ArrayList<>();
        List<SpecInfoVO> list = JSONObject.parseArray(specJson, SpecInfoVO.class);
        for (SpecInfoVO specInfoVO : list) {
            SpecInfoVO vo = new SpecInfoVO();
            vo.setSpecName(specInfoVO.getSpecName());
            vo.setIsMainSpec(specInfoVO.getIsMainSpec());
            IntegralGoodsSpecExample example = new IntegralGoodsSpecExample();
            example.setSpecName(specInfoVO.getSpecName());
            example.setStoreId(vendor.getStoreId());
            List<IntegralGoodsSpec> goodsSpecList = integralGoodsSpecReadMapper.listByExample(example);
            List<SpecInfoVO.SpecValueInfo> valueInfos = new ArrayList<>();
            if (!CollectionUtils.isEmpty(goodsSpecList)) {
                IntegralGoodsSpec integralGoodsSpec = goodsSpecList.get(0);
                vo.setSpecId(integralGoodsSpec.getSpecId());
                for (SpecInfoVO.SpecValueInfo specValueInfo : specInfoVO.getSpecValueList()) {
                    SpecInfoVO.SpecValueInfo valueInfo = new SpecInfoVO.SpecValueInfo();
                    valueInfo.setSpecValue(specValueInfo.getSpecValue());
                    IntegralGoodsSpecValueExample specValueExample = new IntegralGoodsSpecValueExample();
                    specValueExample.setSpecId(integralGoodsSpec.getSpecId());
                    specValueExample.setSpecValue(specValueInfo.getSpecValue());
                    List<IntegralGoodsSpecValue> specValueList = integralGoodsSpecValueReadMapper.listByExample(specValueExample);
                    //如果有则替换商城的specValueId，没有就复制
                    if (!CollectionUtils.isEmpty(specValueList)) {
                        IntegralGoodsSpecValue integralGoodsSpecValue = specValueList.get(0);

                        valueInfo.setSpecValueId(integralGoodsSpecValue.getSpecValueId());
                    } else {
                        //查询商城的规格值
                        GoodsSpecValue goodsSpecValue = goodsSpecValueModel.getGoodsSpecValueBySpecValueId(specValueInfo.getSpecValueId());
                        AssertUtil.notNull(goodsSpecValue, "商城的规格值不存在");
                        IntegralGoodsSpecValue integralGoodsSpecValue = new IntegralGoodsSpecValue();
                        integralGoodsSpecValue.setSpecValue(specValueInfo.getSpecValue());
                        integralGoodsSpecValue.setSpecId(integralGoodsSpec.getSpecId());
                        integralGoodsSpecValue.setStoreId(vendor.getStoreId());
                        integralGoodsSpecValue.setCreateId(vendor.getVendorId().intValue());
                        integralGoodsSpecValue.setCreateTime(new Date());
                        int count = integralGoodsSpecValueWriteMapper.insert(integralGoodsSpecValue);
                        AssertUtil.isTrue(count == 0, "积分商城的规格值信息保存失败");

                        valueInfo.setSpecValueId(integralGoodsSpecValue.getSpecValueId());
                    }
                    valueInfos.add(valueInfo);
                }
            } else {
                //没有则复制到积分商城
                //查询商城的规格
                GoodsSpec goodsSpec = goodsSpecModel.getGoodsSpecBySpecId(specInfoVO.getSpecId());
                AssertUtil.notNull(goodsSpec, "商城的规格不存在");

                //复制到积分规格表
                IntegralGoodsSpec integralGoodsSpec = new IntegralGoodsSpec();
                PropertyUtils.copyProperties(integralGoodsSpec, goodsSpec);
                integralGoodsSpec.setUpdateTime(new Date());
                int count = integralGoodsSpecWriteMapper.insert(integralGoodsSpec);
                AssertUtil.isTrue(count == 0, "积分商城的规格信息保存失败");
                vo.setSpecId(integralGoodsSpec.getSpecId());

                //查询商城的规格值
                GoodsSpecValueExample example1 = new GoodsSpecValueExample();
                example1.setSpecId(goodsSpec.getSpecId());
                example1.setStoreId(vendor.getStoreId());
                List<GoodsSpecValue> specValueList = goodsSpecValueModel.getGoodsSpecValueList(example1,null);
                AssertUtil.notEmpty(specValueList, "商城的规格值不存在");
                for (GoodsSpecValue specValue : specValueList) {
                    //复制到积分规格值表
                    IntegralGoodsSpecValue integralGoodsSpecValue = new IntegralGoodsSpecValue();
                    PropertyUtils.copyProperties(integralGoodsSpecValue, specValue);
                    integralGoodsSpecValue.setSpecId(integralGoodsSpec.getSpecId());
                    count = integralGoodsSpecValueWriteMapper.insert(integralGoodsSpecValue);
                    AssertUtil.isTrue(count == 0, "积分商城的规格值信息保存失败");

                    SpecInfoVO.SpecValueInfo valueInfo = new SpecInfoVO.SpecValueInfo();
                    valueInfo.setSpecValue(specValue.getSpecValue());
                    valueInfo.setSpecValueId(integralGoodsSpecValue.getSpecValueId());

                    valueInfos.add(valueInfo);
                }
            }
            vo.setSpecValueList(valueInfos);
            specJsonList.add(vo);
        }
        return JSONObject.toJSONString(specJsonList);
    }

    /**
     * 把商城的规格值id替换为积分商城的最新信息
     *
     * @param specJson
     * @param specValues
     * @return
     */
    @SneakyThrows
    public String dealSpecValueIds(String specJson, String specValues, Long storeId) {
        if (StringUtil.isEmpty(specJson) || StringUtil.isEmpty(specValues)) {
            return null;
        }
        StringBuilder specValueIds = new StringBuilder();
        List<SpecInfoVO> list = JSONObject.parseArray(specJson, SpecInfoVO.class);
        //查询积分规格值是否包含
        String[] specValueArray = specValues.split(",");
        for (String specValue : specValueArray) {
            IntegralGoodsSpecValueExample example = new IntegralGoodsSpecValueExample();
            example.setSpecValue(specValue);
            example.setStoreId(storeId);
            List<IntegralGoodsSpecValue> goodsSpecValueList = integralGoodsSpecValueReadMapper.listByExample(example);
            AssertUtil.notEmpty(goodsSpecValueList, "规格值不存在");
            //是否有规格
            boolean isHasSpec = false;
            for (IntegralGoodsSpecValue goodsSpecValue : goodsSpecValueList) {
                IntegralGoodsSpec integralGoodsSpec = integralGoodsSpecReadMapper.getByPrimaryKey(goodsSpecValue.getSpecId());
                AssertUtil.notNull(integralGoodsSpec, "规格不存在");
                for (SpecInfoVO specInfoVO : list) {
                    if (integralGoodsSpec.getSpecName().equals(specInfoVO.getSpecName())) {
                        specValueIds.append(",").append(goodsSpecValue.getSpecValueId());
                        isHasSpec = true;
                        break;
                    }
                }
            }
            AssertUtil.notNull(!isHasSpec, "规格不存在");
        }
        return specValueIds.toString().substring(1);
    }
}