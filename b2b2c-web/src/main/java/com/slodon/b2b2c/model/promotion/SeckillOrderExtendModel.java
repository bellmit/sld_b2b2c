package com.slodon.b2b2c.model.promotion;


import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.promotion.SeckillOrderExtendReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SeckillOrderExtendWriteMapper;
import com.slodon.b2b2c.promotion.example.SeckillOrderExtendExample;
import com.slodon.b2b2c.promotion.example.SeckillStageProductExample;
import com.slodon.b2b2c.promotion.pojo.SeckillOrderExtend;
import com.slodon.b2b2c.promotion.pojo.SeckillStage;
import com.slodon.b2b2c.promotion.pojo.SeckillStageProduct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 秒杀订单表model
 */
@Component
@Slf4j
public class SeckillOrderExtendModel {
    @Resource
    private SeckillOrderExtendReadMapper seckillOrderExtendReadMapper;

    @Resource
    private SeckillOrderExtendWriteMapper seckillOrderExtendWriteMapper;
    @Resource
    private SeckillStageModel seckillStageModel;
    @Resource
    private SeckillStageProductModel seckillStageProductModel;

    /**
     * 新增秒杀订单表
     *
     * @param seckillOrderExtend
     * @return
     */
    public Integer saveSeckillOrderExtend(SeckillOrderExtend seckillOrderExtend) {
        int count = seckillOrderExtendWriteMapper.insert(seckillOrderExtend);
        if (count == 0) {
            throw new MallException("添加秒杀订单表失败，请重试");
        }
        return count;
    }


    /**
     * 新增秒杀订单表
     *
     * @param orderSn
     * @param paySn
     * @param promotionId
     * @param productId
     * @return
     */
    public Integer saveSeckillOrderExtend(String orderSn, String paySn, Integer promotionId, Long productId) {
        //查询秒杀活动场次信息
        SeckillStage seckillStage = seckillStageModel.getSeckillStageByStageId(promotionId);
        AssertUtil.notNull(seckillStage, "获取秒杀活动信息为空");
        //查询秒杀商品信息
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setStageId(promotionId);
        example.setProductId(productId);
        List<SeckillStageProduct> seckillStageProductList = seckillStageProductModel.getSeckillStageProductList(example,null);
        AssertUtil.notEmpty(seckillStageProductList, "获取秒杀商品信息为空");
        SeckillStageProduct seckillStageProduct = seckillStageProductList.get(0);
        //构造秒杀订单扩展信息
        SeckillOrderExtend seckillOrderExtend = new SeckillOrderExtend();
        seckillOrderExtend.setOrderSn(orderSn);
        seckillOrderExtend.setSeckillId(seckillStageProduct.getSeckillId());
        seckillOrderExtend.setStageId(seckillStageProduct.getStageId());
        seckillOrderExtend.setSeckillPrice(seckillStageProduct.getSeckillPrice());
        seckillOrderExtend.setStageProductstageProductId(seckillStageProduct.getStageProductId());
        return this.saveSeckillOrderExtend(seckillOrderExtend);
    }

    /**
     * 根据externId删除秒杀订单表
     *
     * @param externId externId
     * @return
     */
    public Integer deleteSeckillOrderExtend(Integer externId) {
        if (StringUtils.isEmpty(externId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = seckillOrderExtendWriteMapper.deleteByPrimaryKey(externId);
        if (count == 0) {
            log.error("根据externId：" + externId + "删除秒杀订单表失败");
            throw new MallException("删除秒杀订单表失败,请重试");
        }
        return count;
    }

    /**
     * 根据externId更新秒杀订单表
     *
     * @param seckillOrderExtend
     * @return
     */
    public Integer updateSeckillOrderExtend(SeckillOrderExtend seckillOrderExtend) {
        if (StringUtils.isEmpty(seckillOrderExtend.getExternId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = seckillOrderExtendWriteMapper.updateByPrimaryKeySelective(seckillOrderExtend);
        if (count == 0) {
            log.error("根据externId：" + seckillOrderExtend.getExternId() + "更新秒杀订单表失败");
            throw new MallException("更新秒杀订单表失败,请重试");
        }
        return count;
    }

    /**
     * 根据externId获取秒杀订单表详情
     *
     * @param externId externId
     * @return
     */
    public SeckillOrderExtend getSeckillOrderExtendByExternId(Integer externId) {
        return seckillOrderExtendReadMapper.getByPrimaryKey(externId);
    }

    /**
     * 根据条件获取秒杀订单表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillOrderExtend> getSeckillOrderExtendList(SeckillOrderExtendExample example, PagerInfo pager) {
        List<SeckillOrderExtend> seckillOrderExtendList;
        if (pager != null) {
            pager.setRowsCount(seckillOrderExtendReadMapper.countByExample(example));
            seckillOrderExtendList = seckillOrderExtendReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            seckillOrderExtendList = seckillOrderExtendReadMapper.listByExample(example);
        }
        return seckillOrderExtendList;
    }
}