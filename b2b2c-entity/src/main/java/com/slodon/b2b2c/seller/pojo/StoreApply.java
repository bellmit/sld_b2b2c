package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商家申请表
 */
@Data
public class StoreApply implements Serializable {
    private static final long serialVersionUID = 7492642995071695931L;
    @ApiModelProperty("申请id")
    private Integer applyId;

    @ApiModelProperty("商户用户id")
    private Long vendorId;

    @ApiModelProperty("商户登陆用户名")
    private String vendorName;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺类型:1-自营店铺，2-入驻店铺")
    private Integer storeType;

    @ApiModelProperty("状态：1、店铺信息提交申请；2、店铺信息审核通过；3、店铺信息审核失败；4、开通店铺(支付完成)")
    private Integer state;

    @ApiModelProperty("申请年限，默认为1年")
    private Integer applyYear;

    @ApiModelProperty("申请的店铺等级编号")
    private Integer storeGradeId;

    @ApiModelProperty("申请的店铺分类")
    private Integer storeCategoryId;

    @ApiModelProperty("申请提交时间")
    private Date submitTime;

    @ApiModelProperty("拒绝理由")
    private String refuseReason;

    @ApiModelProperty("平台审核信息")
    private String auditInfo;

    @ApiModelProperty("支付单号")
    private String paySn;

    @ApiModelProperty("支付金额")
    private BigDecimal payAmount;

    @ApiModelProperty("支付成功时间")
    private Date callbackTime;

    @ApiModelProperty("第三方支付交易流水号")
    private String tradeSn;

    @ApiModelProperty("支付方式名称")
    private String paymentName;

    @ApiModelProperty("支付方式code")
    private String paymentCode;
}