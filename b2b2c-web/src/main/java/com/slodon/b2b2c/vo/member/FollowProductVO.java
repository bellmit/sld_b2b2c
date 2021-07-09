package com.slodon.b2b2c.vo.member;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.member.pojo.MemberFollowProduct;
import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 会员收藏商品表
 */
@Data
public class FollowProductVO implements Serializable {

    @ApiModelProperty("收藏id")
    private Integer followId;

    @ApiModelProperty("货品ID")
    private Long productId;

    @ApiModelProperty("收藏商品时的价格")
    private BigDecimal productPrice;

    @ApiModelProperty("商品图片")
    private String productImage;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题，长度建议140个字符内")
    private String goodsBrief;

    @ApiModelProperty("收藏店铺id")
    private Long storeId;

    @ApiModelProperty("收藏店铺名称")
    private String storeName;

    @ApiModelProperty("分享链接")
    private String shareLink;

    @ApiModelProperty("自营店铺1-自营店铺，2-入驻店铺")
    private Integer isOwnStore;

    @ApiModelProperty("自营店铺1-自营店铺，2-入驻店铺")
    private String isOwnStoreValue;

    @ApiModelProperty("销量")
    private Integer salesNum;

    public FollowProductVO(MemberFollowProduct memberFollowProduct, Store store, Goods goods) {
        followId = memberFollowProduct.getFollowId();
        productId = memberFollowProduct.getProductId();
        productPrice = memberFollowProduct.getProductPrice();
        productImage = getProductImageValue(memberFollowProduct);
        goodsId = memberFollowProduct.getGoodsId();
        goodsName = memberFollowProduct.getGoodsName();
        storeId = memberFollowProduct.getStoreId();
        storeName = memberFollowProduct.getStoreName();
        goodsBrief=memberFollowProduct.getGoodsBrief();

        shareLink = DomainUrlUtil.SLD_H5_URL + "/#/pages/product/detail?productId=" + memberFollowProduct.getProductId() + "&goodsId=" + memberFollowProduct.getGoodsId();
        isOwnStore = store.getIsOwnStore();
        isOwnStoreValue = dealIsOwnStoreValue(store.getIsOwnStore());
        salesNum = goods.getVirtualSales() + goods.getActualSales();
    }

    public static String getProductImageValue(MemberFollowProduct memberFollowProduct) {
        if (StringUtils.isEmpty(memberFollowProduct.getProductImage())) return null;
        return DomainUrlUtil.SLD_IMAGE_RESOURCES + memberFollowProduct.getProductImage();
    }

    public static String dealIsOwnStoreValue(Integer isOwnStore) {
        String value = null;
        if (StringUtils.isEmpty(isOwnStore)) return null;
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