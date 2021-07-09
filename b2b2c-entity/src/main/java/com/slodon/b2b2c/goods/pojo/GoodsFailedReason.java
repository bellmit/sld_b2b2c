package com.slodon.b2b2c.goods.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品上架审核驳回、违规下架原因；由平台添加并维护，平台审核驳回或处理违规时选择
 */
@Data
public class GoodsFailedReason implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("原因id")
    private Integer reasonId;

    @ApiModelProperty("类型：1-审核驳回原因，2-违规下架原因")
    private Integer type;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("管理员ID")
    private Integer createAdminId;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("是否显示，1-展示，0-不显示")
    private Integer isVisible;
}