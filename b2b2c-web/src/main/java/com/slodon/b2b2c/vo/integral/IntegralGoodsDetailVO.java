package com.slodon.b2b2c.vo.integral;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.goods.dto.GoodsPublishFrontParamDTO;
import com.slodon.b2b2c.integral.pojo.IntegralGoods;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsBindLabel;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsPicture;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装积分商品详情VO对象
 */
@Data
public class IntegralGoodsDetailVO implements Serializable {

    private static final long serialVersionUID = -7309504710783339204L;
    //基本信息
    @ApiModelProperty(value = "商品ID")
    private Long integralGoodsId;
    @ApiModelProperty("商品名称")
    private String goodsName;
    @ApiModelProperty("商品副标题")
    private String goodsBrief;
    @ApiModelProperty("积分商品标签列表")
    private List<IntegralLabelVO> labelList = new ArrayList<>();

    //物流信息
    @ApiModelProperty("省份编码")
    private String provinceCode;
    @ApiModelProperty("城市编码")
    private String cityCode;

    //发票信息
    @ApiModelProperty("是否可以开具增值税发票0-不可以；1-可以")
    private Integer isVatInvoice;

    //其他信息
    @ApiModelProperty("店铺是否推荐，1==推荐，0==不推荐,默认0")
    private Integer storeIsRecommend;
    @ApiModelProperty("虚拟销量")
    private Integer virtualSales;

    //规格信息
    @ApiModelProperty("规格列表，多规格必传")
    private List<SpecVO> specInfoList = new ArrayList<>();
    @ApiModelProperty("货品列表，多规格必传")
    private List<ProductVO> productList = new ArrayList<>();
    @ApiModelProperty(value = "市场价，无规格填写")
    private BigDecimal marketPrice;
    @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
    private Integer integralPrice;
    @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
    private BigDecimal cashPrice;
    @ApiModelProperty(value = "商品库存，无规格填写")
    private Integer productStock;
    @ApiModelProperty(value = "重量kg，无规格填写")
    private BigDecimal weight;
    @ApiModelProperty(value = "长度cm，无规格填写")
    private BigDecimal length;
    @ApiModelProperty(value = "宽度cm，无规格填写")
    private BigDecimal width;
    @ApiModelProperty(value = "高度cm，无规格填写")
    private BigDecimal height;
    @ApiModelProperty(value = "库存预警值，无规格填写")
    private Integer productStockWarning;
    @ApiModelProperty(value = "货号，无规格填写")
    private String productCode;
    @ApiModelProperty(value = "条形码，无规格填写")
    private String barCode;

    //图片
    @ApiModelProperty("图片列表，无规格或未设置主规格时使用此数据")
    private List<ImageVO> imageList = new ArrayList<>();

    //视频
    @ApiModelProperty(value = "商品视频")
    private String goodsVideo;
    @ApiModelProperty(value = "商品视频绝对地址")
    private String goodsVideoUrl;

    //商品详情
    @ApiModelProperty(value = "商品详情信息")
    private String goodsDetails;


    public IntegralGoodsDetailVO(IntegralGoods goods, List<IntegralGoodsBindLabel> goodsBindLabelList,
                                 List<IntegralGoodsPicture> goodsPictureList, List<IntegralProduct> dbProductList) {
        //基本信息
        this.integralGoodsId = goods.getIntegralGoodsId();
        this.goodsName = goods.getGoodsName();
        this.goodsBrief = goods.getGoodsBrief();
        goodsBindLabelList.forEach(goodsBindLabel -> {
            this.labelList.add(new IntegralLabelVO(goodsBindLabel));
        });

        //物流信息
        this.provinceCode = goods.getProvinceCode();
        this.cityCode = goods.getCityCode();

        //发票信息
        this.isVatInvoice = goods.getIsVatInvoice();

        //其他信息
        this.storeIsRecommend = goods.getStoreIsRecommend();
        this.virtualSales = goods.getVirtualSales();

        //规格信息
        if (StringUtils.isEmpty(goods.getSpecJson())) {
            //单规格
            this.marketPrice = dbProductList.get(0).getMarketPrice();
            this.integralPrice = dbProductList.get(0).getIntegralPrice();
            this.cashPrice = dbProductList.get(0).getCashPrice();
            this.productStock = dbProductList.get(0).getProductStock();
            this.productStockWarning = dbProductList.get(0).getProductStockWarning();
            this.weight = dbProductList.get(0).getWeight();
            this.length = dbProductList.get(0).getLength();
            this.width = dbProductList.get(0).getWidth();
            this.height = dbProductList.get(0).getHeight();
            this.productCode = dbProductList.get(0).getProductCode();
            this.barCode = dbProductList.get(0).getBarCode();
        } else {
            //多规格
            List<GoodsPublishFrontParamDTO.SpecInfo> dbSpecInfoList = JSON.parseArray(goods.getSpecJson(), GoodsPublishFrontParamDTO.SpecInfo.class);
            dbSpecInfoList.forEach(specInfo -> {
                this.specInfoList.add(new SpecVO(specInfo));
            });
        }
        dbProductList.forEach(product -> {
            this.productList.add(new ProductVO(product));
        });

        //图片，设置了主规格时，图片信息在规格信息中存放
        if (StringUtil.isNullOrZero(goods.getMainSpecId())) {
            //未设置主规格
            goodsPictureList.forEach(goodsPicture -> {
                this.imageList.add(new ImageVO(goodsPicture));
            });
        }

        //视频
        this.goodsVideo = goods.getGoodsVideo();
        this.goodsVideoUrl = FileUrlUtil.getFileUrl(goods.getGoodsVideo(), null);

        //商品详情
        this.goodsDetails = goods.getGoodsDetails();
    }

    @Data
    public static class IntegralLabelVO implements Serializable {

        private static final long serialVersionUID = -7367620232129513566L;
        @ApiModelProperty("标签id")
        private Integer labelId;
        @ApiModelProperty("标签名称")
        private String labelName;

        public IntegralLabelVO(IntegralGoodsBindLabel integralGoodsBindLabel) {
            this.labelId = integralGoodsBindLabel.getLabelId2();
            //不存在一级所以直接取二级
            String[] split = integralGoodsBindLabel.getLabelPath().split(" > ");
            this.labelName = split[1];
        }
    }

    /**
     * 规格信息
     */
    @Data
    public static class SpecVO implements Serializable {

        private static final long serialVersionUID = -3946622730056052356L;

        public SpecVO(GoodsPublishFrontParamDTO.SpecInfo specInfo) {
            this.specId = specInfo.getSpecId();
            this.specName = specInfo.getSpecName();
            this.isMainSpec = specInfo.getIsMainSpec();
            specInfo.getSpecValueList().forEach(specValueInfo -> {
                this.specValueList.add(new SpecValueVO(specValueInfo));
            });
        }

        @ApiModelProperty(value = "规格id")
        private Integer specId;
        @ApiModelProperty(value = "规格名称")
        private String specName;
        @ApiModelProperty(value = "是否主规格,1-是，0-不是")
        private Integer isMainSpec;
        @ApiModelProperty(value = "规格值列表")
        private List<SpecValueVO> specValueList = new ArrayList<>();

        /**
         * 规格值信息
         */
        @Data
        public static class SpecValueVO implements Serializable {
            private static final long serialVersionUID = -3703708925169053964L;

            public SpecValueVO(GoodsPublishFrontParamDTO.SpecInfo.SpecValueInfo specValueInfo) {
                this.specValueId = specValueInfo.getSpecValueId();
                this.specValue = specValueInfo.getSpecValue();
                if (!CollectionUtils.isEmpty(specValueInfo.getImageList())) {
                    specValueInfo.getImageList().forEach(imageInfo -> {
                        this.imageList.add(new ImageVO(imageInfo));
                    });
                }
            }

            @ApiModelProperty(value = "规格值id")
            private Integer specValueId;
            @ApiModelProperty(value = "规格值")
            private String specValue;
            @ApiModelProperty(value = "图片路径")
            private List<ImageVO> imageList = new ArrayList<>();
        }
    }

    /**
     * 图片信息
     */
    @Data
    public static class ImageVO implements Serializable {
        private static final long serialVersionUID = -3055490728912342527L;
        @ApiModelProperty("是否主图：1、主图；2、非主图，主图只能有一张")
        private Integer isMain;
        @ApiModelProperty(value = "图片路径")
        private String image;
        @ApiModelProperty(value = "图片完整路径")
        private String imageUrl;

        public ImageVO(IntegralGoodsPicture goodsPicture) {
            this.isMain = goodsPicture.getIsMain();
            this.image = goodsPicture.getImagePath();
            this.imageUrl = FileUrlUtil.getFileUrl(goodsPicture.getImagePath(), null);
        }

        public ImageVO(GoodsPublishFrontParamDTO.ImageInfo imageInfo) {
            this.isMain = imageInfo.getIsMain();
            this.image = imageInfo.getImage();
            this.imageUrl = FileUrlUtil.getFileUrl(imageInfo.getImage(), null);
        }
    }

    /**
     * 商品表信息
     */
    @Data
    public class ProductVO implements Serializable {
        private static final long serialVersionUID = 7430708360305108608L;
        @ApiModelProperty("货品Id")
        private Long integralProductId;
        @ApiModelProperty("规格值，用逗号分隔")
        private String specValues;
        @ApiModelProperty("规格值的ID，用逗号分隔")
        private String specValueIds;
        @ApiModelProperty("市场价")
        private BigDecimal marketPrice;
        @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
        private Integer integralPrice;
        @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
        private BigDecimal cashPrice;
        @ApiModelProperty("货号：即SKU ID（店铺内唯一）")
        private String productCode;
        @ApiModelProperty("商品条形码（标准的商品条形码）")
        private String barCode;
        @ApiModelProperty(value = "库存")
        private Integer productStock;
        @ApiModelProperty(value = "库存预警值")
        private Integer productStockWarning;
        @ApiModelProperty(value = "重量kg")
        private BigDecimal weight;
        @ApiModelProperty(value = "长度cm")
        private BigDecimal length;
        @ApiModelProperty(value = "宽度cm")
        private BigDecimal width;
        @ApiModelProperty(value = "高度cm")
        private BigDecimal height;
        @ApiModelProperty(value = "是否启用，1-启用；2-不启用")
        private Integer state;
        @ApiModelProperty(value = "是否默认展示或加车货品：0-否；1-是")
        private Integer isDefault;

        public ProductVO(IntegralProduct product) {
            this.integralProductId = product.getIntegralProductId();
            this.specValues = product.getSpecValues();
            this.specValueIds = product.getSpecValueIds();
            this.marketPrice = product.getMarketPrice();
            this.integralPrice = product.getIntegralPrice();
            this.cashPrice = product.getCashPrice();
            this.productCode = product.getProductCode();
            this.barCode = product.getBarCode();
            this.productStock = product.getProductStock();
            this.productStockWarning = product.getProductStockWarning();
            this.weight = product.getWeight();
            this.length = product.getLength();
            this.width = product.getWidth();
            this.height = product.getHeight();
            this.isDefault = product.getIsDefault();
            this.state = product.getState();
        }
    }
}
