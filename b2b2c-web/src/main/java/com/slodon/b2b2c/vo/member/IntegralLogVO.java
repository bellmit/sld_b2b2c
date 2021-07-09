package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.constant.MemberIntegralLogConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.member.pojo.MemberIntegralLog;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 积分变化日志表
 */
@Data
public class IntegralLogVO {

    @ApiModelProperty("日志ID")
    private Integer logId;

    @ApiModelProperty("会员ID")
    private Integer memberId;

    @ApiModelProperty("会员名称")
    private String memberName;

    @ApiModelProperty("积分变化值")
    private Integer value;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("具体操作1、会员注册；2、会员登录；3、商品购买；4、商品评论；5、系统添加；6、系统减少；7、订单消费（减少，下单时积分支付扣减）；8、商品退货（增加，用户退货时如果订单有积分支付则返回用户已使用的积分）；9、年度减少扣减经验值（减少）；10、用户签到赠送积分（增加）；11、订单取消退回积分（增加）；12、订单货品明细退货追回积分（减少，订单货品明细发生退货后，扣除用户因为购物（类型3）得到的积分，注意与8、13的区别）；13、订单取消追回积分（减少，订单取消时，扣除用户因为购物（类型3）得到的积分，注意与8、12的区别）；14、积分兑换")
    private Integer type;

    @ApiModelProperty("具体操作1、会员注册；2、会员登录；3、商品购买；4、商品评论；5、系统添加；6、系统减少；7、订单消费（减少，下单时积分支付扣减）；8、商品退货（增加，用户退货时如果订单有积分支付则返回用户已使用的积分）；9、年度减少扣减经验值（减少）；10、用户签到赠送积分（增加）；11、订单取消退回积分（增加）；12、订单货品明细退货追回积分（减少，订单货品明细发生退货后，扣除用户因为购物（类型3）得到的积分，注意与8、13的区别）；13、订单取消追回积分（减少，订单取消时，扣除用户因为购物（类型3）得到的积分，注意与8、12的区别）；14、积分兑换")
    private String typeDesc;

    @ApiModelProperty("操作描述，订单记录订单号，商品评论记录商品ID")
    private String description;


    public IntegralLogVO(MemberIntegralLog integralLog) {
        logId = integralLog.getLogId();
        memberId = integralLog.getMemberId();
        memberName = integralLog.getMemberName();
        value = integralLog.getValue();
        createTime = integralLog.getCreateTime();
        type = integralLog.getType();
        typeDesc = getTypeValue(integralLog.getType());      //
        description = integralLog.getDescription();
    }

    public static String getTypeValue(Integer type) {
        String value = null;
        if (StringUtils.isEmpty(type)) return Language.translate("未知");
        switch (type) {
            case MemberIntegralLogConst.TYPE_1:
                value = "会员注册";
                break;
            case MemberIntegralLogConst.TYPE_2:
                value = "会员登录";
                break;
            case MemberIntegralLogConst.TYPE_3:
                value = "商品购买";
                break;
            case MemberIntegralLogConst.TYPE_4:
                value = "商品评论";
                break;
            case MemberIntegralLogConst.TYPE_5:
                value = "系统添加";
                break;
            case MemberIntegralLogConst.TYPE_6:
                value = "系统减少";
                break;
            case MemberIntegralLogConst.TYPE_7:
                value = "订单消费（减少，下单时积分支付扣减）";
                break;
            case MemberIntegralLogConst.TYPE_8:
                value = "商品退货（增加，用户退货时如果订单有积分支付则返回用户已使用的积分）";
                break;
            case MemberIntegralLogConst.TYPE_9:
                value = "年度减少扣减经验值（减少）";
                break;
            case MemberIntegralLogConst.TYPE_10:
                value = "用户签到赠送积分（增加）";
                break;
            case MemberIntegralLogConst.TYPE_11:
                value = "订单取消退回积分（增加）";
                break;
            case MemberIntegralLogConst.TYPE_12:
                value = "订单货品明细退货追回积分（减少，订单货品明细发生退货后，扣除用户因为购物（类型3）得到的积分，注意与8、13的区别)";
                break;
            case MemberIntegralLogConst.TYPE_13:
                value = "订单取消追回积分（减少，订单取消时，扣除用户因为购物（类型3）得到的积分，注意与8、12的区别）";
                break;
            case MemberIntegralLogConst.TYPE_14:
                value = "积分兑换";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}