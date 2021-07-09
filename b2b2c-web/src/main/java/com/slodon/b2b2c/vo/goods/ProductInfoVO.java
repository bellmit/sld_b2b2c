package com.slodon.b2b2c.vo.goods;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.goods.dto.GoodsAddDTO;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.goods.pojo.GoodsPicture;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.Product;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 货品信息vo
 */
@Data
public class ProductInfoVO {


    @ApiModelProperty("分享图片")
    private String shareImage;

    @ApiModelProperty("分享链接")
    private String shareLink;

    @ApiModelProperty("规格列表")
    private List<GoodsFrontDetailVO.SpecVO> specs = new ArrayList<>();

    @ApiModelProperty("默认货品")
    private GoodsFrontDetailVO.ProductDetailVO defaultProduct;

    @ApiModelProperty("物流信息")
    private GoodsFrontDetailVO.DeliverInfo deliverInfo;



    public ProductInfoVO(
            GoodsExtend goodsExtend,
            Product product,
            List<Product> productList,
            List<GoodsPicture> goodsPictureList,
            GoodsFrontDetailVO.DeliverInfo deliverInfo,
            GoodsPromotion goodsPromotion) {
        this.deliverInfo=deliverInfo;
        this.shareImage= FileUrlUtil.getFileUrl(product.getMainImage(), ImageSizeEnum.SMALL );
        this.shareLink= DomainUrlUtil.SLD_H5_URL+"/#/pages/product/detail?productId="+product.getProductId();
        //处理有效的规格组合
        List<String> effectSpecValueIds=new ArrayList<>();
        productList.forEach(product1 -> {
            if (!StringUtils.isEmpty(product1.getSpecValueIds())) {
                effectSpecValueIds.add(product1.getSpecValueIds());
            }
        });
        defaultProduct = new GoodsFrontDetailVO.ProductDetailVO(product, goodsPictureList,goodsPromotion);
        //处理规格
        dealSpec(goodsExtend.getSpecJson(), product.getSpecValueIds(), effectSpecValueIds);
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
            if (specInfo.getIsMainSpec()== GoodsConst.IS_MAIN_SPEC_YES) {
                //主规格，放在规格列表的第一位
                specs.add(0, new GoodsFrontDetailVO.SpecVO(specInfo, defaultSpecValueIds, effectSpecValueIds));
            } else {
                specs.add(new GoodsFrontDetailVO.SpecVO(specInfo, defaultSpecValueIds, effectSpecValueIds));
            }
        });

    }
}
