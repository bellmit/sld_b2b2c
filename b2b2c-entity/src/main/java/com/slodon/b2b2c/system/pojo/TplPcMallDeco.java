package com.slodon.b2b2c.system.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * PC商城装修页组装信息表（基于实例化数据表组合）
 */
@Data
public class TplPcMallDeco implements Serializable {
    private static final long serialVersionUID = -8651430051441081207L;
    @ApiModelProperty("商城装修页面组装信息id")
    private Integer decoId;

    @ApiModelProperty("装修页类型")
    private String decoType;

    @ApiModelProperty("装修页名称")
    private String decoName;

    @ApiModelProperty("装修页主导航条数据id，无则为0")
    private Integer masterNavigationBarId;

    @ApiModelProperty("装修页主轮播图数据id，无则为0")
    private Integer masterBannerId;

    @ApiModelProperty("有序模板数据id集合，按存储顺序确定位置")
    private String rankedTplDataIds;

    @ApiModelProperty("是否启用该装修页；0==不启用，1==启用")
    private Integer isEnable;

    @ApiModelProperty("创建人id")
    private Long createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新人id")
    private Long updateUserId;

    @ApiModelProperty("更新人名称")
    private String updateUserName;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("店铺id(0为平台)")
    private Long storeId;

    /**
     * 需要封装的一些数据
     */
    @JsonIgnore
    private TplPcMallData masterNavigationBarData;
    @JsonIgnore
    private TplPcMallData masterBannerData;
    @JsonIgnore
    private List<TplPcMallData> rankedTplDataList;
}