package com.slodon.b2b2c.model.goods;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.StoreTplConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.write.goods.GoodsWriteMapper;
import com.slodon.b2b2c.goods.dto.GoodsAuditDTO;
import com.slodon.b2b2c.goods.dto.GoodsLookUpDTO;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.starter.mq.entity.MessageSendProperty;
import com.slodon.b2b2c.starter.mq.entity.MessageSendVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_EXCHANGE_NAME;
import static com.slodon.b2b2c.core.constant.StarterConfigConst.MQ_QUEUE_NAME_SELLER_MSG;

/**
 * 商品公共信息表（SPU）model
 */
@Component
@Slf4j
public class GoodsModel {

    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private GoodsWriteMapper goodsWriteMapper;
    @Resource
    private GoodsExtendModel goodsExtendModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增商品公共信息表（SPU）
     *
     * @param goods
     * @return
     */
    public Integer saveGoods(Goods goods) {
        int count = goodsWriteMapper.insert(goods);
        if (count == 0) {
            throw new MallException("添加商品公共信息表（SPU）失败，请重试");
        }
        return count;
    }


    /**
     * 根据goodsId删除商品公共信息表（SPU）
     *
     * @param goodsId goodsId
     * @return
     */
    public Integer deleteGoods(Long goodsId) {
        if (StringUtils.isEmpty(goodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = goodsWriteMapper.deleteByPrimaryKey(goodsId);
        if (count == 0) {
            log.error("根据goodsId：" + goodsId + "删除商品公共信息表（SPU）失败");
            throw new MallException("删除商品公共信息表（SPU）失败,请重试");
        }
        return count;
    }

    /**
     * 根据goodsId更新商品公共信息表（SPU）
     *
     * @param goods
     * @return
     */
    public Integer updateGoods(Goods goods) {
        if (StringUtils.isEmpty(goods.getGoodsId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = goodsWriteMapper.updateByPrimaryKeySelective(goods);
        if (count == 0) {
            log.error("根据goodsId：" + goods.getGoodsId() + "更新商品公共信息表（SPU）失败");
            throw new MallException("更新商品公共信息表（SPU）失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件更新商品公共信息表（SPU）
     *
     * @param goods
     * @param example
     * @return
     */
    public Integer updateGoodsByExample(Goods goods, GoodsExample example) {
        return goodsWriteMapper.updateByExampleSelective(goods, example);
    }

    /**
     * 根据goodsId获取商品公共信息表（SPU）详情
     *
     * @param goodsId goodsId
     * @return
     */
    public Goods getGoodsByGoodsId(Long goodsId) {
        return goodsReadMapper.getByPrimaryKey(goodsId);
    }

    /**
     * 根据条件获取商品公共信息表（SPU）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<Goods> getGoodsList(GoodsExample example, PagerInfo pager) {
        List<Goods> goodsList;
        if (pager != null) {
            pager.setRowsCount(goodsReadMapper.countByExample(example));
            goodsList = goodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            goodsList = goodsReadMapper.listByExample(example);
        }
        return goodsList;
    }

    /**
     * 商品下架
     *
     * @param goodsLookUpDTO
     * @return
     */
    @Transactional
    public Integer goodsLockup(GoodsLookUpDTO goodsLookUpDTO) {
        int number = 0;
        //批量更新商品表状态为系统下架
        Goods goodsUpdate = new Goods();
        goodsUpdate.setIsOffline(GoodsConst.ILLEGAL_YES);
        goodsUpdate.setState(GoodsConst.GOODS_STATE_LOWER_BY_SYSTEM);
        goodsUpdate.setUpdateTime(new Date());
        GoodsExample example = new GoodsExample();
        example.setGoodsIdIn(goodsLookUpDTO.getGoodsIds());
        number = goodsWriteMapper.updateByExampleSelective(goodsUpdate, example);
        AssertUtil.notNullOrZero(number, "商品下架失败");

        //批量更新商品扩展表 下架原因 备注
        GoodsExtend goodsExtendUpdate = new GoodsExtend();
        goodsExtendUpdate.setOfflineComment(goodsLookUpDTO.getOfflineComment());
        goodsExtendUpdate.setOfflineReason(goodsLookUpDTO.getOfflineReason());
        GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
        goodsExtendExample.setGoodsIdIn(goodsLookUpDTO.getGoodsIds());
        number = goodsExtendModel.updateGoodsExtendByExample(goodsExtendUpdate, goodsExtendExample);
        AssertUtil.notNullOrZero(number, "商品下架失败");

        //发送消息通知
        this.sendMsgGoodsViolations(goodsLookUpDTO.getGoodsIds(), goodsLookUpDTO.getOfflineReason());
        return number;
    }

    /**
     * 商品审核
     *
     * @param goodsAuditDTO
     * @return
     */
    @Transactional
    public Integer Audit(GoodsAuditDTO goodsAuditDTO) {
        int number = 0;
        Goods goodsUpdate = new Goods();
        GoodsExample example = new GoodsExample();
        example.setGoodsIdIn(goodsAuditDTO.getGoodsIds());
        //审核拒绝时 保存拒绝原因 备注
        if (goodsAuditDTO.getState().equals(GoodsConst.GOODS_AUDIT_REJECT)) {
            //批量保存审核拒绝时 保存拒绝原因 备注
            GoodsExtend goodsExtendUpdate = new GoodsExtend();
            goodsExtendUpdate.setAuditReason(goodsAuditDTO.getAuditReason());
            goodsExtendUpdate.setAuditComment(goodsAuditDTO.getAuditComment());
            GoodsExtendExample goodsExtendExample = new GoodsExtendExample();
            goodsExtendExample.setGoodsIdIn(goodsAuditDTO.getGoodsIds());
            number = goodsExtendModel.updateGoodsExtendByExample(goodsExtendUpdate, goodsExtendExample);

            // 放入仓库待审核和立即上架待审核的商品 状态 改为审核拒绝
            goodsUpdate.setState(GoodsConst.GOODS_STATE_REJECT);
            goodsUpdate.setOnlineTime(null);
            goodsUpdate.setUpdateTime(new Date());
            example.setStateIn(GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT + "," + GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT);
            number = goodsWriteMapper.updateByExampleSelective(goodsUpdate, example);

            //发送消息通知
            this.sendMsgGoodsAudit(goodsAuditDTO.getGoodsIds());

        } else { //审核通过处理
            // 放入仓库待审核的商品 状态 改为放入仓库审核通过
            goodsUpdate.setState(GoodsConst.GOODS_STATE_WAREHOUSE_AUDIT_PASS);
            example.setState(GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT);
            goodsUpdate.setOnlineTime(new Date());
            goodsUpdate.setUpdateTime(new Date());
            number = goodsWriteMapper.updateByExampleSelective(goodsUpdate, example);

            // 立即上架待审核的商品 状态 改为GOODS_STATE_UPPER
            goodsUpdate.setState(GoodsConst.GOODS_STATE_UPPER);
            example.setState(GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT);
            goodsUpdate.setOnlineTime(new Date());
            goodsUpdate.setUpdateTime(new Date());
            number += goodsWriteMapper.updateByExampleSelective(goodsUpdate, example);
        }
        return number;
    }

    /**
     * 根据storeId更新商品信息
     *
     * @param goods
     * @param storeId
     */
    public Integer updateGoodsByStoreId(Goods goods, Long storeId) {
        GoodsExample example = new GoodsExample();
        example.setStoreId(storeId);
        return goodsWriteMapper.updateByExampleSelective(goods, example);
    }

    /**
     * 根据条件获取上架时间字段列表
     *
     * @param fields  查询字段，逗号分隔
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<String> getGoodsFieldList(String fields, GoodsExample example, PagerInfo pager) {
        List<String> goodsTimeList = new ArrayList<>();
        if (pager != null) {
            pager.setRowsCount(goodsReadMapper.countByGoodsExample(fields, example));
            goodsTimeList = goodsReadMapper.listFieldsOnTimeByExample(fields, example, pager.getStart(), pager.getPageSize());
        }
        return goodsTimeList;
    }

    /**
     * 获取条件获取商品数量
     *
     * @param example 查询条件信息
     * @return
     */
    public Integer getGoodsCount(GoodsExample example) {
        return goodsReadMapper.countByExample(example);
    }

    /**
     * 发送商品审核拒绝消息通知
     *
     * @param goodsIds 商品id集合
     */
    public void sendMsgGoodsAudit(String goodsIds) {
        String[] goodsIdArray = goodsIds.split(",");
        for (String goodsId : goodsIdArray) {
            if (StringUtil.isEmpty(goodsId)) {
                continue;
            }
            Goods goods = goodsReadMapper.getByPrimaryKey(goodsId);
            if (goods == null) {
                return;
            }
            List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
            messageSendPropertyList.add(new MessageSendProperty("goodsName", goods.getGoodsName()));
            String msgLinkInfo = "{\"goodsId\":\"" + goodsId + "\",\"type\":\"goods_audit_news\"}";
            MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, goods.getStoreId(), StoreTplConst.GOODS_AUDIT_FAILURE_REMINDER, msgLinkInfo);

            //发送到mq
            rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
        }
    }

    /**
     * 发送商品违规下架消息通知
     *
     * @param goodsIds 商品id集合
     */
    public void sendMsgGoodsViolations(String goodsIds, String offlineReason) {
        String[] goodsIdArray = goodsIds.split(",");
        for (String goodsId : goodsIdArray) {
            if (StringUtil.isEmpty(goodsId)) {
                continue;
            }
            Goods goods = goodsReadMapper.getByPrimaryKey(goodsId);
            if (goods == null) {
                return;
            }
            List<MessageSendProperty> messageSendPropertyList = new ArrayList<>();
            messageSendPropertyList.add(new MessageSendProperty("goodsName", goods.getGoodsName()));
            messageSendPropertyList.add(new MessageSendProperty("reason", offlineReason));
            String msgLinkInfo = "{\"goodsId\":\"" + goodsId + "\",\"type\":\"goods_violation_news\"}";
            MessageSendVO messageSendVO = new MessageSendVO(messageSendPropertyList, null, goods.getStoreId(), StoreTplConst.GOODS_VIOLATION_OFF_SHELF_REMINDER, msgLinkInfo);

            //发送到mq
            rabbitTemplate.convertAndSend(MQ_EXCHANGE_NAME, MQ_QUEUE_NAME_SELLER_MSG, messageSendVO);
        }
    }
}