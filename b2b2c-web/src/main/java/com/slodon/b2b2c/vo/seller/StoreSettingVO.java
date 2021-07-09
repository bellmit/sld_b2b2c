package com.slodon.b2b2c.vo.seller;

import com.slodon.b2b2c.core.util.CommonUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装店铺设置信息VO对象
 */
@Data
public class StoreSettingVO {

    @ApiModelProperty("店铺等级名称")
    private String storeGradeName;

    @ApiModelProperty("主营商品")
    private String mainBusiness;

    @ApiModelProperty("店铺logo相对路径")
    private String storeLogo;

    @ApiModelProperty("店铺logo绝对路径")
    private String storeLogoPath;

    @ApiModelProperty("店铺客服电话")
    private String servicePhone;

    @ApiModelProperty("pc端店铺条幅相对路径")
    private String storeBannerPc;

    @ApiModelProperty("pc端店铺条幅绝对路径")
    private String storeBannerPcPath;

    @ApiModelProperty("移动端店铺条幅相对路径")
    private String storeBannerMobile;

    @ApiModelProperty("移动端店铺条幅绝对路径")
    private String storeBannerMobilePath;

    @ApiModelProperty("店铺背景相对路径")
    private String storeBackdrop;

    @ApiModelProperty("店铺背景绝对路径")
    private String storeBackdropPath;

    @ApiModelProperty("店铺seo keyword")
    private String storeSeoKeyword;

    @ApiModelProperty("店铺SEO描述")
    private String storeSeoDesc;

    @ApiModelProperty("免运费额度，默认为“0”，0表示不设置免运费额度，大于0则表示购买金额达到该额度时免运费")
    private Integer freeFreightLimit;

    public StoreSettingVO(Store store) {
        storeGradeName = store.getStoreGradeName();
        storeSeoKeyword = store.getStoreSeoKeyword();
        storeSeoDesc = store.getStoreSeoDesc();
        mainBusiness = store.getMainBusiness();
        storeLogo = store.getStoreLogo();
        storeLogoPath = FileUrlUtil.getFileUrl(store.getStoreLogo(), null);
        servicePhone = CommonUtil.dealMobile(store.getServicePhone());
        storeBannerPc = store.getStoreBannerPc();
        storeBannerPcPath = FileUrlUtil.getFileUrl(store.getStoreBannerPc(), null);
        storeBannerMobile = store.getStoreBannerMobile();
        storeBannerMobilePath = FileUrlUtil.getFileUrl(store.getStoreBannerMobile(), null);
        storeBackdrop = store.getStoreBackdrop();
        storeBackdropPath = FileUrlUtil.getFileUrl(store.getStoreBackdrop(), null);
        freeFreightLimit = store.getFreeFreightLimit();
    }
}
