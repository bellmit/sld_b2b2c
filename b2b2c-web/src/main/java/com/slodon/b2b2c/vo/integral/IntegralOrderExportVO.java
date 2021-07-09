package com.slodon.b2b2c.vo.integral;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.constant.MemberInvoiceConst;
import com.slodon.b2b2c.core.constant.PayConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.integral.pojo.IntegralOrder;
import com.slodon.b2b2c.integral.pojo.IntegralOrderProduct;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * 封装积分订单导出VO对象
 */
@Data
public class IntegralOrderExportVO {

    private String orderSn;                     //订单号
    private String memberName;                  //买家名称
    private String storeName;                   //店铺名称
    private Integer integral;                   //兑换积分
    private BigDecimal cashAmount;              //兑换现金
    private String createTime;                  //兑换时间
    private String paymentName;                 //支付方式(名称)
    private String orderState;                  //订单状态
    private String receiverName;                //收货人姓名
    private String receiverMobile;              //收货人电话
    private String receiverAreaInfo;            //收货地址
    private String expressName;                 //物流公司
    private String expressNumber;               //物流单号
    private String invoice;                     //发票信息
    private String goodsName;                   //商品名称
    private String specValues;                  //商品规格
    private String goodsNum;                    //商品数量
    private String orderRemark;                 //订单备注

    public IntegralOrderExportVO(IntegralOrder integralOrder, IntegralOrderProduct integralOrderProduct) {
        orderSn = integralOrder.getOrderSn();
        orderState = IntegralOrderVO.dealOrderStateValue(integralOrder.getOrderState());
        integral = integralOrder.getIntegral();
        cashAmount = integralOrder.getOrderAmount();
        memberName = integralOrder.getMemberName();
        storeName = integralOrder.getStoreName();
        createTime = TimeUtil.getDateTimeString(integralOrder.getCreateTime());
        paymentName = integralOrder.getIntegral() == 0 && integralOrder.getPaymentCode().contains("INTEGRAL") ?
                (paymentName = integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "支付宝支付" : integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "微信支付" : "在线支付") :
                (paymentName = integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_ALIPAY.toUpperCase()) ? "积分+支付宝支付" : integralOrder.getPaymentCode().toUpperCase().contains(PayConst.METHOD_WX.toUpperCase()) ? "积分+微信支付" : "积分+在线支付");
        receiverName = integralOrder.getReceiverName();
        receiverMobile = CommonUtil.dealMobile(integralOrder.getReceiverMobile());
        receiverAreaInfo = integralOrder.getReceiverAreaInfo() + integralOrder.getReceiverAddress();
        expressNumber = integralOrder.getExpressNumber();
        invoice = dealInvoice(integralOrder.getInvoiceInfo());
        goodsName = integralOrderProduct.getGoodsName();
        specValues = StringUtils.isEmpty(integralOrderProduct.getSpecValues()) ? "无" : integralOrderProduct.getSpecValues();
        goodsNum = integralOrderProduct.getProductNum().toString();
        orderRemark = integralOrder.getOrderRemark();
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
