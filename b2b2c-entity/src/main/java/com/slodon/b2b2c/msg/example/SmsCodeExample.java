package com.slodon.b2b2c.msg.example;


import java.io.Serializable;
import java.util.Date;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

@Data
public class SmsCodeExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer codeIdNotEquals;

    /**
     * 用于批量操作
     */
    private String codeIdIn;

    /**
     * 主键id
     */
    private Integer codeId;

    /**
     * 会员id,注册为0
     */
    private Integer memberId;

    /**
     * 会员名称
     */
    private String memberName;

    /**
     * 会员名称,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 手机验证码
     */
    private String verifyCode;

    /**
     * 短信类型:1.注册,2.登录,3.找回密码,4.其它
     */
    private Integer smsType;

    /**
     * 请求ip
     */
    private String requestIp;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照codeId倒序排列
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
}