package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author sp
 * @program: slodon
 * @Description front封装品牌列表VO对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBrandFrontVO implements Serializable {
    private static final long serialVersionUID = -7033960764803889769L;

    @ApiModelProperty("品牌首字母")
    private String brandInitial;

    @ApiModelProperty("我的足迹集合")
    private List<GoodsBrandInfo> goodsBrandInfoList;

    @Data
    public static class GoodsBrandInfo implements Serializable {
        @ApiModelProperty("品牌id")
        private Integer brandId;

        @ApiModelProperty("品牌名称")
        private String brandName;

        @ApiModelProperty("品牌描述（一段文字）")
        private String brandDesc;

        @ApiModelProperty("品牌图片（相对）")
        private String image;

        @ApiModelProperty("品牌图片（绝对）")
        private String imageUrl;

        @ApiModelProperty("创建时间")
        private Date createTime;

        public GoodsBrandInfo(GoodsBrand brand) {
            this.brandId = brand.getBrandId();
            this.brandName = brand.getBrandName();
            this.brandDesc = brand.getBrandDesc();
            this.image = brand.getImage();
            this.imageUrl = FileUrlUtil.getFileUrl(brand.getImage(),null);
            this.createTime = brand.getCreateTime();
        }
    }

}