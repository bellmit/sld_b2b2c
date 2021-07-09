package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.member.pojo.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装个人中心模块VO对象
 * @Author wuxy
 */
@Data
public class MemberInfoVO implements Serializable {

    private static final long serialVersionUID = 5060587818893586197L;
    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名")
    private String memberName;

    @ApiModelProperty("会员昵称")
    private String memberNickName;

    @ApiModelProperty("用户头像")
    private String memberAvatar;

    @ApiModelProperty("会员余额")
    private BigDecimal memberBalance;

    @ApiModelProperty("会员积分")
    private Integer memberIntegral;

    @ApiModelProperty("邮箱")
    private String memberEmail;

    @ApiModelProperty("手机号")
    private String memberMobile;

    @ApiModelProperty("是否有登录密码")
    private Integer hasLoginPassword;

    @ApiModelProperty("是否有支付密码")
    private Integer hasPayPassword;

    @ApiModelProperty("是否有邮箱")
    private Integer hasMemberEmail;

    @ApiModelProperty("待付款订单")
    private Integer toPaidOrder;

    @ApiModelProperty("待发货订单")
    private Integer toDeliverOrder;

    @ApiModelProperty("待收货订单")
    private Integer toReceivedOrder;

    @ApiModelProperty("待评价订单")
    private Integer toEvaluateOrder;

    @ApiModelProperty("售后数")
    private Integer afterSaleNum;

    @ApiModelProperty("优惠劵数")
    private Integer couponNum;

    @ApiModelProperty("收藏商品数")
    private Integer followProductNum;

    @ApiModelProperty("关注店铺数")
    private Integer followStoreNum;

    @ApiModelProperty("我的足迹数")
    private Integer lookLogNum;

    @ApiModelProperty("未读消息数")
    private Integer msgNum;

    @ApiModelProperty("是否开启商户中心")
    private String sellerSwitch;

    @ApiModelProperty("商户地址")
    private String sellerUrl;

    public MemberInfoVO(Member member) {
        memberId = member.getMemberId();
        memberName = member.getMemberName();
        memberNickName = member.getMemberNickName();
        memberAvatar = StringUtil.isEmpty(member.getMemberAvatar()) ? member.getWxAvatarImg() : FileUrlUtil.getFileUrl(member.getMemberAvatar(), null);
        memberBalance = member.getBalanceAvailable().add(member.getBalanceFrozen());
        memberIntegral = member.getMemberIntegral() + member.getIntegralFrozen();
        memberEmail = member.getMemberEmail();
        memberMobile = member.getMemberMobile();
        hasLoginPassword = StringUtils.isEmpty(member.getLoginPwd()) ? 0 : 1;
        hasPayPassword = StringUtils.isEmpty(member.getPayPwd()) ? 0 : 1;
        hasMemberEmail = StringUtils.isEmpty(member.getMemberEmail()) ? 0 : 1;
    }
}
