package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.SeckillLabelReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SeckillLabelWriteMapper;
import com.slodon.b2b2c.promotion.dto.SeckillLabelAddDTO;
import com.slodon.b2b2c.promotion.dto.SeckillLabelEditDTO;
import com.slodon.b2b2c.promotion.example.SeckillLabelExample;
import com.slodon.b2b2c.promotion.pojo.SeckillLabel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 秒杀标签model
 */
@Component
@Slf4j
public class SeckillLabelModel {

    @Resource
    private SeckillLabelReadMapper seckillLabelReadMapper;
    @Resource
    private SeckillLabelWriteMapper seckillLabelWriteMapper;

    /**
     * 新增秒杀标签
     *
     * @param SeckillLabel
     * @return
     */
    public Integer saveSeckillLabel(SeckillLabel SeckillLabel) {
        int count = seckillLabelWriteMapper.insert(SeckillLabel);
        if (count == 0) {
            throw new MallException("添加秒杀标签失败，请重试");
        }
        return count;
    }

    /**
     * 新增秒杀活动标签
     *
     * @param SeckillLabelAddDTO
     * @param adminId
     * @return
     */
    public Integer saveSeckillLabel(SeckillLabelAddDTO SeckillLabelAddDTO, Integer adminId) throws Exception {

        //根据标签名称查重
        SeckillLabelExample example = new SeckillLabelExample();
        example.setLabelName(SeckillLabelAddDTO.getLabelName());
        List<SeckillLabel> list = seckillLabelReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("秒杀标签名称重复,请重新填写");
        }

        //新增秒杀标签
        SeckillLabel insertOne = new SeckillLabel();
        PropertyUtils.copyProperties(insertOne, SeckillLabelAddDTO);

        insertOne.setCreateAdminId(adminId);
        insertOne.setCreateTime(new Date());
        insertOne.setIsShow(SeckillConst.IS_SHOW_YES);

        int count = seckillLabelWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加秒杀标签失败，请重试");
        }
        return count;
    }

    /**
     * 根据labelId删除秒杀标签
     *
     * @param labelId labelId
     * @return
     */
    public Integer deleteSeckillLabel(Integer labelId) {
        if (StringUtils.isEmpty(labelId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = seckillLabelWriteMapper.deleteByPrimaryKey(labelId);
        if (count == 0) {
            log.error("根据labelId：" + labelId + "删除秒杀标签失败");
            throw new MallException("删除秒杀标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId更新秒杀标签
     *
     * @param SeckillLabel
     * @return
     */
    public Integer updateSeckillLabel(SeckillLabel SeckillLabel) {
        if (StringUtils.isEmpty(SeckillLabel.getLabelId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = seckillLabelWriteMapper.updateByPrimaryKeySelective(SeckillLabel);
        if (count == 0) {
            log.error("根据labelId：" + SeckillLabel.getLabelId() + "更新秒杀标签失败");
            throw new MallException("更新秒杀标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据SeckillLabelEditDTO更新秒杀标签
     *
     * @param SeckillLabelEditDTO
     * @return
     */
    @Transactional
    public Integer updateSeckillLabel(SeckillLabelEditDTO SeckillLabelEditDTO, Integer adminId) throws Exception {
        //根据标签名称查重
        SeckillLabelExample example = new SeckillLabelExample();
        example.setLabelName(SeckillLabelEditDTO.getLabelName());
        example.setLabelIdNotEquals(SeckillLabelEditDTO.getLabelId());
        List<SeckillLabel> list = seckillLabelReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("秒杀标签名称重复,请重新填写");
        }

        //修改秒杀标签
        SeckillLabel updateOne = new SeckillLabel();
        PropertyUtils.copyProperties(updateOne, SeckillLabelEditDTO);
        updateOne.setUpdateTime(new Date());
        updateOne.setUpdateAdminId(adminId);
        int count = seckillLabelWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据labelId：" + SeckillLabelEditDTO.getLabelId() + "更新秒杀标签失败");
            throw new MallException("更新秒杀标签失败,请重试");
        }
        return count;
    }


    /**
     * 根据labelId设置秒杀活动标签显示状态
     *
     * @param labelId
     * @param is_show
     * @param adminId
     * @return
     */
    public Integer setSeckillLabel(Integer labelId, Integer is_show, Integer adminId) {
        //修改秒杀标签
        SeckillLabel updateOne = new SeckillLabel();
        updateOne.setLabelId(labelId);
        updateOne.setIsShow(is_show);
        updateOne.setUpdateTime(new Date());
        updateOne.setUpdateAdminId(adminId);

        int count = seckillLabelWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据labelId：" + labelId + "设置秒杀活动标签显示失败");
            throw new MallException("设置秒杀活动标签显示失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId获取秒杀标签详情
     *
     * @param labelId labelId
     * @return
     */
    public SeckillLabel getSeckillLabelByLabelId(Integer labelId) {
        return seckillLabelReadMapper.getByPrimaryKey(labelId);
    }

    /**
     * 根据条件获取秒杀标签列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillLabel> getSeckillLabelList(SeckillLabelExample example, PagerInfo pager) {
        List<SeckillLabel> SeckillLabelList;
        if (pager != null) {
            pager.setRowsCount(seckillLabelReadMapper.countByExample(example));
            SeckillLabelList = seckillLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            SeckillLabelList = seckillLabelReadMapper.listByExample(example);
        }
        return SeckillLabelList;
    }

    /**
     * 根据labelId批量删除秒杀标签
     *
     * @param labelIds
     * @return
     */
    @Transactional
    public void batchDeleteSeckillLabel(String labelIds) {
        //组装商品标签删除的条件
        SeckillLabelExample example = new SeckillLabelExample();
        example.setLabelIdIn(labelIds);
        int count = seckillLabelWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据labbelIds:" + labelIds + "批量删除秒杀标签失败");
            throw new MallException("删除秒杀标签失败,请重试!");
        }
    }
}