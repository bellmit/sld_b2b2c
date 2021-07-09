package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.i18n.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 订单VO对象
 * @Author wuxy
 */
@Data
public class ChatOrdersVO implements Serializable {

    private static final long serialVersionUID = -558346682929088877L;
    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("订单创建时间")
    private Date createTime;

    @ApiModelProperty("商品金额，等于订单中所有的商品的单价乘以数量之和")
    private BigDecimal goodsAmount;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("订单状态值：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private String orderStateValue;

    @ApiModelProperty("订单类型：1-普通订单；2-拼团订单；3-积分订单；4-定金预售订单；5-全款预售订单；6-阶梯团订单等等")
    private Integer orderType;

    @ApiModelProperty("订单类型值：1-普通订单；2-拼团订单；3-积分订单；4-定金预售订单；5-全款预售订单；6-阶梯团订单等等")
    private String orderTypeValue;

    @ApiModelProperty("订单货品明细列表")
    private List<ChatOrderProductVO> orderProductList;

    public ChatOrdersVO(Order order, List<OrderProduct> productList) {
        orderId = order.getOrderId();
        orderSn = order.getOrderSn();
        createTime = order.getCreateTime();
        goodsAmount = getGoodsAmount();
        orderState = order.getOrderState();
        orderStateValue = dealOrderStateValue(order.getOrderState());
        orderType = order.getOrderType();
        orderTypeValue = dealOrderTypeValue(order.getOrderType());
        orderProductList = dealOrderProductList(productList);
    }

    public static String dealOrderStateValue(Integer orderState) {
        String value = null;
        if (StringUtils.isEmpty(orderState)) return null;
        switch (orderState) {
            case OrderConst.ORDER_STATE_10:
                value = "未付款";
                break;
            case OrderConst.ORDER_STATE_20:
                value = "已付款";
                break;
            case OrderConst.ORDER_STATE_30:
                value = "已发货";
                break;
            case OrderConst.ORDER_STATE_40:
                value = "已完成";
                break;
            case OrderConst.ORDER_STATE_50:
                value = "交易关闭";
                break;
            case OrderConst.ORDER_STATE_0:
                value = "已取消";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public static String dealOrderTypeValue(Integer orderType) {
        String value = null;
        if (StringUtils.isEmpty(orderType)) return Language.translate("未知");
        switch (orderType) {
            case OrderConst.ORDER_TYPE_1:
                value = "普通订单";
                break;
            case PromotionConst.PROMOTION_TYPE_102:
                value = "拼团订单";
                break;
            case PromotionConst.PROMOTION_TYPE_103:
                value = "预售订单";
                break;
            case PromotionConst.PROMOTION_TYPE_104:
                value = "秒杀订单";
                break;
            case PromotionConst.PROMOTION_TYPE_105:
                value = "阶梯团订单";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    public List<ChatOrderProductVO> dealOrderProductList(List<OrderProduct> orderProductList) {
        List<ChatOrderProductVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(orderProductList)) {
            for (OrderProduct product : orderProductList) {
                vos.add(new ChatOrderProductVO(product));
            }
        }
        return vos;
    }
}
