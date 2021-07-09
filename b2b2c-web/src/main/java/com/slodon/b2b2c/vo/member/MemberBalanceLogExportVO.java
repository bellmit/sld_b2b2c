package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.util.ExcelExportUtil;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberBalanceLogExportVO {

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("变化金额")
    private BigDecimal changeValue;

    @ApiModelProperty("冻结金额")
    private BigDecimal freezeValue;

    @ApiModelProperty("变化后的总余额")
    private BigDecimal afterChangeAmount;

    @ApiModelProperty("变化后的冻结余额")
    private BigDecimal freezeAmount;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("操作人名称")
    private String adminName;

    @ApiModelProperty("操作备注")
    private String description;

    public MemberBalanceLogExportVO(MemberBalanceLog memberBalanceLog) {
        memberName = memberBalanceLog.getMemberName();
        changeValue = memberBalanceLog.getChangeValue();
        freezeValue = memberBalanceLog.getFreezeValue();
        afterChangeAmount = memberBalanceLog.getAfterChangeAmount();
        freezeAmount = memberBalanceLog.getFreezeAmount();
        createTime = ExcelExportUtil.getCnDate(memberBalanceLog.getCreateTime());
        adminName = memberBalanceLog.getAdminName();
        description = memberBalanceLog.getDescription();
    }
}
