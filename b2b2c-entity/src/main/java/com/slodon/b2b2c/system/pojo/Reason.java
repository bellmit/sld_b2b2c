package com.slodon.b2b2c.system.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 原因表
 */
@Data
public class Reason implements Serializable {
    private static final long serialVersionUID = -4250716753160102274L;
    @ApiModelProperty("原因id")
    private Integer reasonId;

    @ApiModelProperty("原因类型：0-通用（不可编辑）；101-违规下架；102-商品审核拒绝；103-入驻审核拒绝；104-会员取消订单；105-仅退款-未收货；106-仅退款-已收货；107-退款退货原因；108-商户取消订单")
    private Integer type;

    @ApiModelProperty("理由")
    private String content;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否显示：1-显示，0-不显示")
    private Integer isShow;

    @ApiModelProperty("是否系统内置 1-内置（不可删除、不可修改），0-非内置（可删除、修改）")
    private Integer isInner;

    @ApiModelProperty("管理员ID")
    private Integer createAdminId;

    @ApiModelProperty("添加时间")
    private Date createTime;
}