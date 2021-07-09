package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.promotion.SpellGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SpellReadMapper;
import com.slodon.b2b2c.dao.read.promotion.SpellTeamReadMapper;
import com.slodon.b2b2c.dao.write.promotion.SpellGoodsWriteMapper;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.promotion.example.SpellGoodsExample;
import com.slodon.b2b2c.promotion.example.SpellTeamExample;
import com.slodon.b2b2c.promotion.pojo.Spell;
import com.slodon.b2b2c.promotion.pojo.SpellGoods;
import com.slodon.b2b2c.promotion.pojo.SpellTeam;
import com.slodon.b2b2c.vo.promotion.FrontSpellGoodsVO;
import com.slodon.b2b2c.vo.promotion.SpellGoodsListVO;
import com.slodon.b2b2c.vo.promotion.SpellProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SpellGoodsModel {

    @Resource
    private SpellGoodsReadMapper spellGoodsReadMapper;
    @Resource
    private SpellGoodsWriteMapper spellGoodsWriteMapper;
    @Resource
    private SpellReadMapper spellReadMapper;
    @Resource
    private SpellTeamReadMapper spellTeamReadMapper;
    @Resource
    private ProductModel productModel;

    /**
     * 新增拼团活动绑定商品表
     *
     * @param spellGoods
     * @return
     */
    public Integer saveSpellGoods(SpellGoods spellGoods) {
        int count = spellGoodsWriteMapper.insert(spellGoods);
        if (count == 0) {
            throw new MallException("添加拼团活动绑定商品表失败，请重试");
        }
        return count;
    }

    /**
     * 根据spellGoodsId删除拼团活动绑定商品表
     *
     * @param spellGoodsId spellGoodsId
     * @return
     */
    public Integer deleteSpellGoods(Integer spellGoodsId) {
        if (StringUtils.isEmpty(spellGoodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = spellGoodsWriteMapper.deleteByPrimaryKey(spellGoodsId);
        if (count == 0) {
            log.error("根据spellGoodsId：" + spellGoodsId + "删除拼团活动绑定商品表失败");
            throw new MallException("删除拼团活动绑定商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据goodsId删除拼团活动商品
     *
     * @param spellId
     * @param goodsId
     * @return
     */
    @Transactional
    public Integer deleteSpellGoodsByGoodsId(Integer spellId, Long goodsId, Long storeId) {
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(spellId);
        example.setGoodsId(goodsId);
        List<SpellGoods> list = spellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(list, "获取拼团商品信息为空，请重试！");

        Spell spell = spellReadMapper.getByPrimaryKey(spellId);
        AssertUtil.notNull(spell, "获取拼团活动信息为空，请重试！");
        AssertUtil.isTrue(!spell.getStoreId().equals(storeId), "无权限");

        Date date = new Date();
        AssertUtil.isTrue(spell.getState() == SpellConst.ACTIVITY_STATE_2
                        && (date.after(spell.getStartTime()) && date.before(spell.getEndTime())),
                "正在进行中的的拼团活动不能删除");

        return spellGoodsWriteMapper.deleteByExample(example);
    }

    /**
     * 根据spellGoodsId更新拼团活动绑定商品表
     *
     * @param spellGoods
     * @return
     */
    public Integer updateSpellGoods(SpellGoods spellGoods) {
        if (StringUtils.isEmpty(spellGoods.getSpellGoodsId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = spellGoodsWriteMapper.updateByPrimaryKeySelective(spellGoods);
        if (count == 0) {
            log.error("根据spellGoodsId：" + spellGoods.getSpellGoodsId() + "更新拼团活动绑定商品表失败");
            throw new MallException("更新拼团活动绑定商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据spellGoodsId获取拼团活动绑定商品表详情
     *
     * @param spellGoodsId spellGoodsId
     * @return
     */
    public SpellGoods getSpellGoodsBySpellGoodsId(Integer spellGoodsId) {
        return spellGoodsReadMapper.getByPrimaryKey(spellGoodsId);
    }

    /**
     * 根据条件获取拼团活动绑定商品表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SpellGoods> getSpellGoodsList(SpellGoodsExample example, PagerInfo pager) {
        List<SpellGoods> spellGoodsList;
        if (pager != null) {
            pager.setRowsCount(spellGoodsReadMapper.countByExample(example));
            spellGoodsList = spellGoodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            spellGoodsList = spellGoodsReadMapper.listByExample(example);
        }
        return spellGoodsList;
    }

    /**
     * 根据条件获取拼团活动绑定商品表列表
     *
     * @param fields  查询字段，字段用逗号分隔
     * @param example 查询条件信息
     * @return
     */
    public List<SpellGoods> getSpellGoodsListFieldsByExample(String fields, SpellGoodsExample example) {
        return spellGoodsReadMapper.listFieldsByExample(fields, example);
    }

    /**
     * 拼团活动查看商品列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SpellGoodsListVO> getSpellGoodsList2(SpellGoodsExample example, PagerInfo pager) {
        List<SpellGoodsListVO> list = new ArrayList<>();
        String fields = "goods_id";
        example.setGroupBy("goods_id");
        example.setOrderBy("goods_id desc");
        List<SpellGoods> goodsList = spellGoodsReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        if (!CollectionUtils.isEmpty(goodsList)) {
            pager.setRowsCount(goodsList.size());
            goodsList.forEach(spellGoods -> {
                SpellGoodsListVO goodsListVO = new SpellGoodsListVO();
                //根据goodsId查询货品集合
                example.setGroupBy(null);
                example.setOrderBy(null);
                example.setGoodsId(spellGoods.getGoodsId());
                List<SpellGoods> spellGoodsList = spellGoodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
                if (!CollectionUtils.isEmpty(spellGoodsList)) {
                    goodsListVO = new SpellGoodsListVO(spellGoodsList.get(0));

                    List<SpellProductVO> productList = new ArrayList<>();
                    for (SpellGoods spellGoods1 : spellGoodsList) {
                        SpellProductVO productVO = new SpellProductVO(spellGoods1);
                        //查询sku库存
                        Product product = productModel.getProductByProductId(spellGoods1.getProductId());
                        AssertUtil.notNull(product, "获取货品品信息为空，请重试");
                        productVO.setStock(product.getProductStock());
                        productList.add(productVO);
                    }
                    goodsListVO.setProductList(productList);
                }
                //根据goodsId查询拼团团队
                SpellTeamExample teamExample = new SpellTeamExample();
                teamExample.setSpellId(example.getSpellId());
                teamExample.setGoodsId(spellGoods.getGoodsId());
                teamExample.setLeaderPayState(SpellConst.PAY_STATE_1);
                goodsListVO.setSpellTeamNum(spellTeamReadMapper.countByExample(teamExample));
                list.add(goodsListVO);
            });
        }
        return list;
    }

    /**
     * 拼团活动查看商品列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FrontSpellGoodsVO> getFrontSpellGoodsList(SpellGoodsExample example, PagerInfo pager, Map<Integer, Integer> spellMap) {
        List<FrontSpellGoodsVO> list = new ArrayList<>();
        String fields = "goods_id, spell_id";
        example.setGroupBy(fields);
        example.setOrderBy("spell_id desc");
        List<SpellGoods> goodsList = spellGoodsReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        if (!CollectionUtils.isEmpty(goodsList)) {
            pager.setRowsCount(goodsList.size());

            for (SpellGoods spellGoods : goodsList) {
                //根据goodsId查询货品集合
                example.setSpellIdIn(null);
                example.setGroupBy(null);
                example.setOrderBy(null);
                example.setGoodsId(spellGoods.getGoodsId());
                List<SpellGoods> spellGoodsList = spellGoodsReadMapper.listByExample(example);
                if (!CollectionUtils.isEmpty(spellGoodsList)) {
                    SpellGoods goods = spellGoodsList.get(0);
                    FrontSpellGoodsVO vo = new FrontSpellGoodsVO(goods);
                    vo.setRequiredNum(spellMap.get(goods.getSpellId()));
                    list.add(vo);
                }
            }
        }
        return list;
    }

}