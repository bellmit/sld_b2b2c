package com.slodon.b2b2c.promotion.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 拼团活动团队成员表
 */
@Data
public class SpellTeamMember implements Serializable {
    private static final long serialVersionUID = -4651029377794757108L;
    @ApiModelProperty("拼团活动团队成员id")
    private Integer spellTeamMemberId;

    @ApiModelProperty("拼团活动团队id")
    private Integer spellTeamId;

    @ApiModelProperty("拼团活动id")
    private Integer spellId;

    @ApiModelProperty("拼团商品id")
    private Integer spellGoodsId;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("活动商品id（sku）")
    private Long productId;

    @ApiModelProperty("参团会员id")
    private Integer memberId;

    @ApiModelProperty("参团会员名称")
    private String memberName;

    @ApiModelProperty("会员头像")
    private String memberAvatar;

    @ApiModelProperty("团长标识(0-非团长；1-团长）")
    private Integer isLeader;

    @ApiModelProperty("参团or开团时间")
    private Date participateTime;

    @ApiModelProperty("支付状态：0-未支付；1-已支付")
    private Integer payState;
}