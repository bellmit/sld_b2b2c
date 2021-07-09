package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @program: slodon
 * @Description 封装商品标签Vo对象
 * @Author wuxy
 */
@Data
public class IntegralGoodsLabelVO implements Serializable {

    private static final long serialVersionUID = 2981612630112819005L;
    @ApiModelProperty("标签id")
    private Integer labelId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("上级标签id")
    private Integer parentLabelId;

    @ApiModelProperty("级别，1-一级，2-二级")
    private Integer grade;

    @ApiModelProperty("二级标签图片")
    private String image;

    @ApiModelProperty("二级标签图片(绝对路径)")
    private String imageUrl;

    @ApiModelProperty("标签状态：0、不显示；1、显示")
    private Integer state;

    @ApiModelProperty("一级分类广告图")
    private String data;

    @ApiModelProperty("二级标签列表")
    private List<IntegralGoodsLabelVO> children;

    public IntegralGoodsLabelVO(IntegralGoodsLabel integralGoodsLabel) {
        this.labelId = integralGoodsLabel.getLabelId();
        this.labelName = integralGoodsLabel.getLabelName();
        this.sort = integralGoodsLabel.getSort();
        this.parentLabelId = integralGoodsLabel.getParentLabelId();
        this.grade = integralGoodsLabel.getGrade();
        this.image = integralGoodsLabel.getImage();
        this.imageUrl = FileUrlUtil.getFileUrl(integralGoodsLabel.getImage(), ImageSizeEnum.SMALL);
        this.state = integralGoodsLabel.getState();
        this.data = integralGoodsLabel.getData();
    }
}
