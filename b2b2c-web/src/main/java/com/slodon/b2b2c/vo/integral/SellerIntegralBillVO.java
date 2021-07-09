package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.constant.BillConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.integral.pojo.IntegralBill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * seller-积分商城结算列表
 * @author spp
 */
@Data
public class SellerIntegralBillVO implements Serializable {

    private static final long serialVersionUID = -630019727410674044L;
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

    @ApiModelProperty("结算金额")
    private BigDecimal settleAmount;

    @ApiModelProperty("结算状态：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成")
    private Integer state;

    @ApiModelProperty("结算状态：1、待确认；2、待审核；3、待结算；4、结算完成 ;")
    private String stateValue;

    @ApiModelProperty("出账时间")
    private Date createTime;

    public SellerIntegralBillVO(IntegralBill integralBill) {
        this.billId = integralBill.getBillId();
        this.billSn = integralBill.getBillSn();
        this.storeId = integralBill.getStoreId();
        this.storeName = integralBill.getStoreName();
        this.startTime = integralBill.getStartTime();
        this.endTime = integralBill.getEndTime();
        this.cashAmount = integralBill.getCashAmount();
        this.integral = integralBill.getIntegral();
        this.integralCashAmount = integralBill.getIntegralCashAmount();
        this.settleAmount = integralBill.getSettleAmount();
        this.state = integralBill.getState();
        this.stateValue = dealStateValue(integralBill.getState());
        this.createTime = integralBill.getCreateTime();
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