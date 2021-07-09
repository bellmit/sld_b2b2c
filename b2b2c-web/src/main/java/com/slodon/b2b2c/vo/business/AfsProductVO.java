package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.OrderProduct;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AfsProductVO implements Serializable {
    private static final long serialVersionUID = 2653367042941698029L;
    private Long orderProductId;                //订单货品ID
    private Long goodsId;                       //商品id
    private Integer afsNum;                     //申请售后数量
    private BigDecimal returnMoneyAmount;       //退款金额
    private Integer returnIntegralAmount;       //退换积分数量
    private Integer deductIntegralAmount;       //扣除积分数量（购物赠送的积分）
    private BigDecimal returnExpressAmount;     //退还运费的金额（用于待发货订单仅退款时处理）
    private String returnVoucherCode;           //退还优惠券编码（最后一单退还优惠券）
    private BigDecimal commissionRate;          //平台对应类别的佣金比例，0-1数字
    private BigDecimal commissionAmount;        //佣金金额

    public AfsProductVO(OrderProduct orderProduct) {
        orderProductId = orderProduct.getOrderProductId();
        goodsId = orderProduct.getGoodsId();
        commissionRate = orderProduct.getCommissionRate();
    }
}
