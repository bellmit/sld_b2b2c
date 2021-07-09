package com.slodon.b2b2c.vo.integral;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.dto.GoodsAddDTO;
import com.slodon.b2b2c.integral.pojo.IntegralGoods;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsPicture;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 货品信息vo
 */
@Data
public class FrontProductInfoVO implements Serializable {

    private static final long serialVersionUID = -3225537710294977591L;
    @ApiModelProperty("商品ID")
    private Long integralGoodsId;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("商品副标题")
    private String goodsBrief;

    @ApiModelProperty("商品详情信息")
    private String goodsDetails;

    @ApiModelProperty("虚拟销量+实际销量")
    private Integer sales;

    @ApiModelProperty("分享图片")
    private String shareImage;

    @ApiModelProperty("分享链接")
    private String shareLink;

    @ApiModelProperty("规格列表")
    private List<FrontGoodsDetailVO.SpecVO> specs = new ArrayList<>();

    @ApiModelProperty("默认货品")
    private FrontGoodsDetailVO.ProductDetailVO defaultProduct;

    public FrontProductInfoVO(IntegralGoods integralGoods, IntegralProduct integralProduct,
                              List<IntegralProduct> productList, List<IntegralGoodsPicture> goodsPictureList) {
        this.integralGoodsId = integralGoods.getIntegralGoodsId();
        this.goodsName = integralGoods.getGoodsName();
        this.goodsBrief = integralGoods.getGoodsBrief();
        this.goodsDetails = integralGoods.getGoodsDetails();
        this.sales = integralProduct.getActualSales();
        this.shareImage = FileUrlUtil.getFileUrl(integralProduct.getMainImage(), ImageSizeEnum.SMALL);
        this.shareLink = DomainUrlUtil.SLD_H5_URL + "/#/pages/point/product/detail?productId=" + integralProduct.getIntegralProductId();
        //处理有效的规格组合
        List<String> effectSpecValueIds = new ArrayList<>();
        productList.forEach(product1 -> {
            if (!StringUtils.isEmpty(product1.getSpecValueIds())) {
                effectSpecValueIds.add(product1.getSpecValueIds());
            }
        });
        defaultProduct = new FrontGoodsDetailVO.ProductDetailVO(integralProduct, goodsPictureList);
        //处理规格
        dealSpec(integralGoods.getSpecJson(), integralProduct.getSpecValueIds(), effectSpecValueIds);
    }

    /**
     * 构造规格信息
     *
     * @param specJson            商品规格json
     * @param defaultSpecValueIds 当前选中的规格，格式为规格值id1,规格值id2...
     * @param effectSpecValueIds  商品所有有效的规格值ids
     */
    private void dealSpec(String specJson, String defaultSpecValueIds, List<String> effectSpecValueIds) {
        if (StringUtils.isEmpty(specJson)) {
            //无规格商品，无需构造
            return;
        }
        JSONObject.parseArray(specJson, GoodsAddDTO.SpecInfo.class).forEach(specInfo -> {
            if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                //主规格，放在规格列表的第一位
                specs.add(0, new FrontGoodsDetailVO.SpecVO(specInfo, defaultSpecValueIds, effectSpecValueIds));
            } else {
                specs.add(new FrontGoodsDetailVO.SpecVO(specInfo, defaultSpecValueIds, effectSpecValueIds));
            }
        });

    }
}
