package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.goods.example.*;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.model.goods.*;
import com.slodon.b2b2c.vo.goods.GoodsAttributeVO;
import com.slodon.b2b2c.vo.goods.GoodsBrandVO;
import com.slodon.b2b2c.vo.goods.GoodsCategoryBindVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "seller分类绑定信息")
@RestController
@RequestMapping("v3/goods/seller/goodsAttribute")
public class GoodsSellerAttributeController {

    @Resource
    private GoodsAttributeModel goodsAttributeModel;
    @Resource
    private GoodsAttributeValueModel goodsAttributeValueModel;
    @Resource
    private GoodsCategoryBindAttributeModel goodsCategoryBindAttributeModel;
    @Resource
    private GoodsCategoryBindBrandModel goodsCategoryBindBrandModel;
    @Resource
    private GoodsBrandModel goodsBrandModel;

    @ApiOperation("分类绑定的属性,品牌列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "分类Id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "1", paramType = "query")
    })
    @GetMapping("listByCategoryId")
    public JsonResult<GoodsCategoryBindVO> getList(HttpServletRequest request,
                                                   @RequestParam(value = "categoryId", required = false) Integer categoryId) {
        AssertUtil.isTrue(categoryId < 1, "分类Id错误");
        GoodsCategoryBindVO goodsCategoryBindVO = new GoodsCategoryBindVO(getBrandList(categoryId), getAttributeList(categoryId));
        return SldResponse.success(goodsCategoryBindVO);
    }

    /**
     * 查询分类绑定的属性列表
     *
     * @param categoryId
     * @return
     */
    private List<GoodsAttributeVO> getAttributeList(Integer categoryId) {
        //查分类绑定的属性表
        GoodsCategoryBindAttributeExample goodsCategoryBindAttributeExample = new GoodsCategoryBindAttributeExample();
        goodsCategoryBindAttributeExample.setCategoryId(categoryId);
        List<GoodsCategoryBindAttribute> goodsCategoryBindAttributeList = goodsCategoryBindAttributeModel.getGoodsCategoryBindAttributeList(goodsCategoryBindAttributeExample, null);
        if (CollectionUtils.isEmpty(goodsCategoryBindAttributeList)) {
            return new ArrayList<>();
        }

        StringBuffer attributeIds = new StringBuffer();
        goodsCategoryBindAttributeList.forEach(goodsCategoryBindAttribute -> {
            attributeIds.append(",").append(goodsCategoryBindAttribute.getAttributeId());
        });
        //查属性表中的对应属性
        GoodsAttributeExample goodsAttributeExample = new GoodsAttributeExample();
        goodsAttributeExample.setAttributeIdIn(attributeIds.toString().substring(1));
        goodsAttributeExample.setIsShow(GoodsConst.IS_ATTRIBUTE_YES);
        goodsAttributeExample.setOrderBy("sort asc, create_time desc");
        List<GoodsAttribute> goodsAttributeList = goodsAttributeModel.getGoodsAttributeList(goodsAttributeExample, null);
        if (CollectionUtils.isEmpty(goodsAttributeList)) {
            return new ArrayList<>();
        }

        List<GoodsAttributeVO> vos = new ArrayList<>();

        goodsAttributeList.forEach(attribute -> {
            //查属性值表
            GoodsAttributeValueExample goodsAttributeValueExample = new GoodsAttributeValueExample();
            goodsAttributeValueExample.setAttributeId(attribute.getAttributeId());
            List<GoodsAttributeValue> goodsAttributeValueList = goodsAttributeValueModel.getGoodsAttributeValueList(goodsAttributeValueExample, null);

            GoodsAttributeVO vo = new GoodsAttributeVO(attribute, goodsAttributeValueList);
            vos.add(vo);
        });

        return vos;
    }

    /**
     * 查询分类绑定的品牌
     *
     * @param categoryId
     * @return
     */
    private List<GoodsBrandVO> getBrandList(Integer categoryId) {
        GoodsCategoryBindBrandExample goodsCategoryBindBrandExample = new GoodsCategoryBindBrandExample();
        goodsCategoryBindBrandExample.setCategoryId(categoryId);
        List<GoodsCategoryBindBrand> goodsCategoryBindBrandList = goodsCategoryBindBrandModel.getGoodsCategoryBindBrandList(goodsCategoryBindBrandExample, null);

        if (CollectionUtils.isEmpty(goodsCategoryBindBrandList)) {
            //未绑定品牌
            return new ArrayList<>();
        }

        StringBuffer brandIds = new StringBuffer();
        goodsCategoryBindBrandList.forEach(goodsCategoryBindBrand -> {
            brandIds.append(",").append(goodsCategoryBindBrand.getBrandId());
        });

        GoodsBrandExample goodsBrandExample = new GoodsBrandExample();
        goodsBrandExample.setBrandIdIn(brandIds.toString().substring(1));
        List<GoodsBrand> brandList = goodsBrandModel.getGoodsBrandList(goodsBrandExample, null);
        List<GoodsBrandVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(brandList)) {
            brandList.forEach(brand -> {
                GoodsBrandVO vo = new GoodsBrandVO(brand);
                vos.add(vo);
            });
        }
        return vos;
    }
}
