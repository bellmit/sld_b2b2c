package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.member.pojo.Member;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberVO implements Serializable {

    private static final long serialVersionUID = -8426680439937326616L;
    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名")
    private String memberName;

    @ApiModelProperty("会员昵称")
    private String memberNickName;

    @ApiModelProperty("真实姓名")
    private String memberTrueName;

    @ApiModelProperty("生日")
    private String memberBirthday;

    @ApiModelProperty("邮箱")
    private String memberEmail;

    @ApiModelProperty("mobile")
    private String memberMobile;

    @ApiModelProperty("用户头像")
    private String memberAvatar;

    @ApiModelProperty("有无登录密码:0 无,1 有")
    private Integer isLoginPwd;

    @ApiModelProperty("有无账户支付密码:0 无,1 有")
    private Integer isPayPwd;

    @ApiModelProperty("性别：0、保密；1、男；2、女")
    private Integer gender;

    @ApiModelProperty("性别：0、保密；1、男；2、女")
    private String genderValue;

    @ApiModelProperty("会员可用积分")
    private Integer memberIntegral;

    @ApiModelProperty("会员状态：0-禁用，1-启用；默认为1")
    private Integer state;

    @ApiModelProperty("会员状态：0-禁用，1-启用；默认为1")
    private String stateValue;

    @ApiModelProperty("会员余额")
    private BigDecimal balance;

    @ApiModelProperty("注册时间")
    private Date registerTime;

    public MemberVO(Member member) {
        memberId = member.getMemberId();
        memberName = member.getMemberName();
        memberNickName = member.getMemberNickName();
        memberTrueName = member.getMemberTrueName();
        memberBirthday = TimeUtil.getZDDay(member.getMemberBirthday());
        memberEmail = member.getMemberEmail();
        memberMobile = CommonUtil.dealMobile(member.getMemberMobile());
        memberAvatar = StringUtil.isEmpty(member.getMemberAvatar()) ? member.getWxAvatarImg() : FileUrlUtil.getFileUrl(member.getMemberAvatar(), null);
        isLoginPwd = dealIsPwd(member.getLoginPwd());
        isPayPwd = dealIsPwd(member.getPayPwd());
        gender = member.getGender();
        genderValue = dealGenderValue(member.getGender());
        memberIntegral = member.getMemberIntegral();
        state = member.getState();
        balance = member.getBalanceAvailable().add(member.getBalanceFrozen());
        registerTime = member.getRegisterTime();
    }

    public static String dealGenderValue(Integer gender) {
        String value = null;
        if (StringUtils.isEmpty(gender))
            Language.translate("未知");
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

    public static Integer dealIsPwd(String pwd) {
        //有无密码
        Integer value = MemberConst.IS_PWD_NO;
        if (!StringUtils.isEmpty(pwd)) {
            value = MemberConst.IS_PWD_YES;
        }
        return value;
    }

}
