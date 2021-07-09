package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 运费模板
 */
@Data
public class GoodsFreightTemplate implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("运费模板ID")
    private Integer freightTemplateId;

    @ApiModelProperty("运费模板名称")
    private String templateName;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("最后更新时间")
    private Date updateTime;

    @ApiModelProperty("发货时间")
    private Integer deliverTime;

    @ApiModelProperty("是否免费配送，1-免费，0-收费")
    private String isFree;

    @ApiModelProperty("计费方式：1-按件，2-按重量，3-按体积")
    private String chargeType;

    List<GoodsFreightExtend> freightExtendList;
}