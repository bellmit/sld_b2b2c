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
public class InformationVO {

    @ApiModelProperty("资讯id")
    private Integer informationId;

    @ApiModelProperty("分类名称")
    private String cateName;

    @ApiModelProperty("资讯标题")
    private String title;

    @ApiModelProperty("资讯封面相对路径")
    private String coverImage;

    @ApiModelProperty("资讯封面绝对路径")
    private String coverImageUrl;

    @ApiModelProperty("阅读量")
    private Integer pageView;

    @ApiModelProperty("显示状态：0、不显示；1、显示")
    private Integer isShow;

    @ApiModelProperty("是否推荐，0-否，1-是")
    private Integer isRecommend;

    @ApiModelProperty("发布时间")
    private Date createTime;

    public InformationVO(Information information) {
        informationId = information.getInformationId();
        cateName = information.getCateName();
        title = information.getTitle();
        coverImage = information.getCoverImage();
        coverImageUrl = FileUrlUtil.getFileUrl(information.getCoverImage(),null);
        pageView = information.getPageView();
        isShow = information.getIsShow();
        isRecommend = information.getIsRecommend();
        createTime = information.getCreateTime();
    }
}
