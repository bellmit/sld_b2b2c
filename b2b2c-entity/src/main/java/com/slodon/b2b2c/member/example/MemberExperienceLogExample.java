package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MemberExperienceLogExample implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用于编辑时的重复判断
     */
    private Integer logIdNotEquals;

    /**
     * 用于批量操作
     */
    private String logIdIn;

    /**
     * 日志ID
     */
    private Integer logId;

    /**
     * 会员ID
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
     * 经验变化值
     */
    private Integer experienceValue;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 具体操作1、会员注册；2、会员登录；3、商品购买；4、商品评论；5、系统添加；6、系统减少；7、订单消费（减少，下单时积分支付扣减）；8、商品退货（增加，用户退货时如果订单有积分支付则返回用户已使用的积分）；9、年度减少扣减经验值（减少）；10、用户签到赠送积分（增加）；11、订单取消退回积分（增加）；12、订单货品明细退货追回积分（减少，订单货品明细发生退货后，扣除用户因为购物（类型3）得到的积分，注意与8、13的区别）；13、订单取消追回积分（减少，订单取消时，扣除用户因为购物（类型3）得到的积分，注意与8、12的区别）
     */
    private Integer type;

    /**
     * 操作描述，订单记录订单号，商品评论记录商品ID
     */
    private String description;

    /**
     * 关联code，根据type判断字段值意义，3-订单sn，4-订单货品明细ID，7-订单sn，8-订单货品明细ID，11-订单sn，12-订单货品明细ID，13-订单sn
     */
    private String refCode;

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