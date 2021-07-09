package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 秒杀订单表example
 */
@Data
public class SeckillOrderExtendExample implements Serializable {

    private static final long serialVersionUID = 7194826812667542295L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer externIdNotEquals;

    /**
     * 用于批量操作
     */
    private String externIdIn;

    /**
     * 主键索引
     */
    private Integer externId;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 订单号,用于模糊查询
     */
    private String orderSnLike;

    /**
     * 秒杀商品记录id
     */
    private Integer seckillId;

    /**
     * 秒杀价
     */
    private BigDecimal seckillPrice;

    /**
     * 场次
     */
    private Integer stageId;

    /**
     *  场次货品绑定id
     */
    private Integer stageProductstageProductId;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照externId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;
}