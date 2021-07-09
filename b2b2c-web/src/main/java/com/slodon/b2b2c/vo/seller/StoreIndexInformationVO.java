package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.Vendor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装商户首页店铺信息VO对象
 */
@Data
public class StoreIndexInformationVO {

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺logo相对地址")
    private String storeLogo;

    @ApiModelProperty("店铺logo绝对地址")
    private String storeLogoUrl;

    @ApiModelProperty("店铺等级名称")
    private String storeGradeName;

    @ApiModelProperty("描述相符")
    private String descriptionScore;

    @ApiModelProperty("服务态度")
    private String serviceScore;

    @ApiModelProperty("物流服务")
    private String deliverScore;

    @ApiModelProperty("pc端店铺横幅")
    private String storeBannerPc;

    @ApiModelProperty("登陆账号")
    private String vendorName;

    @ApiModelProperty("最后登陆时间")
    private Date latestLoginTime;

    @ApiModelProperty("店铺类型:1-自营店铺，2-入驻店铺")
    private Integer isOwnStore;

    @ApiModelProperty("店铺类型值:1-自营店铺，2-入驻店铺")
    private String isOwnStoreValue;

    /**
     * 其他字段
     */
    @ApiModelProperty("登陆角色名称")
    private String rolesName;

    @ApiModelProperty("综合评分")
    private String comprehensiveScore;

    @ApiModelProperty("平台logo绝对地址")
    private String adminLogoUrl;

    public StoreIndexInformationVO(Store store, Vendor vendor) {
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        storeLogo = store.getStoreLogo();
        storeLogoUrl = FileUrlUtil.getFileUrl(store.getStoreLogo(), null);
        storeGradeName = store.getStoreGradeName();
        descriptionScore = store.getDescriptionScore();
        serviceScore = store.getServiceScore();
        deliverScore = store.getDeliverScore();
        storeBannerPc = FileUrlUtil.getFileUrl(store.getStoreBannerPc(), null);
        vendorName = vendor.getVendorName();
        latestLoginTime = vendor.getLatestLoginTime();
        isOwnStore = store.getIsOwnStore();
        isOwnStoreValue = getRealIsOwnStoreValue(isOwnStore);

        comprehensiveScore = dealComprehensiveScore(store.getServiceScore(), store.getDeliverScore(), store.getDescriptionScore());
    }

    public static String dealComprehensiveScore(String serviceScore, String deliverScore, String descriptionScore) {
        BigDecimal avgPoint = (new BigDecimal(serviceScore).add(new BigDecimal(deliverScore)).add(new BigDecimal(descriptionScore)))
                .divide((new BigDecimal("3.0")), 1, BigDecimal.ROUND_HALF_UP);
        return avgPoint.toString();
    }

    public static String getRealIsOwnStoreValue(Integer isOwnStore) {
        String value = null;
        if (StringUtils.isEmpty(isOwnStore))
            return Language.translate("未知");
        switch (isOwnStore) {
            case StoreConst.IS_OWN_STORE:
                value = "自营店铺";
                break;
            case StoreConst.NO_OWN_STORE:
                value = "入驻店铺";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}
