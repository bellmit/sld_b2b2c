package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分商城结算表
 */
@Data
public class IntegralBill implements Serializable {

    private static final long serialVersionUID = -1264340408793627142L;
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

    @ApiModelProperty("打款备注")
    private String paymentRemark;

    @ApiModelProperty("打款凭证")
    private String paymentEvidence;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;
}