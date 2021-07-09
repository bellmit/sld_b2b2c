package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.system.pojo.Bill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 结算导出VO对象
 * @Author wuxy
 */
@Data
public class BillExportVO {

    @ApiModelProperty("结算id")
    private Integer billId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("结算开始时间")
    private String startTime;

    @ApiModelProperty("结算结束时间")
    private String endTime;

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

    @ApiModelProperty("结算状态值：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成")
    private String stateValue;

    public BillExportVO(Bill bill) {
        billId = bill.getBillId();
        storeName = bill.getStoreName();
        startTime = TimeUtil.getDateTimeString(bill.getStartTime());
        endTime = TimeUtil.getDateTimeString(bill.getEndTime());
        orderAmount = bill.getOrderAmount();
        commission = bill.getCommission();
        refundCommission = bill.getRefundCommission();
        refundAmount = bill.getRefundAmount();
        platformActivityAmount = bill.getPlatformActivityAmount();
        platformVoucherAmount = bill.getPlatformVoucherAmount();
        integralCashAmount = bill.getIntegralCashAmount();
        settleAmount = bill.getSettleAmount();
        stateValue = BillVO.dealStateValue(bill.getState());
    }
}
