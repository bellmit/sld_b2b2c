package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsBindAttributeValueReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsBindAttributeValueWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.goods.example.GoodsBindAttributeValueExample;
import com.slodon.b2b2c.goods.pojo.GoodsBindAttributeValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品对应属性表(保存商品时插入)model
 */

@Component
@Slf4j
public class GoodsBindAttributeValueModel {
    @Resource
    private GoodsBindAttributeValueReadMapper goodsBindAttributeValueReadMapper;

    @Resource
    private GoodsBindAttributeValueWriteMapper goodsBindAttributeValueWriteMapper;

    @Resource
    private GoodsAttributeValueModel goodsAttributeValueModel;


    /**
     * 新增商品对应属性表(保存商品时插入)
     *
     * @param goodsBindAttributeValue
     * @return
     */
    public Integer saveGoodsBindAttributeValue(GoodsBindAttributeValue goodsBindAttributeValue) {
        int count = goodsBindAttributeValueWriteMapper.insert(goodsBindAttributeValue);
        if (count == 0) {
            throw new MallException("添加商品对应属性表(保存商品时插入)失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除商品对应属性表(保存商品时插入)
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteGoodsBindAttributeValue(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsBindAttributeValueWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除商品对应属性表(保存商品时插入)失败");
            throw new MallException("删除商品对应属性表(保存商品时插入)失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件删除
     *
     * @param goodsId
     */
    public void deleteGoodsBindAttributeValueByGoodsId(Long goodsId){
        AssertUtil.notNullOrZero(goodsId,"请选择操作的商品id");
        GoodsBindAttributeValueExample example = new GoodsBindAttributeValueExample();
        example.setGoodsId(goodsId);
        goodsBindAttributeValueWriteMapper.deleteByExample(example);
    }

    /**
     * 根据bindId更新商品对应属性表(保存商品时插入)
     *
     * @param goodsBindAttributeValue
     * @return
     */
    public Integer updateGoodsBindAttributeValue(GoodsBindAttributeValue goodsBindAttributeValue) {
        if (StringUtils.isEmpty(goodsBindAttributeValue.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsBindAttributeValueWriteMapper.updateByPrimaryKeySelective(goodsBindAttributeValue);
        if (count == 0) {
            log.error("根据bindId：" + goodsBindAttributeValue.getBindId() + "更新商品对应属性表(保存商品时插入)失败");
            throw new MallException("更新商品对应属性表(保存商品时插入)失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取商品对应属性表(保存商品时插入)详情
     *
     * @param bindId bindId
     * @return
     */
    public GoodsBindAttributeValue getGoodsBindAttributeValueByBindId(Integer bindId) {
        return goodsBindAttributeValueReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取商品对应属性表(保存商品时插入)列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsBindAttributeValue> getGoodsBindAttributeValueList(GoodsBindAttributeValueExample example, PagerInfo pager) {
        List<GoodsBindAttributeValue> goodsBindAttributeValueList;
        if (pager != null) {
            pager.setRowsCount(goodsBindAttributeValueReadMapper.countByExample(example));
            goodsBindAttributeValueList = goodsBindAttributeValueReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsBindAttributeValueList = goodsBindAttributeValueReadMapper.listByExample(example);
        }
        return goodsBindAttributeValueList;
    }


}