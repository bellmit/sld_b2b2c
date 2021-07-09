package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberExample implements Serializable {
    private static final long serialVersionUID = -3320183787038395718L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer memberIdNotEquals;

    /**
     * 用于批量操作
     */
    private String memberIdIn;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 用户名（登录名称）
     */
    private String memberName;

    /**
     * 用户名（登录名称）,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 会员昵称
     */
    private String memberNickName;

    /**
     * 会员昵称,用于模糊查询
     */
    private String memberNickNameLike;

    /**
     * 真实姓名
     */
    private String memberTrueName;

    /**
     * 真实姓名,用于模糊查询
     */
    private String memberTrueNameLike;

    /**
     * 生日
     */
    private Date memberBirthday;

    /**
     * 会员可用积分
     */
    private Integer memberIntegral;

    /**
     * 冻结积分数量
     */
    private Integer integralFrozen;

    /**
     * 邮箱
     */
    private String memberEmail;

    /**
     * qq
     */
    private String memberQq;

    /**
     * mobile
     */
    private String memberMobile;

    /**
     * mobile,用于模糊查询
     */
    private String memberMobileLike;

    /**
     * 用户头像
     */
    private String memberAvatar;

    /**
     * 密码
     */
    private String loginPwd;

    /**
     * 账户支付密码
     */
    private String payPwd;

    /**
     * 性别：0、保密；1、男；2、女
     */
    private Integer gender;

    /**
     * 会员等级
     */
    private Integer grade;

    /**
     * 会员经验值
     */
    private Integer experienceValue;

    /**
     * 大于等于开始时间
     */
    private Date registerTimeAfter;


    /**
     * 小于等于结束时间
     */
    private Date registerTimeBefore;


    /**
     * 大于等于开始时间
     */
    private Date lastLoginTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date lastLoginTimeBefore;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 登录次数
     */
    private Integer loginNumber;

    /**
     * 上次使用的支付方式
     */
    private Integer lastPaymentCode;

    /**
     * 支付密码输入错误次数，正确支付后此数清零
     */
    private Integer payErrorCount;

    /**
     * 登录密码输入错误次数，正确登录后此数清零
     */
    private Integer loginErrorCount;

    /**
     * 会员来源：1、pc；2、H5；3、Android；4、IOS ;5 商城管理平台 ; 6 微信商城
     */
    private Integer registerChannel;

    /**
     * 可用现金余额
     */
    private BigDecimal balanceAvailable;

    /**
     * 冻结金额（提现）
     */
    private BigDecimal balanceFrozen;

    /**
     * 会员状态：0-禁用，1-启用；默认为1
     */
    private Integer state;

    /**
     * 邮箱是否验证：0-未验证；1-验证
     */
    private Integer isEmailVerify;

    /**
     * 手机是否验证：0-未验证；1-验证
     */
    private Integer isMobileVerify;

    /**
     * 是否接收短信：0-不接收；1-接收
     */
    private Integer isReceiveSms;

    /**
     * 是否接收邮件：0-不接收；1-接收
     */
    private Integer isReceiveEmail;

    /**
     * 是否允许购买：1-允许，0-禁止
     */
    private Integer isAllowBuy;

    /**
     * 是否允许提问：1-允许，0-禁止
     */
    private Integer isAllowAsk;

    /**
     * 是否允许评论：1-允许，0-禁止
     */
    private Integer isAllowComment;

    /**
     * 微信用户统一标识
     */
    private String wxUnionid;

    /**
     * 微信用户头像
     */
    private String wxAvatarImg;

    /**
     * 大于等于开始时间
     */
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照memberId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;

    /**
     * 用户名或者手机号
     */
    private String memberNameOrMemberMobile;
}