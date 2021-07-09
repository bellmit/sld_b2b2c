package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.core.constant.LadderGroupConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupGoodsReadMapper;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupOrderExtendReadMapper;
import com.slodon.b2b2c.dao.read.promotion.LadderGroupReadMapper;
import com.slodon.b2b2c.dao.write.promotion.LadderGroupOrderExtendWriteMapper;
import com.slodon.b2b2c.promotion.example.LadderGroupGoodsExample;
import com.slodon.b2b2c.promotion.example.LadderGroupOrderExtendExample;
import com.slodon.b2b2c.promotion.pojo.LadderGroup;
import com.slodon.b2b2c.promotion.pojo.LadderGroupGoods;
import com.slodon.b2b2c.promotion.pojo.LadderGroupOrderExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 阶梯团订单扩展信息表model
 */
@Component
@Slf4j
public class LadderGroupOrderExtendModel {

    @Resource
    private LadderGroupOrderExtendReadMapper ladderGroupOrderExtendReadMapper;
    @Resource
    private LadderGroupOrderExtendWriteMapper ladderGroupOrderExtendWriteMapper;
    @Resource
    private LadderGroupReadMapper ladderGroupReadMapper;
    @Resource
    private LadderGroupGoodsReadMapper ladderGroupGoodsReadMapper;

    /**
     * 新增阶梯团订单扩展信息表
     *
     * @param ladderGroupOrderExtend
     * @return
     */
    public Integer saveLadderGroupOrderExtend(LadderGroupOrderExtend ladderGroupOrderExtend) {
        int count = ladderGroupOrderExtendWriteMapper.insert(ladderGroupOrderExtend);
        if (count == 0) {
            throw new MallException("添加阶梯团订单扩展信息表失败，请重试");
        }
        return count;
    }

    /**
     * 根据extendId删除阶梯团订单扩展信息表
     *
     * @param extendId extendId
     * @return
     */
    public Integer deleteLadderGroupOrderExtend(Integer extendId) {
        if (StringUtils.isEmpty(extendId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = ladderGroupOrderExtendWriteMapper.deleteByPrimaryKey(extendId);
        if (count == 0) {
            log.error("根据extendId：" + extendId + "删除阶梯团订单扩展信息表失败");
            throw new MallException("删除阶梯团订单扩展信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId更新阶梯团订单扩展信息表
     *
     * @param ladderGroupOrderExtend
     * @return
     */
    public Integer updateLadderGroupOrderExtend(LadderGroupOrderExtend ladderGroupOrderExtend) {
        if (StringUtils.isEmpty(ladderGroupOrderExtend.getExtendId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = ladderGroupOrderExtendWriteMapper.updateByPrimaryKeySelective(ladderGroupOrderExtend);
        if (count == 0) {
            log.error("根据extendId：" + ladderGroupOrderExtend.getExtendId() + "更新阶梯团订单扩展信息表失败");
            throw new MallException("更新阶梯团订单扩展信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId更新阶梯团订单扩展信息表
     *
     * @param ladderGroupOrderExtend
     * @return
     */
    public Integer updateLadderGroupOrderExtendByOrderSn(LadderGroupOrderExtend ladderGroupOrderExtend, String orderSnIn) {
        if (StringUtils.isEmpty(orderSnIn)) {
            throw new MallException("请选择要修改的数据");
        }
        LadderGroupOrderExtendExample example = new LadderGroupOrderExtendExample();
        example.setOrderSnIn("'" + orderSnIn.replace(",", "','") + "'");
        int count = ladderGroupOrderExtendWriteMapper.updateByExampleSelective(ladderGroupOrderExtend, example);
        if (count == 0) {
            throw new MallException("更新阶梯团订单扩展信息表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId获取阶梯团订单扩展信息表详情
     *
     * @param extendId extendId
     * @return
     */
    public LadderGroupOrderExtend getLadderGroupOrderExtendByExtendId(Integer extendId) {
        return ladderGroupOrderExtendReadMapper.getByPrimaryKey(extendId);
    }

    /**
     * 根据条件获取阶梯团订单扩展信息表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<LadderGroupOrderExtend> getLadderGroupOrderExtendList(LadderGroupOrderExtendExample example, PagerInfo pager) {
        List<LadderGroupOrderExtend> ladderGroupOrderExtendList;
        if (pager != null) {
            pager.setRowsCount(ladderGroupOrderExtendReadMapper.countByExample(example));
            ladderGroupOrderExtendList = ladderGroupOrderExtendReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            ladderGroupOrderExtendList = ladderGroupOrderExtendReadMapper.listByExample(example);
        }
        return ladderGroupOrderExtendList;
    }

    /**
     * 根据条件获取阶梯团订单扩展信息表列表数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getLadderGroupOrderExtendCount(LadderGroupOrderExtendExample example) {
        return ladderGroupOrderExtendReadMapper.countByExample(example);
    }

    /**
     * 保存阶梯团订单扩展信息
     *
     * @param orderSn
     * @param paySn
     * @param memberId
     * @param memberName
     * @param promotionId
     * @param productId
     * @return
     */
    public Integer insertLadderGroupOrder(String orderSn, String paySn, Integer memberId, String memberName,
                                          Integer promotionId, Long productId, Integer productNum) {
        //查询阶梯团活动信息
        LadderGroup ladderGroup = ladderGroupReadMapper.getByPrimaryKey(promotionId);
        AssertUtil.notNull(ladderGroup, "获取阶梯团活动信息为空");
        //查询阶梯团商品信息
        LadderGroupGoodsExample example = new LadderGroupGoodsExample();
        example.setGroupId(promotionId);
        example.setProductId(productId);
        List<LadderGroupGoods> groupGoodsList = ladderGroupGoodsReadMapper.listByExample(example);
        AssertUtil.notEmpty(groupGoodsList, "获取阶梯团商品信息信息为空");
        LadderGroupGoods groupGoods = groupGoodsList.get(0);
        //构造阶梯团订单扩展信息
        LadderGroupOrderExtend orderExtend = new LadderGroupOrderExtend();
        orderExtend.setGroupId(promotionId);
        orderExtend.setOrderSn(orderSn);
        orderExtend.setMemberId(memberId);
        orderExtend.setMemberName(memberName);
        orderExtend.setGoodsId(groupGoods.getGoodsId());
        orderExtend.setGoodsName(groupGoods.getGoodsName());
        orderExtend.setGoodsImage(groupGoods.getGoodsImage());
        orderExtend.setProductId(groupGoods.getProductId());
        orderExtend.setProductPrice(groupGoods.getProductPrice());
        orderExtend.setProductNum(productNum);
        orderExtend.setDepositPaySn(paySn);
        orderExtend.setOrderSubState(LadderGroupConst.ORDER_SUB_STATE_1);
        orderExtend.setAdvanceDeposit(groupGoods.getAdvanceDeposit());
        orderExtend.setRemainStartTime(ladderGroup.getEndTime());
        //活动结束时间加上尾款时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ladderGroup.getEndTime());
        calendar.add(Calendar.HOUR, ladderGroup.getBalanceTime());
        orderExtend.setRemainEndTime(calendar.getTime());
        orderExtend.setParticipateTime(new Date());
        return this.saveLadderGroupOrderExtend(orderExtend);
    }
}