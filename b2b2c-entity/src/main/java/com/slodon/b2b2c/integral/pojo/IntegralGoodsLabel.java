package com.slodon.b2b2c.integral.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 积分商城商品标签
 */
@Data
public class IntegralGoodsLabel implements Serializable {
    private static final long serialVersionUID = 7106787645284925736L;
    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("上级标签id")
    private Integer parentLabelId;

    @ApiModelProperty("级别，1-一级，2-二级")
    private Integer grade;

    @ApiModelProperty("二级标签图片")
    private String image;

    @ApiModelProperty("标签状态：0、不显示；1、显示")
    private Integer state;

    @ApiModelProperty("创建人ID")
    private Integer createAdminId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人ID")
    private Integer updateAdminId;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("一级分类广告图")
    private String data;
}