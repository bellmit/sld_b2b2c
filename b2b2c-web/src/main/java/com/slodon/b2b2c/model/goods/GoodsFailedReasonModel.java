package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsFailedReasonReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsFailedReasonWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.example.GoodsFailedReasonExample;
import com.slodon.b2b2c.goods.pojo.GoodsFailedReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsFailedReasonModel {
    @Resource
    private GoodsFailedReasonReadMapper goodsFailedReasonReadMapper;

    @Resource
    private GoodsFailedReasonWriteMapper goodsFailedReasonWriteMapper;

    /**
     * 新增商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择
     *
     * @param goodsFailedReason
     * @return
     */
    public Integer saveGoodsFailedReason(GoodsFailedReason goodsFailedReason) {
        int count = goodsFailedReasonWriteMapper.insert(goodsFailedReason);
        if (count == 0) {
            throw new MallException("添加商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择失败，请重试");
        }
        return count;
    }

    /**
     * 根据reasonId删除商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择
     *
     * @param reasonId reasonId
     * @return
     */
    public Integer deleteGoodsFailedReason(Integer reasonId) {
        if (StringUtils.isEmpty(reasonId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsFailedReasonWriteMapper.deleteByPrimaryKey(reasonId);
        if (count == 0) {
            log.error("根据reasonId：" + reasonId + "删除商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择失败");
            throw new MallException("删除商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择失败,请重试");
        }
        return count;
    }

    /**
     * 根据reasonId更新商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择
     *
     * @param goodsFailedReason
     * @return
     */
    public Integer updateGoodsFailedReason(GoodsFailedReason goodsFailedReason) {
        if (StringUtils.isEmpty(goodsFailedReason.getReasonId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsFailedReasonWriteMapper.updateByPrimaryKeySelective(goodsFailedReason);
        if (count == 0) {
            log.error("根据reasonId：" + goodsFailedReason.getReasonId() + "更新商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择失败");
            throw new MallException("更新商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择失败,请重试");
        }
        return count;
    }

    /**
     * 根据reasonId获取商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择详情
     *
     * @param reasonId reasonId
     * @return
     */
    public GoodsFailedReason getGoodsFailedReasonByReasonId(Integer reasonId) {
        return goodsFailedReasonReadMapper.getByPrimaryKey(reasonId);
    }

    /**
     * 根据条件获取商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsFailedReason> getGoodsFailedReasonList(GoodsFailedReasonExample example, PagerInfo pager) {
        List<GoodsFailedReason> goodsFailedReasonList;
        if (pager != null) {
            pager.setRowsCount(goodsFailedReasonReadMapper.countByExample(example));
            goodsFailedReasonList = goodsFailedReasonReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsFailedReasonList = goodsFailedReasonReadMapper.listByExample(example);
        }
        return goodsFailedReasonList;
    }
}