package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品品牌
 */
@Data
public class GoodsBrand implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("品牌id")
    private Integer brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("品牌描述（一段文字）")
    private String brandDesc;

    @ApiModelProperty("品牌首字母")
    private String brandInitial;

    @ApiModelProperty("品牌图片")
    private String image;

    @ApiModelProperty("1-推荐；0-不推荐")
    private Integer isRecommend;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("创建管理员id(如果是商户申请的即为审核管理员id)")
    private Integer createAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新管理员id")
    private Integer updateAdminId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("申请商户id(如果为系统后台创建，此项为0)")
    private Long applyVendorId;

    @ApiModelProperty("申请店铺id(如果为系统后台创建，此项为0)")
    private Long applyStoreId;

    @ApiModelProperty("审核失败理由，默认为空")
    private String failReason;

    @ApiModelProperty("(状态操作只有系统管理员可操作，店铺申请后店铺就不能再操作了)状态 1、系统创建显示中；2、提交审核（待审核，不显示）；3、审核失败；4、删除")
    private Integer state;

    @ApiModelProperty("三级商品分类id，商家申请品牌绑定")
    private Integer goodsCategoryId3;

    @ApiModelProperty("分类路径，一级分类名称/二级分类名称/三级分类名称，商家申请品牌绑定")
    private String goodsCategoryPath;

    @ApiModelProperty("店铺名称")
    private String storeName;
}