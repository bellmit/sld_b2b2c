package com.slodon.b2b2c.goods.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.slodon.b2b2c.core.constant.GoodsConst;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 商户发布商品参数
 * @author cwl
 */
@Data
public class GoodsAddDTO implements Serializable {

    private static final long serialVersionUID = 3787874474286521914L;

    @ApiModelProperty(value ="商品名称为3到50个字符(商品副标题)",required = true)
    private String goodsName;

    @ApiModelProperty(value ="商品副标题，长度建议140个字符内")
    private String goodsBrief;

    @ApiModelProperty(value ="商品关键字，用于检索商品，用逗号分隔",required = true)
    private String keyword;

    @ApiModelProperty(value ="品牌ID",required = true)
    private Integer brandId;

    @ApiModelProperty(value ="3级分类ID",required = true)
    private Integer categoryId3;

    @ApiModelProperty(value="虚拟销量")
    private Integer virtualSales;

    @ApiModelProperty(value="0-没有启用规格；1-启用规格",required = true)
    private Integer isSpec;

    @ApiModelProperty(value="商品主规格；0-无主规格，其他id为对应的主规格ID，主规格值切换商品主图会切换",required = true)
    private Integer mainSpecId;

    @ApiModelProperty(value="商品详情信息")
    private String goodsDetails;

    @ApiModelProperty(value="商品推荐，0-不推荐；1-推荐（店铺内是否推荐）",required = true)
    private Integer storeIsRecommend;

    @ApiModelProperty(value="商品发布，1-放入仓库中，2-立即售卖",required = true)
    private Integer goodsState;

    @ApiModelProperty(value="商品图片",required = true)
    private  List<ImageInfo>  ImageList;

    @ApiModelProperty(value="商品视频")
    private String goodsVideo;

    @ApiModelProperty(value="商品服务标签；用英文逗号分隔（仅作展示使用）")
    private String serviceLabelIds;


    @ApiModelProperty(value="商品属性json（商品发布使用的属性信息，系统、自定义）")
    private List<AttributeInfo> attributeList;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("运费模板id(与固定运费二选一,必有一项)")
    private Integer freightId;

    @ApiModelProperty("固定运费(与运费模版id二选一,必有一项)")
    private Integer freightFee;

    @ApiModelProperty(value="商品参数，json格式，商品详情顶部显示")
    List<GoodsParameterInfo> goodsParameterList;

    @ApiModelProperty(value="顶部关联模版ID")
    private Integer relatedTemplateIdTop;

     @ApiModelProperty(value="底部关联模版ID")
    private Integer relatedTemplateIdBottom;

    @ApiModelProperty(value="货品列表")
    private List<ProductAddDTO> productList;

    @ApiModelProperty(value="规格及图片列表")
    private List<SpecInfo> specInfoList;

    @ApiModelProperty("是否可以开具增值税发票0-不可以；1-可以")
    private Integer isVatInvoice;

    @JsonIgnore
    public  String getSpecJson()
    {
        JSONArray specInfoArray= JSONArray.parseArray(JSON.toJSONString(this.specInfoList));
        for(int i=0;i<specInfoArray.size();i++) {
            JSONObject specInfoObject=specInfoArray.getJSONObject(i);
            JSONArray specValueArray=(JSONArray)specInfoObject.get("specValueList");
            for(int j=0;j<specValueArray.size();j++) {
                JSONObject specValueObject=specInfoArray.getJSONObject(j);
                specValueObject.remove("imageList");
            }
        }
        return  specInfoArray.toJSONString();
    }

    /**
     * 关联属性
     */
    @Data
    public static class AttributeInfo implements Serializable {
        private static final long serialVersionUID = 324355440938531468L;
        private Integer attribute_id;           //检索属性ID
        private Integer attribute_value_id;     //属性值ID
    }

    /**
     * 规格信息
     */
    @Data
    public static class SpecInfo implements Serializable {
        private static final long serialVersionUID = 8147878363234844459L;
        @ApiModelProperty(value ="规格id")
        private Integer specId;
        @ApiModelProperty(value ="规格名称")
        private String specName;
        @ApiModelProperty(value ="是否主规格,1-是，0-不是")
        private Integer isMainSpec;
        @ApiModelProperty(value ="规格值列表")
        private List<SpecValueInfo> specValueList;

        /**
         * 规格值信息
         */
        @Data
        public static class SpecValueInfo implements Serializable {
            private static final long serialVersionUID = -3703708925169053964L;
            @ApiModelProperty(value ="规格值id")
            private Integer specValueId;
            @ApiModelProperty(value ="规格值")
            private String specValue;
            @ApiModelProperty(value ="图片路径")
            private List<ImageInfo> imageList;

            /**
             * 获取规格主图
             * @return
             */
            @JsonIgnore
            public String getMainImage(){
                if (CollectionUtils.isEmpty(imageList)){
                    return null;
                }
                for (ImageInfo imageInfo : imageList) {
                    if (imageInfo.getIsMain() == GoodsConst.PICTURE_IS_MAIN_YES) {
                        return imageInfo.image;
                    }
                }
                return imageList.get(0).image;
            }
        }
    }


    /**
     * 图片信息
     */
    @Data
    public static class ImageInfo implements Serializable {
        private static final long serialVersionUID =3435456712233455641L;
        @ApiModelProperty("是否主图：1、主图；2、非主图，主图只能有一张")
        private Integer isMain;
        @ApiModelProperty(value ="图片路径")
        private String image;
    }

    /**
     * 店铺自定义商品参数表
     */
    @Data
    public static class GoodsParameterInfo implements Serializable {
        private static final long serialVersionUID = 156688987665521780L;

        public GoodsParameterInfo()
        {
        }

        @ApiModelProperty("属性id")
        private Integer parameterId;

        @ApiModelProperty("分组id")
        private Integer groupId;

        @ApiModelProperty("属性名称")
        private String parameterName;

        @ApiModelProperty("属性值，用逗号隔开")
        private String parameterValue;
    }


}
