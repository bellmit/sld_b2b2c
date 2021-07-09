package com.slodon.b2b2c.cms.dto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lxk
 */
@Data
public class InformationAddDTO implements Serializable {

    private static final long serialVersionUID = 4342481166489891009L;

    @ApiModelProperty(value = "资讯分类id",required = true)
    private Integer cateId;

    @ApiModelProperty(value = "资讯标题",required = true)
    private String title;

    @ApiModelProperty(value = "资讯封面",required = true)
    private String coverImage;

    @ApiModelProperty(value = "资讯内容",required = true)
    private String content;
}
