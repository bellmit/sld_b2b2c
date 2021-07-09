package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class MemberBalanceLogVO implements Serializable {

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

    @ApiModelProperty("类型：1、充值；2、退款；3、消费；4、提款；5、系统添加；6、系统减少")
    private String typeValue;

    @ApiModelProperty("操作备注")
    private String description;

    @ApiModelProperty("操作人ID(默认为零，如果是零的，就是会员自己；如果是系统操作的话就是管理员ID)")
    private Integer adminId;

    @ApiModelProperty("操作人名称")
    private String adminName;

    public MemberBalanceLogVO(MemberBalanceLog memberBalanceLog) {
        logId = memberBalanceLog.getLogId();
        memberId = memberBalanceLog.getMemberId();
        memberName = memberBalanceLog.getMemberName();
        afterChangeAmount = memberBalanceLog.getAfterChangeAmount();
        changeValue = memberBalanceLog.getChangeValue();
        freezeAmount = memberBalanceLog.getFreezeAmount();
        freezeValue = memberBalanceLog.getFreezeValue();
        createTime = memberBalanceLog.getCreateTime();
        type = memberBalanceLog.getType();
        typeValue = getTypeValue(memberBalanceLog);
        description = memberBalanceLog.getDescription();
        adminId = memberBalanceLog.getAdminId();
        adminName = memberBalanceLog.getAdminName();
    }

    public static String getTypeValue(MemberBalanceLog memberBalanceLog) {
        String value =null;
        if (StringUtils.isEmpty(memberBalanceLog.getType()))return Language.translate("未知");
        switch (memberBalanceLog.getType()){
            //类型：1、充值；2、退款；3、消费；4、提款；5、系统添加；6、系统减少
            case MemberConst.TYPE_1:
                value = "充值";
                break;
            case MemberConst.TYPE_2:
                value = "退款";
                break;
            case MemberConst.TYPE_3:
                value = "消费";
                break;
            case MemberConst.TYPE_4:
                value = "提款";
                break;
            case MemberConst.TYPE_5:
                value = "系统添加";
                break;
            case MemberConst.TYPE_6:
                value = "系统减少";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
