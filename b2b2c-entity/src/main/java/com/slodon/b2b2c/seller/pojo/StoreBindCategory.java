package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺可用商品分类（已申请绑定），自营平台默认可用所有分类
 */
@Data
public class StoreBindCategory implements Serializable {
    private static final long serialVersionUID = -502842882198985849L;

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("申请人")
    private Long createVendorId;

    @ApiModelProperty("申请时间")
    private Date createTime;

    @ApiModelProperty("申请分类名称,提交类目组合")
    private String goodsCateName;

    @ApiModelProperty("分佣比例")
    private BigDecimal scaling;

    @ApiModelProperty("1-提交审核;2-审核通过;3-审核失败;")
    private Integer state;

    @ApiModelProperty("审核管理员ID")
    private Integer auditAdminId;

    @ApiModelProperty("审核时间")
    private Date auditTime;

    @ApiModelProperty("审核拒绝理由")
    private String refuseReason;

    @ApiModelProperty("申请分类id（一级）")
    private Integer goodsCategoryId1;

    @ApiModelProperty("申请分类id（二级）")
    private Integer goodsCategoryId2;

    @ApiModelProperty("申请分类id（三级）")
    private Integer goodsCategoryId3;
}