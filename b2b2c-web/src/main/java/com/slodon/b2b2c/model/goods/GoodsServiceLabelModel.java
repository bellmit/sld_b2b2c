package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsServiceLabelReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsServiceLabelWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.dto.GoodsServiceLabelAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsServiceLabelEditDTO;
import com.slodon.b2b2c.goods.example.GoodsServiceLabelExample;
import com.slodon.b2b2c.goods.pojo.GoodsServiceLabel;
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
 * 商品服务标签（比如：7天无理由退货、急速发货）model
 */
@Component
@Slf4j
public class GoodsServiceLabelModel {

    @Resource
    private GoodsServiceLabelReadMapper goodsServiceLabelReadMapper;
    @Resource
    private GoodsServiceLabelWriteMapper goodsServiceLabelWriteMapper;

    /**
     * 新增商品服务标签（比如：7天无理由退货、急速发货）
     *
     * @param goodsServiceLabel
     * @return
     */
    public Integer saveGoodsServiceLabel(GoodsServiceLabel goodsServiceLabel) {
        int count = goodsServiceLabelWriteMapper.insert(goodsServiceLabel);
        if (count == 0) {
            throw new MallException("添加商品服务标签（比如：7天无理由退货、急速发货）失败，请重试");
        }
        return count;
    }

    /**
     * 新增商品服务标签（比如：7天无理由退货、急速发货）
     *
     * @param goodsServiceLabelAddDTO,adminId
     * @return
     */
    public Integer saveGoodsServiceLabel(GoodsServiceLabelAddDTO goodsServiceLabelAddDTO, Integer adminId) throws Exception {

        //根据标签名称查重
        GoodsServiceLabelExample example = new GoodsServiceLabelExample();
        example.setLabelName(goodsServiceLabelAddDTO.getLabelName());
        List<GoodsServiceLabel> list = getGoodsServiceLabelList(example, null);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("商品服务标签名称重复,请重新填写");
        }

        //新增商品标签
        GoodsServiceLabel insertOne = new GoodsServiceLabel();
        PropertyUtils.copyProperties(insertOne, goodsServiceLabelAddDTO);

        insertOne.setCreateAdminId(adminId);
        insertOne.setCreateTime(new Date());

        int count = goodsServiceLabelWriteMapper.insert(insertOne);
        if (count == 0) {
            throw new MallException("添加商品服务标签失败，请重试");
        }
        return count;
    }

    /**
     * 根据labelId删除商品服务标签（比如：7天无理由退货、急速发货）
     *
     * @param labelId labelId
     * @return
     */
    public Integer deleteGoodsServiceLabel(Integer labelId) {
        if (StringUtils.isEmpty(labelId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsServiceLabelWriteMapper.deleteByPrimaryKey(labelId);
        if (count == 0) {
            log.error("根据labelId：" + labelId + "删除商品服务标签（比如：7天无理由退货、急速发货）失败");
            throw new MallException("删除商品服务标签（比如：7天无理由退货、急速发货）失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId更新商品服务标签（比如：7天无理由退货、急速发货）
     *
     * @param goodsServiceLabel
     * @return
     */
    public Integer updateGoodsServiceLabel(GoodsServiceLabel goodsServiceLabel) {
        if (StringUtils.isEmpty(goodsServiceLabel.getLabelId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsServiceLabelWriteMapper.updateByPrimaryKeySelective(goodsServiceLabel);
        if (count == 0) {
            log.error("根据labelId：" + goodsServiceLabel.getLabelId() + "更新商品服务标签（比如：7天无理由退货、急速发货）失败");
            throw new MallException("更新商品服务标签（比如：7天无理由退货、急速发货）失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId更新商品服务标签（比如：7天无理由退货、急速发货）
     *
     * @param goodsServiceLabelEditDTO
     * @return
     */
    public Integer updateGoodsServiceLabel(GoodsServiceLabelEditDTO goodsServiceLabelEditDTO) throws Exception {
        //根据标签名称查重
        GoodsServiceLabelExample example = new GoodsServiceLabelExample();
        example.setLabelName(goodsServiceLabelEditDTO.getLabelName());
        List<GoodsServiceLabel> list = goodsServiceLabelReadMapper.listByExample(example);
        if (!CollectionUtils.isEmpty(list)) {
            throw new MallException("商品服务标签名称重复,请重新填写");
        }

        //修改商品标签
        GoodsServiceLabel updateOne = new GoodsServiceLabel();
        PropertyUtils.copyProperties(updateOne, goodsServiceLabelEditDTO);
        updateOne.setUpdateTime(new Date());

        int count = goodsServiceLabelWriteMapper.updateByPrimaryKeySelective(updateOne);
        if (count == 0) {
            log.error("根据labelId：" + goodsServiceLabelEditDTO.getLabelId() + "更新商品服务标签失败");
            throw new MallException("更新商品服务标签失败,请重试");
        }
        return count;
    }

    /**
     * 根据labelId获取商品服务标签（比如：7天无理由退货、急速发货）详情
     *
     * @param labelId labelId
     * @return
     */
    public GoodsServiceLabel getGoodsServiceLabelByLabelId(Integer labelId) {
        return goodsServiceLabelReadMapper.getByPrimaryKey(labelId);
    }

    /**
     * 根据条件获取商品服务标签（比如：7天无理由退货、急速发货）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsServiceLabel> getGoodsServiceLabelList(GoodsServiceLabelExample example, PagerInfo pager) {
        List<GoodsServiceLabel> goodsServiceLabelList;
        if (pager != null) {
            pager.setRowsCount(goodsServiceLabelReadMapper.countByExample(example));
            goodsServiceLabelList = goodsServiceLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsServiceLabelList = goodsServiceLabelReadMapper.listByExample(example);
        }
        return goodsServiceLabelList;
    }

    /**
     * 根据labelId批量删除商品服务标签
     *
     * @param labelIds
     * @return
     */
    @Transactional
    public void batchDeleteGoodServiceLabel(String labelIds) {
        if (StringUtils.isEmpty(labelIds)) {
            throw new MallException("请选择要删除的数据");
        }
        //组装商品标签删除的条件
        GoodsServiceLabelExample example = new GoodsServiceLabelExample();
        example.setLabelIdIn(labelIds);
        int count = goodsServiceLabelWriteMapper.deleteByExample(example);
        if (count == 0) {
            log.error("根据labbelIds:" + labelIds + "批量删除商品服务标签失败");
            throw new MallException("删除商品服务标签失败,请重试!");
        }

    }
}