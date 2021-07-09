package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.member.pojo.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberDetailVO {

    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("用户名（登录名称）")
    private String memberName;

    @ApiModelProperty("会员昵称")
    private String memberNickName;

    @ApiModelProperty("真实姓名")
    private String memberTrueName;

    @ApiModelProperty("生日")
    private Date memberBirthday;

    @ApiModelProperty("会员可用积分")
    private Integer memberIntegral;

    @ApiModelProperty("邮箱")
    private String memberEmail;

    @ApiModelProperty("mobile")
    private String memberMobile;

    @ApiModelProperty("用户头像")
    private String memberAvatar;

    @ApiModelProperty("性别：0、保密；1、男；2、女")
    private Integer gender;

    @ApiModelProperty("性别：0、保密；1、男；2、女")
    private String genderValue;

    @ApiModelProperty("注册时间")
    private Date registerTime;

    @ApiModelProperty("最后登录时间")
    private Date lastLoginTime;

    @ApiModelProperty("最后登录IP")
    private String lastLoginIp;

    @ApiModelProperty("会员来源：1、pc；2、H5；3、Android；4、IOS ;5 商城管理平台 ; 6 微信商城")
    private Integer registerChannel;

    @ApiModelProperty("会员来源值：1、pc；2、H5；3、Android；4、IOS ;5 商城管理平台 ; 6 微信商城")
    private String registerChannelValue;

    @ApiModelProperty("可用现金余额")
    private BigDecimal balanceAvailable;

    @ApiModelProperty("会员状态：0-禁用，1-启用；默认为1")
    private Integer state;

    @ApiModelProperty("可用优惠券数")
    private Integer couponNumber;

    @ApiModelProperty("最近下单时间")
    private Date createTime;

    @ApiModelProperty("客单价")
    private BigDecimal pstPrice;

    @ApiModelProperty("累计消费金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("累计消费订单数")
    private Integer orderNumber;

    @ApiModelProperty("累计退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty("累计退款订单数")
    private Integer refundOrderNumber;

    public MemberDetailVO(Member member) {
        memberId = member.getMemberId();
        memberName = member.getMemberName();
        memberNickName = member.getMemberNickName();
        memberTrueName = member.getMemberTrueName();
        memberBirthday = member.getMemberBirthday();
        memberIntegral = member.getMemberIntegral();
        memberEmail = member.getMemberEmail();
        memberMobile = CommonUtil.dealMobile(member.getMemberMobile());
        memberAvatar = StringUtil.isEmpty(member.getMemberAvatar()) ? member.getWxAvatarImg() : FileUrlUtil.getFileUrl(member.getMemberAvatar(), null);
        gender = member.getGender();
        genderValue = dealGenderValue(gender);
        registerTime = member.getRegisterTime();
        lastLoginTime = member.getLastLoginTime();
        lastLoginIp = member.getLastLoginIp();
        registerChannel = member.getRegisterChannel();
        registerChannelValue = dealChannelValue(member.getRegisterChannel());
        balanceAvailable = member.getBalanceAvailable();
        state = member.getState();
    }

    public static String dealGenderValue(Integer gender) {
        //性别：0、保密；1、男；2、女
        String value = null;
        if (StringUtils.isEmpty(gender)) return Language.translate("未知");
        switch (gender) {
            case MemberConst.GENDER_SECRECY:
                value = "保密";
                break;
            case MemberConst.GENDER_MAN:
                value = "男";
                break;
            case MemberConst.GENDER_WOMAN:
                value = "女";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

    private String dealChannelValue(Integer channel) {
        String value = null;
        if (StringUtil.isNullOrZero(channel))
            return Language.translate("未知");
        switch (channel) {
            case MemberConst.MEMBER_FROM_PC:
                value = "pc";
            case MemberConst.MEMBER_FROM_H5:
                value = "H5";
            case MemberConst.MEMBER_FROM_Android:
                value = "Android";
            case MemberConst.MEMBER_FROM_IOS:
                value = "IOS";
            case MemberConst.MEMBER_FROM_MALL:
                value = "商城管理平台";
            case MemberConst.MEMBER_FROM_WXMALL:
                value = "微信商城";
            default:
                value = "未知";
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
