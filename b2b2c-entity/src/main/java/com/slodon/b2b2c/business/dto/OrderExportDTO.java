package com.slodon.b2b2c.business.dto;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.constant.MemberInvoiceConst;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单导出dto
 */
@Data
public class OrderExportDTO implements Serializable {

    private static final long serialVersionUID = -991433869153800343L;

    private String                      orderSn;                     //订单号
    private String                      orderState;                 //订单状态
    private BigDecimal                  orderAmount;             //订单金额
    private BigDecimal                  expressFee;              //物流费用
    private Integer                     memberId;                   //买家id
    private String                      memberName;                  //买家名称
    private Long                        storeId;                       //店铺id
    private String                      storeName;                   //店铺名称
    private String                      createTime;                  //下单时间
    private String                      payTime;                     //支付时间
    private String                      paymentName;                 //支付方式(名称)
    private String                      finishTime;                  //订单完成时间
    private String                      receiverName;                //收货人姓名
    private String                      receiverMobile;              //收货人电话
    private String                      receiverAreaInfo;            //收货地址
    private String                      expressNumber;               //发货单号
    private String                      deliverName;                 //发货人姓名
    private String                      deliverMobile;               //发货人电话
    private String                      deliverAreaInfo;             //发货地址
    private String                      invoice;                     //发票
    private List<OrderProductExportDTO> productList;                 //订单货品列表

    public String getOrderState() {
        String value = null;
        if (StringUtils.isEmpty(orderState)) return Language.translate("未知");
        switch (Integer.parseInt(orderState)) {
            case OrderConst.ORDER_STATE_0:
                value = "已取消";
                break;
            case OrderConst.ORDER_STATE_10:
                value = "待付款";
                break;
            case OrderConst.ORDER_STATE_20:
                value = "待发货";
                break;
            case OrderConst.ORDER_STATE_30:
                value = "待收货";
                break;
            case OrderConst.ORDER_STATE_40:
                value = "已完成";
                break;
        }
        return value;
    }

    public String getPaymentName() {
        if (StringUtils.isEmpty(paymentName)){
            return "";
        }
        if (this.paymentName.toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase())){
            return "支付宝支付";
        }
        if (this.paymentName.toUpperCase().contains(PayConst.METHOD_WX.toUpperCase())){
            return "微信支付";
        }
        return "在线支付";
    }

    public String getInvoice() {
        String value;
        if (!StringUtil.isEmpty(this.invoice)) {
            MemberInvoice memberInvoice = JSONObject.parseObject(this.invoice, MemberInvoice.class);
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
                            + memberInvoice.getRegisterAddr() + ",注册电话:" + memberInvoice.getRegisterPhone() + ",开户银行:"
                            + memberInvoice.getBankName() + ",银行账户:" + memberInvoice.getBankAccount() + ",收票人:"
                            + memberInvoice.getReceiverName() + ",收票人电话:" + memberInvoice.getReceiverMobile()
                            + ",收票地址:" + memberInvoice.getReceiverAddress();
                }
            }
        } else {
            value = "-";
        }
        return value;
    }
}
