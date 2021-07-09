package com.slodon.b2b2c.vo.business;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderExtend;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lxk
 */
@Data
public class ChildOrdersVO {

    @ApiModelProperty("订单id")
    private Integer orderId;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺logo")
    private String storeLogo;

    @ApiModelProperty("发票信息")
    private JSONObject invoiceInfo;

    @ApiModelProperty("发票状态0-未开、1-已开")
    private Integer invoiceStatus;

    @ApiModelProperty("发票状态值0-未开、1-已开")
    private String invoiceStatusValue;

    @ApiModelProperty("活动优惠总金额 （= 店铺优惠券 + 平台优惠券 + 活动优惠【店铺活动 + 平台活动】 + 积分抵扣金额）")
    private BigDecimal activityDiscountAmount;

    @ApiModelProperty("订单货品列表")
    private List<OrderProductListVO> orderProductListVOList;

    @ApiModelProperty("店铺客服电话")
    private String servicePhone;

    public ChildOrdersVO(Order order, OrderExtend orderExtend, String storeAvatar) {
        orderId = order.getOrderId();
        orderSn = order.getOrderSn();
        storeId = order.getStoreId();
        storeName = order.getStoreName();
        storeLogo = FileUrlUtil.getFileUrl(storeAvatar, null);
        invoiceInfo = JSONObject.parseObject(orderExtend.getInvoiceInfo());
        invoiceStatus = orderExtend.getInvoiceStatus();
        invoiceStatusValue = getRealInvoiceStatusValue(invoiceStatus);
        activityDiscountAmount = order.getActivityDiscountAmount();
    }

    public static String getRealInvoiceStatusValue(Integer invoiceStatus) {
        String value = null;
        if (StringUtils.isEmpty(invoiceStatus)) return "未知";
        switch (invoiceStatus) {
            case OrderConst.INVOICE_STATE_0:
                value = "未开发票";
                break;
            case OrderConst.INVOICE_STATE_1:
                value = "已开发票";
                break;
        }
        return value;
    }
}
