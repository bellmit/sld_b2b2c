package com.slodon.b2b2c.vo.cms;

import com.slodon.b2b2c.cms.pojo.Information;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @program: slodon
 * @Description 封装资讯VO对象
 * @Author lxk
 */
@Data
public class PcInformationVO {

    @ApiModelProperty("资讯id")
    private Integer informationId;

    @ApiModelProperty("资讯分类id")
    private Integer cateId;

    @ApiModelProperty("分类名称")
    private String cateName;

    @ApiModelProperty("资讯标题")
    private String title;

    @ApiModelProperty("资讯内容")
    private String content;

    @ApiModelProperty("资讯封面")
    private String coverImage;

    @ApiModelProperty("阅读量")
    private Integer pageView;

    @ApiModelProperty("发布时间")
    private Date createTime;

    public PcInformationVO(Information information) {
        informationId = information.getInformationId();
        cateId = information.getCateId();
        cateName = information.getCateName();
        title = information.getTitle();
        content = information.getContent();
        coverImage = FileUrlUtil.getFileUrl(information.getCoverImage(),null);
        pageView = information.getPageView();
        createTime = information.getCreateTime();
    }
}
