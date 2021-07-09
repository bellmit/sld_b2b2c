package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 预售编辑DTO
 * @Author wuxy
 */
@Data
public class PreSellUpdateDTO implements Serializable {

    private static final long serialVersionUID = 3286345408410772629L;
    @ApiModelProperty(value = "预售活动id", required = true)
    private Integer presellId;

    @ApiModelProperty(value = "预售活动名称", required = true)
    private String presellName;

    @ApiModelProperty(value = "预售活动标签id", required = true)
    private Integer presellLabelId;

    @ApiModelProperty(value = "活动开始时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间", required = true)
    private Date endTime;

    @ApiModelProperty(value = "预售类型（1-定金预售，2-全款预售）", required = true)
    private Integer type;

    @ApiModelProperty(value = "最大限购数量；0为不限购", required = true)
    private Integer buyLimit;

    @ApiModelProperty(value = "发货时间", required = true)
    private Date deliverTime;

    @ApiModelProperty("支付尾款的开始时间")
    private Date remainStartTime;

    @ApiModelProperty("支付尾款的结束时间")
    private Date remainEndTime;

    @ApiModelProperty(value = "货品信息集合,json字符串（[{\"productId\":101,\"presellPrice\":150,\"firstMoney\":50,\"firstExpand\":100,\"presellStock\":100}]）", required = true)
    private String goodsList;

}
