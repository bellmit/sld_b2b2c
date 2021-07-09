package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import com.slodon.b2b2c.goods.pojo.GoodsAttribute;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装商品分类VO对象
 */
@Data
public class GoodsCategoryVO {

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

    @ApiModelProperty("绑定的属性列表")
    private List<GoodsAttribute> attributeList;

    @ApiModelProperty("绑定的品牌列表")
    private List<GoodsBrand> brandList;

    public GoodsCategoryVO(GoodsCategory category) {
        this.categoryId = category.getCategoryId();
        this.categoryId = category.getCategoryId();
        this.categoryName = category.getCategoryName();
        this.categoryAlias = category.getCategoryAlias();
        this.pid = category.getPid();
        this.description = category.getDescription();
        this.createTime = category.getCreateTime();
        this.updateTime = category.getUpdateTime();
        this.sort = category.getSort();
        this.state = category.getState();
        this.grade = category.getGrade();
        this.categoryImage = FileUrlUtil.getFileUrl(category.getCategoryImage(), null);
        this.recommendPicture = FileUrlUtil.getFileUrl(category.getRecommendPicture(), null);
        this.mobileImage = FileUrlUtil.getFileUrl(category.getMobileImage(), null);

    }
}