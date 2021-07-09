package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsLabelReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsLabelWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.dto.GoodsLabelAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsLabelEditDTO;
import com.slodon.b2b2c.goods.example.GoodsLabelExample;
import com.slodon.b2b2c.goods.pojo.GoodsLabel;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 商品标签model
 */

@Component
@Slf4j
public class GoodsLabelModel {
    @Resource
    private GoodsLabelReadMapper goodsLabelReadMapper;

    @Resource
    private GoodsLabelWriteMapper goodsLabelWriteMapper;


    /**
     * 新增商品标签
     *
     * @param goodsLabel
     * @return
     */
    public Integer saveGoodsLabel(GoodsLabel goodsLabel) {
        int count = goodsLabelWriteMapper.insert(goodsLabel);
        if (count == 0) {
            throw new MallException("添加商品标签失败，请重试");
        }
        return count;
    }

    /**
     * 新增商品标签
     *
     * @param goodsLabelAddDTO,adminId
     * @return
     */
    public Integer saveGoodsLabel(GoodsLabelAddDTO goodsLabelAddDTO, Integer adminId) throws Exception {

        //根据标签名称查重
        GoodsLabelExample example = new GoodsLabelExample();
        example.setLabelName(goodsLabelAddDTO.getLabelName());
        List<GoodsLabel> list = goodsLabelReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("商品标签名称重复,请重新填写");
        }

        //新增商品标签
        GoodsLabel insertOne = new GoodsLabel();
        PropertyUtils.copyProperties(insertOne, goodsLabelAddDTO);

        insertOne.setCreateAdminId(adminId);
        insertOne.setCreateTime(new Date());

        int count = goodsLabelWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加商品标签失败，请重试");
        }
        return count;
    }

    /**
     * 根据labelId删除商品标签
     *
     * @param labelId labelId
     * @return
     */
    public Integer deleteGoodsLabel(Integer labelId) {
        if (StringUtils.isEmpty(labelId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsLabelWriteMapper.deleteByPrimaryKey(labelId);
        if (count == 0) {
            log.error("根据labelId：" + labelId + "删除商品标签失败");
            throw new MallException("删除商品标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId更新商品标签
     *
     * @param goodsLabel
     * @return
     */
    public Integer updateGoodsLabel(GoodsLabel goodsLabel) {
        if (StringUtils.isEmpty(goodsLabel.getLabelId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsLabelWriteMapper.updateByPrimaryKeySelective(goodsLabel);
        if (count == 0) {
            log.error("根据labelId：" + goodsLabel.getLabelId() + "更新商品标签失败");
            throw new MallException("更新商品标签失败,请重试");
        }
        return count;
    }


    /**
     * 根据GoodsLabelEditDTO更新商品标签
     *
     * @param goodsLabelEditDTO
     * @return
     */
    @Transactional
    public Integer updateGoodsLabel(GoodsLabelEditDTO goodsLabelEditDTO) throws Exception {
        //根据标签名称查重
        GoodsLabelExample example = new GoodsLabelExample();
        example.setLabelName(goodsLabelEditDTO.getLabelName());
        List<GoodsLabel> list = goodsLabelReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            if (list.size() > 0) {
                if (list.get(0).getLabelId() != goodsLabelEditDTO.getLabelId()) {
                    throw new MallException("商品标签名称重复,请重新填写");
                }
            }
        }

        //修改商品标签
        GoodsLabel updateOne = new GoodsLabel();
        PropertyUtils.copyProperties(updateOne, goodsLabelEditDTO);
        updateOne.setUpdateTime(new Date());

        int count = goodsLabelWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据labelId：" + goodsLabelEditDTO.getLabelId() + "更新商品标签失败");
            throw new MallException("更新商品标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId获取商品标签详情
     *
     * @param labelId labelId
     * @return
     */
    public GoodsLabel getGoodsLabelByLabelId(Integer labelId) {
        return goodsLabelReadMapper.getByPrimaryKey(labelId);
    }

    /**
     * 根据条件获取商品标签列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsLabel> getGoodsLabelList(GoodsLabelExample example, PagerInfo pager) {
        List<GoodsLabel> goodsLabelList;
        if (pager != null) {
            pager.setRowsCount(goodsLabelReadMapper.countByExample(example));
            goodsLabelList = goodsLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsLabelList = goodsLabelReadMapper.listByExample(example);
        }
        return goodsLabelList;
    }

    /**
     * 根据条件获取商品标签列表
     *
     * @param serviceLabelIds 多个LabelId
     * @return
     */
    public List<GoodsLabel> getGoodsLabelByLabelIds(String serviceLabelIds) {
        List<GoodsLabel> goodsLabelList;
        GoodsLabelExample goodsLabelExample=new GoodsLabelExample();
        goodsLabelExample.setLabelIdIn(serviceLabelIds);
        goodsLabelList = goodsLabelReadMapper.listByExample(goodsLabelExample);
        return goodsLabelList;
    }


    /**
     * 根据labelId批量删除商品标签
     *
     * @param labelIds
     * @return
     */
    @Transactional
    public void batchDeleteGoodsLabel(String labelIds) {
        if (StringUtils.isEmpty(labelIds)) {
            throw new MallException("请选择要删除的数据");
        }
        //组装商品标签删除的条件
        GoodsLabelExample example = new GoodsLabelExample();
        example.setLabelIdIn(labelIds);
        int count = goodsLabelWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据labbelIds:" + labelIds + "批量删除商品标签失败");
            throw new MallException("删除商品标签失败,请重试!");
        }

    }
}