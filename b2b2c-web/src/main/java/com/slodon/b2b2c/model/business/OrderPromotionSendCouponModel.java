package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.example.OrderPromotionSendCouponExample;
import com.slodon.b2b2c.business.pojo.OrderPromotionSendCoupon;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.business.OrderPromotionSendCouponReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderPromotionSendCouponWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单活动赠送优惠券表model
 */
@Component
@Slf4j
public class OrderPromotionSendCouponModel {

    @Resource
    private OrderPromotionSendCouponReadMapper orderPromotionSendCouponReadMapper;
    @Resource
    private OrderPromotionSendCouponWriteMapper orderPromotionSendCouponWriteMapper;

    /**
     * 新增订单活动赠送优惠券表
     *
     * @param orderPromotionSendCoupon
     * @return
     */
    public Integer saveOrderPromotionSendCoupon(OrderPromotionSendCoupon orderPromotionSendCoupon) {
        int count = orderPromotionSendCouponWriteMapper.insert(orderPromotionSendCoupon);
        if (count == 0) {
            throw new MallException("添加订单活动赠送优惠券表失败，请重试");
        }
        return count;
    }

    /**
     * 根据sendCouponId删除订单活动赠送优惠券表
     *
     * @param sendCouponId sendCouponId
     * @return
     */
    public Integer deleteOrderPromotionSendCoupon(Integer sendCouponId) {
        if (StringUtils.isEmpty(sendCouponId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderPromotionSendCouponWriteMapper.deleteByPrimaryKey(sendCouponId);
        if (count == 0) {
            log.error("根据sendCouponId：" + sendCouponId + "删除订单活动赠送优惠券表失败");
            throw new MallException("删除订单活动赠送优惠券表失败,请重试");
        }
        return count;
    }

    /**
     * 根据sendCouponId更新订单活动赠送优惠券表
     *
     * @param orderPromotionSendCoupon
     * @return
     */
    public Integer updateOrderPromotionSendCoupon(OrderPromotionSendCoupon orderPromotionSendCoupon) {
        if (StringUtils.isEmpty(orderPromotionSendCoupon.getSendCouponId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderPromotionSendCouponWriteMapper.updateByPrimaryKeySelective(orderPromotionSendCoupon);
        if (count == 0) {
            log.error("根据sendCouponId：" + orderPromotionSendCoupon.getSendCouponId() + "更新订单活动赠送优惠券表失败");
            throw new MallException("更新订单活动赠送优惠券表失败,请重试");
        }
        return count;
    }

    /**
     * 根据sendCouponId获取订单活动赠送优惠券表详情
     *
     * @param sendCouponId sendCouponId
     * @return
     */
    public OrderPromotionSendCoupon getOrderPromotionSendCouponBySendCouponId(Integer sendCouponId) {
        return orderPromotionSendCouponReadMapper.getByPrimaryKey(sendCouponId);
    }

    /**
     * 根据条件获取订单活动赠送优惠券表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderPromotionSendCoupon> getOrderPromotionSendCouponList(OrderPromotionSendCouponExample example, PagerInfo pager) {
        List<OrderPromotionSendCoupon> orderPromotionSendCouponList;
        if (pager != null) {
            pager.setRowsCount(orderPromotionSendCouponReadMapper.countByExample(example));
            orderPromotionSendCouponList = orderPromotionSendCouponReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderPromotionSendCouponList = orderPromotionSendCouponReadMapper.listByExample(example);
        }
        return orderPromotionSendCouponList;
    }

    /**
     * 提交订单-保存订单活动赠送优惠券
     *
     * @param orderInfo
     * @param orderSn
     */
    public void insertOrderPromotionSendCoupons(OrderSubmitDTO.OrderInfo orderInfo, String orderSn) {
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (!CollectionUtils.isEmpty(promotionInfoList)) {
            //参与了优惠活动
            promotionInfoList.forEach(promotionInfo -> {
                List<OrderSubmitDTO.PromotionInfo.SendCoupon> sendCouponList = promotionInfo.getSendCouponList();
                if (!CollectionUtils.isEmpty(sendCouponList)) {
                    //此活动赠送了优惠券
                    sendCouponList.forEach(sendCoupon -> {
                        OrderPromotionSendCoupon orderPromotionSendCoupon = new OrderPromotionSendCoupon();
                        orderPromotionSendCoupon.setOrderSn(orderSn);
                        orderPromotionSendCoupon.setPromotionGrade(promotionInfo.getPromotionType() / 100);
                        orderPromotionSendCoupon.setPromotionType(promotionInfo.getPromotionType());
                        orderPromotionSendCoupon.setPromotionId(promotionInfo.getPromotionId());
                        orderPromotionSendCoupon.setIsStore(promotionInfo.getIsStore() ? OrderConst.IS_STORE_PROMOTION_YES : OrderConst.IS_STORE_PROMOTION_NO);
                        orderPromotionSendCoupon.setCouponId(sendCoupon.getCouponId());
                        orderPromotionSendCoupon.setNumber(sendCoupon.getNum());
                        this.saveOrderPromotionSendCoupon(orderPromotionSendCoupon);
                    });
                }
            });
        }
    }
}