package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.example.OrderProductExtendExample;
import com.slodon.b2b2c.business.pojo.OrderProductExtend;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.business.OrderProductExtendReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderProductExtendWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class OrderProductExtendModel {

    @Resource
    private OrderProductExtendReadMapper orderProductExtendReadMapper;
    @Resource
    private OrderProductExtendWriteMapper orderProductExtendWriteMapper;

    /**
     * 新增活动和订单商品的扩展关系表
     *
     * @param orderProductExtend
     * @return
     */
    public Integer saveOrderProductExtend(OrderProductExtend orderProductExtend) {
        int count = orderProductExtendWriteMapper.insert(orderProductExtend);
        if (count == 0) {
            throw new MallException("添加活动和订单商品的扩展关系表失败，请重试");
        }
        return count;
    }

    /**
     * 根据extendId删除活动和订单商品的扩展关系表
     *
     * @param extendId extendId
     * @return
     */
    public Integer deleteOrderProductExtend(Integer extendId) {
        if (StringUtils.isEmpty(extendId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderProductExtendWriteMapper.deleteByPrimaryKey(extendId);
        if (count == 0) {
            log.error("根据extendId：" + extendId + "删除活动和订单商品的扩展关系表失败");
            throw new MallException("删除活动和订单商品的扩展关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId更新活动和订单商品的扩展关系表
     *
     * @param orderProductExtend
     * @return
     */
    public Integer updateOrderProductExtend(OrderProductExtend orderProductExtend) {
        if (StringUtils.isEmpty(orderProductExtend.getExtendId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderProductExtendWriteMapper.updateByPrimaryKeySelective(orderProductExtend);
        if (count == 0) {
            log.error("根据extendId：" + orderProductExtend.getExtendId() + "更新活动和订单商品的扩展关系表失败");
            throw new MallException("更新活动和订单商品的扩展关系表失败,请重试");
        }
        return count;
    }

    /**
     * 根据extendId获取活动和订单商品的扩展关系表详情
     *
     * @param extendId extendId
     * @return
     */
    public OrderProductExtend getOrderProductExtendByExtendId(Integer extendId) {
        return orderProductExtendReadMapper.getByPrimaryKey(extendId);
    }

    /**
     * 根据条件获取活动和订单商品的扩展关系表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderProductExtend> getOrderProductExtendList(OrderProductExtendExample example, PagerInfo pager) {
        List<OrderProductExtend> orderProductExtendList;
        if (pager != null) {
            pager.setRowsCount(orderProductExtendReadMapper.countByExample(example));
            orderProductExtendList = orderProductExtendReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderProductExtendList = orderProductExtendReadMapper.listByExample(example);
        }
        return orderProductExtendList;
    }

    /**
     * 提交订单保存订单货品扩展（活动优惠详情）
     *
     * @param orderSn        订单号
     * @param orderProductId 订单货品id
     * @param promotionInfo  活动优惠信息
     */
    public void insertOrderProductExtend(String orderSn, Long orderProductId, OrderSubmitDTO.PromotionInfo promotionInfo) {
        OrderProductExtend extend = new OrderProductExtend();
        extend.setOrderProductId(orderProductId);
        extend.setOrderSn(orderSn);
        extend.setPromotionGrade(promotionInfo.getPromotionType() / 100);
        extend.setPromotionType(promotionInfo.getPromotionType());
        extend.setPromotionId(promotionInfo.getPromotionId());
        extend.setPromotionAmount(promotionInfo.getDiscount());
        extend.setCreateTime(new Date());
        extend.setIsStore(promotionInfo.getIsStore() ? OrderConst.IS_STORE_PROMOTION_YES : OrderConst.IS_STORE_PROMOTION_NO);
        this.saveOrderProductExtend(extend);
    }
}