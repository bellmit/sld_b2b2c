package com.slodon.b2b2c.vo.promotion;

import com.slodon.b2b2c.core.constant.CouponConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.promotion.pojo.CouponMember;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: slodon
 * @Description 封装优惠券用户VO对象
 * @Author wuxy
 */
@Data
public class CouponMemberVO implements Serializable {

    private static final long serialVersionUID = 3984401192044308845L;
    @ApiModelProperty("会员优惠券ID")
    private Integer couponMemberId;

    @ApiModelProperty("优惠券id")
    private Integer couponId;

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("领取时间")
    private Date receiveTime;

    @ApiModelProperty("使用时间")
    private Date useTime;

    @ApiModelProperty("使用状态(1-未使用；2-使用；3-过期无效）")
    private Integer useState;

    @ApiModelProperty("使用状态值(1-未使用；2-使用；3-过期无效）")
    private String useStateValue;

    public CouponMemberVO(CouponMember couponMember) {
        couponMemberId = couponMember.getCouponMemberId();
        couponId = couponMember.getCouponId();
        memberId = couponMember.getMemberId();
        memberName = couponMember.getMemberName();
        receiveTime = couponMember.getReceiveTime();
        useTime = couponMember.getUseTime();
        useState = couponMember.getUseState();
        useStateValue = dealUseStateValue(couponMember.getUseState());
    }

    public static String dealUseStateValue(Integer useState) {
        String value = null;
        if (StringUtils.isEmpty(useState)) return null;
        switch (useState) {
            case CouponConst.USE_STATE_1:
                value = "未使用";
                break;
            case CouponConst.USE_STATE_2:
                value = "已使用";
                break;
            case CouponConst.USE_STATE_3:
                value = "已过期";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
