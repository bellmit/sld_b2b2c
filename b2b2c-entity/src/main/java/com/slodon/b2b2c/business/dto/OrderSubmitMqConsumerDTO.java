package com.slodon.b2b2c.business.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 提交订单请求放入mq队列的dto
 */
@Data
public class OrderSubmitMqConsumerDTO implements Serializable {
    private static final long serialVersionUID = -1259632721554196812L;
    /**
     * 提交订单前端传参
     */
    private OrderSubmitParamDTO paramDTO;
    /**
     * 会员id
     */
    private Integer memberId;
    /**
     * 支付单号，入队列前统一生成，用于查询订单是否已经生成
     */
    private String paySn;
    /**
     * 订单预处理信息（是否计算优惠、是否描述）
     */
    private PreOrderDTO preOrderDTO;
}
