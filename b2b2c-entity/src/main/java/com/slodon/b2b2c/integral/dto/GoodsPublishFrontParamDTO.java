package com.slodon.b2b2c.integral.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商户发布商品前端传参dto
 */
@Data
public class GoodsPublishFrontParamDTO implements Serializable {
    private static final long serialVersionUID = 8482547961941799992L;
    @ApiModelProperty(value = "商品id，编辑商品时必传")
    private Long integralGoodsId;

    //基本信息
    @ApiModelProperty(value = "积分商品名称", required = true)
    private String goodsName;
    @ApiModelProperty(value = "积分商品广告")
    private String goodsBrief;
    @ApiModelProperty(value = "积分商品标签ids集合，用逗号隔开", required = true)
    private String labelIds;

    //物流信息
    @ApiModelProperty("省份编码")
    private String provinceCode;
    @ApiModelProperty("城市编码")
    private String cityCode;

    //发票信息
    @ApiModelProperty("是否可以开具增值税发票0-不可以；1-可以,默认0")
    private Integer isVatInvoice = 0;

    //其他信息
    @ApiModelProperty(value = "是否立即售卖，true==是，false==放入仓库", required = true)
    private Boolean sellNow = false;
    @ApiModelProperty("店铺是否推荐，1==推荐，0==不推荐,默认0")
    private Integer storeIsRecommend = 0;
    @ApiModelProperty(value = "虚拟销量，0-999999999之间的整数，默认为0")
    private Integer virtualSales = 0;

    //规格信息
    @ApiModelProperty("规格列表，多规格必传")
    private List<SpecInfo> specInfoList;
    @ApiModelProperty("货品列表，多规格必传")
    private List<IntegralProductInfo> productList;
    @ApiModelProperty(value = "市场价，无规格填写")
    private BigDecimal marketPrice;
    @ApiModelProperty("积分，不能为零")
    private Integer integralPrice;
    @ApiModelProperty("现金，为零则只显示积分")
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

    //图片信息
    @ApiModelProperty(value = "商品图片列表，无规格或未设置主规格时必传")
    private List<ImageInfo> imageList;

    //视频
    @ApiModelProperty(value = "商品视频")
    private String goodsVideo;

    //详情板式
    @ApiModelProperty(value = "商品详情信息")
    private String goodsDetails;

    //region 内部类

    /**
     * 规格信息
     */
    @Data
    public static class SpecInfo implements Serializable {
        private static final long serialVersionUID = 8147878363234844459L;
        @ApiModelProperty(value = "规格id")
        private Integer specId;
        @ApiModelProperty(value = "规格名称")
        private String specName;
        @ApiModelProperty(value = "是否主规格,1-是，0-不是")
        private Integer isMainSpec = 0;
        @ApiModelProperty(value = "规格值列表")
        private List<SpecValueInfo> specValueList;

        /**
         * 规格值信息
         */
        @Data
        public static class SpecValueInfo implements Serializable {
            private static final long serialVersionUID = -3703708925169053964L;
            @ApiModelProperty(value = "规格值id")
            private Integer specValueId;
            @ApiModelProperty(value = "规格值")
            private String specValue;
            @ApiModelProperty(value = "图片路径")
            private List<ImageInfo> imageList;
        }
    }

    /**
     * 多规格货品信息
     */
    @Data
    public static class IntegralProductInfo implements Serializable {
        private static final long serialVersionUID = -2290830500828493759L;
        @ApiModelProperty(value = "货品规格信息列表")
        private List<ProductSpecInfo> specInfoList;
        @ApiModelProperty(value = "市场价")
        private BigDecimal marketPrice;
        @ApiModelProperty("价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零")
        private Integer integralPrice;
        @ApiModelProperty("价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分")
        private BigDecimal cashPrice;
        @ApiModelProperty(value = "商品库存")
        private Integer productStock;
        @ApiModelProperty(value = "重量kg")
        private BigDecimal weight;
        @ApiModelProperty(value = "长度cm")
        private BigDecimal length;
        @ApiModelProperty(value = "宽度cm")
        private BigDecimal width;
        @ApiModelProperty(value = "高度cm")
        private BigDecimal height;
        @ApiModelProperty(value = "库存预警值")
        private Integer productStockWarning;
        @ApiModelProperty(value = "货号")
        private String productCode;
        @ApiModelProperty(value = "条形码")
        private String barCode;
        @ApiModelProperty(value = "是否启用，1-启用；2-不启用")
        private Integer state;
        @ApiModelProperty(value = "是否默认货品：0-否；1-是，只有一个默认，如果未设置默认，则默认第一个货品")
        private Integer isDefault = 0;

        /**
         * 货品规格信息
         */
        @Data
        public static class ProductSpecInfo implements Serializable {
            private static final long serialVersionUID = -7232553246063119991L;
            @ApiModelProperty(value = "规格id")
            private Integer specId;
            @ApiModelProperty(value = "规格名称")
            private String specName;
            @ApiModelProperty(value = "规格值Id")
            private Integer specValueId;
            @ApiModelProperty(value = "规格值名称")
            private String specValue;
        }
    }

    /**
     * 图片信息
     */
    @Data
    public static class ImageInfo implements Serializable {
        private static final long serialVersionUID = 3435456712233455641L;
        @ApiModelProperty("是否主图：1、主图；2、非主图，主图只能有一张")
        private Integer isMain = 2;
        @ApiModelProperty(value = "图片路径")
        private String image;
    }
    //endregion
}
