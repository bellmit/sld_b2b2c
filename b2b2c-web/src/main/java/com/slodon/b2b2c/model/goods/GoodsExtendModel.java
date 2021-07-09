package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsExtendReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsExtendWriteMapper;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsExtendModel {

    @Resource
    private GoodsExtendReadMapper goodsExtendReadMapper;
    @Resource
    private GoodsExtendWriteMapper goodsExtendWriteMapper;

    /**
     * 新增商品扩展信息表
     *
     * @param goodsExtend
     * @return
     */
    public Integer saveGoodsExtend(GoodsExtend goodsExtend) {
        int count = goodsExtendWriteMapper.insert(goodsExtend);
        if (count == 0) {
            throw new MallException("添加商品扩展信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据extendId删除商品扩展信息表
     *
     * @param extendId extendId
     * @return
     */
    public Integer deleteGoodsExtend(Long extendId) {
        if (StringUtils.isEmpty(extendId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsExtendWriteMapper.deleteByPrimaryKey(extendId);
        if (count == 0) {
            log.error("根据extendId：" + extendId + "删除商品扩展信息表失败");
            throw new MallException("删除商品扩展信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId更新商品扩展信息表
     *
     * @param goodsExtend
     * @return
     */
    public Integer updateGoodsExtend(GoodsExtend goodsExtend) {
        if (StringUtils.isEmpty(goodsExtend.getExtendId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsExtendWriteMapper.updateByPrimaryKeySelective(goodsExtend);
        if (count == 0) {
            log.error("根据extendId：" + goodsExtend.getExtendId() + "更新商品扩展信息表失败");
            throw new MallException("更新商品扩展信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件更新商品扩展信息表
     *
     * @param goodsExtend
     * @param example
     * @return
     */
    public Integer updateGoodsExtendByExample(GoodsExtend goodsExtend, GoodsExtendExample example) {
        return goodsExtendWriteMapper.updateByExampleSelective(goodsExtend, example);
    }

    /**
     * 根据extendId获取商品扩展信息表详情
     *
     * @param extendId extendId
     * @return
     */
    public GoodsExtend getGoodsExtendByExtendId(Long extendId) {
        return goodsExtendReadMapper.getByPrimaryKey(extendId);
    }

    /**
     * 根据goodsId获取商品扩展信息表详情
     *
     * @param goodsId goodsId
     * @return
     */
    public GoodsExtend getGoodsExtendByGoodsId(Long goodsId) {
        GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
        goodsExtendExample.setGoodsId(goodsId);
        List<GoodsExtend> goodsExtendList = getGoodsExtendList(goodsExtendExample, null);
        AssertUtil.notEmpty(goodsExtendList, "获取商品扩展信息为空");
        return goodsExtendList.get(0);
    }

    /**
     * 根据条件获取商品扩展信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsExtend> getGoodsExtendList(GoodsExtendExample example, PagerInfo pager) {
        List<GoodsExtend> goodsExtendList;
        if (pager != null) {
            pager.setRowsCount(goodsExtendReadMapper.countByExample(example));
            goodsExtendList = goodsExtendReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsExtendList = goodsExtendReadMapper.listByExample(example);
        }
        return goodsExtendList;
    }


}