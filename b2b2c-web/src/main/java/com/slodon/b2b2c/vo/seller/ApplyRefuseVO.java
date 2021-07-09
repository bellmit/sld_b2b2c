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
public class ApplyRefuseVO {

    @ApiModelProperty("拒绝理由")
    private String refuseReason;

    @ApiModelProperty("备注")
    private String auditInfo;

    @ApiModelProperty("状态值：1、待审核；2、代付款；3、已拒绝；")
    private String stateValue;

    public ApplyRefuseVO(StoreApply storeApply) {
        refuseReason = storeApply.getRefuseReason();
        auditInfo = storeApply.getAuditInfo();
        Integer state = storeApply.getState();
        stateValue = getRealStateValue(state);
    }

    public static String getRealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return Language.translate("未知");
        switch (state) {
            case StoreConst.STATE_1_SEND_APPLY:
                value = "待审核";
                break;
            case StoreConst.STATE_2_DONE_APPLY:
                value = "代付款";
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
