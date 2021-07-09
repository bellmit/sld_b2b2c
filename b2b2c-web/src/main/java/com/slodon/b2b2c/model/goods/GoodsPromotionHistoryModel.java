package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsPromotionHistoryReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsPromotionHistoryWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.example.GoodsPromotionHistoryExample;
import com.slodon.b2b2c.goods.pojo.GoodsPromotionHistory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsPromotionHistoryModel {
    @Resource
    private GoodsPromotionHistoryReadMapper goodsPromotionHistoryReadMapper;

    @Resource
    private GoodsPromotionHistoryWriteMapper goodsPromotionHistoryWriteMapper;

    /**
     * 新增商品活动绑定历史信息
     *
     * @param goodsPromotionHistory
     * @return
     */
    public Integer saveGoodsPromotionHistory(GoodsPromotionHistory goodsPromotionHistory) {
        int count = goodsPromotionHistoryWriteMapper.insert(goodsPromotionHistory);
        if (count == 0) {
            throw new MallException("添加商品活动绑定历史信息失败，请重试");
        }
        return count;
    }

    /**
     * 根据historyId删除商品活动绑定历史信息
     *
     * @param historyId historyId
     * @return
     */
    public Integer deleteGoodsPromotionHistory(Integer historyId) {
        if (StringUtils.isEmpty(historyId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsPromotionHistoryWriteMapper.deleteByPrimaryKey(historyId);
        if (count == 0) {
            log.error("根据historyId：" + historyId + "删除商品活动绑定历史信息失败");
            throw new MallException("删除商品活动绑定历史信息失败,请重试");
        }
        return count;
    }

    /**
     * 根据historyId更新商品活动绑定历史信息
     *
     * @param goodsPromotionHistory
     * @return
     */
    public Integer updateGoodsPromotionHistory(GoodsPromotionHistory goodsPromotionHistory) {
        if (StringUtils.isEmpty(goodsPromotionHistory.getHistoryId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsPromotionHistoryWriteMapper.updateByPrimaryKeySelective(goodsPromotionHistory);
        if (count == 0) {
            log.error("根据historyId：" + goodsPromotionHistory.getHistoryId() + "更新商品活动绑定历史信息失败");
            throw new MallException("更新商品活动绑定历史信息失败,请重试");
        }
        return count;
    }

    /**
     * 根据historyId获取商品活动绑定历史信息详情
     *
     * @param historyId historyId
     * @return
     */
    public GoodsPromotionHistory getGoodsPromotionHistoryByHistoryId(Integer historyId) {
        return goodsPromotionHistoryReadMapper.getByPrimaryKey(historyId);
    }

    /**
     * 根据条件获取商品活动绑定历史信息列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsPromotionHistory> getGoodsPromotionHistoryList(GoodsPromotionHistoryExample example, PagerInfo pager) {
        List<GoodsPromotionHistory> goodsPromotionHistoryList;
        if (pager != null) {
            pager.setRowsCount(goodsPromotionHistoryReadMapper.countByExample(example));
            goodsPromotionHistoryList = goodsPromotionHistoryReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsPromotionHistoryList = goodsPromotionHistoryReadMapper.listByExample(example);
        }
        return goodsPromotionHistoryList;
    }
}