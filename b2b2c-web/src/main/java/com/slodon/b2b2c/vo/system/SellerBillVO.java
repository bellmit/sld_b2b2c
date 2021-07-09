package com.slodon.b2b2c.vo.system;

import com.slodon.b2b2c.system.pojo.Bill;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装店铺结算VO对象
 * @Author wuxy
 */
@Data
public class SellerBillVO implements Serializable {

    private static final long serialVersionUID = -7149062506669294054L;
    @ApiModelProperty("结算id")
    private Integer billId;

    @ApiModelProperty("结算单号")
    private String billSn;

    @ApiModelProperty("结算开始时间")
    private Date startTime;

    @ApiModelProperty("结算结束时间")
    private Date endTime;

    @ApiModelProperty("结算金额")
    private BigDecimal settleAmount;

    @ApiModelProperty("结算状态：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成")
    private Integer state;

    @ApiModelProperty("结算状态值：1、生成结算单；2、店铺确认；3、平台审核；4、结算完成")
    private String stateValue;

    @ApiModelProperty("出账时间")
    private Date createTime;

    public SellerBillVO(Bill bill) {
        billId = bill.getBillId();
        billSn = bill.getBillSn();
        startTime = bill.getStartTime();
        endTime = bill.getEndTime();
        settleAmount = bill.getSettleAmount();
        state = bill.getState();
        stateValue = BillVO.dealStateValue(bill.getState());
        createTime = bill.getCreateTime();
    }
}
