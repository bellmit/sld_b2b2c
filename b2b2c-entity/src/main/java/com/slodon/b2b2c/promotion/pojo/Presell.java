package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 预售活动表
 */
@Data
public class Presell implements Serializable {
    private static final long serialVersionUID = 5061720666761012830L;
    @ApiModelProperty("预售活动id")
    private Integer presellId;

    @ApiModelProperty("预售活动名称")
    private String presellName;

    @ApiModelProperty("预售活动标签id")
    private Integer presellLabelId;

    @ApiModelProperty("预售活动标签名称")
    private String presellLabelName;

    @ApiModelProperty("活动开始时间")
    private Date startTime;

    @ApiModelProperty("活动结束时间")
    private Date endTime;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("活动状态(1-创建；2-发布；3-失效；4-删除)")
    private Integer state;

    @ApiModelProperty("预售类型（1-定金预售，2-全款预售）")
    private Integer type;

    @ApiModelProperty("最大限购数量；0为不限购")
    private Integer buyLimit;

    @ApiModelProperty("发货时间类型（1-固定日期，2-固定天数）")
    private Integer deliverTimeType;

    @ApiModelProperty("发货时间")
    private Date deliverTime;

    @ApiModelProperty("支付尾款的开始时间")
    private Date remainStartTime;

    @ApiModelProperty("支付尾款的结束时间")
    private Date remainEndTime;

    @ApiModelProperty("发货开始时间(以天数为单位)")
    private Integer deliverStartTime;

    @ApiModelProperty("赔偿定金的倍数")
    private Integer depositCompensate;

    @ApiModelProperty("创建商户id")
    private Long createVendorId;

    @ApiModelProperty("创建预售活动时间")
    private Date createTime;

    @ApiModelProperty("活动编辑时间")
    private Date updateTime;
}