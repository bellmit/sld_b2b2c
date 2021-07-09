package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装移动端店铺VO对象
 */
@Data
public class FrontStoreVO {

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺logo相对路径")
    private String storeLogo;

    @ApiModelProperty("店铺logo绝对路径")
    private String storeLogoUrl;

    @ApiModelProperty("店铺服务评分")
    private String serviceScore;

    @ApiModelProperty("店铺发货评分")
    private String deliverScore;

    @ApiModelProperty("店铺描述评分")
    private String descriptionScore;

    @ApiModelProperty("综合评分")
    private String comprehensiveScore;

    @ApiModelProperty("店铺收藏数")
    private Integer followNumber;

    @ApiModelProperty("以售商品数")
    private Integer orderFinishedCount;

    @ApiModelProperty("店铺类型 1-自营店铺，2-入驻店铺")
    private Integer isOwnShop;

    @ApiModelProperty("店铺类型 1-自营店铺，2-入驻店铺")
    private String isOwnShopValue;

    @ApiModelProperty("店铺背景图相对地址")
    private String storeBackdrop;

    @ApiModelProperty("店铺背景图绝对地址")
    private String storeBackdropUrl;

    @ApiModelProperty("所在地")
    private String address;

    @ApiModelProperty("主营商品")
    private String mainBusiness;

    @ApiModelProperty("开店时间")
    private Date createTime;

    @ApiModelProperty("公司名称")
    private String companyName;

    @ApiModelProperty("店铺二维码")
    private String storeQRCode;

    @ApiModelProperty("会员是否收藏,true:收藏，false:未收藏")
    private String isFollow;

    @ApiModelProperty("店铺客服电话")
    private String servicePhone;

    @ApiModelProperty("分享链接")
    private String shareLink;

    public FrontStoreVO(Store store) {
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        storeLogo = store.getStoreLogo();
        storeLogoUrl = FileUrlUtil.getFileUrl(store.getStoreLogo(), null);
        serviceScore = store.getServiceScore();
        deliverScore = store.getDeliverScore();
        descriptionScore = store.getDescriptionScore();
        comprehensiveScore = dealComprehensiveScore(store.getServiceScore(), store.getDeliverScore(), store.getDescriptionScore());
        followNumber = store.getFollowNumber();
        orderFinishedCount = store.getOrderFinishedCount();
        isOwnShop = store.getIsOwnStore();
        isOwnShopValue = getRealIsOwnShopValue(isOwnShop);
        storeBackdrop = store.getStoreBackdrop();
        storeBackdropUrl = FileUrlUtil.getFileUrl(store.getStoreBannerMobile(), null);
        address = store.getAddress();
        mainBusiness = store.getMainBusiness();
        createTime = store.getCreateTime();
        servicePhone = CommonUtil.dealMobile(store.getServicePhone());
    }

    public static String getRealIsOwnShopValue(Integer isOwnShop) {
        String value = null;
        if (StringUtils.isEmpty(isOwnShop)) return Language.translate("未知");
        switch (isOwnShop) {
            case StoreConst.IS_OWN_STORE:
                value = "自营店铺";
                break;
            case StoreConst.NO_OWN_STORE:
                value = "入驻店铺";
                break;
        }
        return Language.translate(value);
    }

    public static String dealComprehensiveScore(String serviceScore, String deliverScore, String descriptionScore) {
        BigDecimal avgPoint = (new BigDecimal(serviceScore).add(new BigDecimal(deliverScore)).add(new BigDecimal(descriptionScore)))
                .divide((new BigDecimal("3.0")), 1, BigDecimal.ROUND_HALF_UP);
        return avgPoint.toString();
    }
}
