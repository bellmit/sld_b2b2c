package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PreSellConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.promotion.PresellGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.PresellOrderExtendReadMapper;
import com.slodon.b2b2c.dao.read.promotion.PresellReadMapper;
import com.slodon.b2b2c.dao.write.promotion.PresellOrderExtendWriteMapper;
import com.slodon.b2b2c.promotion.example.PresellGoodsExample;
import com.slodon.b2b2c.promotion.example.PresellOrderExtendExample;
import com.slodon.b2b2c.promotion.pojo.Presell;
import com.slodon.b2b2c.promotion.pojo.PresellGoods;
import com.slodon.b2b2c.promotion.pojo.PresellOrderExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 预售订单扩展信息表model
 */
@Component
@Slf4j
public class PresellOrderExtendModel {

    @Resource
    private PresellOrderExtendReadMapper presellOrderExtendReadMapper;
    @Resource
    private PresellOrderExtendWriteMapper presellOrderExtendWriteMapper;
    @Resource
    private PresellReadMapper presellReadMapper;
    @Resource
    private PresellGoodsReadMapper presellGoodsReadMapper;

    /**
     * 新增预售订单扩展信息表
     *
     * @param presellOrderExtend
     * @return
     */
    public Integer savePresellOrderExtend(PresellOrderExtend presellOrderExtend) {
        int count = presellOrderExtendWriteMapper.insert(presellOrderExtend);
        if (count == 0) {
            throw new MallException("添加预售订单扩展信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据extendId删除预售订单扩展信息表
     *
     * @param extendId extendId
     * @return
     */
    public Integer deletePresellOrderExtend(Integer extendId) {
        if (StringUtils.isEmpty(extendId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = presellOrderExtendWriteMapper.deleteByPrimaryKey(extendId);
        if (count == 0) {
            log.error("根据extendId：" + extendId + "删除预售订单扩展信息表失败");
            throw new MallException("删除预售订单扩展信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId更新预售订单扩展信息表
     *
     * @param presellOrderExtend
     * @return
     */
    public Integer updatePresellOrderExtend(PresellOrderExtend presellOrderExtend) {
        if (StringUtils.isEmpty(presellOrderExtend.getExtendId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = presellOrderExtendWriteMapper.updateByPrimaryKeySelective(presellOrderExtend);
        if (count == 0) {
            log.error("根据extendId：" + presellOrderExtend.getExtendId() + "更新预售订单扩展信息表失败");
            throw new MallException("更新预售订单扩展信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId更新预售订单扩展信息表
     *
     * @param presellOrderExtend
     * @return
     */
    public Integer updatePresellOrderExtendByOrderSn(PresellOrderExtend presellOrderExtend, String orderSnIn) {
        if (StringUtils.isEmpty(orderSnIn)) {
            throw new MallException("请选择要修改的数据");
        }
        PresellOrderExtendExample example = new PresellOrderExtendExample();
        example.setOrderSnIn("'" + orderSnIn.replace(",", "','") + "'");
        int count = presellOrderExtendWriteMapper.updateByExampleSelective(presellOrderExtend, example);
        if (count == 0) {
            throw new MallException("更新预售订单扩展信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId获取预售订单扩展信息表详情
     *
     * @param extendId extendId
     * @return
     */
    public PresellOrderExtend getPresellOrderExtendByExtendId(Integer extendId) {
        return presellOrderExtendReadMapper.getByPrimaryKey(extendId);
    }

    /**
     * 根据条件获取预售订单扩展信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<PresellOrderExtend> getPresellOrderExtendList(PresellOrderExtendExample example, PagerInfo pager) {
        List<PresellOrderExtend> presellOrderExtendList;
        if (pager != null) {
            pager.setRowsCount(presellOrderExtendReadMapper.countByExample(example));
            presellOrderExtendList = presellOrderExtendReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            presellOrderExtendList = presellOrderExtendReadMapper.listByExample(example);
        }
        return presellOrderExtendList;
    }

    /**
     * 保存订单预售信息
     *
     * @param orderSn
     * @param paySn
     * @param promotionId
     * @param productId
     * @return
     */
    public Integer insertOrderPreSale(String orderSn, String paySn, Integer promotionId, Long productId, Integer productNum, Integer memberId) {
        //查询预售活动信息
        Presell presell = presellReadMapper.getByPrimaryKey(promotionId);
        AssertUtil.notNull(presell, "获取预售活动信息为空");
        //查询预售商品信息
        PresellGoodsExample example = new PresellGoodsExample();
        example.setPresellId(promotionId);
        example.setProductId(productId);
        List<PresellGoods> presellGoodsList = presellGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(presellGoodsList, "获取预售商品信息信息为空");
        PresellGoods presellGoods = presellGoodsList.get(0);
        //构造预售订单扩展信息
        PresellOrderExtend presellOrderExtend = new PresellOrderExtend();
        presellOrderExtend.setPresellId(promotionId);
        presellOrderExtend.setOrderSn(orderSn);
        presellOrderExtend.setDepositPaySn(paySn);
        presellOrderExtend.setGoodsId(presellGoods.getGoodsId());
        presellOrderExtend.setProductId(productId);
        presellOrderExtend.setProductNum(productNum);
        presellOrderExtend.setPresellPrice(presellGoods.getPresellPrice());
        //待付款状态
        presellOrderExtend.setOrderSubState(OrderConst.ORDER_SUB_STATE_101);
        if (presell.getType() == PreSellConst.PRE_SELL_TYPE_1) {
            if (!StringUtil.isNullOrZero(presellGoods.getFirstExpand())) {
                presellOrderExtend.setFirstExpand(presellGoods.getFirstExpand());
            }
            presellOrderExtend.setDepositAmount(presellGoods.getFirstMoney());
            presellOrderExtend.setIsAllPay(OrderConst.IS_ALL_PAY_0);
        } else {
            presellOrderExtend.setDepositAmount(presellGoods.getPresellPrice());
            presellOrderExtend.setIsAllPay(OrderConst.IS_ALL_PAY_1);
        }
        presellOrderExtend.setRemainAmount(presellGoods.getSecondMoney());
        presellOrderExtend.setDepositEndTime(presell.getEndTime());
        presellOrderExtend.setRemainStartTime(presell.getRemainStartTime());
        presellOrderExtend.setRemainEndTime(presell.getRemainEndTime());
        presellOrderExtend.setDeliverTime(presell.getDeliverTime());
        presellOrderExtend.setMemberId(memberId);
        presellOrderExtend.setCreateTime(new Date());
        return this.savePresellOrderExtend(presellOrderExtend);
    }
}