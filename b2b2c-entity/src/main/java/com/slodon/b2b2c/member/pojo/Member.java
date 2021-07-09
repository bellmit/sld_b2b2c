package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员
 */
@Data
public class Member implements Serializable {
    private static final long serialVersionUID = -1413089121177657839L;
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

    @ApiModelProperty("冻结积分数量")
    private Integer integralFrozen;

    @ApiModelProperty("邮箱")
    private String memberEmail;

    @ApiModelProperty("qq")
    private String memberQq;

    @ApiModelProperty("mobile")
    private String memberMobile;

    @ApiModelProperty("用户头像")
    private String memberAvatar;

    @ApiModelProperty("密码")
    private String loginPwd;

    @ApiModelProperty("账户支付密码")
    private String payPwd;

    @ApiModelProperty("性别：0、保密；1、男；2、女")
    private Integer gender;

    @ApiModelProperty("会员等级")
    private Integer grade;

    @ApiModelProperty("会员经验值")
    private Integer experienceValue;

    @ApiModelProperty("注册时间")
    private Date registerTime;

    @ApiModelProperty("最后登录时间")
    private Date lastLoginTime;

    @ApiModelProperty("最后登录IP")
    private String lastLoginIp;

    @ApiModelProperty("登录次数")
    private Integer loginNumber;

    @ApiModelProperty("上次使用的支付方式")
    private String lastPaymentCode;

    @ApiModelProperty("支付密码输入错误次数，正确支付后此数清零")
    private Integer payErrorCount;

    @ApiModelProperty("登录密码输入错误次数，正确登录后此数清零")
    private Integer loginErrorCount;

    @ApiModelProperty("会员来源：1、pc；2、H5；3、Android；4、IOS ;5 商城管理平台 ; 6 微信商城")
    private Integer registerChannel;

    @ApiModelProperty("可用现金余额")
    private BigDecimal balanceAvailable;

    @ApiModelProperty("冻结金额（提现）")
    private BigDecimal balanceFrozen;

    @ApiModelProperty("会员状态：0-禁用，1-启用；默认为1")
    private Integer state;

    @ApiModelProperty("邮箱是否验证：0-未验证；1-验证")
    private Integer isEmailVerify;

    @ApiModelProperty("手机是否验证：0-未验证；1-验证")
    private Integer isMobileVerify;

    @ApiModelProperty("是否接收短信：0-不接收；1-接收")
    private Integer isReceiveSms;

    @ApiModelProperty("是否接收邮件：0-不接收；1-接收")
    private Integer isReceiveEmail;

    @ApiModelProperty("是否允许购买：1-允许，0-禁止")
    private Integer isAllowBuy;

    @ApiModelProperty("是否允许提问：1-允许，0-禁止")
    private Integer isAllowAsk;

    @ApiModelProperty("是否允许评论：1-允许，0-禁止")
    private Integer isAllowComment;

    @ApiModelProperty("微信用户统一标识")
    private String wxUnionid;

    @ApiModelProperty("微信用户头像")
    private String wxAvatarImg;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    /**
     * 获取账户余额
     */
    public BigDecimal getBalance() {
        if (this.getBalanceAvailable() == null || this.getBalanceFrozen() == null) {
            return null;
        }
        return this.getBalanceAvailable().add(this.getBalanceFrozen());
    }
}