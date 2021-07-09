package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.seller.pojo.StoreBindCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装审核经营类目列表VO对象
 */
@Data
public class AuditCateListVO {

    @ApiModelProperty("绑定id")
    private Integer bindId;

    @ApiModelProperty("店铺id")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店主账号")
    private String vendorName;

    @ApiModelProperty("申请人")
    private Long createVendorId;

    @ApiModelProperty("申请时间")
    private Date createTime;

    @ApiModelProperty("商品信息：申请分类名称,提交类目组合")
    private String goodsCateName;

    @ApiModelProperty("分佣比例")
    private BigDecimal scaling;

    @ApiModelProperty("状态:1-待审核，2-审核通过，3-审核失败")
    private Integer state;

    @ApiModelProperty("状态值:1-待审核，2-审核通过，3-审核失败")
    private String stateValue;

    @ApiModelProperty("拒绝理由")
    private String refuseReason;

    public AuditCateListVO(StoreBindCategory storeBindCategory) {
        bindId = storeBindCategory.getBindId();
        storeId = storeBindCategory.getStoreId();
        createVendorId = storeBindCategory.getCreateVendorId();
        createTime = storeBindCategory.getCreateTime();
        goodsCateName = storeBindCategory.getGoodsCateName();
        scaling = storeBindCategory.getScaling();
        state = storeBindCategory.getState();
        stateValue = getRealStateValue(state);
        refuseReason = storeBindCategory.getRefuseReason();
    }

    public static String getRealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return Language.translate("未知");
        switch (state) {
            case StoreConst.STORE_CATEGORY_STATE_SEND:
                value = "待审核";
                break;
            case StoreConst.STORE_CATEGORY_STATE_PASS:
                value = "审核通过";
                break;
            case StoreConst.STORE_CATEGORY_STATE_FALSE:
                value = "审核失败";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
