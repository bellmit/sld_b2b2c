package com.slodon.b2b2c.vo.goods;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.goods.dto.GoodsPublishFrontParamDTO;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.seller.pojo.StoreLabelBindGoods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 商户获取商品信息参数
 * @author slodonc
 */
@Data
public class SellerGoodsDetailVO implements Serializable {

    private static final long serialVersionUID = 5736774274286541718L;
    //id
    @ApiModelProperty(value ="商品ID")
    private Long goodsId;

    //基本信息
    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;
    @ApiModelProperty("商品副标题，长度建议140个字符内")
    private String goodsBrief;
    @ApiModelProperty("品牌ID")
    private Integer brandId;
    @ApiModelProperty("品牌名称")
    private String brandName;
    @ApiModelProperty("1级分类ID")
    private Integer categoryId1;
    @ApiModelProperty("2级分类ID")
    private Integer categoryId2;
    @ApiModelProperty("3级分类ID")
    private Integer categoryId3;
    @ApiModelProperty("分类路径")
    private String categoryPath;
    @ApiModelProperty(value="检索属性列表")
    private List<GoodsPublishFrontParamDTO.AttributeAndParameter.Attribute> attributeList;
    @ApiModelProperty(value="自定义参数分组")
    private GoodsPublishFrontParamDTO.AttributeAndParameter.ParameterGroup parameterGroup;

    //物流信息
    @ApiModelProperty("省份编码")
    private String provinceCode;
    @ApiModelProperty("城市编码")
    private String cityCode;
    @ApiModelProperty("运费模板id(与固定运费二选一,必有一项)")
    private Integer freightId;
    @ApiModelProperty("固定运费(与运费模版id二选一,必有一项)")
    private BigDecimal freightFee;

    //发票信息
    @ApiModelProperty("是否可以开具增值税发票0-不可以；1-可以")
    private Integer isVatInvoice;

    //其他信息
    @ApiModelProperty(value = "店铺内部分类列表",required = true)
    private List<StoreLabelBindGoods> storeInnerLabelList;
    @ApiModelProperty("商品标签列表")
    private List<GoodsBindLabel> goodsLabelList;
    @ApiModelProperty("店铺是否推荐，1==推荐，0==不推荐,默认0")
    private Integer storeIsRecommend;
    @ApiModelProperty("虚拟销量")
    private Integer virtualSales;

    //规格信息
    @ApiModelProperty("规格列表，多规格必传")
    private List<SpecVO> specInfoList = new ArrayList<>();
    @ApiModelProperty("货品列表，多规格必传")
    private List<ProductVO> productList = new ArrayList<>();
    @ApiModelProperty(value ="市场价，无规格填写")
    private BigDecimal marketPrice;
    @ApiModelProperty(value ="销售价，无规格填写")
    private BigDecimal productPrice;
    @ApiModelProperty(value ="商品库存，无规格填写")
    private Integer productStock;
    @ApiModelProperty(value ="库存预警值，无规格填写")
    private Integer productStockWarning;
    @ApiModelProperty(value ="重量kg，无规格填写")
    private BigDecimal weight;
    @ApiModelProperty(value ="长度cm，无规格填写")
    private BigDecimal length;
    @ApiModelProperty(value ="宽度cm，无规格填写")
    private BigDecimal width;
    @ApiModelProperty(value ="高度cm，无规格填写")
    private BigDecimal height;
    @ApiModelProperty(value ="商品编码，无规格填写")
    private String productCode;
    @ApiModelProperty(value ="条形码，无规格填写")
    private String barCode;

    //图片
    @ApiModelProperty("图片列表，无规格或未设置主规格时使用此数据")
    private List<ImageVO> imageList = new ArrayList<>();

    //视频
    @ApiModelProperty(value="商品视频")
    private String goodsVideo;
    @ApiModelProperty(value="商品视频绝对地址")
    private String goodsVideoUrl;

    //商品详情
    @ApiModelProperty(value="商品详情信息")
    private String goodsDetails;
    @ApiModelProperty(value="顶部关联模版ID")
    private Integer relatedTemplateIdTop;
    @ApiModelProperty(value="底部关联模版ID")
    private Integer relatedTemplateIdBottom;


    public SellerGoodsDetailVO(Goods goods,
                               GoodsExtend goodsExtend,
                               List<GoodsBindLabel> goodsBindLabelList,
                               List<StoreLabelBindGoods> storeLabelBindGoodsList,
                               List<GoodsPicture> goodsPictureList,//无规格时的图片
                               List<Product> dbProductList) {
        //id
        this.goodsId=goods.getGoodsId();

        //基本信息
        this.goodsName=goods.getGoodsName();
        this.goodsBrief=goods.getGoodsBrief();
        this.brandId=goods.getBrandId();
        this.brandName = goods.getBrandName();
        this.categoryPath = goods.getCategoryPath();
        this.categoryId1=goods.getCategoryId1();
        this.categoryId2=goods.getCategoryId2();
        this.categoryId3=goods.getCategoryId3();
        if (!StringUtils.isEmpty(goodsExtend.getAttributeJson())) {
            this.attributeList = JSON.parseArray(goodsExtend.getAttributeJson(), GoodsPublishFrontParamDTO.AttributeAndParameter.Attribute.class);
        }
        if (!StringUtils.isEmpty(goodsExtend.getGoodsParameter())){
            this.parameterGroup = JSON.parseObject(goodsExtend.getGoodsParameter(), GoodsPublishFrontParamDTO.AttributeAndParameter.ParameterGroup.class);
        }

        //物流信息
        this.provinceCode=goodsExtend.getProvinceCode();
        this.cityCode=goodsExtend.getCityCode();
        this.freightId=goodsExtend.getFreightId();
        this.freightFee=goodsExtend.getFreightFee();

        //发票信息
        this.isVatInvoice=goods.getIsVatInvoice();

        //其他信息
        this.goodsLabelList = goodsBindLabelList;
        this.storeInnerLabelList = storeLabelBindGoodsList;
        this.storeIsRecommend = goods.getStoreIsRecommend();
        this.virtualSales = goods.getVirtualSales();

        //规格信息
        if (StringUtils.isEmpty(goodsExtend.getSpecJson())){
            //单规格
            this.marketPrice = dbProductList.get(0).getMarketPrice();
            this.productPrice = dbProductList.get(0).getProductPrice();
            this.productStock = dbProductList.get(0).getProductStock();
            this.productStockWarning = dbProductList.get(0).getProductStockWarning();
            this.weight = dbProductList.get(0).getWeight();
            this.length = dbProductList.get(0).getLength();
            this.width = dbProductList.get(0).getWidth();
            this.height = dbProductList.get(0).getHeight();
            this.productCode = dbProductList.get(0).getProductCode();
            this.barCode = dbProductList.get(0).getBarCode();
        }else {
            //多规格
            List<GoodsPublishFrontParamDTO.SpecInfo> dbSpecInfoList = JSON.parseArray(goodsExtend.getSpecJson(), GoodsPublishFrontParamDTO.SpecInfo.class);
            dbSpecInfoList.forEach(specInfo -> {
                this.specInfoList.add(new SpecVO(specInfo));
            });
        }
        dbProductList.forEach(product -> {
            this.productList.add(new ProductVO(product));
        });

        //图片，设置了主规格时，图片信息在规格信息中存放
        if (StringUtil.isNullOrZero(goods.getMainSpecId())){
            //未设置主规格
            goodsPictureList.forEach(goodsPicture -> {
                this.imageList.add(new ImageVO(goodsPicture));
            });
        }

        //视频
        this.goodsVideo=goods.getGoodsVideo();
        this.goodsVideoUrl= FileUrlUtil.getFileUrl(goods.getGoodsVideo(),null);

        //商品详情
        this.goodsDetails=goodsExtend.getGoodsDetails();
        this.relatedTemplateIdTop=goodsExtend.getRelatedTemplateIdTop();
        this.relatedTemplateIdBottom=goodsExtend.getRelatedTemplateIdBottom();
    }


    /**
     * 规格信息
     */
    @Data
    public static class SpecVO implements Serializable {
        private static final long serialVersionUID = 8147878363234844459L;

        public SpecVO(GoodsPublishFrontParamDTO.SpecInfo specInfo) {
            this.specId = specInfo.getSpecId();
            this.specName = specInfo.getSpecName();
            this.isMainSpec = specInfo.getIsMainSpec();
            specInfo.getSpecValueList().forEach(specValueInfo -> {
                this.specValueList.add(new SpecValueVO(specValueInfo));
            });
        }

        @ApiModelProperty(value ="规格id")
        private Integer specId;
        @ApiModelProperty(value ="规格名称")
        private String specName;
        @ApiModelProperty(value ="是否主规格,1-是，0-不是")
        private Integer isMainSpec;
        @ApiModelProperty(value ="规格值列表")
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
                if (!CollectionUtils.isEmpty(specValueInfo.getImageList())){
                    specValueInfo.getImageList().forEach(imageInfo -> {
                        this.imageList.add(new ImageVO(imageInfo));
                    });
                }
            }

            @ApiModelProperty(value ="规格值id")
            private Integer specValueId;
            @ApiModelProperty(value ="规格值")
            private String specValue;
            @ApiModelProperty(value ="图片路径")
            private List<ImageVO> imageList = new ArrayList<>();
        }
    }



    /**
     * 图片信息
     */
    @Data
    public static class ImageVO implements Serializable {
        private static final long serialVersionUID =3435456712233455641L;
        @ApiModelProperty("是否主图：1、主图；2、非主图，主图只能有一张")
        private Integer isMain;
        @ApiModelProperty(value ="图片路径")
        private String image;
        @ApiModelProperty(value ="图片完整路径")
        private String imageUrl;

        public ImageVO(GoodsPicture goodsPicture)
        {
            this.isMain=goodsPicture.getIsMain();
            this.image=goodsPicture.getImagePath();
            this.imageUrl= FileUrlUtil.getFileUrl(goodsPicture.getImagePath(),null);
        }

        public ImageVO(GoodsPublishFrontParamDTO.ImageInfo imageInfo) {
            this.isMain = imageInfo.getIsMain();
            this.image = imageInfo.getImage();
            this.imageUrl = FileUrlUtil.getFileUrl(imageInfo.getImage(),null);
        }
    }

    /**
     * 商品表信息
     */
    @Data
    public class ProductVO implements Serializable {
        private static final long serialVersionUID = 454850847372893214L;

        @ApiModelProperty("规格值，用逗号分隔")
        private String specValues;

        @ApiModelProperty("规格值的ID，用逗号分隔")
        private String specValueIds;

        @ApiModelProperty("货品Id")
        private  Long productId;

        @ApiModelProperty("货品货号：即SKU ID（店铺内唯一）")
        private String productCode;

        @ApiModelProperty(value="货品价格")
        private BigDecimal productPrice;

        @ApiModelProperty("市场价")
        private BigDecimal marketPrice;

        @ApiModelProperty("商品条形码（标准的商品条形码）")
        private String barCode;

        @ApiModelProperty(value="库存")
        private Integer productStock;

        @ApiModelProperty(value="库存预警值")
        private Integer productStockWarning;

        @ApiModelProperty(value="重量kg")
        private BigDecimal weight;

        @ApiModelProperty(value="长度cm")
        private BigDecimal length;

        @ApiModelProperty(value="宽度cm")
        private BigDecimal width;

        @ApiModelProperty(value="高度cm")
        private BigDecimal height;

        @ApiModelProperty(value ="是否启用，1-启用；2-不启用")
        private Integer state;

        @ApiModelProperty(value="是否默认展示或加车货品：0-否；1-是")
        private Integer isDefault;

        public ProductVO(Product product)
        {
            this.productId=product.getProductId();
            this.specValues=product.getSpecValues();
            this.specValueIds=product.getSpecValueIds();
            this.productCode=product.getProductCode();
            this.productPrice=product.getProductPrice();
            this.marketPrice=product.getMarketPrice();
            this.barCode=product.getBarCode();
            this.productStock=product.getProductStock();
            this.productStockWarning=product.getProductStockWarning();
            this.weight=product.getWeight();
            this.length=product.getLength();
            this.width=product.getWidth();
            this.height=product.getHeight();
            this.isDefault=product.getIsDefault();
            this.state=product.getState();
        }
    }
}
