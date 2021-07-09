package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.GoodsBrand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author cwl
 * @program: slodon
 * @Description 封装商户品牌列表VO对象
 */
@Data
public class SellerBrandVO {

    @ApiModelProperty("品牌id")
    private Integer brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("品牌描述（一段文字）")
    private String brandDesc;

    @ApiModelProperty("品牌图片相对地址")
    private String image;

    @ApiModelProperty("品牌图片绝对地址")
    private String imageUrl;

    @ApiModelProperty("分类路径")
    private String goodsCategoryPath;

    @ApiModelProperty("审核失败理由，默认为空")
    private String failReason;

    @ApiModelProperty("(状态操作只有系统管理员可操作，店铺申请后店铺就不能再操作了)状态 1、系统创建显示中；2、提交审核（待审核，不显示）；3、审核失败")
    private Integer state;

    @ApiModelProperty("(状态操作只有系统管理员可操作，店铺申请后店铺就不能再操作了)状态值 1、系统创建显示中；2、提交审核（待审核，不显示）；3、审核失败")
    private String stateValue;

    @ApiModelProperty("三级商品分类id，商家申请品牌绑定")
    private Integer goodsCategoryId3;

    @ApiModelProperty("一级商品分类id")
    private Integer goodsCategoryId1;

    @ApiModelProperty("二级商品分类id")
    private Integer goodsCategoryId2;

    public SellerBrandVO(GoodsBrand brand) {
        this.brandId = brand.getBrandId();
        this.brandName = brand.getBrandName();
        this.brandDesc = brand.getBrandDesc();
        this.image = brand.getImage();
        this.imageUrl = FileUrlUtil.getFileUrl(brand.getImage(), null);
        this.failReason = brand.getFailReason();
        this.state = brand.getState();
        this.goodsCategoryId3 = brand.getGoodsCategoryId3();
        this.goodsCategoryPath = brand.getGoodsCategoryPath();
        stateValue = getRealStateValue(state);
    }

    public static String getRealStateValue(Integer state) {
        String value = null;
        if (StringUtils.isEmpty(state)) return Language.translate("未知");
        switch (state) {
            case GoodsConst.BRAND_STATE_1:
                value = "审核通过";
                break;
            case GoodsConst.BRAND_STATE_2:
                value = "提交审核";
                break;
            case GoodsConst.BRAND_STATE_3:
                value = "审核失败";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }
}