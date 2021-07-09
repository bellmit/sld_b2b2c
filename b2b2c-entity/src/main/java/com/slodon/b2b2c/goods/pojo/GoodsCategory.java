package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品分类
 */
@Data
public class GoodsCategory implements Serializable {
    private static final long serialVersionUID = -2559915237044765353L;
    @ApiModelProperty("分类id")
    private Integer categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("分类别名")
    private String categoryAlias;

    @ApiModelProperty("父类ID")
    private Integer pid;

    @ApiModelProperty("分类描述")
    private String description;

    @ApiModelProperty("上级分类路径")
    private String path;

    @ApiModelProperty("分佣比例(商家给平台的分佣比例，填写0到1的数字)")
    private BigDecimal scaling;

    @ApiModelProperty("创建人id")
    private Integer createAdminId;

    @ApiModelProperty("更新人id")
    private Integer updateAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("分类状态：0-未启用；1-启用；默认是1")
    private Integer state;

    @ApiModelProperty("分类级别: 1-一级，2-二级，3-三级")
    private Integer grade;

    @ApiModelProperty("移动端分类图片路径（仅在二三级分类使用）")
    private String categoryImage;

    @ApiModelProperty("广告图（仅在PC端一级分类下使用）")
    private String recommendPicture;

    @ApiModelProperty("移动端图片（仅在一级分类使用）")
    private String mobileImage;
}