package com.slodon.b2b2c.promotion.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 拼团新增DTO
 * @Author wuxy
 * @date 2020.11.04 19:47
 */
@Data
public class SpellAddDTO implements Serializable {

    private static final long serialVersionUID = -7769761307411084049L;
    @ApiModelProperty(value = "拼团活动名称", required = true)
    private String spellName;

    @ApiModelProperty(value = "拼团活动标签id", required = true)
    private Integer spellLabelId;

    @ApiModelProperty(value = "活动开始时间", required = true)
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间", required = true)
    private Date endTime;

    @ApiModelProperty(value = "要求成团人数", required = true)
    private Integer requiredNum;

    @ApiModelProperty(value = "成团周期（开团-截团时长），单位：小时", required = true)
    private Integer cycle;

    @ApiModelProperty(value = "活动最大限购数量；0为不限购", required = true)
    private Integer buyLimit;

    @ApiModelProperty("是否模拟成团(0-关闭/1-开启）")
    private Integer isSimulateGroup;

    @ApiModelProperty("团长是否有优惠(0-没有/1-有）")
    private Integer leaderIsPromotion;

    @ApiModelProperty(value = "货品信息集合,json字符串([{\"productId\":101,\"spellPrice\":50,\"leaderPrice\":10,\"spellStock\":100}])", required = true)
    private String goodsList;

}
