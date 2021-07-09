package com.slodon.b2b2c.starter.client;

import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.starter.entity.SlodonPayRequest;
import com.slodon.b2b2c.starter.entity.SlodonPayResponse;
import com.slodon.b2b2c.starter.entity.SlodonRefundRequest;
import com.slodon.b2b2c.starter.entity.SlodonRefundResponse;

/**
 * slodon支付客户端接口定义
 */
public abstract class SlodonPayClient {
    /**
     * 支付参数
     */
    protected SlodonPayRequest payRequest;

    /**
     * 退款参数
     */
    protected SlodonRefundRequest refundRequest;

    /**
     * 支付构造函数
     * @param payRequest
     */
    SlodonPayClient(SlodonPayRequest payRequest) {
        //参数校验
        AssertUtil.notNull(payRequest,"支付请求参数不能为空");
        AssertUtil.notEmpty(payRequest.getPayMethod(),"请选择支付方式");
        AssertUtil.notEmpty(payRequest.getPayType(),"请选择支付类型");
        AssertUtil.notEmpty(payRequest.getPaySn(),"支付单号不能为空");
        AssertUtil.notEmpty(payRequest.getNotifyUrl(),"回调地址不能为空");
        AssertUtil.notNullOrZero(payRequest.getPayAmount(),"支付金额有误");
        this.payRequest = payRequest;
    }

    /**
     * 退款构造函数
     * @param refundRequest
     */
    SlodonPayClient(SlodonRefundRequest refundRequest) {
        //参数校验
        AssertUtil.notNull(refundRequest,"退款请求参数不能为空");
        AssertUtil.notEmpty(refundRequest.getPaySn(),"支付单号不能为空");
        AssertUtil.notEmpty(refundRequest.getRefundSn(),"退款单号不能为空");
        AssertUtil.notNullOrZero(refundRequest.getRefundAmount(),"退款金额有误");
        this.refundRequest = refundRequest;
    }

    /**
     * 执行支付
     *
     * @return
     * @throws Exception
     */
    public abstract SlodonPayResponse doPay() throws Exception;

    /**
     * 检测支付状态
     *
     * @param tradeNo 三方支付流水号
     * @param outTradeNo 商城的支付单号
     * @return
     */
    public abstract void checkPayState(String tradeNo, String outTradeNo,String payType) throws Exception;

    /**
     * 执行退款
     */
    public abstract SlodonRefundResponse doRefund() throws Exception;
}
