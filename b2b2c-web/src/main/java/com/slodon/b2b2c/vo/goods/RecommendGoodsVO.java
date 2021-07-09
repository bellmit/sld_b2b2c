package com.slodon.b2b2c.vo.goods;

import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.i18n.Language;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @program: slodon
 * @Description 封装推荐商品VO对象
 * @Author wuxy
 */
@Data
public class RecommendGoodsVO implements Serializable {

    private static final long serialVersionUID = 8136040698440552851L;
    @ApiModelProperty("商品ID")
    private Long goodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题")
    private String goodsBrief;

    @ApiModelProperty("商品关键字")
    private String keyword;

    @ApiModelProperty("品牌ID")
    private Integer brandId;

    @ApiModelProperty("品牌名称")
    private String brandName;

    @ApiModelProperty("商城价销售价格")
    private BigDecimal goodsPrice;

    @ApiModelProperty("商品库存")
    private Integer goodsStock;

    @ApiModelProperty("实际销量")
    private Integer actualSales;

    @ApiModelProperty("1-自营；2-商家")
    private Integer isSelf;

    @ApiModelProperty("1-自营；2-商家")
    private String isSelfValue;

    @ApiModelProperty("商品主图路径")
    private String mainImage;

    @ApiModelProperty("商品详情链接地址")
    private String goodsLinkUrl;

    public RecommendGoodsVO(Goods goods) {
        goodsId = goods.getGoodsId();
        goodsName = goods.getGoodsName();
        goodsBrief = goods.getGoodsBrief();
        keyword = goods.getKeyword();
        brandId = goods.getBrandId();
        brandName = goods.getBrandName();
        goodsPrice = goods.getGoodsPrice();
        goodsStock = goods.getGoodsStock();
        actualSales = goods.getActualSales();
        isSelf = goods.getIsSelf();
        isSelfValue = dealIsSelfValue(goods.getIsSelf());
        mainImage = FileUrlUtil.getFileUrl(goods.getMainImage(), ImageSizeEnum.SMALL);
        goodsLinkUrl = DomainUrlUtil.SLD_H5_URL + "/#/pages/product/detail?productId=" + goods.getDefaultProductId() + "&goodsId=" + goods.getGoodsId();
    }

    public static String dealIsSelfValue(Integer isSelf) {
        String value = null;
        if (StringUtils.isEmpty(isSelf)) return Language.translate("未知");
        switch (isSelf) {
            case GoodsConst.IS_SELF_YES:
                value = "自营";
                break;
            case GoodsConst.IS_SELF_NO:
                value = "入驻商家";
                break;
        }
        //翻译
        value = Language.translate(value);
        return value;
    }

}
