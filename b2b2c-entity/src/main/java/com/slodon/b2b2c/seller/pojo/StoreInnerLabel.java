package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 店铺内商品标签
 */
@Data
public class StoreInnerLabel implements Serializable {
    private static final long serialVersionUID = -6652009332711326129L;

    @ApiModelProperty("店铺内分类ID")
    private Integer innerLabelId;

    @ApiModelProperty("分类名称")
    private String innerLabelName;

    @ApiModelProperty("店铺内分类排序")
    private Integer innerLabelSort;

    @ApiModelProperty("是否显示，0-不显示，1-显示")
    private Integer isShow;

    @ApiModelProperty("父分类ID")
    private Integer parentInnerLabelId;

    @ApiModelProperty("店铺ID")
    private Long storeId;

    @ApiModelProperty("添加时间")
    private Date createTime;

    @ApiModelProperty("创建人ID")
    private Long createVendorId;

    @ApiModelProperty("创建人名字")
    private String createVendorName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("更新人ID")
    private Long updateVendorId;

    @ApiModelProperty("更新人名字")
    private String updateVendorName;
}