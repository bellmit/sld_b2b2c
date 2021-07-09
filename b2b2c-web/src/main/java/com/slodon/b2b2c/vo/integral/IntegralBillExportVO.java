package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.integral.pojo.IntegralBill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * admin-积分商城结算导出列表vo
 */
@Data
public class IntegralBillExportVO {

    @ApiModelProperty("结算id")
    private Integer billId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("结算开始时间")
    private String startTime;

    @ApiModelProperty("结算结束时间")
    private String endTime;

    @ApiModelProperty("现金使用金额")
    private BigDecimal cashAmount;

    @ApiModelProperty("积分使用数量")
    private Integer integral;

    @ApiModelProperty("积分抵扣金额")
    private BigDecimal integralCashAmount;

    @ApiModelProperty("应结金额(现金使用金额+积分抵现金额)")
    private BigDecimal settleAmount;

    @ApiModelProperty("结算状态：1、待确认；2、待审核；3、待结算；4、结算完成 ;")
    private String stateValue;

    public IntegralBillExportVO(IntegralBill integralBill) {
        this.billId = integralBill.getBillId();
        this.storeName = integralBill.getStoreName();
        this.startTime = TimeUtil.getDateTimeString(integralBill.getStartTime());
        this.endTime = TimeUtil.getDateTimeString(integralBill.getEndTime());
        this.cashAmount = integralBill.getCashAmount();
        this.integral = integralBill.getIntegral();
        this.integralCashAmount = integralBill.getIntegralCashAmount();
        this.settleAmount = integralBill.getSettleAmount();
        this.stateValue = dealStateValue(integralBill.getState());
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
}