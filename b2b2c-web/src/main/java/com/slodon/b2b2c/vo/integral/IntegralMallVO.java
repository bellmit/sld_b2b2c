package com.slodon.b2b2c.vo.integral;

import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsLabel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 封装积分商城Vo对象
 */
@Data
public class IntegralMallVO implements Serializable {

    private static final long serialVersionUID = -7874088426653367674L;
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

    @ApiModelProperty("一级分类广告图")
    private String data;

    @ApiModelProperty("商品列表")
    private List<IntegralGoodsListVO> goodsList = new ArrayList<>();

    @ApiModelProperty("二级标签列表")
    private List<IntegralMallVO> children = new ArrayList<>();

    public IntegralMallVO(IntegralGoodsLabel integralGoodsLabel) {
        this.labelId = integralGoodsLabel.getLabelId();
        this.labelName = integralGoodsLabel.getLabelName();
        this.sort = integralGoodsLabel.getSort();
        this.parentLabelId = integralGoodsLabel.getLabelId();
        this.grade = integralGoodsLabel.getGrade();
        this.image = FileUrlUtil.getFileUrl(integralGoodsLabel.getImage(), ImageSizeEnum.SMALL);
        this.data = integralGoodsLabel.getData();
    }

}
