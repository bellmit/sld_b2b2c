package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.member.pojo.MemberBalanceLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BalanceLogVO implements Serializable {
    private static final long serialVersionUID = 3634786922947740862L;


    @ApiModelProperty("会员id")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("变化金额")
    private BigDecimal changeValue;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("类型：1、充值；2、退款；3、消费；4、提款；5、系统添加；6、系统减少")
    private Integer type;

    @ApiModelProperty("操作备注")
    private String description;

    @ApiModelProperty("状态:0 默认, 1 收入, 2 支出")
    private Integer state;

    public BalanceLogVO(MemberBalanceLog memberBalanceLog) {
        memberId = memberBalanceLog.getMemberId();
        memberName = memberBalanceLog.getMemberName();
        changeValue = memberBalanceLog.getChangeValue();
        createTime = memberBalanceLog.getCreateTime();
        type = memberBalanceLog.getType();
        state = dealState(memberBalanceLog.getType());
        description = memberBalanceLog.getDescription();
    }

    public static Integer dealState(Integer type) {
        switch (type){
            case MemberConst.TYPE_1:
            case MemberConst.TYPE_2:
            case MemberConst.TYPE_5:
                return 1;
            case MemberConst.TYPE_3:
            case MemberConst.TYPE_4:
            case MemberConst.TYPE_6:
                return 2;
            default:
                return 0;
        }
    }


}
