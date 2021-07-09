package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.system.pojo.Bill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装结算VO对象
 * @Author wuxy
 */
@Data
public class BillVO implements Serializable {

    private static final long serialVersionUID = -8840594166842124169L;
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

    @ApiModelProperty("结算状态值：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成")
    private String stateValue;

    @ApiModelProperty("出账时间")
    private Date createTime;

    public BillVO(Bill bill) {
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
        stateValue = dealStateValue(bill.getState());
        createTime = bill.getCreateTime();
    }

    public static String dealStateValue(Integer state) {
        String value = null;
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
