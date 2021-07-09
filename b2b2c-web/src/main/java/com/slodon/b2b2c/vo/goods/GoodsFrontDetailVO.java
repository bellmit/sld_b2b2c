package com.slodon.b2b2c.vo.goods;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.dto.GoodsAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsPublishFrontParamDTO;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.seller.pojo.Store;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 商城展示商品详情vo
 */
@Data
public class GoodsFrontDetailVO {

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称为3到50个字符(商品副标题)")
    private String goodsName;

    @ApiModelProperty("商品副标题，长度建议140个字符内")
    private String goodsBrief;

    @ApiModelProperty("品牌ID")
    private Integer brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("1-创建；2-待审核；3-上架（a. 审核通过上架，b. 不需要平台审核，商户创建商品后点击上架操作）；4-审核驳回(平台驳回）；5-商品下架（商户自行下架）；6-违规下架（平台违规下架操作）；7-已删除（状态1、5、6可以删除后进入此状态）")
    private Integer state;

    @ApiModelProperty("是否收藏，true-收藏，未登录时为false")
    private Boolean followGoods;

    @ApiModelProperty("商品所属分类路径，(例如：分类1/分类2/分类3，前后都无斜杠)")
    private String categoryPath;

    @ApiModelProperty("商品详情信息")
    private String goodsDetails;

    @ApiModelProperty("虚拟销量+实际销量")
    private Integer sales;

    @ApiModelProperty("分享图片")
    private String shareImage;

    @ApiModelProperty("分享链接")
    private String shareLink;

    @ApiModelProperty("购买商品赠送积分")
    private Integer integral;

    @ApiModelProperty("商品参数，json格式，商品详情顶部显示")
    private List<GoodsPublishFrontParamDTO.AttributeAndParameter.ParameterGroup.Parameter> goodsParameterList = new ArrayList<>();

    @ApiModelProperty("店铺信息")
    private StoreInf storeInf;

    @ApiModelProperty("服务标签列表")
    private List<ServiceLabelVO> serviceLabels;

    @ApiModelProperty("规格列表")
    private List<SpecVO> specs = new ArrayList<>();

    @ApiModelProperty("有效的规格值id组合，每个组合格式为 规格值id1,规格值id2...")
    private List<String> effectSpecValueIds = new ArrayList<>();

    @ApiModelProperty("默认货品")
    private ProductDetailVO defaultProduct;

    @ApiModelProperty("物流信息")
    private DeliverInfo deliverInfo;

    public GoodsFrontDetailVO(Goods goods,
                              Boolean isFavorite,
                              List<ServiceLabelVO> serviceLabels,
                              GoodsExtend goodsExtend,
                              Product product,
                              List<Product> productList,
                              List<GoodsPicture> goodsPictureList,
                              Store store,
                              Boolean isFollowStore,
                              DeliverInfo deliverInfo,
                              GoodsPromotion goodsPromotion) {
        this.goodsId = goods.getGoodsId();
        this.goodsName = goods.getGoodsName();
        this.goodsBrief = goods.getGoodsBrief();
        this.brandId = goods.getBrandId();
        this.brandName = goods.getBrandName();
        this.state = goods.getState();
        this.categoryPath = goods.getCategoryPath();
        this.sales = goods.getActualSales() + goods.getVirtualSales();
        this.followGoods = isFavorite;
        this.serviceLabels = serviceLabels;
        this.storeInf = new StoreInf(store, isFollowStore);
        this.deliverInfo = deliverInfo;

        this.shareImage = FileUrlUtil.getFileUrl(product.getMainImage(), ImageSizeEnum.SMALL);
        this.shareLink = DomainUrlUtil.SLD_H5_URL + "/#/pages/product/detail?productId=" + product.getProductId();

        //处理商品参数
        if (!StringUtils.isEmpty(goodsExtend.getGoodsParameter())) {
            GoodsPublishFrontParamDTO.AttributeAndParameter.ParameterGroup parameterGroup = JSONObject.parseObject(goodsExtend.getGoodsParameter(), GoodsPublishFrontParamDTO.AttributeAndParameter.ParameterGroup.class);
            if (!CollectionUtils.isEmpty(parameterGroup.getParameterList())) {
                parameterGroup.getParameterList().forEach(goodsParameterInfo -> {
                    this.goodsParameterList.add(goodsParameterInfo);
                });
            }
        }

        //处理有效的规格组合
        productList.forEach(product1 -> {
            if (!StringUtils.isEmpty(product1.getSpecValueIds())) {
                effectSpecValueIds.add(product1.getSpecValueIds());
            }
        });
        defaultProduct = new ProductDetailVO(product, goodsPictureList, goodsPromotion);

        //处理规格
        dealSpec(goodsExtend.getSpecJson(), product.getSpecValueIds(), effectSpecValueIds);
    }

    /**
     * 构造规格信息
     *
     * @param specJson            商品规格json
     * @param defaultSpecValueIds 当前选中的规格，格式为规格值id1,规格值id2...
     * @param effectSpecValueIds  商品所有有效的规格值ids
     */
    private void dealSpec(String specJson, String defaultSpecValueIds, List<String> effectSpecValueIds) {
        if (StringUtils.isEmpty(specJson)) {
            //无规格商品，无需构造
            return;
        }
        JSONObject.parseArray(specJson, GoodsAddDTO.SpecInfo.class).forEach(specInfo -> {
            if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                //主规格，放在规格列表的第一位
                specs.add(0, new SpecVO(specInfo, defaultSpecValueIds, effectSpecValueIds));
            } else {
                specs.add(new SpecVO(specInfo, defaultSpecValueIds, effectSpecValueIds));
            }
        });

    }

    /**
     * 规格信息
     */
    @Data
    public static class SpecVO {

        @ApiModelProperty("规格id")
        private Integer specId;
        @ApiModelProperty("规格名称")
        private String specName;
        @ApiModelProperty("规格值列表")
        private List<SpecValue> specValueList = new ArrayList<>();

        /**
         * 商品详情规格信息构造函数
         *
         * @param specInfo            规格信息
         * @param defaultSpecValueIds 当前选中的货品规格值ids，格式为规格值id1,规格值id2...
         * @param effectSpecValueIds  商品所有可用的规格值ids
         */
        public SpecVO(GoodsAddDTO.SpecInfo specInfo, String defaultSpecValueIds, List<String> effectSpecValueIds) {

            specId = specInfo.getSpecId();
            specName = specInfo.getSpecName();
            specInfo.getSpecValueList().forEach(specValueInfo -> {
                specValueList.add(new SpecValue(specValueInfo, defaultSpecValueIds, effectSpecValueIds));
            });
        }

        /**
         * 规格值信息
         */
        @Data
        public static class SpecValue {

            @ApiModelProperty("规格值id")
            private Integer specValueId;
            @ApiModelProperty("规格值")
            private String specValue;
            @ApiModelProperty("规格主图")
            private String image;
            @ApiModelProperty("选择状态 1-选中，2-可选，3-禁用")
            private Integer checkState;

            public SpecValue(GoodsAddDTO.SpecInfo.SpecValueInfo specValueInfo, String defaultSpecValueIds, List<String> effectSpecValueIds) {
                specValueId = specValueInfo.getSpecValueId();
                specValue = specValueInfo.getSpecValue();
                image = FileUrlUtil.getFileUrl(specValueInfo.getMainImage(), null);
                if (contains(defaultSpecValueIds, specValueId)) {
                    //当前选中的规格组合包含此规格值，设为选中
                    checkState = 1;
                } else if (contains(effectSpecValueIds, defaultSpecValueIds, specValueId)) {
                    //此规格值与当前选中的规格可以组合出有效的货品规格，设为可选
                    checkState = 2;
                } else {
                    //无法组合出有效的货品规格，设置禁用
                    checkState = 3;
                }
            }
        }

        /**
         * 判断规格值组合中是否包含某个规格值
         *
         * @param specValueIds 规格值id组合，格式为规格值id1,规格值id2...
         * @param specValueId  规格值id
         * @return true==包含
         */
        private static Boolean contains(String specValueIds, Integer specValueId) {
            for (String specValueIdStr : specValueIds.split(",")) {
                if (specValueIdStr.equals(specValueId.toString())) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 判断默认货品规格与传入的规格值id重新组合后，是否能组合成有效的货品规格
         *
         * @param effectSpecValueIds  商品所有有效的规格组合
         * @param defaultSpecValueIds 默认货品规格
         * @param specValueId         传入的规格值id
         * @return
         */
        private static Boolean contains(List<String> effectSpecValueIds, String defaultSpecValueIds, Integer specValueId) {
            //用 传入的规格值id 分别替换 defaultSpecValueIds 中的每个规格值id,组合
            List<String> defaultSpecValueIdList = Arrays.asList(defaultSpecValueIds.split(","));
            List<List<String>> newValueIdsList = new ArrayList<>();//每个元素为一个重新组合的规格值idList
            for (int i = 0; i < defaultSpecValueIdList.size(); i++) {
                List<String> newValueIds = new ArrayList<>(defaultSpecValueIdList);
                newValueIds.set(i, specValueId.toString());//重新组合
                newValueIdsList.add(newValueIds);
            }

            for (List<String> list : newValueIdsList) {
                Collections.sort(list);
                for (String s : effectSpecValueIds) {
                    //循环每个有效组合，判断重新组合的是否为有效
                    List<String> effList = Arrays.asList(s.split(","));
                    Collections.sort(effList);
                    if (list.equals(effList)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * sku信息
     */
    @Data
    public static class ProductDetailVO implements Serializable {
        private static final long serialVersionUID = 1131242342535363466L;

        @ApiModelProperty("货品id")
        private Long productId;

        @ApiModelProperty("规格属性值ID，用逗号分隔")
        private String getSpecValueIds;
        @ApiModelProperty("规格值，用逗号分隔")
        private String getSpecValues;
        @ApiModelProperty("货品价格")
        private BigDecimal productPrice;
        @ApiModelProperty("市场价格")
        private BigDecimal marketPrice;
        @ApiModelProperty("库存")
        private Integer productStock;
        @ApiModelProperty("总销量")
        private Integer actualSales;
        @ApiModelProperty("规格图片")
        private List<String> goodsPics = new ArrayList<>();
        @ApiModelProperty("活动类型  101-单品立减, 102-拼团活动, 103-预售活动 104-秒杀活动")
        private Integer promotionType;
        @ApiModelProperty("活动id")
        private Integer promotionId;


        public ProductDetailVO(Product product, List<GoodsPicture> goodsPictureList, GoodsPromotion goodsPromotion) {
            productId = product.getProductId();
            getSpecValueIds = product.getSpecValueIds();
            getSpecValues = product.getSpecValues();
            productPrice = product.getProductPrice();
            marketPrice = product.getMarketPrice();
            productStock = product.getProductStock();
            actualSales = product.getActualSales();
            //处理图片
            if (!CollectionUtils.isEmpty(goodsPictureList)) {
                for (GoodsPicture goodsPicture : goodsPictureList) {
                    goodsPics.add(FileUrlUtil.getFileUrl(goodsPicture.getImagePath(), null));
                }
            }
            if (goodsPromotion != null) {
                this.promotionType = goodsPromotion.getPromotionType();
                this.promotionId = goodsPromotion.getPromotionId();
            }
        }

    }

    /**
     * 店铺信息
     */
    @Data
    public class StoreInf implements Serializable {
        private static final long serialVersionUID = 2345666298519908713L;

        public StoreInf(Store store, Boolean isFollowStore) {
            this.storeId = store.getStoreId();
            this.storeName = store.getStoreName();
            this.storeGradeName = store.getStoreGradeName();
            this.isOwnStore = store.getIsOwnStore();
            this.storeLogo = FileUrlUtil.getFileUrl(store.getStoreLogo(), null);
            this.serviceScore = store.getServiceScore();
            this.deliverScore = store.getDeliverScore();
            this.descriptionScore = store.getDescriptionScore();
            this.followNumber = store.getFollowNumber();
            this.isFollowStore = isFollowStore;
        }

        @ApiModelProperty("商家ID")
        private Long storeId;

        @ApiModelProperty("店铺名称")
        private String storeName;

        @ApiModelProperty("店铺等级名称")
        private String storeGradeName;

        @ApiModelProperty("店铺logo")
        private String storeLogo;

        @ApiModelProperty("店铺评分服务（每天定时任务计算积分）")
        private String serviceScore;

        @ApiModelProperty("店铺评分发货（每天定时任务计算积分）")
        private String deliverScore;

        @ApiModelProperty("店铺评分描述（每天定时任务计算积分）")
        private String descriptionScore;

        @ApiModelProperty("自营店铺1-自营店铺，2-入驻店铺")
        private Integer isOwnStore;

        @ApiModelProperty("店铺收藏")
        private Integer followNumber;

        @ApiModelProperty("是否关注店铺，true-关注，未登录时为false")
        private Boolean isFollowStore;
    }

    /**
     * 商品服务标签
     */
    @Data
    public static class ServiceLabelVO {

        public ServiceLabelVO(GoodsLabel goodsLabel) {
            labelName = goodsLabel.getLabelName();
            description = goodsLabel.getDescription();
        }

        @ApiModelProperty("标签名称")
        private String labelName;

        @ApiModelProperty("标签描述")
        private String description;
    }


    /**
     * 物流信息
     */
    @Data
    public static class DeliverInfo implements Serializable {
        private static final long serialVersionUID = 4568968734786237462L;

        @ApiModelProperty("运费")
        private BigDecimal expressFee;

        @ApiModelProperty("发货时间")
        private Integer transTime;

        @ApiModelProperty("发货地")
        private String cityName;
    }
}
