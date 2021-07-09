package com.slodon.b2b2c.model.business;

import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.business.example.OrderPromotionDetailExample;
import com.slodon.b2b2c.business.pojo.OrderPromotionDetail;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.dao.read.business.OrderPromotionDetailReadMapper;
import com.slodon.b2b2c.dao.write.business.OrderPromotionDetailWriteMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 订单活动优惠明细model
 */
@Component
@Slf4j
public class OrderPromotionDetailModel {

    @Resource
    private OrderPromotionDetailReadMapper orderPromotionDetailReadMapper;
    @Resource
    private OrderPromotionDetailWriteMapper orderPromotionDetailWriteMapper;

    /**
     * 新增订单活动优惠明细
     *
     * @param orderPromotionDetail
     * @return
     */
    public Integer saveOrderPromotionDetail(OrderPromotionDetail orderPromotionDetail) {
        int count = orderPromotionDetailWriteMapper.insert(orderPromotionDetail);
        if (count == 0) {
            throw new MallException("添加订单活动优惠明细失败，请重试");
        }
        return count;
    }

    /**
     * 根据detailId删除订单活动优惠明细
     *
     * @param detailId detailId
     * @return
     */
    public Integer deleteOrderPromotionDetail(Integer detailId) {
        if (StringUtils.isEmpty(detailId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = orderPromotionDetailWriteMapper.deleteByPrimaryKey(detailId);
        if (count == 0) {
            log.error("根据detailId：" + detailId + "删除订单活动优惠明细失败");
            throw new MallException("删除订单活动优惠明细失败,请重试");
        }
        return count;
    }

    /**
     * 根据detailId更新订单活动优惠明细
     *
     * @param orderPromotionDetail
     * @return
     */
    public Integer updateOrderPromotionDetail(OrderPromotionDetail orderPromotionDetail) {
        if (StringUtils.isEmpty(orderPromotionDetail.getDetailId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = orderPromotionDetailWriteMapper.updateByPrimaryKeySelective(orderPromotionDetail);
        if (count == 0) {
            log.error("根据detailId：" + orderPromotionDetail.getDetailId() + "更新订单活动优惠明细失败");
            throw new MallException("更新订单活动优惠明细失败,请重试");
        }
        return count;
    }

    /**
     * 根据detailId获取订单活动优惠明细详情
     *
     * @param detailId detailId
     * @return
     */
    public OrderPromotionDetail getOrderPromotionDetailByDetailId(Integer detailId) {
        return orderPromotionDetailReadMapper.getByPrimaryKey(detailId);
    }

    /**
     * 根据条件获取订单活动优惠明细列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<OrderPromotionDetail> getOrderPromotionDetailList(OrderPromotionDetailExample example, PagerInfo pager) {
        List<OrderPromotionDetail> orderPromotionDetailList;
        if (pager != null) {
            pager.setRowsCount(orderPromotionDetailReadMapper.countByExample(example));
            orderPromotionDetailList = orderPromotionDetailReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            orderPromotionDetailList = orderPromotionDetailReadMapper.listByExample(example);
        }
        return orderPromotionDetailList;
    }

    /**
     * 提交订单-保存订单活动优惠明细
     *
     * @param orderInfo
     * @param orderSn
     */
    public void insertOrderPromotionDetails(OrderSubmitDTO.OrderInfo orderInfo, String orderSn) {
        List<OrderSubmitDTO.PromotionInfo> promotionInfoList = orderInfo.getPromotionInfoList();
        if (!CollectionUtils.isEmpty(promotionInfoList)) {
            //参与了优惠活动
            promotionInfoList.forEach(promotionInfo -> {
                OrderPromotionDetail detail = new OrderPromotionDetail();
                detail.setOrderSn(orderSn);
                detail.setPromotionGrade(promotionInfo.getPromotionType() / 100);
                detail.setPromotionType(promotionInfo.getPromotionType());
                detail.setPromotionId(promotionInfo.getPromotionId());
                detail.setPromotionAmount(promotionInfo.getDiscount());
                detail.setIsStore(promotionInfo.getIsStore() ? OrderConst.IS_STORE_PROMOTION_YES : OrderConst.IS_STORE_PROMOTION_NO);
                detail.setSendIntegral(promotionInfo.getSendIntegral());
                detail.setCreateTime(new Date());
                this.saveOrderPromotionDetail(detail);
            });
        }
    }
}