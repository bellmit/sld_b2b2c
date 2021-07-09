package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupReadMapper;
import com.slodon.b2b2c.dao.write.promotion.LadderGroupGoodsWriteMapper;
import com.slodon.b2b2c.promotion.example.LadderGroupGoodsExample;
import com.slodon.b2b2c.promotion.pojo.LadderGroup;
import com.slodon.b2b2c.promotion.pojo.LadderGroupGoods;
import com.slodon.b2b2c.vo.promotion.FrontLadderGroupVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 阶梯团商品表model
 */
@Component
@Slf4j
public class LadderGroupGoodsModel {

    @Resource
    private LadderGroupGoodsReadMapper ladderGroupGoodsReadMapper;
    @Resource
    private LadderGroupGoodsWriteMapper ladderGroupGoodsWriteMapper;
    @Resource
    private LadderGroupReadMapper ladderGroupReadMapper;

    /**
     * 新增阶梯团商品表
     *
     * @param ladderGroupGoods
     * @return
     */
    public Integer saveLadderGroupGoods(LadderGroupGoods ladderGroupGoods) {
        int count = ladderGroupGoodsWriteMapper.insert(ladderGroupGoods);
        if (count == 0) {
            throw new MallException("添加阶梯团商品表失败，请重试");
        }
        return count;
    }

    /**
     * 根据groupGoodsId删除阶梯团商品表
     *
     * @param groupGoodsId groupGoodsId
     * @return
     */
    public Integer deleteLadderGroupGoods(Integer groupGoodsId) {
        if (StringUtils.isEmpty(groupGoodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = ladderGroupGoodsWriteMapper.deleteByPrimaryKey(groupGoodsId);
        if (count == 0) {
            log.error("根据groupGoodsId：" + groupGoodsId + "删除阶梯团商品表失败");
            throw new MallException("删除阶梯团商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据groupGoodsId更新阶梯团商品表
     *
     * @param ladderGroupGoods
     * @return
     */
    public Integer updateLadderGroupGoods(LadderGroupGoods ladderGroupGoods) {
        if (StringUtils.isEmpty(ladderGroupGoods.getGroupGoodsId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = ladderGroupGoodsWriteMapper.updateByPrimaryKeySelective(ladderGroupGoods);
        if (count == 0) {
            log.error("根据groupGoodsId：" + ladderGroupGoods.getGroupGoodsId() + "更新阶梯团商品表失败");
            throw new MallException("更新阶梯团商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据groupGoodsId获取阶梯团商品表详情
     *
     * @param groupGoodsId groupGoodsId
     * @return
     */
    public LadderGroupGoods getLadderGroupGoodsByGroupGoodsId(Integer groupGoodsId) {
        return ladderGroupGoodsReadMapper.getByPrimaryKey(groupGoodsId);
    }

    /**
     * 根据条件获取阶梯团商品表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<LadderGroupGoods> getLadderGroupGoodsList(LadderGroupGoodsExample example, PagerInfo pager) {
        List<LadderGroupGoods> ladderGroupGoodsList;
        if (pager != null) {
            pager.setRowsCount(ladderGroupGoodsReadMapper.countByExample(example));
            ladderGroupGoodsList = ladderGroupGoodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            ladderGroupGoodsList = ladderGroupGoodsReadMapper.listByExample(example);
        }
        return ladderGroupGoodsList;
    }

    /**
     * 阶梯团活动查看商品列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FrontLadderGroupVO.FrontLadderGroupGoodsVO> getFrontLadderGroupGoodsList(LadderGroupGoodsExample example, PagerInfo pager) {
        List<FrontLadderGroupVO.FrontLadderGroupGoodsVO> list = new ArrayList<>();
        String fields = "goods_id, group_id";
        example.setGroupBy(fields);
        example.setOrderBy("group_id desc");
        List<LadderGroupGoods> goodsList = ladderGroupGoodsReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        if (!CollectionUtils.isEmpty(goodsList)) {
            pager.setRowsCount(goodsList.size());

            for (LadderGroupGoods ladderGroupGoods : goodsList) {
                example.setGroupIdIn(null);
                example.setGroupBy(null);
                example.setOrderBy(null);
                example.setGoodsId(ladderGroupGoods.getGoodsId());
                List<LadderGroupGoods> groupGoodsList = ladderGroupGoodsReadMapper.listByExample(example);
                if (!CollectionUtils.isEmpty(groupGoodsList)) {
                    LadderGroupGoods goods = groupGoodsList.get(0);
                    //查询阶梯活动类型
                    LadderGroup ladderGroup = ladderGroupReadMapper.getByPrimaryKey(goods.getGroupId());
                    AssertUtil.notNull(ladderGroup, "获取阶梯团活动信息为空，请重试！");
                    if (ladderGroup.getDiscountType() == LadderGroupConst.DISCOUNT_TYPE_2) {
                        goods.setLadderPrice1(goods.getProductPrice().multiply(goods.getLadderPrice1().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)));
                        if (!StringUtil.isNullOrZero(goods.getLadderPrice2())) {
                            goods.setLadderPrice2(goods.getProductPrice().multiply(goods.getLadderPrice2().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)));
                        }
                        if (!StringUtil.isNullOrZero(goods.getLadderPrice3())) {
                            goods.setLadderPrice3(goods.getProductPrice().multiply(goods.getLadderPrice3().divide(new BigDecimal(10), 2, RoundingMode.HALF_UP)));
                        }
                    }
                    list.add(new FrontLadderGroupVO.FrontLadderGroupGoodsVO(goods));
                }
            }
        }
        return list;
    }
}