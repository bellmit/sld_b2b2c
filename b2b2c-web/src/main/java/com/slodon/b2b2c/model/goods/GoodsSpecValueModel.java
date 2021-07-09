package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsSpecValueReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsSpecValueWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.goods.example.GoodsSpecValueExample;
import com.slodon.b2b2c.goods.pojo.GoodsSpecValue;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 规格值表（系统维护）model
 */
@Component
@Slf4j
public class GoodsSpecValueModel {
    @Resource
    private GoodsSpecValueReadMapper goodsSpecValueReadMapper;

    @Resource
    private GoodsSpecValueWriteMapper goodsSpecValueWriteMapper;

    /**
     * 新增规格值表（系统维护）
     *
     * @param goodsSpecValue
     * @return
     */
    public Integer saveGoodsSpecValue(GoodsSpecValue goodsSpecValue) {
        int count = goodsSpecValueWriteMapper.insert(goodsSpecValue);
        if (count == 0) {
            throw new MallException("添加规格值表（系统维护）失败，请重试");
        }
        return count;
    }
    /**
     * 新增规格值表（系统维护）
     *
     * @param  storeId
     * @param  storeId
     * @param  specId
     * @param  specValues
     * @return
     */
    @Transactional
    public Integer saveGoodsSpecValue(Long storeId,Integer creatId, Integer specId, String specValues) {

        String[] specValueArray = specValues.split(",");
        int count=0;
        int specValueId = 0;
        for (int i = 0; i < specValueArray.length; i++) {
            //判断规则值是否重复
            GoodsSpecValueExample goodsSpecValueExample = new GoodsSpecValueExample();
            goodsSpecValueExample.setSpecId(specId);
            goodsSpecValueExample.setSpecValue(specValueArray[i]);
            List<GoodsSpecValue>  goodsSpecValues = goodsSpecValueReadMapper.listByExample(goodsSpecValueExample);
            AssertUtil.isTrue(goodsSpecValues!=null&&goodsSpecValues.size()>0,"规则值"+specValueArray[i]+"已存在，请重新填写");

            //插入规格值表
            GoodsSpecValue goodsSpecValueInsert = new GoodsSpecValue();
            goodsSpecValueInsert.setSpecId(specId);
            goodsSpecValueInsert.setSpecValue(specValueArray[i]);
            goodsSpecValueInsert.setStoreId(storeId);
            goodsSpecValueInsert.setCreateId(creatId);
            goodsSpecValueInsert.setCreateTime(new Date());
            count += goodsSpecValueWriteMapper.insert(goodsSpecValueInsert);
            if (count == 0) {
                throw new MallException("添加规格值表失败，请重试");
            }
            specValueId = goodsSpecValueInsert.getSpecValueId();
        }
        return specValueId;
    }

    /**
     * 根据specValueId删除规格值表（系统维护）
     *
     * @param specValueId specValueId
     * @return
     */
    public Integer deleteGoodsSpecValue(Integer specValueId) {
        if (StringUtils.isEmpty(specValueId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsSpecValueWriteMapper.deleteByPrimaryKey(specValueId);
        if (count == 0) {
            log.error("根据specValueId：" + specValueId + "删除规格值表（系统维护）失败");
            throw new MallException("删除规格值表（系统维护）失败,请重试");
        }
        return count;
    }

    /**
     * 根据规格Id删除规格值表中的记录
     *
     * @param specID specID
     * @return
     */
    public Integer deleteGoodsSpecValueBySpecID(Integer specID) {

        GoodsSpecValueExample GoodsSpecValueExample=new GoodsSpecValueExample();
        GoodsSpecValueExample.setSpecId(specID);
        int count = goodsSpecValueWriteMapper.deleteByExample(GoodsSpecValueExample);
        if (count == 0) {
            log.error("根据规格Id：" + specID + "删除规格值表失败");
            throw new MallException("删除规格值表失败");
        }
        return count;
    }

    /**
     * 根据specValueId更新规格值表（系统维护）
     *
     * @param goodsSpecValue
     * @return
     */
    public Integer updateGoodsSpecValue(GoodsSpecValue goodsSpecValue) {
        if (StringUtils.isEmpty(goodsSpecValue.getSpecValueId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsSpecValueWriteMapper.updateByPrimaryKeySelective(goodsSpecValue);
        if (count == 0) {
            log.error("根据specValueId：" + goodsSpecValue.getSpecValueId() + "更新规格值表（系统维护）失败");
            throw new MallException("更新规格值表（系统维护）失败,请重试");
        }
        return count;
    }

    /**
     * 根据specValueId获取规格值表（系统维护）详情
     *
     * @param specValueId specValueId
     * @return
     */
    public GoodsSpecValue getGoodsSpecValueBySpecValueId(Integer specValueId) {
        return goodsSpecValueReadMapper.getByPrimaryKey(specValueId);
    }

    /**
     * 根据条件获取规格值表（系统维护）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsSpecValue> getGoodsSpecValueList(GoodsSpecValueExample example, PagerInfo pager) {
        List<GoodsSpecValue> goodsSpecValueList;
        if (pager != null) {
            pager.setRowsCount(goodsSpecValueReadMapper.countByExample(example));
            goodsSpecValueList = goodsSpecValueReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsSpecValueList = goodsSpecValueReadMapper.listByExample(example);
        }
        return goodsSpecValueList;
    }
}