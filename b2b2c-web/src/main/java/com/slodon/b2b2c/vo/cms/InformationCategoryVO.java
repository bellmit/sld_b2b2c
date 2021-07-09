package com.slodon.b2b2c.vo.cms;

import com.slodon.b2b2c.cms.pojo.InformationCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @program: slodon
 * @Description 封装资讯分类VO对象
 * @Author lxk
 */
@Data
public class InformationCategoryVO {

    @ApiModelProperty("分类id")
    private Integer cateId;

    @ApiModelProperty("分类名称")
    private String cateName;

    @ApiModelProperty("排序，值越小越靠前")
    private Integer sort;

    @ApiModelProperty("是否显示，0-不显示，1-显示，默认0")
    private Integer isShow;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("创建人id")
    private Integer createAdminId;

    @ApiModelProperty("创建人名称")
    private String createAdminName;

    public InformationCategoryVO(InformationCategory informationCategory) {
        cateId = informationCategory.getCateId();
        cateName = informationCategory.getCateName();
        sort = informationCategory.getSort();
        isShow = informationCategory.getIsShow();
        createTime = informationCategory.getCreateTime();
        createAdminId = informationCategory.getCreateAdminId();
        createAdminName = informationCategory.getCreateAdminName();
    }
}
