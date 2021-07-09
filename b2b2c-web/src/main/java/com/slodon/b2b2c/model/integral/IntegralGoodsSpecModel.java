package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsSpecReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsSpecWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralGoodsSpecExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 积分商品规格表model
 */
@Component
@Slf4j
public class IntegralGoodsSpecModel {

    @Resource
    private IntegralGoodsSpecReadMapper integralGoodsSpecReadMapper;
    @Resource
    private IntegralGoodsSpecWriteMapper integralGoodsSpecWriteMapper;

    /**
     * 新增积分商品规格表
     *
     * @param integralGoodsSpec
     * @return
     */
    public Integer saveIntegralGoodsSpec(IntegralGoodsSpec integralGoodsSpec) {
        int count = integralGoodsSpecWriteMapper.insert(integralGoodsSpec);
        if (count == 0) {
            throw new MallException("添加积分商品规格表失败，请重试");
        }
        return integralGoodsSpec.getSpecId();
    }

    /**
     * 根据specId删除积分商品规格表
     *
     * @param specId specId
     * @return
     */
    public Integer deleteIntegralGoodsSpec(Integer specId) {
        if (StringUtils.isEmpty(specId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralGoodsSpecWriteMapper.deleteByPrimaryKey(specId);
        if (count == 0) {
            log.error("根据specId：" + specId + "删除积分商品规格表失败");
            throw new MallException("删除积分商品规格表失败,请重试");
        }
        return count;
    }

    /**
     * 根据specId更新积分商品规格表
     *
     * @param integralGoodsSpec
     * @return
     */
    public Integer updateIntegralGoodsSpec(IntegralGoodsSpec integralGoodsSpec) {
        if (StringUtils.isEmpty(integralGoodsSpec.getSpecId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralGoodsSpecWriteMapper.updateByPrimaryKeySelective(integralGoodsSpec);
        if (count == 0) {
            log.error("根据specId：" + integralGoodsSpec.getSpecId() + "更新积分商品规格表失败");
            throw new MallException("更新积分商品规格表失败,请重试");
        }
        return count;
    }

    /**
     * 根据specId获取积分商品规格表详情
     *
     * @param specId specId
     * @return
     */
    public IntegralGoodsSpec getIntegralGoodsSpecBySpecId(Integer specId) {
        return integralGoodsSpecReadMapper.getByPrimaryKey(specId);
    }

    /**
     * 根据条件获取积分商品规格表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralGoodsSpec> getIntegralGoodsSpecList(IntegralGoodsSpecExample example, PagerInfo pager) {
        List<IntegralGoodsSpec> integralGoodsSpecList;
        if (pager != null) {
            pager.setRowsCount(integralGoodsSpecReadMapper.countByExample(example));
            integralGoodsSpecList = integralGoodsSpecReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralGoodsSpecList = integralGoodsSpecReadMapper.listByExample(example);
        }
        return integralGoodsSpecList;
    }
}