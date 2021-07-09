package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.DiscountPcDecoReadMapper;
import com.slodon.b2b2c.dao.write.promotion.DiscountPcDecoWriteMapper;
import com.slodon.b2b2c.promotion.example.DiscountPcDecoExample;
import com.slodon.b2b2c.promotion.pojo.DiscountPcDeco;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DiscountPcDecoModel {
    @Resource
    private DiscountPcDecoReadMapper discountPcDecoReadMapper;

    @Resource
    private DiscountPcDecoWriteMapper discountPcDecoWriteMapper;

    /**
     * 新增折扣活动PC首页装修数据
     *
     * @param discountPcDeco
     * @return
     */
    public Integer saveDiscountPcDeco(DiscountPcDeco discountPcDeco) {
        int count = discountPcDecoWriteMapper.insert(discountPcDeco);
        if (count == 0) {
            throw new MallException("添加折扣活动PC首页装修数据失败，请重试");
        }
        return count;
    }

    /**
     * 根据integralDecoId删除折扣活动PC首页装修数据
     *
     * @param integralDecoId integralDecoId
     * @return
     */
    public Integer deleteDiscountPcDeco(Integer integralDecoId) {
        if (StringUtils.isEmpty(integralDecoId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = discountPcDecoWriteMapper.deleteByPrimaryKey(integralDecoId);
        if (count == 0) {
            log.error("根据integralDecoId：" + integralDecoId + "删除折扣活动PC首页装修数据失败");
            throw new MallException("删除折扣活动PC首页装修数据失败,请重试");
        }
        return count;
    }

    /**
     * 根据integralDecoId更新折扣活动PC首页装修数据
     *
     * @param discountPcDeco
     * @return
     */
    public Integer updateDiscountPcDeco(DiscountPcDeco discountPcDeco) {
        if (StringUtils.isEmpty(discountPcDeco.getIntegralDecoId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = discountPcDecoWriteMapper.updateByPrimaryKeySelective(discountPcDeco);
        if (count == 0) {
            log.error("根据integralDecoId：" + discountPcDeco.getIntegralDecoId() + "更新折扣活动PC首页装修数据失败");
            throw new MallException("更新折扣活动PC首页装修数据失败,请重试");
        }
        return count;
    }

    /**
     * 根据integralDecoId获取折扣活动PC首页装修数据详情
     *
     * @param integralDecoId integralDecoId
     * @return
     */
    public DiscountPcDeco getDiscountPcDecoByIntegralDecoId(Integer integralDecoId) {
        return discountPcDecoReadMapper.getByPrimaryKey(integralDecoId);
    }

    /**
     * 根据条件获取折扣活动PC首页装修数据列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<DiscountPcDeco> getDiscountPcDecoList(DiscountPcDecoExample example, PagerInfo pager) {
        List<DiscountPcDeco> discountPcDecoList;
        if (pager != null) {
            pager.setRowsCount(discountPcDecoReadMapper.countByExample(example));
            discountPcDecoList = discountPcDecoReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            discountPcDecoList = discountPcDecoReadMapper.listByExample(example);
        }
        return discountPcDecoList;
    }
}