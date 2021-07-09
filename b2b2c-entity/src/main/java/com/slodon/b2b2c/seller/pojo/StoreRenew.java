package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺续签
 */
@Data
public class StoreRenew implements Serializable {
    private static final long serialVersionUID = 8568851493109563123L;

    @ApiModelProperty("续签id")
    private Integer renewId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店主账号")
    private String vendorName;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("申请时间")
    private Date applyTime;

    @ApiModelProperty("续签时长")
    private Integer duration;

    @ApiModelProperty("店铺等级")
    private Integer gradeId;

    @ApiModelProperty("付款凭证")
    private String paymentEvidence;

    @ApiModelProperty("付款凭证说明")
    private String paymentEvidenceDesc;

    @ApiModelProperty("付款时间")
    private Date payTime;

    @ApiModelProperty("续签状态，1：待付款；2续签成功；；")
    private Integer state;

    @ApiModelProperty("续签生效日期")
    private Date startTime;

    @ApiModelProperty("续签失效日期")
    private Date endTime;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("第三方支付交易流水号")
    private String tradeSn;

    @ApiModelProperty("支付方式名称")
    private String paymentName;

    @ApiModelProperty("支付方式code")
    private String paymentCode;
}