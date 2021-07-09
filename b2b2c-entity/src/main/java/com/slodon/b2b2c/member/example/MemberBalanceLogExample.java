package com.slodon.b2b2c.member.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberBalanceLogExample implements Serializable {
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
     * 日志id
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
     * 变化后的总余额
     */
    private BigDecimal afterChangeAmount;

    /**
     * 变化金额
     */
    private BigDecimal changeValue;

    /**
     * 变化后的冻结余额
     */
    private BigDecimal freezeAmount;

    /**
     * 变化冻结金额
     */
    private BigDecimal freezeValue;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 类型：1、充值；2、退款；3、消费；4、提款；5、系统添加；6、系统减少
     */
    private Integer type;

    /**
     * 批量
     * 类型：1、充值；2、退款；3、消费；4、提款；5、系统添加；6、系统减少
     */
    private String typeIn;

    /**
     * 操作备注
     */
    private String description;

    /**
     * 操作人ID(默认为零，如果是零的，就是会员自己；如果是系统操作的话就是管理员ID)
     */
    private Integer adminId;

    /**
     * 操作人名称
     */
    private String adminName;

    /**
     * 操作人名称,用于模糊查询
     */
    private String adminNameLike;

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