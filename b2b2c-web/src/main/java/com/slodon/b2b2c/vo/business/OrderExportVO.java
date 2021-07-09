package com.slodon.b2b2c.vo.business;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderExtend;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.MemberInvoiceConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装订单导出VO对象
 * @Author wuxy
 * @date 2020.11.16 19:14
 */
@Data
public class OrderExportVO implements Serializable {

    private static final long serialVersionUID = -5132178296198914786L;
    private String orderSn;                     //订单号
    private String orderState;                  //订单状态
    private BigDecimal orderAmount;             //订单金额
    private BigDecimal expressFee;              //物流费用
    private Integer memberId;                   //买家id
    private String memberName;                  //买家名称
    private Long storeId;                       //店铺id
    private String storeName;                   //店铺名称
    private String createTime;                  //下单时间
    private String payTime;                     //支付时间
    private String paymentName;                 //支付方式(名称)
    private String finishTime;                  //订单完成时间
    private String receiverName;                //收货人姓名
    private String receiverMobile;              //收货人电话
    private String receiverAreaInfo;            //收货地址
    private String expressNumber;               //发货单号
    private String deliverName;                 //发货人姓名
    private String deliverMobile;               //发货人电话
    private String deliverAreaInfo;             //发货地址
    private String invoice;                     //发票
    //    private String invoiceType;                 //发票信息类型
//    private String invoiceTitle;                //发票抬头
//    private String invoiceContent;              //发票内容
//    private String deliverTime;                 //发货时间
//    private String expressName;                 //物流公司名称
//    private BigDecimal moneyDiscount;           //优惠金额总额
//    private BigDecimal balanceAmount;           //余额账户支付总金额
//    private BigDecimal cashAmount;              //现金支付金额
//    private BigDecimal storeVoucherAmount;      //商家优惠券优惠金额
//    private BigDecimal platformVoucherAmount;   //平台优惠券优惠金额
//    private BigDecimal moneyPromotionFull;      //订单满减金额
//    private BigDecimal integralCashAmount;      //积分换算金额
//    private BigDecimal refundAmount;            //退款的金额
//    private String orderGoods;                  //订单商品
    private String goodsName;                   //商品名称
    private String specValues;                  //商品规格
    private String goodsNum;                    //商品数量
    private String goodsPrice;                  //商品单价(元)

    public OrderExportVO(Order order, OrderExtend orderExtend, OrderProduct orderProduct) {
        orderSn = order.getOrderSn();
        orderState = OrderListVO.getRealOrderStateValue(order.getOrderState());
        orderAmount = order.getOrderAmount();
        expressFee = order.getExpressFee();
        memberId = order.getMemberId();
        memberName = order.getMemberName();
        storeId = order.getStoreId();
        storeName = order.getStoreName();
        createTime = TimeUtil.getDateTimeString(order.getCreateTime());
        payTime = TimeUtil.getDateTimeString(order.getPayTime());
        paymentName = order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "支付宝支付"
                : order.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "微信支付" : "在线支付";
        finishTime = TimeUtil.getDateTimeString(order.getFinishTime());
        receiverName = order.getReceiverName();
        receiverMobile = CommonUtil.dealMobile(order.getReceiverMobile());
        receiverAreaInfo = order.getReceiverAreaInfo() + order.getReceiverAddress();
        expressNumber = order.getExpressNumber();
        invoice = dealInvoice(orderExtend.getInvoiceInfo());
        goodsName = orderProduct.getGoodsName();
        specValues = StringUtils.isEmpty(orderProduct.getSpecValues()) ? "无" : orderProduct.getSpecValues();
        goodsNum = orderProduct.getProductNum().toString();
        goodsPrice = orderProduct.getProductShowPrice().toString();
    }

    public static String dealInvoice(String invoiceInfo) {
        String value = "";
        if (!StringUtil.isEmpty(invoiceInfo)) {
            MemberInvoice memberInvoice = JSONObject.parseObject(invoiceInfo, MemberInvoice.class);
            //发票类型
            String invoiceType = memberInvoice.getInvoiceType() == MemberInvoiceConst.INVOICE_TYPE_1 ? "普通发票" : "增值税发票";
            //发票内容
            String invoiceContent = memberInvoice.getInvoiceContent() == MemberInvoiceConst.INVOICE_CONTENT_1 ? "商品明细" : "商品类别";
            //发票抬头
            String invoiceTitle = memberInvoice.getTitleType() == MemberInvoiceConst.INVOICE_TYPE_1 ? "个人" : "公司";
            if (memberInvoice.getTitleType() == MemberInvoiceConst.TITLE_TYPE_1) {
                value = "发票类型:" + invoiceType + ",发票内容:" + invoiceContent + ",发票抬头:" + invoiceTitle + ",收票邮箱:" + memberInvoice.getReceiverEmail();
            } else {
                if (memberInvoice.getInvoiceType() == MemberInvoiceConst.INVOICE_TYPE_1) {
                    value = "发票类型:" + invoiceType + ",发票内容:" + invoiceContent + ",发票抬头:" + invoiceTitle + ",单位名称:"
                            + memberInvoice.getInvoiceTitle() + ",税号:" + memberInvoice.getTaxCode() + ",收票邮箱:" + memberInvoice.getReceiverEmail();
                } else {
                    value = "发票类型:" + invoiceType + ",发票内容:" + invoiceContent + ",发票抬头:" + invoiceTitle + ",单位名称:"
                            + memberInvoice.getInvoiceTitle() + ",税号:" + memberInvoice.getTaxCode() + ",注册地址:"
                            + memberInvoice.getRegisterAddr() + ",注册电话:" + CommonUtil.dealMobile(memberInvoice.getRegisterPhone()) + ",开户银行:"
                            + memberInvoice.getBankName() + ",银行账户:" + memberInvoice.getBankAccount() + ",收票人:"
                            + memberInvoice.getReceiverName() + ",收票人电话:" + CommonUtil.dealMobile(memberInvoice.getReceiverMobile())
                            + ",收票地址:" + memberInvoice.getReceiverAddress();
                }
            }
        } else {
            value = "-";
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}
