package com.slodon.b2b2c.member.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 账户余额变化明细表
 */
@Data
public class MemberBalanceLog implements Serializable {
    private static final long serialVersionUID = 1581167170247845100L;
    @ApiModelProperty("日志id")
    private Integer logId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("变化后的总余额")
    private BigDecimal afterChangeAmount;

    @ApiModelProperty("变化金额")
    private BigDecimal changeValue;

    @ApiModelProperty("变化后的冻结余额")
    private BigDecimal freezeAmount;

    @ApiModelProperty("变化冻结金额")
    private BigDecimal freezeValue;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("类型：1、充值；2、退款；3、消费；4、提款；5、系统添加；6、系统减少")
    private Integer type;

    @ApiModelProperty("操作备注")
    private String description;

    @ApiModelProperty("操作人ID(默认为零，如果是零的，就是会员自己；如果是系统操作的话就是管理员ID)")
    private Integer adminId;

    @ApiModelProperty("操作人名称")
    private String adminName;
}