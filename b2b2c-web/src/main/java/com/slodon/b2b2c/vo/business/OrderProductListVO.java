package com.slodon.b2b2c.vo.business;

import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.OrderProductConst;
import com.slodon.b2b2c.core.constant.OrdersAfsConst;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.i18n.Language;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author lxk
 */
@Data
public class OrderProductListVO {

    @ApiModelProperty("订单货品id")
    private Long orderProductId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("货品单价，与订单表中goods_amount对应")
    private BigDecimal productShowPrice;

    @ApiModelProperty("商品数量")
    private Integer productNum;

    @ApiModelProperty("商品id")
    private Long goodsId;

    @ApiModelProperty("货品id")
    private Long productId;

    @ApiModelProperty("规格详情")
    private String specValues;

    @ApiModelProperty("商品主图")
    private String productImage;

    @ApiModelProperty("是否评价:0-未评价，1-已评价")
    private Integer isComment;

    @ApiModelProperty("售后按钮，100-退款（商家未发货），200-退款（商家发货,买家未收货），300-申请售后，401-退款中，402-退款完成,403-换货中，404-换货完成")
    private Integer afsButton;

    @ApiModelProperty("售后按钮，100-退款（商家未发货），200-退款（商家发货,买家未收货），300-申请售后，401-退款中，402-退款完成,403-换货中，404-换货完成")
    private String afsButtonValue;

    @ApiModelProperty("售后单号,查看售后详情用")
    private String afsSn;

    @ApiModelProperty("售后状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家处理仅退款申请；201-商家处理退货退款申请；300-退款完成；301-退款关闭（商家拒绝退款申请）")
    private Integer afsState;

    @ApiModelProperty("售后状态：100-买家申请仅退款；101-买家申请退货退款；102-买家退货给商家；200-商家处理仅退款申请；201-商家处理退货退款申请；300-退款完成；301-退款关闭（商家拒绝退款申请）")
    private String afsStateValue;

    @ApiModelProperty("是否是赠品，0、不是；1、是；")
    private Integer isGift;

    public OrderProductListVO(OrderProduct orderProduct) {
        orderProductId = orderProduct.getOrderProductId();
        goodsName = orderProduct.getGoodsName();
        productShowPrice = orderProduct.getProductShowPrice();
        productNum = orderProduct.getProductNum();
        goodsId = orderProduct.getGoodsId();
        productId = orderProduct.getProductId();
        specValues = orderProduct.getSpecValues();
        productImage = FileUrlUtil.getFileUrl(orderProduct.getProductImage(), null);
        isComment = orderProduct.getIsComment();
        afsButton = orderProduct.getAfsButton();
        afsButtonValue = dealAfsButtonValue(orderProduct.getAfsButton());
        afsSn = orderProduct.getAfsSn();
        afsState = orderProduct.getAfsState();
        afsStateValue = dealAfsStateValue(orderProduct.getAfsState());
        isGift = orderProduct.getIsGift();
    }

    public static String dealAfsButtonValue(Integer afsButton) {
        if (afsButton == null) return null;
        switch (afsButton) {
            case OrderProductConst.AFS_BUTTON_100:
                return OrderProductConst.AFS_BUTTON_VALUE_100;
            case OrderProductConst.AFS_BUTTON_200:
                return OrderProductConst.AFS_BUTTON_VALUE_200;
            case OrderProductConst.AFS_BUTTON_300:
                return OrderProductConst.AFS_BUTTON_VALUE_300;
            case OrderProductConst.AFS_BUTTON_301:
                return OrderProductConst.AFS_BUTTON_VALUE_301;
            case OrderProductConst.AFS_BUTTON_401:
                return OrderProductConst.AFS_BUTTON_VALUE_401;
            case OrderProductConst.AFS_BUTTON_402:
                return OrderProductConst.AFS_BUTTON_VALUE_402;
            case OrderProductConst.AFS_BUTTON_403:
                return OrderProductConst.AFS_BUTTON_VALUE_403;
            case OrderProductConst.AFS_BUTTON_404:
                return OrderProductConst.AFS_BUTTON_VALUE_404;
            default:
                return null;
        }
    }

    public static String dealAfsStateValue(Integer afsState) {
        String value = "";
        if (afsState == null) return null;
        switch (afsState) {
            case OrdersAfsConst.RETURN_STATE_100:
            case OrdersAfsConst.RETURN_STATE_101:
                value = "申请退款中";
                break;
            case OrdersAfsConst.RETURN_STATE_102:
                value = "待商家收货";
                break;
            case OrdersAfsConst.RETURN_STATE_202:
                value = "退款失败";
                break;
            case OrdersAfsConst.RETURN_STATE_200:
            case OrdersAfsConst.RETURN_STATE_201:
                value = "退款中";
                break;
            case OrdersAfsConst.RETURN_STATE_300:
                value = "退款完成";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
