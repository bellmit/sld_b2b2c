package com.slodon.b2b2c.goods.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author cwl
 */
@Data
public class GoodsCategoryUpdateDTO implements Serializable {

    private static final long serialVersionUID = 3023343554888901014L;


    @ApiModelProperty(value = "分类id",required = true)
    private Integer categoryId;

    @ApiModelProperty(value ="分类名称")
    private String categoryName;

    @ApiModelProperty(value ="分类别名")
    private String categoryAlias;

    @ApiModelProperty(value ="父类ID")
    private Integer pid;

    @ApiModelProperty(value ="分佣比例(商家给平台的分佣比例，填写0到1的数字)")
    private BigDecimal scaling;

    @ApiModelProperty(value ="排序")
    private Integer sort;

    @ApiModelProperty(value ="移动端分类图片路径（仅在二三级分类使用）")
    private String categoryImage;

    @ApiModelProperty(value ="广告图（仅在PC端一级分类下使用）")
    private String recommendPicture;

    @ApiModelProperty(value ="移动端图片（仅在一级分类使用）")
    private String mobileImage;

    @ApiModelProperty(value ="绑定品牌列表")
    private String bindBrands;

    @ApiModelProperty(value ="绑定属性列表")
    private String bindAttributes;
}
