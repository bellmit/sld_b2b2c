package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.promotion.DiscountGoodsReadMapper;
import com.slodon.b2b2c.dao.write.promotion.DiscountGoodsWriteMapper;
import com.slodon.b2b2c.promotion.example.DiscountGoodsExample;
import com.slodon.b2b2c.promotion.pojo.DiscountGoods;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class DiscountGoodsModel {
    @Resource
    private DiscountGoodsReadMapper discountGoodsReadMapper;

    @Resource
    private DiscountGoodsWriteMapper discountGoodsWriteMapper;

    /**
     * 新增折扣活动商品关联表
     *
     * @param discountGoods
     * @return
     */
    public Integer saveDiscountGoods(DiscountGoods discountGoods) {
        int count = discountGoodsWriteMapper.insert(discountGoods);
        if (count == 0) {
            throw new MallException("添加折扣活动商品关联表失败，请重试");
        }
        return count;
    }

    /**
     * 根据discountGoodsId删除折扣活动商品关联表
     *
     * @param discountGoodsId discountGoodsId
     * @return
     */
    public Integer deleteDiscountGoods(Integer discountGoodsId) {
        if (StringUtils.isEmpty(discountGoodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = discountGoodsWriteMapper.deleteByPrimaryKey(discountGoodsId);
        if (count == 0) {
            log.error("根据discountGoodsId：" + discountGoodsId + "删除折扣活动商品关联表失败");
            throw new MallException("删除折扣活动商品关联表失败,请重试");
        }
        return count;
    }

    /**
     * 根据discountGoodsId更新折扣活动商品关联表
     *
     * @param discountGoods
     * @return
     */
    public Integer updateDiscountGoods(DiscountGoods discountGoods) {
        if (StringUtils.isEmpty(discountGoods.getDiscountGoodsId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = discountGoodsWriteMapper.updateByPrimaryKeySelective(discountGoods);
        if (count == 0) {
            log.error("根据discountGoodsId：" + discountGoods.getDiscountGoodsId() + "更新折扣活动商品关联表失败");
            throw new MallException("更新折扣活动商品关联表失败,请重试");
        }
        return count;
    }

    /**
     * 根据discountGoodsId获取折扣活动商品关联表详情
     *
     * @param discountGoodsId discountGoodsId
     * @return
     */
    public DiscountGoods getDiscountGoodsByDiscountGoodsId(Integer discountGoodsId) {
        return discountGoodsReadMapper.getByPrimaryKey(discountGoodsId);
    }

    /**
     * 根据条件获取折扣活动商品关联表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<DiscountGoods> getDiscountGoodsList(DiscountGoodsExample example, PagerInfo pager) {
        List<DiscountGoods> discountGoodsList;
        if (pager != null) {
            pager.setRowsCount(discountGoodsReadMapper.countByExample(example));
            discountGoodsList = discountGoodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            discountGoodsList = discountGoodsReadMapper.listByExample(example);
        }
        return discountGoodsList;
    }
}