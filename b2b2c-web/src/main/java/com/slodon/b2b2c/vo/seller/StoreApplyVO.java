package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装入驻审核列表VO对象
 */
@Data
public class StoreApplyVO {

    @ApiModelProperty("申请id")
    private Integer applyId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店主账号")
    private String vendorName;

    @ApiModelProperty("联系人名称")
    private String contactName;

    @ApiModelProperty("联系电话")
    private String contactPhone;

    @ApiModelProperty("店铺等级id")
    private Integer storeGradeId;

    @ApiModelProperty("状态：1、店铺信息提交申请；2、店铺信息审核通过；3、店铺信息审核失败；")
    private Integer state;

    @ApiModelProperty("状态值：1、待审核；2、待付款；3、已拒绝；")
    private String stateValue;

    @ApiModelProperty("店铺等级名称")
    private String storeGradeName;

    @ApiModelProperty("拒绝理由")
    private String refuseReason;

    @ApiModelProperty("备注")
    private String auditInfo;

    public StoreApplyVO(StoreApply storeApply) {
        applyId = storeApply.getApplyId();
        storeName = storeApply.getStoreName();
        vendorName = storeApply.getVendorName();
        storeGradeId = storeApply.getStoreGradeId();
        state = storeApply.getState();
        stateValue = getRealStateValue(state);
        refuseReason = storeApply.getRefuseReason();
        auditInfo = storeApply.getAuditInfo();
    }

    public static String getRealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state))
            return Language.translate("未知");
        switch (state) {
            case StoreConst.STATE_1_SEND_APPLY:
                value = "待审核";
                break;
            case StoreConst.STATE_2_DONE_APPLY:
                value = "待付款";
                break;
            case StoreConst.STATE_3_FAIL_APPLY:
                value = "已拒绝";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
