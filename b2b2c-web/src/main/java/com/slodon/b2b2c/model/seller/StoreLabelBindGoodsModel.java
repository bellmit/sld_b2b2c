package com.slodon.b2b2c.model.seller;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.seller.StoreLabelBindGoodsReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreLabelBindGoodsWriteMapper;
import com.slodon.b2b2c.seller.example.StoreLabelBindGoodsExample;
import com.slodon.b2b2c.seller.pojo.StoreLabelBindGoods;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class StoreLabelBindGoodsModel {

    @Resource
    private StoreLabelBindGoodsReadMapper storeLabelBindGoodsReadMapper;
    @Resource
    private StoreLabelBindGoodsWriteMapper storeLabelBindGoodsWriteMapper;

    /**
     * 新增店铺内“商品-标签”绑定关系
     *
     * @param storeLabelBindGoods
     * @return
     */
    public Integer saveStoreLabelBindGoods(StoreLabelBindGoods storeLabelBindGoods) {
        int count = storeLabelBindGoodsWriteMapper.insert(storeLabelBindGoods);
        if (count == 0) {
            throw new MallException("添加店铺内“商品-标签”绑定关系失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除店铺内“商品-标签”绑定关系
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteStoreLabelBindGoods(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = storeLabelBindGoodsWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除店铺内“商品-标签”绑定关系失败");
            throw new MallException("删除店铺内“商品-标签”绑定关系失败,请重试");
        }
        return count;
    }

    /**
     * 根据商品id删除绑定关系
     *
     * @param goodsId
     */
    public void deleteStoreLabelBindGoodsByGoodsId(Long goodsId) {
        AssertUtil.notNullOrZero(goodsId, "请选择要删除的数据");
        StoreLabelBindGoodsExample example = new StoreLabelBindGoodsExample();
        example.setGoodsId(goodsId);
        storeLabelBindGoodsWriteMapper.deleteByExample(example);
    }

    /**
     * 根据bindId更新店铺内“商品-标签”绑定关系
     *
     * @param storeLabelBindGoods
     * @return
     */
    public Integer updateStoreLabelBindGoods(StoreLabelBindGoods storeLabelBindGoods) {
        if (StringUtils.isEmpty(storeLabelBindGoods.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = storeLabelBindGoodsWriteMapper.updateByPrimaryKeySelective(storeLabelBindGoods);
        if (count == 0) {
            log.error("根据bindId：" + storeLabelBindGoods.getBindId() + "更新店铺内“商品-标签”绑定关系失败");
            throw new MallException("更新店铺内“商品-标签”绑定关系失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取店铺内“商品-标签”绑定关系详情
     *
     * @param bindId bindId
     * @return
     */
    public StoreLabelBindGoods getStoreLabelBindGoodsByBindId(Integer bindId) {
        return storeLabelBindGoodsReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取店铺内“商品-标签”绑定关系列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<StoreLabelBindGoods> getStoreLabelBindGoodsList(StoreLabelBindGoodsExample example, PagerInfo pager) {
        List<StoreLabelBindGoods> storeLabelBindGoodsList;
        if (pager != null) {
            pager.setRowsCount(storeLabelBindGoodsReadMapper.countByExample(example));
            storeLabelBindGoodsList = storeLabelBindGoodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            storeLabelBindGoodsList = storeLabelBindGoodsReadMapper.listByExample(example);
        }
        return storeLabelBindGoodsList;
    }
}