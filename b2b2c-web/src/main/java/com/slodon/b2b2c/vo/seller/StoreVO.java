package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Date;

import static com.slodon.b2b2c.vo.seller.OwnStoreDetailVO.getRealBillTypeValue;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装入驻店铺列表VO对象
 */
@Data
public class StoreVO {

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺状态 1、开启；2、关闭")
    private Integer state;

    @ApiModelProperty("店主账号")
    private String vendorName;

    @ApiModelProperty("店铺等级名称")
    private String storeGradeName;

    @ApiModelProperty("开店时间")
    private Date createTime;

    @ApiModelProperty("店铺到期时间")
    private Date storeExpireTime;

    @ApiModelProperty("联系人电话")
    private String contactPhone;

    @ApiModelProperty(value = "结算方式：1-按月结算，2-按周结算")
    private Integer billType;

    @ApiModelProperty(value = "结算方式值：1-按月结算，2-按周结算")
    private String billTypeValue;

    @ApiModelProperty("结算周期")
    private String billDay;

    public StoreVO(Store store) {
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        state = store.getState();
        storeGradeName = store.getStoreGradeName();
        createTime = store.getCreateTime();
        storeExpireTime = store.getStoreExpireTime();
        billType = store.getBillType();
        billTypeValue = getRealBillTypeValue(billType);
        billDay = store.getBillDay();
    }

    public static String getRealBillTypeValue(Integer billType) {
        String value = null;
        if (StringUtils.isEmpty(billType)) return Language.translate("未知");
        switch (billType) {
            case StoreConst.BILL_TYPE_MONTH:
                value = "按月结算";
                break;
            case StoreConst.BILL_TYPE_WEEK:
                value = "按周结算";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
