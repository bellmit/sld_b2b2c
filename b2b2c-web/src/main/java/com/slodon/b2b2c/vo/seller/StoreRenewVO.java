package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.seller.pojo.StoreRenew;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装店铺续签VO对象
 */
@Data
public class StoreRenewVO {

    @ApiModelProperty("续签id")
    private Integer renewId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺等级id")
    private Integer gradeId;

    @ApiModelProperty("店铺等级名称")
    private String gradeName;

    @ApiModelProperty("收费标准（每年）")
    private String price;

    @ApiModelProperty("续签时长（年）")
    private Integer duration;

    @ApiModelProperty("付款金额（元）")
    private BigDecimal payAmount;

    @ApiModelProperty("续签生效日期")
    private Date startTime;

    @ApiModelProperty("续签失效日期")
    private Date endTime;

    @ApiModelProperty("状态:1：待付款；2:已完成")
    private Integer state;

    @ApiModelProperty("状态值:1：待付款；2:已完成")
    private String stateValue;

    @ApiModelProperty("支付方式名称")
    private String paymentName;

    @ApiModelProperty("支付方式code")
    private String paymentCode;

    @ApiModelProperty("支付单号")
    private String paySn;

    public StoreRenewVO(StoreRenew storeRenew) {
        renewId = storeRenew.getRenewId();
        storeId = storeRenew.getStoreId();
        gradeId = storeRenew.getGradeId();
        duration = storeRenew.getDuration();
        payAmount = storeRenew.getPayAmount();
        startTime = storeRenew.getStartTime();
        endTime = storeRenew.getEndTime();
        state = storeRenew.getState();
        stateValue = getRealStateValue(state);
        paymentName = storeRenew.getPaymentName();
        paymentCode = storeRenew.getPaymentCode();
        paySn = storeRenew.getPaySn();
    }

    public static String getRealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return Language.translate("未知");
        switch (state) {
            case StoreConst.STORE_RENEW_STATE_WAITPAY:
                value = "待付款";
                break;
            case StoreConst.STORE_RENEW_STATE_SUCCESS:
                value = "已完成";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
