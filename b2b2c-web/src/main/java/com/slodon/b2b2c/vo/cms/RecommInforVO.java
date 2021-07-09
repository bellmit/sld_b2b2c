package com.slodon.b2b2c.vo.cms;

import com.slodon.b2b2c.cms.pojo.Information;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @program: slodon
 * @Description 封装推荐资讯VO对象
 * @Author lxk
 */
@Data
public class RecommInforVO {

    @ApiModelProperty("资讯id")
    private Integer informationId;

    @ApiModelProperty("资讯标题")
    private String title;

    @ApiModelProperty("资讯封面")
    private String coverImage;

    public RecommInforVO(Information information) {
        informationId = information.getInformationId();
        title = information.getTitle();
        coverImage = FileUrlUtil.getFileUrl(information.getCoverImage(),null);
    }
}
