package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品对应图片表
 */
@Data
public class GoodsPicture implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("图片id")
    private Integer pictureId;

    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品主spec_value_id，对于没有启用主规格的此值为0")
    private Integer specValueId;

    @ApiModelProperty("图片路径")
    private String imagePath;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("上传人")
    private Long createId;

    @ApiModelProperty("上传时间")
    private Date createTime;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("是否主图：1、主图；2、非主图，主图只能有一张")
    private Integer isMain;
}