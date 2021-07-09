package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsBindLabelReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsBindLabelWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.example.GoodsBindLabelExample;
import com.slodon.b2b2c.goods.pojo.GoodsBindLabel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsBindLabelModel {
    @Resource
    private GoodsBindLabelReadMapper goodsBindLabelReadMapper;

    @Resource
    private GoodsBindLabelWriteMapper goodsBindLabelWriteMapper;


    /**
     * 新增商品标签和商品绑定关系
     *
     * @param goodsBindLabel
     * @return
     */
    public Integer saveGoodsBindLabel(GoodsBindLabel goodsBindLabel) {
        int count = goodsBindLabelWriteMapper.insert(goodsBindLabel);
        if (count == 0) {
            throw new MallException("添加商品标签和商品绑定关系失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除商品标签和商品绑定关系
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteGoodsBindLabel(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsBindLabelWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除商品标签和商品绑定关系失败");
            throw new MallException("删除商品标签和商品绑定关系失败,请重试");
        }
        return count;
    }

    /**
     * 根据goodsId删除商品标签和商品绑定关系
     *
     * @param goodsId goodsId
     * @return
     */
    public void deleteGoodsBindLabelByGoodsId(Long goodsId) {
        if (StringUtils.isEmpty(goodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        GoodsBindLabelExample goodsBindLabelExample=new GoodsBindLabelExample();
        goodsBindLabelExample.setGoodsId(goodsId);
        goodsBindLabelWriteMapper.deleteByExample(goodsBindLabelExample);
    }

    /**
     * 根据bindId更新商品标签和商品绑定关系
     *
     * @param goodsBindLabel
     * @return
     */
    public Integer updateGoodsBindLabel(GoodsBindLabel goodsBindLabel) {
        if (StringUtils.isEmpty(goodsBindLabel.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsBindLabelWriteMapper.updateByPrimaryKeySelective(goodsBindLabel);
        if (count == 0) {
            log.error("根据bindId：" + goodsBindLabel.getBindId() + "更新商品标签和商品绑定关系失败");
            throw new MallException("更新商品标签和商品绑定关系失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取商品标签和商品绑定关系详情
     *
     * @param bindId bindId
     * @return
     */
    public GoodsBindLabel getGoodsBindLabelByBindId(Integer bindId) {
        return goodsBindLabelReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取商品标签和商品绑定关系列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsBindLabel> getGoodsBindLabelList(GoodsBindLabelExample example, PagerInfo pager) {
        List<GoodsBindLabel> goodsBindLabelList;
        if (pager != null) {
            pager.setRowsCount(goodsBindLabelReadMapper.countByExample(example));
            goodsBindLabelList = goodsBindLabelReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsBindLabelList = goodsBindLabelReadMapper.listByExample(example);
        }
        return goodsBindLabelList;
    }



}