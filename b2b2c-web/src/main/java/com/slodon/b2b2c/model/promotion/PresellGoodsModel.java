package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.constant.PreSellConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.promotion.PresellGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.PresellReadMapper;
import com.slodon.b2b2c.dao.write.promotion.PresellGoodsWriteMapper;
import com.slodon.b2b2c.promotion.example.PresellGoodsExample;
import com.slodon.b2b2c.promotion.pojo.Presell;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import com.slodon.b2b2c.vo.promotion.FrontPresellVO;
import com.slodon.b2b2c.vo.promotion.PreSellGoodsListVO;
import com.slodon.b2b2c.vo.promotion.PreSellProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class PresellGoodsModel {

    @Resource
    private PresellGoodsReadMapper presellGoodsReadMapper;
    @Resource
    private PresellGoodsWriteMapper presellGoodsWriteMapper;
    @Resource
    private PresellReadMapper presellReadMapper;

    /**
     * 新增预售活动商品表
     *
     * @param presellGoods
     * @return
     */
    public Integer savePresellGoods(PresellGoods presellGoods) {
        int count = presellGoodsWriteMapper.insert(presellGoods);
        if (count == 0) {
            throw new MallException("添加预售活动商品表失败，请重试");
        }
        return count;
    }

    /**
     * 根据presellGoodsId删除预售活动商品表
     *
     * @param presellGoodsId presellGoodsId
     * @return
     */
    public Integer deletePresellGoods(Integer presellGoodsId) {
        if (StringUtils.isEmpty(presellGoodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = presellGoodsWriteMapper.deleteByPrimaryKey(presellGoodsId);
        if (count == 0) {
            log.error("根据presellGoodsId：" + presellGoodsId + "删除预售活动商品表失败");
            throw new MallException("删除预售活动商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据goodsId删除预售活动商品
     *
     * @param goodsId goodsId
     * @return
     */
    @Transactional
    public Integer deletePreSellGoodsByGoodsId(Integer presellId, Long goodsId, Long storeId) {
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(presellId);
        example.setGoodsId(goodsId);
        List<PresellGoods> list = presellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(list, "获取预售商品信息为空，请重试！");

        Presell presell = presellReadMapper.getByPrimaryKey(presellId);
        AssertUtil.notNull(presell, "获取预售活动信息为空，请重试！");
        AssertUtil.isTrue(!presell.getStoreId().equals(storeId), "无权限");

        Date date = new Date();
        AssertUtil.isTrue(date.before(presell.getEndTime()) && presell.getState() == PreSellConst.ACTIVITY_STATE_2,
                "正在进行中的预售活动商品不能删除，请重试！");

        return presellGoodsWriteMapper.deleteByExample(example);
    }

    /**
     * 根据presellGoodsId更新预售活动商品表
     *
     * @param presellGoods
     * @return
     */
    public Integer updatePresellGoods(PresellGoods presellGoods) {
        if (StringUtils.isEmpty(presellGoods.getPresellGoodsId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = presellGoodsWriteMapper.updateByPrimaryKeySelective(presellGoods);
        if (count == 0) {
            log.error("根据presellGoodsId：" + presellGoods.getPresellGoodsId() + "更新预售活动商品表失败");
            throw new MallException("更新预售活动商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据presellGoodsId获取预售活动商品表详情
     *
     * @param presellGoodsId presellGoodsId
     * @return
     */
    public PresellGoods getPresellGoodsByPresellGoodsId(Integer presellGoodsId) {
        return presellGoodsReadMapper.getByPrimaryKey(presellGoodsId);
    }

    /**
     * 根据条件获取预售活动商品表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<PresellGoods> getPresellGoodsList(PresellGoodsExample example, PagerInfo pager) {
        List<PresellGoods> presellGoodsList;
        if (pager != null) {
            pager.setRowsCount(presellGoodsReadMapper.countByExample(example));
            presellGoodsList = presellGoodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            presellGoodsList = presellGoodsReadMapper.listByExample(example);
        }
        return presellGoodsList;
    }

    /**
     * 根据条件获取预售活动商品表列表
     *
     * @param fields  查询字段，字段用逗号分隔
     * @param example 查询条件信息
     * @return
     */
    public List<PresellGoods> getPresellGoodsListFieldsByExample(String fields, PresellGoodsExample example) {
        return presellGoodsReadMapper.listFieldsByExample(fields, example);
    }

    /**
     * 预售活动查看商品列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<PreSellGoodsListVO> getPreSellGoodsList(PresellGoodsExample example, PagerInfo pager) {
        List<PreSellGoodsListVO> list = new ArrayList<>();
        String fields = "goods_id";
        example.setGroupBy("goods_id");
        example.setOrderBy("goods_id desc");
        List<PresellGoods> goodsList = presellGoodsReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        if (!CollectionUtils.isEmpty(goodsList)) {
            pager.setRowsCount(goodsList.size());

            for (PresellGoods presellGoods : goodsList) {
                PreSellGoodsListVO goodsListVO = new PreSellGoodsListVO();

                //根据goodsId查询货品集合
                example.setGroupBy(null);
                example.setOrderBy(null);
                example.setGoodsId(presellGoods.getGoodsId());
                List<PresellGoods> presellGoodsList = presellGoodsReadMapper.listByExample(example);
                if (!CollectionUtils.isEmpty(presellGoodsList)) {
                    goodsListVO = new PreSellGoodsListVO(presellGoodsList.get(0));

                    //预售库存
                    Integer presellStock = 0;
                    Integer saleNum = 0;
                    List<PreSellProductVO> productList = new ArrayList<>();
                    for (PresellGoods presellGoods1 : presellGoodsList) {
                        productList.add(new PreSellProductVO(presellGoods1));

                        presellStock += presellGoods1.getPresellStock();
                        saleNum += presellGoods1.getActualSale();
                    }
                    goodsListVO.setPresellStock(presellStock);
                    goodsListVO.setSaleNum(saleNum);
                    goodsListVO.setProductList(productList);
                }
                list.add(goodsListVO);
            }
        }
        return list;
    }

    /**
     * 预售活动查看商品列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<FrontPresellVO.FrontPreSellGoodsVO> getFrontPreSellGoodsList(PresellGoodsExample example, PagerInfo pager) {
        List<FrontPresellVO.FrontPreSellGoodsVO> list = new ArrayList<>();
        String fields = "goods_id, presell_id";
        example.setGroupBy(fields);
        example.setOrderBy("presell_id desc");
        List<PresellGoods> goodsList = presellGoodsReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        if (!CollectionUtils.isEmpty(goodsList)) {
            pager.setRowsCount(goodsList.size());

            for (PresellGoods presellGoods : goodsList) {
                //根据goodsId查询货品集合
                example.setPresellIdIn(null);
                example.setGroupBy(null);
                example.setOrderBy(null);
                example.setGoodsId(presellGoods.getGoodsId());
                List<PresellGoods> presellGoodsList = presellGoodsReadMapper.listByExample(example);
                if (!CollectionUtils.isEmpty(presellGoodsList)) {
                    list.add(new FrontPresellVO.FrontPreSellGoodsVO(presellGoodsList.get(0)));
                }
            }
        }
        return list;
    }

}