package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.dao.read.goods.GoodsFreightExtendReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsFreightExtendWriteMapper;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.goods.example.GoodsFreightExtendExample;
import com.slodon.b2b2c.goods.pojo.GoodsFreightExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class GoodsFreightExtendModel {
    @Resource
    private GoodsFreightExtendReadMapper goodsFreightExtendReadMapper;

    @Resource
    private GoodsFreightExtendWriteMapper goodsFreightExtendWriteMapper;

    /**
     * 新增运费模板扩展表-区域信息
     *
     * @param goodsFreightExtend
     * @return
     */
    public Integer saveGoodsFreightExtend(GoodsFreightExtend goodsFreightExtend) {
        int count = goodsFreightExtendWriteMapper.insert(goodsFreightExtend);
        if (count == 0) {
            throw new MallException("添加运费模板扩展表-区域信息失败，请重试");
        }
        return count;
    }

    /**
     * 根据freightExtendId删除运费模板扩展表-区域信息
     *
     * @param freightExtendId freightExtendId
     * @return
     */
    public Integer deleteGoodsFreightExtend(Integer freightExtendId) {
        GoodsFreightExtend goodsFreightExtend = goodsFreightExtendReadMapper.getByPrimaryKey(freightExtendId);
        AssertUtil.notNull(goodsFreightExtend, "获取运费模板扩展对象失败");

        int count = goodsFreightExtendWriteMapper.deleteByPrimaryKey(freightExtendId);
        if (count == 0) {
            log.error("根据freightExtendId：" + freightExtendId + "删除运费模板扩展表-区域信息失败");
            throw new MallException("删除运费模板扩展表-区域信息失败,请重试");
        }
        return count;
    }

    /**
     * 根据freightExtendId更新运费模板扩展表-区域信息
     *
     * @param goodsFreightExtend
     * @return
     */
    public Integer updateGoodsFreightExtend(GoodsFreightExtend goodsFreightExtend) {
        if (StringUtils.isEmpty(goodsFreightExtend.getFreightExtendId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsFreightExtendWriteMapper.updateByPrimaryKeySelective(goodsFreightExtend);
        if (count == 0) {
            log.error("根据freightExtendId：" + goodsFreightExtend.getFreightExtendId() + "更新运费模板扩展表-区域信息失败");
            throw new MallException("更新运费模板扩展表-区域信息失败,请重试");
        }
        return count;
    }

    /**
     * 根据freightExtendId获取运费模板扩展表-区域信息详情
     *
     * @param freightExtendId freightExtendId
     * @return
     */
    public GoodsFreightExtend getGoodsFreightExtendByFreightExtendId(Integer freightExtendId) {
        return goodsFreightExtendReadMapper.getByPrimaryKey(freightExtendId);
    }

    /**
     * 根据条件获取运费模板扩展表-区域信息列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<GoodsFreightExtend> getGoodsFreightExtendList(GoodsFreightExtendExample example, PagerInfo pager) {
        List<GoodsFreightExtend> goodsFreightExtendList;
        if (pager != null) {
            pager.setRowsCount(goodsFreightExtendReadMapper.countByExample(example));
            goodsFreightExtendList = goodsFreightExtendReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsFreightExtendList = goodsFreightExtendReadMapper.listByExample(example);
        }
        return goodsFreightExtendList;
    }
}