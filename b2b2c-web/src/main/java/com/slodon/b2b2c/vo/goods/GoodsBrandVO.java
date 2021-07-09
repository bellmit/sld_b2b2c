package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装品牌列表VO对象
 */
@Data
public class GoodsBrandVO {

    @ApiModelProperty("品牌id")
    private Integer brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("品牌描述（一段文字）")
    private String brandDesc;

    @ApiModelProperty("品牌图片（相对）")
    private String image;

    @ApiModelProperty("品牌图片（绝对）")
    private String imageUrl;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("在售商品数")
    private Integer onSaleGoodsNum;

    @ApiModelProperty("全部商品数")
    private Integer totalGoodsNum;

    public GoodsBrandVO(GoodsBrand brand) {
        this.brandId = brand.getBrandId();
        this.brandName = brand.getBrandName();
        this.brandDesc = brand.getBrandDesc();
        this.image = brand.getImage();
        this.imageUrl = FileUrlUtil.getFileUrl(brand.getImage(), null);
        this.createTime = brand.getCreateTime();
    }

}