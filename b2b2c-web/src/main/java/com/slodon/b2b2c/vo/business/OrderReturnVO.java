package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.business.pojo.OrderReturn;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装售后管理VO对象
 * @Author wuxy
 */
@Data
public class OrderReturnVO implements Serializable {

    private static final long serialVersionUID = 8725413727660309288L;
    @ApiModelProperty("退货id")
    private Integer returnId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("货品id")
    private Long productId;

    @ApiModelProperty("商品图片")
    private String productImage;

    @ApiModelProperty("商品规格情")
    private String specValues;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("退款编号")
    private String afsSn;

    @ApiModelProperty("退货数量")
    private Integer returnNum;

    @ApiModelProperty("退款金额")
    private BigDecimal returnMoneyAmount;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("退款方式：1-仅退款 2-退货退款")
    private Integer returnType;

    @ApiModelProperty("退款方式值：1-仅退款 2-退货退款")
    private String returnTypeValue;

    @ApiModelProperty("售后状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家同意退款申请；201-商家同意退货退款申请；202-商家拒绝退款申请(退款关闭/拒收关闭)；203-商家确认收货；300-平台确认退款(已完成)")
    private Integer state;

    @ApiModelProperty("售后状态值：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家同意退款申请；201-商家同意退货退款申请；202-商家拒绝退款申请(退款关闭/拒收关闭)；203-商家确认收货；300-平台确认退款(已完成)")
    private String stateValue;

    @ApiModelProperty("申请时间")
    private Date applyTime;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    public OrderReturnVO(OrderReturn orderReturn, OrderProduct orderProduct, String queryType) {
        returnId = orderReturn.getReturnId();
        goodsName = orderProduct.getGoodsName();
        goodsId = orderProduct.getGoodsId();
        productId = orderProduct.getProductId();
        productImage = FileUrlUtil.getFileUrl(orderProduct.getProductImage(), ImageSizeEnum.SMALL);
        specValues = orderProduct.getSpecValues();
        orderSn = orderReturn.getOrderSn();
        afsSn = orderReturn.getAfsSn();
        returnNum = orderReturn.getReturnNum();
        returnMoneyAmount = orderReturn.getReturnMoneyAmount().add(orderReturn.getReturnExpressAmount());
        storeId = orderReturn.getStoreId();
        storeName = orderReturn.getStoreName();
        memberId = orderReturn.getMemberId();
        memberName = orderReturn.getMemberName();
        returnType = orderReturn.getReturnType();
        returnTypeValue = dealReturnTypeValue(orderReturn.getReturnType());
        state = orderReturn.getState();
        if (!StringUtil.isEmpty(queryType) && "audit".equals(queryType)) {
            stateValue = dealStateValue2(orderReturn.getState());
        } else {
            stateValue = dealStateValue(orderReturn.getState());
        }
        applyTime = orderReturn.getApplyTime();
    }

    public static String dealReturnTypeValue(Integer returnType) {
        String value = "";
        if (StringUtils.isEmpty(returnType)) return Language.translate("未知");
        switch (returnType) {
            case OrdersAfsConst.RETURN_TYPE_1:
                value = "仅退款";
                break;
            case OrdersAfsConst.RETURN_TYPE_2:
                value = "退货退款";
                break;
        }
        return Language.translate(value);
    }

    public static String dealStateValue(Integer state) {
        String value = "";
        if (StringUtils.isEmpty(state)) return Language.translate("未知");
        switch (state) {
            case OrdersAfsConst.RETURN_STATE_100:
            case OrdersAfsConst.RETURN_STATE_101:
                value = "待商家审核";
                break;
            case OrdersAfsConst.RETURN_STATE_102:
                value = "待商家收货";
                break;
            case OrdersAfsConst.RETURN_STATE_200:
            case OrdersAfsConst.RETURN_STATE_203:
                value = "待平台处理";
                break;
            case OrdersAfsConst.RETURN_STATE_201:
                value = "待买家发货";
                break;
            case OrdersAfsConst.RETURN_STATE_202:
                value = "售后关闭";
                break;
            case OrdersAfsConst.RETURN_STATE_300:
                value = "退款成功";
                break;
        }
        return Language.translate(value);
    }

    public static String dealStateValue2(Integer state) {
        String value = "";
        if (StringUtils.isEmpty(state)) return Language.translate("未知");
        switch (state) {
            case OrdersAfsConst.RETURN_STATE_200:
            case OrdersAfsConst.RETURN_STATE_203:
                value = "待处理";
                break;
            case OrdersAfsConst.RETURN_STATE_300:
                value = "已完成";
                break;
        }
        return Language.translate(value);
    }
}
