package com.slodon.b2b2c.promotion.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SignLogExample implements Serializable {
    private static final long serialVersionUID = 2765227897387149674L;
    /**
     * 用于编辑时的重复判断
     */
    private Integer logIdNotEquals;

    /**
     * 用于批量操作
     */
    private String logIdIn;

    /**
     * 日志id
     */
    private Integer logId;

    /**
     * 签到活动ID
     */
    private Integer signActivityId;

    /**
     * 会员id
     */
    private Integer memberId;

    /**
     * 会员名字
     */
    private String memberName;

    /**
     * 会员名字,用于模糊查询
     */
    private String memberNameLike;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 大于等于开始时间
     */
    private Date signTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date signTimeBefore;

    /**
     * 会员来源：1、pc；2、H5；3、小程序；4、Android；5、IOS ;
     */
    private Integer signSource;

    /**
     * 签到类型：0-每日签到，1-连续签到
     */
    private Integer signType;

    /**
     * 签到奖励积分
     */
    private Integer bonusIntegral;

    /**
     * 签到奖励优惠券
     */
    private Integer bonusVoucher;

    /**
     * 连续签到次数，从record表中获取
     */
    private Integer continueNum;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照logId倒序排列
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