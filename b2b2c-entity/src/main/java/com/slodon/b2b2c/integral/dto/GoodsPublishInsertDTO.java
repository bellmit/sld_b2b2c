package com.slodon.b2b2c.integral.dto;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 商户发布商品入库dto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GoodsPublishInsertDTO extends GoodsPublishFrontParamDTO {

    private static final long serialVersionUID = -5808288725594950039L;
    @ApiModelProperty(value = "默认货品")
    private ProductInsertInfo defaultProduct;
    @ApiModelProperty(value = "主规格id")
    private Integer mainSpecId = 0;

    public GoodsPublishInsertDTO(GoodsPublishFrontParamDTO paramDTO) {
        //复制属性
        BeanUtils.copyProperties(paramDTO, this);
        //构造货品列表
        if (CollectionUtils.isEmpty(this.getSpecInfoList())) {
            AssertUtil.isTrue(StringUtil.isNullOrZero(this.getIntegralPrice()) && StringUtil.isNullOrZero(this.getCashPrice()), "请填写积分或现金");
            AssertUtil.notNullOrZero(this.getProductStock(), "请填写商品库存");
            if (!StringUtil.isNullOrZero(this.getCashPrice())) {
                AssertUtil.isTrue(this.getCashPrice().compareTo(new BigDecimal(9999999)) > 0, "现金必须在0.00~9999999之间");
            }
            if (!StringUtil.isNullOrZero(this.getMarketPrice())) {
                AssertUtil.isTrue(this.getMarketPrice().compareTo(new BigDecimal(9999999)) > 0, "市场价必须在0.00~9999999之间");
            }
            //无规格，构造一个货品信息
            IntegralProductInfo productInfo = new IntegralProductInfo();
            productInfo.setMarketPrice(this.getMarketPrice());
            productInfo.setIntegralPrice(this.getIntegralPrice());
            productInfo.setCashPrice(this.getCashPrice());
            productInfo.setProductStock(this.getProductStock());
            productInfo.setWeight(this.getWeight());
            productInfo.setLength(this.getLength());
            productInfo.setWidth(this.getWidth());
            productInfo.setHeight(this.getHeight());
            productInfo.setProductStockWarning(this.getProductStockWarning());
            productInfo.setProductCode(this.getProductCode());
            productInfo.setBarCode(this.getBarCode());
            productInfo.setState(GoodsConst.PRODUCT_STATE_1);
            productInfo.setIsDefault(GoodsConst.PRODUCT_IS_DEFAULT_YES);
            //构造入库货品信息
            this.setProductList(Collections.singletonList(new ProductInsertInfo(productInfo, this.getSpecInfoList(), this.getImageList())));
        } else {
            //多规格
            AssertUtil.notEmpty(this.getProductList(), "规格货品列表不能为空");
            //参数校验+设置默认规格
            boolean hasDefault = false;//是否有默认规格
            List<IntegralProductInfo> productInsertInfoList = new ArrayList<>();//入库货品信息列表
            for (int i = 0; i < this.getProductList().size(); i++) {
                IntegralProductInfo productInfo = this.getProductList().get(i);
                AssertUtil.isTrue(StringUtil.isNullOrZero(productInfo.getIntegralPrice()) && StringUtil.isNullOrZero(productInfo.getCashPrice()), "请填写第" + (i + 1) + "个货品的积分或现金");
                AssertUtil.notNullOrZero(productInfo.getProductStock(), "请填写第" + (i + 1) + "个货品的库存");
                AssertUtil.isTrue(productInfo.getCashPrice().compareTo(new BigDecimal(9999999)) > 0, "现金必须在0.00~9999999之间");
                if (!StringUtil.isNullOrZero(productInfo.getMarketPrice())) {
                    AssertUtil.isTrue(productInfo.getMarketPrice().compareTo(new BigDecimal(9999999)) > 0, "市场价必须在0.00~9999999之间");
                }
                if (productInfo.getIsDefault() == GoodsConst.PRODUCT_IS_DEFAULT_YES) {
                    if (hasDefault) {
                        //已经有默认货品的，将此货品设置为非默认
                        productInfo.setIsDefault(GoodsConst.PRODUCT_IS_DEFAULT_NO);
                    } else {
                        //默认货品
                        hasDefault = true;
                    }
                }

                //构造入库货品
                productInsertInfoList.add(new ProductInsertInfo(productInfo, this.getSpecInfoList(), this.getImageList()));
            }
            if (!hasDefault) {
                //未设置默认货品，默认第一个
                productInsertInfoList.get(0).setIsDefault(GoodsConst.PRODUCT_IS_DEFAULT_YES);
            }
            this.setProductList(productInsertInfoList);

            //主规格
            for (SpecInfo specInfo : this.getSpecInfoList()) {
                if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                    this.mainSpecId = specInfo.getSpecId();
                    break;
                }
            }
        }
    }

    /**
     * 获取默认货品
     *
     * @return
     */
    public ProductInsertInfo getDefaultProduct() {
        for (IntegralProductInfo productInfo : this.getProductList()) {
            if (productInfo.getIsDefault() == GoodsConst.GOODS_IS_DELETE_YES) {
                return (ProductInsertInfo) productInfo;
            }
        }
        return (ProductInsertInfo) this.getProductList().get(0);
    }

    /**
     * 多规格货品信息
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class ProductInsertInfo extends IntegralProductInfo {

        private static final long serialVersionUID = 7473966858195838354L;

        public ProductInsertInfo(IntegralProductInfo productInfo, List<SpecInfo> specInfoList, List<ImageInfo> imageList) {
            //复制属性
            BeanUtils.copyProperties(productInfo, this);
            this.specValues = this.getProductSpecValues(productInfo.getSpecInfoList());
            this.specValueIds = this.getProductSpecValueIds(productInfo.getSpecInfoList());
            this.mainImage = this.getProductMainImage(productInfo.getSpecInfoList(), specInfoList, imageList);
        }

        @ApiModelProperty(value = "货品id")
        private Long integralProductId;
        @ApiModelProperty(value = "货品主图")
        private String mainImage;
        @ApiModelProperty(value = "货品规格值集合")
        private String specValues;
        @ApiModelProperty(value = "货品规格值id集合")
        private String specValueIds;

        /**
         * 获取sku主图
         *
         * @param productSpecInfoList 货品规格列表
         * @param specInfoList        规格信息列表
         * @param imageList           图片
         * @return
         */
        private String getProductMainImage(List<ProductSpecInfo> productSpecInfoList, List<SpecInfo> specInfoList, List<ImageInfo> imageList) {
            if (CollectionUtils.isEmpty(productSpecInfoList) || CollectionUtils.isEmpty(specInfoList)) {
                //无规格
                return this.getMainImageByImageList(imageList);
            }
            //多规格，如果设置了主规格，则从主规格中选
            for (SpecInfo specInfo : specInfoList) {
                if (specInfo.getIsMainSpec() != GoodsConst.IS_MAIN_SPEC_YES) {
                    continue;
                }
                for (SpecInfo.SpecValueInfo specValueInfo : specInfo.getSpecValueList()) {
                    for (ProductSpecInfo productSpecInfo : productSpecInfoList) {
                        if (productSpecInfo.getSpecValueId().equals(specValueInfo.getSpecValueId())) {
                            return this.getMainImageByImageList(specValueInfo.getImageList());
                        }
                    }
                }
            }
            //未设置主规格，从图片列表中获取
            return this.getMainImageByImageList(imageList);
        }

        /**
         * 根据图片列表获取主图
         *
         * @param imageList 图片列表
         * @return
         */
        private String getMainImageByImageList(List<ImageInfo> imageList) {
            for (ImageInfo imageInfo : imageList) {
                if (imageInfo.getIsMain() == GoodsConst.PICTURE_IS_MAIN_YES) {
                    return imageInfo.getImage();
                }
            }
            //没有设置主图，返回第一张图
            return imageList.get(0).getImage();
        }

        /**
         * 构造货品规格值信息，多个规格值用逗号分开
         *
         * @param specInfoList 货品规格列表
         * @return
         */
        private String getProductSpecValues(List<ProductSpecInfo> specInfoList) {
            if (CollectionUtils.isEmpty(specInfoList)) return "";
            StringBuilder specValues = new StringBuilder();
            specInfoList.forEach(productSpecInfo -> {
                specValues.append(",").append(productSpecInfo.getSpecValue());
            });
            return specValues.toString().substring(1);
        }

        /**
         * 构造货品规格值Id信息，多个规格值Id用逗号分开
         *
         * @param specInfoList 货品规格列表
         * @return
         */
        private String getProductSpecValueIds(List<ProductSpecInfo> specInfoList) {
            if (CollectionUtils.isEmpty(specInfoList)) return "";
            StringBuilder specValueIds = new StringBuilder();
            specInfoList.forEach(productSpecInfo -> {
                specValueIds.append(",").append(productSpecInfo.getSpecValueId());
            });
            return specValueIds.toString().substring(1);
        }
    }

}
