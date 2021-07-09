package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.constant.OrderConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.promotion.pojo.SpellTeamMember;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @program: slodon
 * @Description 拼团活动会员订单信息VO
 * @Author wuxy
 * @date 2020.11.05 19:15
 */
@Data
public class SpellMemberOrderVO implements Serializable {

    private static final long serialVersionUID = 1218365935887470253L;
    @ApiModelProperty("拼团活动团队成员id")
    private Integer spellTeamMemberId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名")
    private String memberName;

    @ApiModelProperty("团长标识(0-非团长；1-团长）")
    private Integer isLeader;

    @ApiModelProperty("会员头像")
    private String memberAvatar;

    @ApiModelProperty("规格")
    private String specValues;

    @ApiModelProperty("单价")
    private BigDecimal spellPrice;

    @ApiModelProperty("数量")
    private Integer productNum;

    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("订单状态：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private Integer orderState;

    @ApiModelProperty("订单状态值：0-已取消；10-未付款订单；20-已付款；30-已发货；40-已完成;50-已关闭")
    private String orderStateValue;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("下单时间")
    private Date createTime;

    public SpellMemberOrderVO(SpellTeamMember member, OrderProduct orderProduct) {
        spellTeamMemberId = member.getSpellTeamMemberId();
        memberId = member.getMemberId();
        memberName = member.getMemberName();
        isLeader = member.getIsLeader();
        memberAvatar = dealMemberAvatar(member.getMemberAvatar().trim());
        specValues = orderProduct.getSpecValues();
        spellPrice = orderProduct.getProductShowPrice();
        productNum = orderProduct.getProductNum();
        orderAmount = orderProduct.getMoneyAmount();
        orderSn = member.getOrderSn();
        createTime = member.getParticipateTime();
    }

    public static String dealOrderStateValue(Integer orderState) {
        String value = null;
        if (StringUtils.isEmpty(orderState)) return null;
        switch (orderState) {
            case OrderConst.ORDER_STATE_10:
                value = "未付款";
                break;
            case OrderConst.ORDER_STATE_20:
                value = "待发货";
                break;
            case OrderConst.ORDER_STATE_30:
                value = "已发货";
                break;
            case OrderConst.ORDER_STATE_40:
                value = "已完成";
                break;
            case OrderConst.ORDER_STATE_50:
                value = "已关闭";
                break;
            case OrderConst.ORDER_STATE_0:
                value = "已取消";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }


    public static String dealMemberAvatar(String memberAvatar) {
        String value = null;
        if (StringUtils.isEmpty(memberAvatar)) return null;
        if (memberAvatar.startsWith("http")) {
            value = memberAvatar;
        } else {
            value = FileUrlUtil.getFileUrl(memberAvatar, ImageSizeEnum.SMALL);
        }
        return value;
    }
}
