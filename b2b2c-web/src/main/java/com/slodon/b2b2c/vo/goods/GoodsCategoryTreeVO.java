package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.GoodsCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装商品分类树VO对象
 * @Author wuxy
 */
@Data
public class GoodsCategoryTreeVO implements Serializable {

    private static final long serialVersionUID = -8248211718510653845L;
    @ApiModelProperty("分类ID")
    private Integer categoryId;

    @ApiModelProperty("分类名称")
    private String categoryName;

    @ApiModelProperty("父类ID")
    private Integer pid;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("分类级别: 1-一级，2-二级，3-三级")
    private Integer grade;

    @ApiModelProperty("移动端图片（仅在一级分类使用）")
    private String mobileImage;

    @ApiModelProperty("移动端分类图片路径（仅在二三级分类使用）")
    private String categoryImage;

    @ApiModelProperty("广告图（仅在PC端一级分类下使用）")
    private String recommendPicture;

    @ApiModelProperty("子分类列表")
    private List<GoodsListVO> goodsList;

    @ApiModelProperty("子分类列表")
    private List<GoodsCategoryTreeVO> children;

    public GoodsCategoryTreeVO(GoodsCategory goodsCategory) {
        categoryId = goodsCategory.getCategoryId();
        categoryName = goodsCategory.getCategoryName();
        pid = goodsCategory.getPid();
        sort = goodsCategory.getSort();
        grade = goodsCategory.getGrade();
        mobileImage = goodsCategory.getMobileImage();
        categoryImage = FileUrlUtil.getFileUrl(goodsCategory.getCategoryImage(), null);
        recommendPicture = FileUrlUtil.getFileUrl(goodsCategory.getRecommendPicture(), null);
    }
}
