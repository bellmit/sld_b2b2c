package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.goods.pojo.GoodsAttribute;
import com.slodon.b2b2c.goods.pojo.GoodsAttributeValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装商品属性列表VO对象
 */
@Data
public class GoodsAttributeVO {
    @ApiModelProperty("属性id")
    private Integer attributeId;

    @ApiModelProperty("属性名称")
    private String attributeName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("排序0到255，越小越靠前展示")
    private Integer sort;

    @ApiModelProperty("是否展示：0-不展示，1-展示")
    private Integer isShow;

    @ApiModelProperty("属性值")
    private List<String> attributeValues = new ArrayList<>();

    @ApiModelProperty("属性值(带id)")
    private List<GoodsAttributeValueVO> attributeValueList = new ArrayList<>();

    public GoodsAttributeVO(GoodsAttribute attribute, List<GoodsAttributeValue> goodsAttributeValueList) {
        this.attributeId=attribute.getAttributeId();
        this.attributeName=attribute.getAttributeName();
        this.sort=attribute.getSort();
        this.isShow=attribute.getIsShow();
        this.createTime=attribute.getCreateTime();
        if (!CollectionUtils.isEmpty(goodsAttributeValueList)){
            goodsAttributeValueList.forEach(goodsAttributeValue -> {
                this.attributeValues.add(goodsAttributeValue.getAttributeValue());
                this.attributeValueList.add(new GoodsAttributeValueVO(goodsAttributeValue));
            });
        }
    }

    /**
     * 属性值VO
     */
    @Data
    public static class GoodsAttributeValueVO{
        public GoodsAttributeValueVO(GoodsAttributeValue goodsAttributeValue) {
            this.valueId = goodsAttributeValue.getValueId();
            this.attributeValue = goodsAttributeValue.getAttributeValue();
        }

        @ApiModelProperty("主键id")
        private Integer valueId;

        @ApiModelProperty("属性值")
        private String attributeValue;
    }
}