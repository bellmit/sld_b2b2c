package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.integral.pojo.IntegralBill;
import com.slodon.b2b2c.integral.pojo.IntegralBillLog;
import com.slodon.b2b2c.integral.pojo.IntegralBillOrderBind;
import com.slodon.b2b2c.system.pojo.BillAccount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * admin-积分商城结算详情vo
 * @author spp
 */
@Data
public class AdminIntegralBillDetailVO implements Serializable {

    private static final long serialVersionUID = -5126876679572308938L;
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

    @ApiModelProperty("现金使用金额")
    private BigDecimal cashAmount;

    @ApiModelProperty("积分使用数量")
    private Integer integral;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("应结金额(现金使用金额+积分抵现金额)")
    private BigDecimal settleAmount;

    @ApiModelProperty("结算状态：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成")
    private Integer state;

    @ApiModelProperty("结算状态：1、待确认；2、待审核；3、待结算；4、结算完成 ;")
    private String stateValue;

    @ApiModelProperty("打款备注")
    private String paymentRemark;

    @ApiModelProperty("打款凭证")
    private String paymentEvidence;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

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
    private List<IntegralBillOrderBind> orderList;

    @ApiModelProperty("日志列表")
    private List<BillLogVO> logList;

    public AdminIntegralBillDetailVO(IntegralBill integralBill) {
        billId = integralBill.getBillId();
        billSn = integralBill.getBillSn();
        storeId = integralBill.getStoreId();
        storeName = integralBill.getStoreName();
        startTime = integralBill.getStartTime();
        endTime = integralBill.getEndTime();
        cashAmount = integralBill.getCashAmount();
        integral = integralBill.getIntegral();
        integralCashAmount = integralBill.getIntegralCashAmount();
        settleAmount = integralBill.getSettleAmount();
        state = integralBill.getState();
        stateValue = dealStateValue(integralBill.getState());
        paymentRemark = integralBill.getPaymentRemark();
        paymentEvidence = FileUrlUtil.getFileUrl(integralBill.getPaymentEvidence(), null);
    }

    public AdminIntegralBillDetailVO(IntegralBill integralBill, BillAccount billAccount) {
        billId = integralBill.getBillId();
        billSn = integralBill.getBillSn();
        storeId = integralBill.getStoreId();
        storeName = integralBill.getStoreName();
        startTime = integralBill.getStartTime();
        endTime = integralBill.getEndTime();
        cashAmount = integralBill.getCashAmount();
        integral = integralBill.getIntegral();
        integralCashAmount = integralBill.getIntegralCashAmount();
        settleAmount = integralBill.getSettleAmount();
        state = integralBill.getState();
        stateValue = dealStateValue(integralBill.getState());
        paymentRemark = integralBill.getPaymentRemark();
        paymentEvidence = FileUrlUtil.getFileUrl(integralBill.getPaymentEvidence(), null);

        accountType = billAccount.getAccountType();
        alipayName = billAccount.getAlipayName();
        alipayAccount = billAccount.getAlipayAccount();
        bankAccountName = billAccount.getBankAccountName();
        bankAccountNumber = billAccount.getBankAccountNumber();
        bankBranch = billAccount.getBankBranch();
        addressAll = billAccount.getAddressAll();
    }
    
    public static String dealStateValue(Integer state) {
        String value = "";
        if (StringUtils.isEmpty(state)) return null;
        switch (state) {
            case BillConst.STATE_1:
                value = "待确认";
                break;
            case BillConst.STATE_2:
                value = "待审核";
                break;
            case BillConst.STATE_3:
                value = "待结算";
                break;
            case BillConst.STATE_4:
                value = "结算完成";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    @Data
    public static class BillLogVO implements Serializable {

        private static final long serialVersionUID = -3709251272887329350L;
        @ApiModelProperty("日志id")
        private Integer logId;

        @ApiModelProperty("操作状态")
        private Integer state;

        @ApiModelProperty("操作行为")
        private String content;

        @ApiModelProperty("操作时间")
        private Date createTime;

        public BillLogVO(IntegralBillLog billLog) {
            logId = billLog.getLogId();
            state = billLog.getState();
            content = billLog.getContent();
            createTime = billLog.getCreateTime();
        }
    }
}