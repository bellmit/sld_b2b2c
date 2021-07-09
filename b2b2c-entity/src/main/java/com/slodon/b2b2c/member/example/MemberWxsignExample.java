package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class MemberWxsignExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer signIdNotEquals;

    /**
     * 用于批量操作
     */
    private String signIdIn;

    /**
     * 登录id
     */
    private Integer signId;

    /**
     * 微信用户标识
     */
    private String openid;

    /**
     * 用户id
     */
    private Integer memberId;

    /**
     * 用户来源：1==pc应用，2==app应用，3==公众号，4==小程序
     */
    private Integer resource;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照signId倒序排列
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