package com.slodon.b2b2c.cms.pojo;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

/**
 * 资讯首页装修数据表
 */
@Data
public class InformationIndex implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("首页id")
    private Integer indexId;

    @ApiModelProperty("资讯首页轮播广告数据")
    private String data;
}