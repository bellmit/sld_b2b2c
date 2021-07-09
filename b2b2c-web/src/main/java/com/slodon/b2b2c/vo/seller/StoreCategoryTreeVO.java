package com.slodon.b2b2c.vo.seller;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author lxk
 * @program: slodon
 * @Description 封装自营店铺列表VO对象
 */
@Data
public class StoreCategoryTreeVO implements Serializable {

    private static final long serialVersionUID = 3537553673934223084L;

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

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    private List<StoreCategoryTreeVO> children;

}
