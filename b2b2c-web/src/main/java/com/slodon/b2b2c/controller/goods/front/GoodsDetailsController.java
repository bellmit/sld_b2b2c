package com.slodon.b2b2c.controller.goods.front;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.CalculateExpressDTO;
import com.slodon.b2b2c.goods.dto.GoodsAddDTO;
import com.slodon.b2b2c.goods.example.*;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.member.example.MemberAddressExample;
import com.slodon.b2b2c.member.example.MemberFollowProductExample;
import com.slodon.b2b2c.member.example.MemberFollowStoreExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberAddress;
import com.slodon.b2b2c.member.pojo.MemberFollowProduct;
import com.slodon.b2b2c.member.pojo.MemberFollowStore;
import com.slodon.b2b2c.model.goods.*;
import com.slodon.b2b2c.model.member.MemberAddressModel;
import com.slodon.b2b2c.model.member.MemberFollowProductModel;
import com.slodon.b2b2c.model.member.MemberFollowStoreModel;
import com.slodon.b2b2c.model.promotion.PromotionCommonModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.model.system.RegionCityModel;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.system.example.RegionCityExample;
import com.slodon.b2b2c.system.pojo.RegionCity;
import com.slodon.b2b2c.vo.goods.GoodsCommentsInfoVO;
import com.slodon.b2b2c.vo.goods.GoodsFrontDetailVO;
import com.slodon.b2b2c.vo.goods.GoodsPromotionVO;
import com.slodon.b2b2c.vo.goods.ProductInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Api(tags = "front-商品详情")
@RestController
@RequestMapping("v3/goods/front/goods")
public class GoodsDetailsController extends BaseController {

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsExtendModel goodsExtendModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsPictureModel goodsPictureModel;
    @Resource
    private GoodsLabelModel goodsLabelModel;
    @Resource
    private GoodsCommentModel goodsCommentModel;
    @Resource
    private GoodsSpecValueModel goodsSpecValueModel;
    @Resource
    private GoodsFreightTemplateModel goodsFreightTemplateModel;
    @Resource
    private GoodsRelatedTemplateModel goodsRelatedTemplateModel;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private MemberFollowProductModel memberFollowProductModel;
    @Resource
    private MemberFollowStoreModel memberFollowStoreModel;
    @Resource
    private RegionCityModel regionCityModel;
    @Resource
    private MemberAddressModel memberAddressModel;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @ApiOperation("获取商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品Id", required = true, paramType = "query"),
    })
    @GetMapping("details")
    public JsonResult<GoodsFrontDetailVO> getGoodsDetails(HttpServletRequest request, Long productId) {
        Product product = productModel.getProductByProductId(productId);
        AssertUtil.notNull(product, "货品不存在，请重试！");
        Long goodsId = product.getGoodsId();
        Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
        AssertUtil.notNull(goods, "商品不存在，请重试！");
        GoodsExtend goodsExtend = goodsExtendModel.getGoodsExtendByGoodsId(goodsId);
        AssertUtil.notNull(goodsExtend, "商品不存在，请重试！");

        //查询货品信息，如果没有传货品ID则获取默认货品
        ProductExample productExample = new ProductExample();
        productExample.setGoodsId(goodsId);
        productExample.setState(GoodsConst.PRODUCT_STATE_1);
        List<Product> productList = productModel.getProductList(productExample, null);
        if (product.getProductStock() == 0) {
            for (Product p : productList) {
                //判断哪个规格有库存，展示有库存规格，否则默认原来的
                if (p.getProductStock() > 0) {
                    product = p;
                    break;
                }
            }
        }

        //获取登录用户信息
        Member member = UserUtil.getUser(request, Member.class);
        Boolean isFavorite = checkFavorite(member.getMemberId(), goodsId, product.getProductId());

        //是否关注店铺
        Boolean isFollowStore = checkIsFollowStore(member.getMemberId(), product.getStoreId());

        //查询规格图片
        GoodsPictureExample goodsPictureExample = new GoodsPictureExample();
        goodsPictureExample.setOrderBy("is_main asc,picture_id asc");
        goodsPictureExample.setGoodsId(goodsId);
        //有主规格
        if (goodsExtend.getMainSpecId() != 0) {
            goodsPictureExample.setSpecValueIdIn(product.getSpecValueIds());
        }
        List<GoodsPicture> goodsPictureList = goodsPictureModel.getGoodsPictureList(goodsPictureExample, null);

        Store store = storeModel.getStoreByStoreId(goods.getStoreId());
        //默认店铺logo
        if (StringUtils.isEmpty(store.getStoreLogo())) {
            store.setStoreLogo(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
        }
        GoodsPromotion goodsPromotion = goodsPromotionModel.getGoodsPromotionByProductId(product.getProductId());
        if (goodsPromotion != null && goodsPromotion.getPromotionType() == PromotionConst.PROMOTION_TYPE_104) {
            //秒杀库存
            String seckillStock = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + product.getProductId());
            Integer stock = seckillStock == null ? 0 : Integer.parseInt(seckillStock);
            //秒杀活动未开始（商品库存减去秒杀库存），已开始取redis库存
            if (promotionCommonModel.isPromotionAvailable(PromotionConst.PROMOTION_TYPE_104, product.getProductId().toString())) {
                //活动正在进行
                product.setProductStock(stock);
            } else {
                //活动未开始
                product.setProductStock(product.getProductStock() - stock);
            }
        }
        List<GoodsFrontDetailVO.ServiceLabelVO> LabelVOList = getLabelNames(goods.getServiceLabelIds());

        GoodsFrontDetailVO.DeliverInfo deliverInfo = getDeliverInfo(goodsExtend, product, member);

        GoodsFrontDetailVO goodsFrontDetailVO = new GoodsFrontDetailVO(goods, isFavorite, LabelVOList, goodsExtend, product, productList, goodsPictureList, store, isFollowStore, deliverInfo, goodsPromotion);
        goodsFrontDetailVO.setGoodsDetails(getDetailsRelatedTemplate(goodsExtend));

        //从redis查询购买商品赠送多少积分
        String goodsBuy = stringRedisTemplate.opsForValue().get("integral_present_goods_buy");
        int buyGoodsIntegral = StringUtil.isEmpty(goodsBuy) ? 0 : Integer.parseInt(goodsBuy);
        //订单最多赠送积分
        String orderMax = stringRedisTemplate.opsForValue().get("integral_present_order_max");
        int orderMaxIntegral = StringUtil.isEmpty(orderMax) ? 0 : Integer.parseInt(orderMax);
        int sendIntegral = 0;
        if (buyGoodsIntegral > 0) {
            if (orderMaxIntegral > 0 && buyGoodsIntegral > orderMaxIntegral) {
                sendIntegral = orderMaxIntegral;
            } else {
                sendIntegral = buyGoodsIntegral;
            }
        }
        goodsFrontDetailVO.setIntegral(sendIntegral);
        return SldResponse.success(goodsFrontDetailVO);
    }

    @ApiOperation("获取货品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品Id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "specValueIds", value = "规格属性值Id", paramType = "query"),
    })
    @GetMapping("productInfo")
    public JsonResult<ProductInfoVO> getProductInfo(HttpServletRequest request, Long goodsId, String specValueIds) {
        //获得货品信息 包含某规格商品的库存及价格信息
        Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
        AssertUtil.notNull(goods, "商品不存在，请重试！");
        GoodsExtend goodsExtend = goodsExtendModel.getGoodsExtendByGoodsId(goodsId);
        AssertUtil.notNull(goodsExtend, "未查询到货品，请重试！");
        //查询所有有效规格
        ProductExample productExample = new ProductExample();
        productExample.setGoodsId(goodsId);
        productExample.setStateIn(GoodsConst.PRODUCT_STATE_1 + "," + GoodsConst.PRODUCT_STATE_3);
        List<Product> productList = productModel.getProductList(productExample, null);
        List<String> effectSpecValueIds = new ArrayList<>();
        productList.forEach(product1 -> effectSpecValueIds.add(product1.getSpecValueIds()));

        Product product = null;//要选中的货品信息

        //查询要选中的规格id组合
        String[] specAttrIdArr = specValueIds.split(",");
        Arrays.sort(specAttrIdArr);
        for (Product product1 : productList) {
            String[] effArr = product1.getSpecValueIds().split(",");
            Arrays.sort(effArr);
            if (Arrays.equals(effArr, specAttrIdArr)) {
                product = product1;
                break;
            }
        }
        if (product == null) {
            //未找到货品，默认第一个
            product = productList.get(0);
        }

        //无规格时specValueId取0
        Integer specValueId = 0;
        //查询主规格值列表
        //有主规格
        if (goodsExtend.getMainSpecId() != 0) {
            GoodsSpecValueExample goodsSpecValueExample = new GoodsSpecValueExample();
            goodsSpecValueExample.setSpecId(goodsExtend.getMainSpecId());
            goodsSpecValueExample.setSpecValueIdIn(product.getSpecValueIds());
            List<GoodsSpecValue> goodsSpecValueList = goodsSpecValueModel.getGoodsSpecValueList(goodsSpecValueExample, null);
            specValueId = goodsSpecValueList.get(0).getSpecValueId();
        }

        //查询对应主规格下的图片
        GoodsPictureExample goodsPictureExample = new GoodsPictureExample();
        goodsPictureExample.setGoodsId(goodsId);
        goodsPictureExample.setSpecValueId(specValueId);
        goodsPictureExample.setOrderBy("is_main asc,picture_id asc");
        List<GoodsPicture> goodsPictureList = goodsPictureModel.getGoodsPictureList(goodsPictureExample, null);

        List<GoodsFrontDetailVO.SpecVO> specs = new ArrayList<>();//规格列表
        //构造规格列表
        for (GoodsAddDTO.SpecInfo specInfo : JSONObject.parseArray(goodsExtend.getSpecJson(), GoodsAddDTO.SpecInfo.class)) {
            if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                //主规格，放在规格列表的第一位
                specs.add(0, new GoodsFrontDetailVO.SpecVO(specInfo, product.getSpecValueIds(), effectSpecValueIds));
            } else {
                specs.add(new GoodsFrontDetailVO.SpecVO(specInfo, product.getSpecValueIds(), effectSpecValueIds));
            }
        }
        Member member = UserUtil.getUser(request, Member.class);
        GoodsFrontDetailVO.DeliverInfo deliverInfo = getDeliverInfo(goodsExtend, product, member);
        GoodsPromotion goodsPromotion = goodsPromotionModel.getGoodsPromotionByProductId(product.getProductId());
        if (goodsPromotion != null && goodsPromotion.getPromotionType() == PromotionConst.PROMOTION_TYPE_104) {
            //秒杀库存
            String seckillStock = stringRedisTemplate.opsForValue().get(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + product.getProductId());
            Integer stock = seckillStock == null ? 0 : Integer.parseInt(seckillStock);
            //秒杀活动未开始（商品库存减去秒杀库存），已开始取redis库存
            if (promotionCommonModel.isPromotionAvailable(PromotionConst.PROMOTION_TYPE_104, product.getProductId().toString())) {
                //活动正在进行
                product.setProductStock(stock);
            } else {
                //活动未开始
                product.setProductStock(product.getProductStock() - stock);
            }
        }
        ProductInfoVO productInfoVO = new ProductInfoVO(goodsExtend, product, productList, goodsPictureList, deliverInfo, goodsPromotion);
        return SldResponse.success(productInfoVO);
    }

    @ApiOperation("商品评价")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品Id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "type", value = "评价等级[(好评)high,(有图)hasPic,(中评)middle,(差评)low]", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("comment")
    public JsonResult<GoodsCommentsInfoVO> commentsList(HttpServletRequest request, Long productId, String type) {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //根据productId查询商品
        Product product = productModel.getProductByProductId(productId);
        GoodsCommentsInfoVO vo = goodsCommentModel.getGoodsCommentList(product.getGoodsId(), type, pager);

        return SldResponse.success(vo);
    }

    @ApiOperation("活动列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品Id", required = true, paramType = "query")
    })
    @GetMapping("activityList")
    public JsonResult<List<GoodsPromotionVO>> activityList(HttpServletRequest request, Long productId) {
        Product product = productModel.getProductByProductId(productId);
        if (product == null) {
            return SldResponse.success(new ArrayList<>());
        }
        //商品参与的活动
        GoodsPromotionExample promotionExample = new GoodsPromotionExample();
        promotionExample.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
        promotionExample.setStartTimeBefore(new Date());
        promotionExample.setEndTimeAfter(new Date());
        //一级活动，绑定商品
        promotionExample.setBindType(PromotionConst.BIND_TYPE_1);
        promotionExample.setGoodsId(product.getGoodsId());
        List<GoodsPromotion> list = goodsPromotionModel.getGoodsPromotionList(promotionExample, null);
        //二级活动，绑定店铺
        promotionExample.setGoodsId(null);
        promotionExample.setStoreId(product.getStoreId());
        promotionExample.setBindType(PromotionConst.BIND_TYPE_2);
        list.addAll(goodsPromotionModel.getGoodsPromotionList(promotionExample, null));
        //三级活动，绑定三级分类
        promotionExample.setStoreId(null);
        promotionExample.setGoodsCategoryId3(product.getCategoryId3());
        promotionExample.setBindType(PromotionConst.BIND_TYPE_3);
        list.addAll(goodsPromotionModel.getGoodsPromotionList(promotionExample, null));
        List<GoodsPromotionVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsPromotion -> {
                GoodsPromotionVO promotionVO = new GoodsPromotionVO();
                promotionVO.setGoodsPromotionId(goodsPromotion.getGoodsPromotionId());
                promotionVO.setPromotionId(goodsPromotion.getPromotionId());
                promotionVO.setPromotionName(promotionCommonModel.getPromotionName(goodsPromotion.getPromotionType()));
                promotionVO.setDescriptionList(promotionCommonModel.getPromotionDescription(goodsPromotion.getPromotionType(), goodsPromotion.getPromotionId()));
                promotionVO.setPromotionType(goodsPromotion.getPromotionType());
                promotionVO.setPromotionGrade(goodsPromotion.getPromotionGrade());
                vos.add(promotionVO);
            });
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("计算运费")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "productId", value = "货品Id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "cityCode", value = "城市编码", required = true, paramType = "query")
    })
    @GetMapping("calculateExpress")
    public JsonResult<BigDecimal> calculateExpressFee(HttpServletRequest request, Long productId, String cityCode) {
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.notNullOrZero(productId, "货品id不能为空");
        AssertUtil.notEmpty(cityCode, "城市编码不能为空");

        CalculateExpressDTO calculateExpressDTO = new CalculateExpressDTO();
        Product product = productModel.getProductByProductId(productId);
        AssertUtil.notNull(product, "货品信息为空");
        CalculateExpressDTO.ProductInfo productInfo = new CalculateExpressDTO.ProductInfo();
        try {
            PropertyUtils.copyProperties(productInfo, product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        productInfo.setNumber(1);
        calculateExpressDTO.setCityCode(cityCode);
        calculateExpressDTO.getProductList().add(productInfo);
        BigDecimal productFee = BigDecimal.ZERO;
        productFee = goodsFreightTemplateModel.calculateExpressFee(calculateExpressDTO);
        System.out.println("productFee:" + productFee);

        return SldResponse.success(productFee);
    }


    /**
     * 构造服务标签
     *
     * @param LabelIds
     * @return
     */
    private List<GoodsFrontDetailVO.ServiceLabelVO> getLabelNames(String LabelIds) {
        if (StringUtils.isEmpty(LabelIds)) {
            return null;
        }
        GoodsLabelExample example = new GoodsLabelExample();
        example.setLabelIdIn(LabelIds);
        example.setOrderBy("sort asc, create_time desc");
        List<GoodsLabel> labels = goodsLabelModel.getGoodsLabelList(example, null);
        List<GoodsFrontDetailVO.ServiceLabelVO> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(labels)) {
            labels.forEach(goodsLabel -> list.add(new GoodsFrontDetailVO.ServiceLabelVO(goodsLabel)));
        }
        return list;
    }

    /**
     * 获取物流信息
     *
     * @param goodsExtend
     * @return DeliverInfo
     */
    private GoodsFrontDetailVO.DeliverInfo getDeliverInfo(GoodsExtend goodsExtend, Product product, Member member) {
        String cityCode = goodsExtend.getCityCode();
        GoodsFrontDetailVO.DeliverInfo deliverInfo = new GoodsFrontDetailVO.DeliverInfo();

        //查发货地
        if (!StringUtils.isEmpty(cityCode)) {
            RegionCityExample regionCityExample = new RegionCityExample();
            regionCityExample.setRemarkCode(cityCode);
            List<RegionCity> regionCityList = regionCityModel.getRegionCityList(regionCityExample, null);
            if (!CollectionUtils.isEmpty(regionCityList)) {
                deliverInfo.setCityName(regionCityList.get(0).getCityName());
            }
        }

        Integer freightId = goodsExtend.getFreightId();
        BigDecimal freightFee = goodsExtend.getFreightFee();

        //有运费模版根据运费模版计算运费
        if (freightId != null && freightId != 0) {
            //获取会员默认地址
            if (member.getMemberId() != null) {
                MemberAddressExample memberAddressExample = new MemberAddressExample();
                memberAddressExample.setMemberId(member.getMemberId());
                List<MemberAddress> memberAddressList = memberAddressModel.getMemberAddressList(memberAddressExample, null);
                if (!CollectionUtils.isEmpty(memberAddressList)) {
                    MemberAddress defaultMemberAddress = null;
                    for (MemberAddress memberAddress : memberAddressList) {
                        if (MemberConst.IS_DEFAULT_1 == (memberAddress.getIsDefault())) {
                            defaultMemberAddress = memberAddress;
                        }
                    }
                    if (defaultMemberAddress == null) {
                        defaultMemberAddress = memberAddressList.get(0);
                    }

                    //查运费
                    CalculateExpressDTO calculateExpressDTO = new CalculateExpressDTO();
                    //设置收货地城市编码
                    calculateExpressDTO.setCityCode(defaultMemberAddress.getCityCode());
                    calculateExpressDTO.getProductList()
                            .add(new CalculateExpressDTO.ProductInfo(
                                    goodsExtend.getGoodsId(),
                                    product.getProductId(),
                                    product.getProductPrice(),
                                    1));
                    BigDecimal expressFee = goodsFreightTemplateModel.calculateExpressFee(calculateExpressDTO);
                    deliverInfo.setExpressFee(expressFee);
                }
            }
            //根据运费模版获取发货时间
            GoodsFreightTemplate goodsFreightTemplate = goodsFreightTemplateModel.getGoodsFreightTemplateByFreightTemplateId(freightId);
            AssertUtil.notNull(goodsFreightTemplate, "商品运费模板有误");
            deliverInfo.setTransTime(goodsFreightTemplate.getDeliverTime());

        } else {
            deliverInfo.setExpressFee(freightFee);
        }
        return deliverInfo;
    }

    //判断用户是否收藏商品
    private Boolean checkFavorite(Integer memberId, long goodsId, long productId) {
        Boolean isFavorite = false;
        if (memberId != null) {
            MemberFollowProductExample memberFollowProductExample = new MemberFollowProductExample();
            memberFollowProductExample.setGoodsId(goodsId);
            memberFollowProductExample.setMemberId(memberId);
            memberFollowProductExample.setProductId(productId);
            List<MemberFollowProduct> memberFollowProductList = memberFollowProductModel.getMemberFollowProductList(memberFollowProductExample, null);
            if (memberFollowProductList.size() > 0) {
                isFavorite = true;
            }
        }
        return isFavorite;
    }

    //判断用户是否关注店铺
    private Boolean checkIsFollowStore(Integer memberId, Long storeId) {
        Boolean isFollowStore = false;
        if (memberId != null) {
            MemberFollowStoreExample memberFollowStoreExample = new MemberFollowStoreExample();
            memberFollowStoreExample.setMemberId(memberId);
            memberFollowStoreExample.setStoreId(storeId);
            List<MemberFollowStore> list = memberFollowStoreModel.getMemberFollowStoreList(memberFollowStoreExample, null);
            if (list.size() > 0) {
                isFollowStore = true;
            }
        }
        return isFollowStore;
    }


    //获取关联版式内容
    private String getRelatedTemplateContent(Integer templateId) {
        String templateContent = "";
        if (templateId != null && templateId != 0) {
            GoodsRelatedTemplate goodsRelatedTemplate = goodsRelatedTemplateModel.getGoodsRelatedTemplateByTemplateId(templateId);
            if (goodsRelatedTemplate != null) {
                templateContent = goodsRelatedTemplate.getTemplateContent();
            }
        }
        return templateContent;
    }

    //将详情和关联版式拼在一起
    private String getDetailsRelatedTemplate(GoodsExtend goodsExtend) {
        String details = getRelatedTemplateContent(goodsExtend.getRelatedTemplateIdTop());
        if (goodsExtend.getGoodsDetails() != null && goodsExtend.getGoodsDetails() != "") {
            details += goodsExtend.getGoodsDetails();
        }
        details += getRelatedTemplateContent(goodsExtend.getRelatedTemplateIdBottom());
        return details;
    }

}
