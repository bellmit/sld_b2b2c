package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.system.pojo.Bill;
import com.slodon.b2b2c.system.pojo.BillAccount;
import com.slodon.b2b2c.system.pojo.BillLog;
import com.slodon.b2b2c.system.pojo.BillOrderBind;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装结算详情VO对象
 * @Author wuxy
 */
@Data
public class BillDetailVO implements Serializable {

    private static final long serialVersionUID = -3623783013231320988L;
    @ApiModelProperty("结算id")
    private Integer billId;

    @ApiModelProperty("结算单号")
    private String billSn;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("结算开始时间")
    private Date startTime;

    @ApiModelProperty("结算结束时间")
    private Date endTime;

    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("平台佣金")
    private BigDecimal commission;

    @ApiModelProperty("退还佣金")
    private BigDecimal refundCommission;

    @ApiModelProperty("退单金额")
    private BigDecimal refundAmount;

    @ApiModelProperty("平台活动优惠金额")
    private BigDecimal platformActivityAmount;

    @ApiModelProperty("平台优惠券")
    private BigDecimal platformVoucherAmount;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("应结金额：order_amount-refund_amount-commision")
    private BigDecimal settleAmount;

    @ApiModelProperty("结算状态：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成")
    private Integer state;

    @ApiModelProperty("打款备注")
    private String paymentRemark;

    @ApiModelProperty("打款凭证")
    private String paymentEvidence;

    @ApiModelProperty("店铺联系人电话")
    private String contactPhone;

    @ApiModelProperty("店铺联系人姓名")
    private String contactName;

    @ApiModelProperty("账户类型：1-银行账号；2-支付宝账号")
    private Integer accountType;

    @ApiModelProperty("支付宝姓名")
    private String alipayName;

    @ApiModelProperty("支付宝账号")
    private String alipayAccount;

    @ApiModelProperty("银行开户名")
    private String bankAccountName;

    @ApiModelProperty("公司银行账号")
    private String bankAccountNumber;

    @ApiModelProperty("开户银行支行")
    private String bankBranch;

    @ApiModelProperty("开户行所在地")
    private String addressAll;

    @ApiModelProperty("订单列表")
    private List<BillOrderBind> orderList;

    @ApiModelProperty("日志列表")
    private List<BillLogVO> logList;

    public BillDetailVO(Bill bill) {
        billId = bill.getBillId();
        billSn = bill.getBillSn();
        storeId = bill.getStoreId();
        storeName = bill.getStoreName();
        startTime = bill.getStartTime();
        endTime = bill.getEndTime();
        orderAmount = bill.getOrderAmount();
        commission = bill.getCommission();
        refundCommission = bill.getRefundCommission();
        refundAmount = bill.getRefundAmount();
        platformActivityAmount = bill.getPlatformActivityAmount();
        platformVoucherAmount = bill.getPlatformVoucherAmount();
        integralCashAmount = bill.getIntegralCashAmount();
        settleAmount = bill.getSettleAmount();
        state = bill.getState();
        paymentRemark = bill.getPaymentRemark();
        paymentEvidence = FileUrlUtil.getFileUrl(bill.getPaymentEvidence(), null);
    }

    public BillDetailVO(Bill bill, BillAccount billAccount) {
        billId = bill.getBillId();
        billSn = bill.getBillSn();
        storeId = bill.getStoreId();
        storeName = bill.getStoreName();
        startTime = bill.getStartTime();
        endTime = bill.getEndTime();
        orderAmount = bill.getOrderAmount();
        commission = bill.getCommission();
        refundCommission = bill.getRefundCommission();
        refundAmount = bill.getRefundAmount();
        platformActivityAmount = bill.getPlatformActivityAmount();
        platformVoucherAmount = bill.getPlatformVoucherAmount();
        integralCashAmount = bill.getIntegralCashAmount();
        settleAmount = bill.getSettleAmount();
        state = bill.getState();
        paymentRemark = bill.getPaymentRemark();
        paymentEvidence = FileUrlUtil.getFileUrl(bill.getPaymentEvidence(), null);
        accountType = billAccount.getAccountType();
        alipayName = billAccount.getAlipayName();
        alipayAccount = billAccount.getAlipayAccount();
        bankAccountName = billAccount.getBankAccountName();
        bankAccountNumber = billAccount.getBankAccountNumber();
        bankBranch = billAccount.getBankBranch();
        addressAll = billAccount.getAddressAll();
    }

    @Data
    public static class BillLogVO implements Serializable {

        private static final long serialVersionUID = 931833914687946960L;
        @ApiModelProperty("日志id")
        private Integer logId;

        @ApiModelProperty("操作状态")
        private Integer state;

        @ApiModelProperty("操作行为")
        private String content;

        @ApiModelProperty("操作时间")
        private Date createTime;

        public BillLogVO(BillLog billLog) {
            logId = billLog.getLogId();
            state = billLog.getState();
            content = billLog.getContent();
            createTime = billLog.getCreateTime();
        }
    }
}
