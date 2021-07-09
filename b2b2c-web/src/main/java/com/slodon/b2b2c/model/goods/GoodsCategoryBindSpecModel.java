package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsCategoryBindSpecReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsCategoryBindSpecWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.goods.example.GoodsCategoryBindSpecExample;
import com.slodon.b2b2c.goods.pojo.GoodsCategoryBindSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsCategoryBindSpecModel {
    @Resource
    private GoodsCategoryBindSpecReadMapper goodsCategoryBindSpecReadMapper;

    @Resource
    private GoodsCategoryBindSpecWriteMapper goodsCategoryBindSpecWriteMapper;

    /**
     * 新增商品分类与规格绑定关系表
     *
     * @param goodsCategoryBindSpec
     * @return
     */
    public Integer saveGoodsCategoryBindSpec(GoodsCategoryBindSpec goodsCategoryBindSpec) {
        int count = goodsCategoryBindSpecWriteMapper.insert(goodsCategoryBindSpec);
        if (count == 0) {
            throw new MallException("添加商品分类与规格绑定关系表失败，请重试");
        }
        return count;
    }

    /**
     * 根据bindId删除商品分类与规格绑定关系表
     *
     * @param bindId bindId
     * @return
     */
    public Integer deleteGoodsCategoryBindSpec(Integer bindId) {
        if (StringUtils.isEmpty(bindId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsCategoryBindSpecWriteMapper.deleteByPrimaryKey(bindId);
        if (count == 0) {
            log.error("根据bindId：" + bindId + "删除商品分类与规格绑定关系表失败");
            throw new MallException("删除商品分类与规格绑定关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId更新商品分类与规格绑定关系表
     *
     * @param goodsCategoryBindSpec
     * @return
     */
    public Integer updateGoodsCategoryBindSpec(GoodsCategoryBindSpec goodsCategoryBindSpec) {
        if (StringUtils.isEmpty(goodsCategoryBindSpec.getBindId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsCategoryBindSpecWriteMapper.updateByPrimaryKeySelective(goodsCategoryBindSpec);
        if (count == 0) {
            log.error("根据bindId：" + goodsCategoryBindSpec.getBindId() + "更新商品分类与规格绑定关系表失败");
            throw new MallException("更新商品分类与规格绑定关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据bindId获取商品分类与规格绑定关系表详情
     *
     * @param bindId bindId
     * @return
     */
    public GoodsCategoryBindSpec getGoodsCategoryBindSpecByBindId(Integer bindId) {
        return goodsCategoryBindSpecReadMapper.getByPrimaryKey(bindId);
    }

    /**
     * 根据条件获取商品分类与规格绑定关系表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsCategoryBindSpec> getGoodsCategoryBindSpecList(GoodsCategoryBindSpecExample example, PagerInfo pager) {
        List<GoodsCategoryBindSpec> goodsCategoryBindSpecList;
        if (pager != null) {
            pager.setRowsCount(goodsCategoryBindSpecReadMapper.countByExample(example));
            goodsCategoryBindSpecList = goodsCategoryBindSpecReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsCategoryBindSpecList = goodsCategoryBindSpecReadMapper.listByExample(example);
        }
        return goodsCategoryBindSpecList;
    }
}