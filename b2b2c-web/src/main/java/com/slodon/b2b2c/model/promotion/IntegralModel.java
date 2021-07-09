package com.slodon.b2b2c.model.promotion;

import com.slodon.b2b2c.business.dto.OrderConfirmDTO;
import com.slodon.b2b2c.business.dto.OrderSubmitDTO;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.model.promotion.base.PromotionBaseModel;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class IntegralModel extends PromotionBaseModel {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 活动名称
     *
     * @return
     */
    @Override
    public String promotionName() {
        return "积分换购";
    }

    /**
     * 是否有扩展方法
     *
     * @return
     */
    @Override
    public Boolean specialOrder() {
        return false;
    }

    /**
     * 确认订单计算活动优惠
     *
     * @param promotionCartGroup 按照活动类型-活动id分组的购物车列表
     */
    @Override
    public void orderConfirmCalculationDiscount(OrderConfirmDTO.StoreCartGroup.PromotionCartGroup promotionCartGroup) {

    }

    /**
     * 提交订单计算活动优惠
     *
     * @param dto 拆单后的订单提交信息
     */
    @Override
    public void orderSubmitCalculationDiscount(OrderSubmitDTO dto) {
        if (StringUtil.isNullOrZero(dto.getIntegral())){
            //未使用积分
            return;
        }
        //是否开启积分兑换
        if (!isPromotionAvailable("")){
            throw new MallException("积分兑换未开启");
        }
        //查询积分兑换比例,多少积分换1元
        int rate = Integer.parseInt(stringRedisTemplate.opsForValue().get("integral_conversion_ratio"));
        BigDecimal totalIntegralCashAmount = new BigDecimal(dto.getIntegral()).divide(new BigDecimal(rate),2, RoundingMode.DOWN);//积分抵扣金额，舍弃第三位小数
        dto.setIntegralCashAmount(totalIntegralCashAmount);

        //将积分优惠分配到各个订单货品
        BigDecimal assignedMoney = new BigDecimal("0.00");//已分配的积分抵扣金额
        int assignedIntegral = 0;//已分配的积分数
        BigDecimal totalAmount = dto.getTotalAmount();//总金额，计算分配比例的分母
        List<OrderSubmitDTO.OrderInfo> orderInfoList = dto.getOrderInfoList();
        for (int i = 0, orderInfoListSize = orderInfoList.size(); i < orderInfoListSize; i++) {
            OrderSubmitDTO.OrderInfo orderInfo = orderInfoList.get(i);
            List<OrderSubmitDTO.OrderInfo.OrderProductInfo> orderProductInfoList = orderInfo.getOrderProductInfoList();
            for (int j = 0, orderProductInfoListSize = orderProductInfoList.size(); j < orderProductInfoListSize; j++) {
                OrderSubmitDTO.OrderInfo.OrderProductInfo orderProductInfo = orderProductInfoList.get(j);
                //构造活动信息
                OrderSubmitDTO.PromotionInfo promotionInfo = new OrderSubmitDTO.PromotionInfo();
                promotionInfo.setPromotionType(PromotionConst.PROMOTION_TYPE_401);
                promotionInfo.setPromotionId("0");
                promotionInfo.setPromotionName(this.promotionName());
                promotionInfo.setIsStore(false);
                if (i == orderInfoListSize - 1 && j == orderProductInfoListSize - 1){
                    //最后一个订单货品，分配剩余金额
                    promotionInfo.setDiscount(totalIntegralCashAmount.subtract(assignedMoney));
                    orderProductInfo.setIntegral(dto.getIntegral() - assignedIntegral);
                    orderProductInfo.setIntegralCashAmount(totalIntegralCashAmount.subtract(assignedMoney));
                }else {
                    //不是最后一个订单货品，按比例分配
                    BigDecimal currentAssignedMoney = orderProductInfo.getMoneyAmount()
                            .multiply(totalIntegralCashAmount)
                            .divide(totalAmount,2,RoundingMode.HALF_UP);//四舍五入
                    int currentAssignedIntegral = orderProductInfo.getMoneyAmount()
                            .multiply(new BigDecimal(dto.getIntegral()))
                            .divide(totalAmount,0,RoundingMode.HALF_UP).intValue();
                    promotionInfo.setDiscount(currentAssignedMoney);
                    orderProductInfo.setIntegral(currentAssignedIntegral);
                    orderProductInfo.setIntegralCashAmount(currentAssignedMoney);

                    //已分配金额累加
                    assignedMoney = assignedMoney.add(currentAssignedMoney);
                    assignedIntegral += currentAssignedIntegral;
                }
                //订单货品添加活动
                orderProductInfo.getPromotionInfoList().add(promotionInfo);
            }
        }

    }

    /**
     * 校验活动是否可用
     *
     * @param promotionId
     * @return
     */
    @Override
    public Boolean isPromotionAvailable(String promotionId) {
        return "1".equals(stringRedisTemplate.opsForValue().get("integral_mall_is_enable"));
    }

}
