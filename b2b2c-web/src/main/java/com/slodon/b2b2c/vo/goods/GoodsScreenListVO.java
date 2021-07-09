package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装商品筛选VO对象
 * @Author wuxy
 * @date 2020.12.01 16:54
 */
@Data
public class GoodsScreenListVO implements Serializable {

    private static final long serialVersionUID = 6752107316440759804L;
    @ApiModelProperty("分类列表")
    private List<CategoryVO> categoryList;

    @ApiModelProperty("品牌列表")
    private List<BrandVO> brandList;

    @ApiModelProperty("属性列表")
    private List<AttributeVO> attributeList;

    @Data
    public static class CategoryVO implements Serializable {
        private static final long serialVersionUID = 8409709219184869443L;
        @ApiModelProperty("分类id")
        private Integer categoryId;

        @ApiModelProperty("分类名称")
        private String categoryName;

        public CategoryVO(Integer categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }
    }

    @Data
    public static class BrandVO implements Serializable {
        private static final long serialVersionUID = -6938666307096806377L;
        @ApiModelProperty("品牌id")
        private Integer brandId;

        @ApiModelProperty("品牌名称")
        private String brandName;

        @ApiModelProperty("品牌图片")
        private String image;

        public BrandVO(GoodsBrand goodsBrand) {
            this.brandId = goodsBrand.getBrandId();
            this.brandName = goodsBrand.getBrandName();
            this.image = FileUrlUtil.getFileUrl(goodsBrand.getImage(),null);
        }
    }

    @Data
    public static class AttributeVO implements Serializable {
        private static final long serialVersionUID = -2691139814088336778L;
        @ApiModelProperty("属性id")
        private Integer attributeId;

        @ApiModelProperty("属性名称")
        private String attributeName;

        @ApiModelProperty("属性值列表")
        private List<AttributeValueVO> attributeValueList;

        public AttributeVO(Integer attributeId, String attributeName) {
            this.attributeId = attributeId;
            this.attributeName = attributeName;
        }

        @Data
        public static class AttributeValueVO implements Serializable {
            private static final long serialVersionUID = -6101980034807362187L;
            @ApiModelProperty("主键id")
            private Integer valueId;

            @ApiModelProperty("属性值")
            private String attributeValue;

            public AttributeValueVO(Integer valueId, String attributeValue) {
                this.valueId = valueId;
                this.attributeValue = attributeValue;
            }
        }
    }
}
