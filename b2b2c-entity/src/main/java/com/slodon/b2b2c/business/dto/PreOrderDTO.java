package com.slodon.b2b2c.business.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @program: slodon
 * @Description 封装订单预处理dto对象
 * @Author wuxy
 */
@Data
public class PreOrderDTO implements Serializable {

    private static final long serialVersionUID = 716581852864645352L;
    /**
     * 是否计算优惠
     */
    private Boolean isCalculateDiscount = true;

    /**
     * 订单类型：0-普通订单；102-拼团订单, 103-预售订单, 104-秒杀订单, 105-阶梯团订单
     */
    private Integer orderType = 0;

    /**
     * 商品id(spu)
     */
    private Long goodsId = 0L;
}
