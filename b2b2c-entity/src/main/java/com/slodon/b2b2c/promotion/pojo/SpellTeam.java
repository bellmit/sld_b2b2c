package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 拼团活动团队表
 */
@Data
public class SpellTeam implements Serializable {
    private static final long serialVersionUID = -2834627558430815791L;
    @ApiModelProperty("拼团活动团队id")
    private Integer spellTeamId;

    @ApiModelProperty("拼团活动id")
    private Integer spellId;

    @ApiModelProperty("拼团活动名称")
    private String spellName;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("活动商品id（spu）")
    private Long goodsId;

    @ApiModelProperty("活动商品名称")
    private String goodsName;

    @ApiModelProperty("商品图片")
    private String goodsImage;

    @ApiModelProperty("截团时间=开团时间+成团周期")
    private Date endTime;

    @ApiModelProperty("成团时间")
    private Date finishTime;

    @ApiModelProperty("成团类型：1-自助成团；2-虚拟成团（店铺操作完成）")
    private Integer finishType;

    @ApiModelProperty("团长id")
    private Integer leaderMemberId;

    @ApiModelProperty("团长姓名")
    private String leaderMemberName;

    @ApiModelProperty("团长支付状态：0-未支付；1-已支付")
    private Integer leaderPayState;

    @ApiModelProperty("要求成团人数")
    private Integer requiredNum;

    @ApiModelProperty("已参团人数")
    private Integer joinedNum;

    @ApiModelProperty("团状态(1-进行中；2-成功；3-失败）")
    private Integer state;

    @ApiModelProperty("开团时间")
    private Date createTime;
}