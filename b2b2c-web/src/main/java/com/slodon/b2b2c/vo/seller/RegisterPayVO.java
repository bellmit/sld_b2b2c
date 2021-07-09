package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装支付凭证VO对象
 */
@Data
public class RegisterPayVO {

    @ApiModelProperty("店铺等级id")
    private Integer storeGradeId;

    @ApiModelProperty("店铺等级名称")
    private String gradeName;

    @ApiModelProperty("收费标准（每年）")
    private String price;

    @ApiModelProperty("开店时长（年）")
    private Integer applyYear;

    @ApiModelProperty("应付金额（元）")
    private BigDecimal payAmount;

    @ApiModelProperty("状态值：1、待审核；2、代付款；3、已拒绝；")
    private String stateValue;

    @ApiModelProperty("经营类目集合")
    private List<StoreGoodsCateVO> storeGoodsCateVOList;

    public RegisterPayVO(StoreGrade storeGrade, StoreApply storeApply) {
        storeGradeId = storeGrade.getGradeId();
        gradeName = storeGrade.getGradeName();
        price = storeGrade.getPrice();
        applyYear = storeApply.getApplyYear();
        payAmount = storeApply.getPayAmount();
        Integer state = storeApply.getState();
        stateValue = getRealStateValue(state);
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
