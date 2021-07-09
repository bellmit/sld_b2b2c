package com.slodon.b2b2c.vo.cms;

import com.slodon.b2b2c.cms.pojo.InformationCategory;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: slodon
 * @Description 封装资讯分类VO对象
 * @Author lxk
 */
@Data
public class PcInformationCateVO {

    @ApiModelProperty("分类id")
    private Integer cateId;

    @ApiModelProperty("分类名称")
    private String cateName;

    public PcInformationCateVO(InformationCategory informationCategory) {
        cateId = informationCategory.getCateId();
        cateName = informationCategory.getCateName();
    }
}
