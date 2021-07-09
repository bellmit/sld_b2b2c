package com.slodon.b2b2c.business.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @program: slodon
 * @Description 前后端数据交互DTO
 * @Author wuxy
 */
@Data
public class OrderAfterDTO implements Serializable {
    private static final long serialVersionUID = -3741813446512010231L;
    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("售后服务单类型，1-退货退款单，2-换货单，3-仅退款单")
    private Integer afsType;

    @ApiModelProperty("货物状态：0-未收到货，1-已收到货")
    private Integer goodsState;

    @ApiModelProperty("申请售后服务原因")
    private String applyReasonContent;

    @ApiModelProperty("申请售后详细问题描述")
    private String afsDescription;

    @ApiModelProperty("申请提交图片,逗号分隔")
    private String applyImage;

    @ApiModelProperty("按照此金额退款")
    private BigDecimal finalReturnAmount;

    @ApiModelProperty("订单货品列表")
    private List<AfterProduct> productList;

    @Data
    public static class AfterProduct implements Serializable {
        private static final long serialVersionUID = -5797427510691379966L;
        @ApiModelProperty("订单货品ID")
        private Long orderProductId;

        @ApiModelProperty("申请售后数量")
        private Integer afsNum;

        @ApiModelProperty("退款金额")
        private BigDecimal returnAmount;
    }
}
