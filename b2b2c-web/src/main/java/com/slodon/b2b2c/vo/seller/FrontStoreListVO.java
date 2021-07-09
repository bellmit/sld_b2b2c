package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装移动端店铺VO对象
 */
@Data
public class FrontStoreListVO {

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺logo")
    private String storeLogo;

    @ApiModelProperty("店铺logo绝对地址")
    private String storeLogoUrl;

    @ApiModelProperty("店铺服务评分")
    private String serviceScore;

    @ApiModelProperty("店铺发货评分")
    private String deliverScore;

    @ApiModelProperty("店铺收藏数")
    private Integer followNumber;

    @ApiModelProperty("已售商品数")
    private Integer orderFinishedCount;

    @ApiModelProperty("店铺类型 1-自营店铺，2-入驻店铺")
    private Integer isOwnShop;

    @ApiModelProperty("店铺类型 1-自营店铺，2-入驻店铺")
    private String isOwnShopValue;

    @ApiModelProperty("店铺背景图相对地址")
    private String storeBackdrop;

    @ApiModelProperty("店铺背景图绝对地址")
    private String storeBackdropUrl;

    @ApiModelProperty("会员是否收藏,true:收藏，false:未收藏")
    private String isFollow;

    @ApiModelProperty("店铺热销商品列表")
    private List<StoreGoodsVO> goodsListVOList;

    @ApiModelProperty("店铺本月上新商品列表")
    private List<StoreGoodsVO> newGoodsListVOS;

    @ApiModelProperty("本月上新商品个数")
    private Integer newGoodsNumber;

    @ApiModelProperty("店铺热销商品个数")
    private Integer hotGoodsNumber;

    public FrontStoreListVO(Store store) {
        storeId = store.getStoreId();
        storeName = store.getStoreName();
        storeLogo = store.getStoreLogo();
        storeLogoUrl = FileUrlUtil.getFileUrl(store.getStoreLogo(), null);
        serviceScore = store.getServiceScore();
        deliverScore = store.getDeliverScore();
        followNumber = store.getFollowNumber();
        orderFinishedCount = store.getStoreSalesVolume();
        isOwnShop = store.getIsOwnStore();
        isOwnShopValue = getRealIsOwnShopValue(isOwnShop);
        storeBackdrop = store.getStoreBackdrop();
        storeBackdropUrl = FileUrlUtil.getFileUrl(store.getStoreBannerMobile(), null);
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
        //翻译
        value = Language.translate(value);
        return value;
    }
}
