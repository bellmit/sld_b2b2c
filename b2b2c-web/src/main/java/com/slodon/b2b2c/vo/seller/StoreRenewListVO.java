package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.seller.pojo.StoreRenew;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装店铺续签列表VO对象
 */
@Data
public class StoreRenewListVO {

    @ApiModelProperty("续签id")
    private Integer renewId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店主名称")
    private String vendorName;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

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

    @ApiModelProperty("状态:1：待付款；2:已付款")
    private Integer state;

    @ApiModelProperty("状态值:1：待付款；2:已付款")
    private String stateValue;

    @ApiModelProperty("支付方式名称")
    private String paymentName;

    @ApiModelProperty("支付方式code")
    private String paymentCode;

    public StoreRenewListVO(StoreRenew storeRenew) {
        renewId = storeRenew.getRenewId();
        storeId = storeRenew.getStoreId();
        storeName = storeRenew.getStoreName();
        vendorName = storeRenew.getVendorName();
        contactName = storeRenew.getContactName();
        contactPhone = CommonUtil.dealMobile(storeRenew.getContactPhone());
        gradeId = storeRenew.getGradeId();
        duration = storeRenew.getDuration();
        payAmount = storeRenew.getPayAmount();
        startTime = storeRenew.getStartTime();
        endTime = storeRenew.getEndTime();
        state = storeRenew.getState();
        stateValue = getRealStateValue(state);
        paymentName = storeRenew.getPaymentName();
        paymentCode = storeRenew.getPaymentCode();

    }

    public static String getRealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state))
            return Language.translate("未知");
        switch (state) {
            case StoreConst.STORE_RENEW_STATE_WAITPAY:
                value = "待付款";
                break;
            case StoreConst.STORE_RENEW_STATE_SUCCESS:
                value = "已付款";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
