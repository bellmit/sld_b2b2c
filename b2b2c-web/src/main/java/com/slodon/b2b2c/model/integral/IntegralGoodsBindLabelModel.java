package com.slodon.b2b2c.model.integral;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsBindLabelReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsBindLabelWriteMapper;
import com.slodon.b2b2c.integral.example.IntegralGoodsBindLabelExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsBindLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 积分商品绑定标签表model
 */
@Component
@Slf4j
public class IntegralGoodsBindLabelModel {
    @Resource
    private IntegralGoodsBindLabelReadMapper integralGoodsBindLabelReadMapper;

    @Resource
    private IntegralGoodsBindLabelWriteMapper integralGoodsBindLabelWriteMapper;

    /**
     * 新增积分商品绑定标签表
     *
     * @param integralGoodsBindLabel
     * @return
     */
    public Integer saveIntegralGoodsBindLabel(IntegralGoodsBindLabel integralGoodsBindLabel) {
        int count = integralGoodsBindLabelWriteMapper.insert(integralGoodsBindLabel);
        if (count == 0) {
            throw new MallException("添加积分商品绑定标签表失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除积分商品绑定标签表
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteIntegralGoodsBindLabel(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralGoodsBindLabelWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除积分商品绑定标签表失败");
            throw new MallException("删除积分商品绑定标签表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId更新积分商品绑定标签表
     *
     * @param integralGoodsBindLabel
     * @return
     */
    public Integer updateIntegralGoodsBindLabel(IntegralGoodsBindLabel integralGoodsBindLabel) {
        if (StringUtils.isEmpty(integralGoodsBindLabel.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralGoodsBindLabelWriteMapper.updateByPrimaryKeySelective(integralGoodsBindLabel);
        if (count == 0) {
            log.error("根据bindId：" + integralGoodsBindLabel.getBindId() + "更新积分商品绑定标签表失败");
            throw new MallException("更新积分商品绑定标签表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取积分商品绑定标签表详情
     *
     * @param bindId bindId
     * @return
     */
    public IntegralGoodsBindLabel getIntegralGoodsBindLabelByBindId(Integer bindId) {
        return integralGoodsBindLabelReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取积分商品绑定标签表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralGoodsBindLabel> getIntegralGoodsBindLabelList(IntegralGoodsBindLabelExample example, PagerInfo pager) {
        List<IntegralGoodsBindLabel> integralGoodsBindLabelList;
        if (pager != null) {
            pager.setRowsCount(integralGoodsBindLabelReadMapper.countByExample(example));
            integralGoodsBindLabelList = integralGoodsBindLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralGoodsBindLabelList = integralGoodsBindLabelReadMapper.listByExample(example);
        }
        return integralGoodsBindLabelList;
    }
}