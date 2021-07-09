package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.constant.FreightConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.goods.pojo.GoodsFreightExtend;
import com.slodon.b2b2c.goods.pojo.GoodsFreightTemplate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装运费模板列表VO对象
 */
@Data
public class FreightTemplateListVO {
    @ApiModelProperty("运费模板ID")
    private Integer freightTemplateId;

    @ApiModelProperty("运费模板名称")
    private String templateName;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("最后更新时间")
    private Date updateTime;

    @ApiModelProperty("是否免费配送，1-是，0-否")
    private String isFree;

    @ApiModelProperty("是否免费配送，1-是，0-否")
    private String isFreeValue;

    @ApiModelProperty("计费方式：1-按件，2-按重量，3-按体积")
    private String chargeType;

    @ApiModelProperty("计费方式：1-按件，2-按重量，3-按体积")
    private String chargeTypeValue;

    @ApiModelProperty("运费模板详情")
    private List<GoodsFreightExtend> freightExtendList;

    public FreightTemplateListVO(GoodsFreightTemplate goodsFreightTemplate) {
        freightTemplateId = goodsFreightTemplate.getFreightTemplateId();
        templateName = goodsFreightTemplate.getTemplateName();
        storeId = goodsFreightTemplate.getStoreId();
        updateTime = goodsFreightTemplate.getUpdateTime();
        isFree = goodsFreightTemplate.getIsFree();
        isFreeValue = getRealIsFreeValue(isFree);
        chargeType = goodsFreightTemplate.getChargeType();
        chargeTypeValue = getRealChargeTypeValue(chargeType);
        freightExtendList = goodsFreightTemplate.getFreightExtendList();

    }

    public static String getRealIsFreeValue(String isFree) {
        String value = null;
        if (StringUtils.isEmpty(isFree)) return Language.translate("未知");
        switch (isFree) {
            case FreightConst.IS_FREE_YES:
                value = "免费";
                break;
            case FreightConst.IS_FREE_NO:
                value = "收费";
                break;
        }
        return Language.translate(value);
    }

    public static String getRealChargeTypeValue(String chargeType) {
        String value = null;
        if (StringUtils.isEmpty(chargeType)) return Language.translate("未知");
        switch (chargeType) {
            case FreightConst.CHARGE_TYPE_1:
                value = "按件";
                break;
            case FreightConst.CHARGE_TYPE_2:
                value = "按重量";
                break;
            case FreightConst.CHARGE_TYPE_3:
                value = "按体积";
                break;
        }
        return Language.translate(value);
    }

}