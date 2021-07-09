package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.GoodsPublishFrontParamDTO;
import com.slodon.b2b2c.goods.dto.GoodsPublishInsertDTO;
import com.slodon.b2b2c.goods.example.*;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.model.goods.*;
import com.slodon.b2b2c.model.seller.StoreGradeModel;
import com.slodon.b2b2c.model.seller.StoreLabelBindGoodsModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.seller.example.StoreLabelBindGoodsExample;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import com.slodon.b2b2c.seller.pojo.StoreLabelBindGoods;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.GoodsVO;
import com.slodon.b2b2c.vo.goods.ProductVO;
import com.slodon.b2b2c.vo.goods.RecommendGoodsVO;
import com.slodon.b2b2c.vo.goods.SellerGoodsDetailVO;
import io.swagger.annotations.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-商品管理")
@RestController
@RequestMapping("v3/goods/seller/goods")
public class GoodsSellerController extends BaseController {

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsExtendModel goodsExtendModel;
    @Resource
    private GoodsSellerModel goodsSellerModel;
    @Resource
    private GoodsBindLabelModel goodsBindLabelModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsPictureModel goodsPictureModel;
    @Resource
    private StoreLabelBindGoodsModel storeLabelBindGoodsModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private StoreGradeModel storeGradeModel;

    @ApiOperation("获取商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "StoreCategoryId", value = "店铺分类Id", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态:2-待审核商品；3-在售商品；4-仓库中商品；6-违规下架商品", paramType = "query"),
            @ApiImplicitParam(name = "auditState", value = "审核状态：2-待审核；4-审核拒绝(待审核商品查询使用)", paramType = "query"),
            @ApiImplicitParam(name = "goodsCode", value = "商品货号", paramType = "query"),
            @ApiImplicitParam(name = "barCode", value = "商品条形码", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsVO>> getList(HttpServletRequest request, String goodsName, Integer StoreCategoryId,
                                               Date startTime, Date endTime, Integer state, Integer auditState,
                                               String goodsCode, String barCode) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsExample example = new GoodsExample();
        example.setStoreId(vendor.getStoreId());
        example.setGoodsNameLike(goodsName);
        if (!StringUtil.isNullOrZero(StoreCategoryId)) {
            StringBuffer stringBuffer = new StringBuffer();
            StoreLabelBindGoodsExample storeLabelBindGoodsExample = new StoreLabelBindGoodsExample();
            storeLabelBindGoodsExample.setInnerLabelId(StoreCategoryId);
            List<StoreLabelBindGoods> storeLabelBindGoodsList = storeLabelBindGoodsModel.getStoreLabelBindGoodsList(storeLabelBindGoodsExample, null);
            if (!CollectionUtils.isEmpty(storeLabelBindGoodsList)) {
                for (StoreLabelBindGoods storeLabelBindGoods : storeLabelBindGoodsList) {
                    stringBuffer.append(",").append(storeLabelBindGoods.getGoodsId());
                }
                example.setGoodsIdIn(stringBuffer.toString().substring(1));
            } else {
                //任意一个不存在的值
                example.setGoodsIdIn("-1");
            }
        }
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);
        example.setProductCodeLike(goodsCode);
        example.setBarCodeLike(barCode);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == GoodsConst.GOODS_STATE_2) {
                if (!StringUtil.isNullOrZero(auditState)) {
                    if (auditState == GoodsConst.GOODS_STATE_2) {
                        example.setStateIn(GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT + ", " + GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT);
                    } else {
                        example.setState(GoodsConst.GOODS_STATE_REJECT);
                    }
                } else {
                    example.setStateIn(GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT + ", " + GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT + ", " + GoodsConst.GOODS_STATE_REJECT);
                }
            } else if (state == GoodsConst.GOODS_STATE_4) {
                example.setStateIn(GoodsConst.GOODS_STATE_WAREHOUSE_NO_AUDIT + ", " + GoodsConst.GOODS_STATE_WAREHOUSE_AUDIT_PASS + ", " + GoodsConst.GOODS_STATE_LOWER_BY_STORE);
            } else {
                example.setState(state);
            }
        } else {
            //默认显示在售商品
            example.setState(GoodsConst.GOODS_STATE_UPPER);
        }
        example.setIsDelete(GoodsConst.GOODS_IS_DELETE_NO);
        List<Goods> goodsList = goodsModel.getGoodsList(example, pager);
        //查找goods对应的goodsExtend 构造VO
        List<GoodsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            goodsList.forEach(goods -> {
                //是否达到预警值，true==是
                boolean warning = false;
                GoodsExtend goodsExtend = goodsExtendModel.getGoodsExtendByGoodsId(goods.getGoodsId());
                GoodsVO vo = new GoodsVO(goods, goodsExtend);
                //查询货品列表
                ProductExample productExample = new ProductExample();
                productExample.setGoodsId(goods.getGoodsId());
                productExample.setStateIn(GoodsConst.PRODUCT_STATE_1 + "," + GoodsConst.PRODUCT_STATE_3);
                List<Product> productList = productModel.getProductList(productExample, null);
                AssertUtil.notEmpty(productList, "商品不存在");
                List<ProductVO> productVOS = new ArrayList<>();
                for (Product product : productList) {
                    productVOS.add(new ProductVO(product));
                    if (product.getProductStock().compareTo(product.getProductStockWarning()) <= 0) {
                        warning = true;
                    }
                }
                vo.setProductList(productVOS);
                vo.setWarning(warning);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取商品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品Id", paramType = "query", required = true),
    })
    @GetMapping("getGoodsInfo")
    public JsonResult<SellerGoodsDetailVO> getGoodsInfo(HttpServletRequest request,
                                                        @RequestParam(value = "goodsId") Long goodsId) {
        Goods goods = goodsModel.getGoodsByGoodsId(goodsId);
        if (goods == null) {
            throw new MallException("商品不存在，请重试！");
        }
        GoodsExtend goodsExtend = goodsExtendModel.getGoodsExtendByGoodsId(goodsId);
        if (goodsExtend == null) {
            throw new MallException("商品不存在，请重试！");
        }
        //商品绑定标签
        GoodsBindLabelExample goodsBindLabelExample = new GoodsBindLabelExample();
        goodsBindLabelExample.setGoodsId(goodsId);
        goodsBindLabelExample.setOrderBy("bind_id asc");
        List<GoodsBindLabel> goodsBindLabelList = goodsBindLabelModel.getGoodsBindLabelList(goodsBindLabelExample, null);

        //商品绑定店铺内部分类
        StoreLabelBindGoodsExample storeLabelBindGoodsExample = new StoreLabelBindGoodsExample();
        storeLabelBindGoodsExample.setGoodsId(goodsId);
        storeLabelBindGoodsExample.setOrderBy("bind_id asc");
        List<StoreLabelBindGoods> storeLabelBindGoodsList = storeLabelBindGoodsModel.getStoreLabelBindGoodsList(storeLabelBindGoodsExample, null);

        //正常和禁用的货品
        ProductExample productExample = new ProductExample();
        productExample.setGoodsId(goodsId);
        productExample.setStateIn(GoodsConst.PRODUCT_STATE_1 + "," + GoodsConst.PRODUCT_STATE_2);
        productExample.setOrderBy("product_id asc");
        List<Product> productList = productModel.getProductList(productExample, null);

        //查询商品图片
        GoodsPictureExample goodsPictureExample = new GoodsPictureExample();
        goodsPictureExample.setGoodsId(goodsId);
        goodsPictureExample.setOrderBy("picture_id asc");
        List<GoodsPicture> goodsPictureList = goodsPictureModel.getGoodsPictureList(goodsPictureExample, null);


        SellerGoodsDetailVO sellerGoodsDetailVO = new SellerGoodsDetailVO(goods, goodsExtend, goodsBindLabelList, storeLabelBindGoodsList, goodsPictureList, productList);
        return SldResponse.success(sellerGoodsDetailVO);
    }

    @ApiOperation("商品上架")
    @VendorLogger(option = "商品上架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "商品Id集合，用逗号隔开", required = true, paramType = "query")
    })
    @PostMapping("upperShelf")
    public JsonResult<Integer> upperShelfGoods(HttpServletRequest request, String goodsIds) {
        // 验证参数是否为空
        String logMsg = "上架商品Id" + goodsIds;
        AssertUtil.notEmpty(goodsIds, "上架商品ID不能为空,请重试");
        AssertUtil.notFormatFrontIds(goodsIds, "goodsIds格式错误,请重试");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        int number = goodsSellerModel.upperShelfGoods(vendor.getStoreId(), goodsIds);
        return SldResponse.success(number + "件商品上架成功", logMsg);
    }

    @ApiOperation("商品下架")
    @VendorLogger(option = "商品下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "商品Id集合，用逗号隔开", required = true, paramType = "query")
    })
    @PostMapping("lockup")
    public JsonResult<Integer> lockupGoods(HttpServletRequest request, String goodsIds) {
        // 验证参数是否为空
        String logMsg = "下架商品Id" + goodsIds;
        AssertUtil.notEmpty(goodsIds, "下架商品ID不能为空,请重试");
        AssertUtil.notFormatFrontIds(goodsIds, "goodsIds格式错误,请重试");
        //校验商品锁定状态
        String[] goodsIdArray = goodsIds.split(",");
        for (String goodsId : goodsIdArray) {
            if (StringUtil.isEmpty(goodsId)) {
                continue;
            }
            Goods goods = goodsModel.getGoodsByGoodsId(Long.parseLong(goodsId));
            AssertUtil.notNull(goods, "商品不存在");
            AssertUtil.isTrue(goods.getIsLock() == GoodsConst.IS_LOCK_YES, "商品名称为：" + goods.getGoodsName() + "的商品已锁定，不能下架");
        }

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        int number = goodsSellerModel.lockupGoods(vendor.getStoreId(), goodsIds);

        return SldResponse.success(number + "件商品下架成功", logMsg);
    }

    @ApiOperation("商品删除")
    @VendorLogger(option = "商品删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "商品Id集合，用逗号隔开", required = true, paramType = "query")
    })
    @PostMapping("deleteGoods")
    public JsonResult<Integer> deleteGoods(HttpServletRequest request, String goodsIds) {
        // 验证参数是否为空
        String logMsg = "删除商品Id" + goodsIds;
        AssertUtil.notEmpty(goodsIds, "删除商品ID不能为空,请重试");
        AssertUtil.notFormatFrontIds(goodsIds, "goodsIds格式错误,请重试");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        int number = goodsSellerModel.deleteGoods(vendor.getStoreId(), goodsIds);
        return SldResponse.success(number + "件商品删除成功", logMsg);
    }

    @ApiOperation("是否推荐")
    @VendorLogger(option = "是否推荐")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "商品Id集合，用逗号隔开", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isRecommend", value = "是否推荐:1-设置推荐；0-取消推荐", required = true, paramType = "query")
    })
    @PostMapping("isRecommend")
    public JsonResult<Integer> isRecommend(HttpServletRequest request, String goodsIds, Integer isRecommend) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notEmpty(goodsIds, "商品ID不能为空,请重试");
        AssertUtil.notFormatFrontIds(goodsIds, "goodsIds格式错误,请重试");
        Store storeDb = storeModel.getStoreByStoreId(vendor.getStoreId());
        AssertUtil.notNull(storeDb, "店铺信息为空");
        //自营店铺不用判断
        if (storeDb.getIsOwnStore().equals(StoreConst.NO_OWN_STORE)) {
            //如果是推荐商品，必须先判断是否还能继续推荐
            if (isRecommend == GoodsConst.IS_RECOMMEND_YES) {
                //查询传入的商品是未推荐的商品数
                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setGoodsIdIn(goodsIds);
                goodsExample.setIsRecommend(GoodsConst.IS_RECOMMEND_NO);
                List<Goods> goodsList = goodsModel.getGoodsList(goodsExample, null);
                //查询该店铺已推荐的商品数
                GoodsExample goodsRecommendExample = new GoodsExample();
                goodsRecommendExample.setStoreId(vendor.getStoreId());
                goodsRecommendExample.setStateNotEquals(GoodsConst.GOODS_STATE_DELETE);
                goodsRecommendExample.setStoreIsRecommend(GoodsConst.IS_RECOMMEND_YES);
                List<Goods> goodsRecommends = goodsModel.getGoodsList(goodsRecommendExample, null);
                StoreGrade storeGradeDb = storeGradeModel.getStoreGradeByGradeId(storeDb.getStoreGradeId());
                AssertUtil.isTrue((goodsList.size() + goodsRecommends.size()) > storeGradeDb.getRecommendLimit(), "超出店铺等级限制推荐数");
            }
        }
        //修改商品店铺是否推荐
        GoodsExample example = new GoodsExample();
        example.setGoodsIdIn(goodsIds);
        Goods goods = new Goods();
        goods.setStoreIsRecommend(isRecommend);
        goods.setUpdateTime(new Date());
        goodsModel.updateGoodsByExample(goods, example);
        return SldResponse.success("设置成功");
    }

    @ApiOperation("设置关联版式")
    @VendorLogger(option = "设置关联版式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsIds", value = "商品Id集合，用逗号隔开", required = true, paramType = "query"),
            @ApiImplicitParam(name = "topTemplateId", value = "顶部关联模版ID", paramType = "query"),
            @ApiImplicitParam(name = "bottomTemplateId", value = "底部关联模版ID", paramType = "query")
    })
    @PostMapping("setRelatedTemplate")
    public JsonResult<Integer> setRelatedTemplate(HttpServletRequest request, String goodsIds, Integer topTemplateId, Integer bottomTemplateId) {
        AssertUtil.notEmpty(goodsIds, "商品ID不能为空,请重试");
        AssertUtil.notFormatFrontIds(goodsIds, "goodsIds格式错误,请重试");
        GoodsExtendExample example = new GoodsExtendExample();
        example.setGoodsIdIn(goodsIds);
        GoodsExtend goodsExtend = new GoodsExtend();
        goodsExtend.setRelatedTemplateIdTop(topTemplateId);
        goodsExtend.setRelatedTemplateIdBottom(bottomTemplateId);
        goodsExtendModel.updateGoodsExtendByExample(goodsExtend, example);
        return SldResponse.success("设置成功");
    }


    @ApiOperation("发布商品")
    @VendorLogger(option = "发布商品")
    @PostMapping("add")
    public JsonResult<Integer> addGoods(HttpServletRequest request, @RequestBody GoodsPublishFrontParamDTO paramDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        String logMsg = "发布商品名称" + paramDTO.getGoodsName();
        //参数校验
        this.publishCheck(paramDTO);
        //构造入库dto
        GoodsPublishInsertDTO insertDTO = new GoodsPublishInsertDTO(paramDTO);

        goodsSellerModel.saveGoods(vendor, insertDTO);

        return SldResponse.success("发布商品成功", logMsg);
    }

    @ApiOperation("编辑商品")
    @VendorLogger(option = "编辑商品")
    @PostMapping("edit")
    public JsonResult editGoods(HttpServletRequest request, @RequestBody GoodsPublishFrontParamDTO paramDTO) {
        AssertUtil.notNullOrZero(paramDTO.getGoodsId(), "请选择要编辑的商品");
        //查询数据库中的商品
        Goods goodsDb = goodsModel.getGoodsByGoodsId(paramDTO.getGoodsId());
        AssertUtil.notNull(goodsDb, "商品不存在");
        AssertUtil.isTrue(goodsDb.getIsLock() == GoodsConst.IS_LOCK_YES, "商品已锁定，不能编辑");
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        String logMsg = "编辑商品名称" + paramDTO.getGoodsName();
        this.publishCheck(paramDTO);
        //构造入库dto
        GoodsPublishInsertDTO insertDTO = new GoodsPublishInsertDTO(paramDTO);
        goodsSellerModel.editGoods(vendor, insertDTO, goodsDb);
        return SldResponse.success("编辑商品成功", logMsg);
    }

    /**
     * 发布编辑商品参数校验
     *
     * @param paramDTO
     */
    private void publishCheck(GoodsPublishFrontParamDTO paramDTO) {
        AssertUtil.notEmpty(paramDTO.getGoodsName(), "请填写商品名称");
        AssertUtil.notNullOrZero(paramDTO.getCategoryId3(), "请选择商品分类");
        AssertUtil.isTrue(paramDTO.getFreightFee() == null
                && StringUtil.isNullOrZero(paramDTO.getFreightId()), "请设置统一运费或选择运费模板");

        //多规格校验
        if (!CollectionUtils.isEmpty(paramDTO.getSpecInfoList())) {
            paramDTO.getSpecInfoList().forEach(specInfo -> {
                AssertUtil.notEmpty(specInfo.getSpecValueList(), "规格：" + specInfo.getSpecName() + "未添加规格值");
                if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                    //主规格，规格值图片必传
                    specInfo.getSpecValueList().forEach(specValueInfo -> {
                        AssertUtil.notEmpty(specValueInfo.getImageList(), "请上传主规格图片");
                    });
                }
            });
        }
    }

    @ApiOperation("获取聊天店铺商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("storeGoods")
    public JsonResult<PageVO<RecommendGoodsVO>> sellerGoods(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        // 店铺推荐商品
        GoodsExample example = new GoodsExample();
        example.setStoreId(vendor.getStoreId());
        example.setStoreIsRecommend(GoodsConst.IS_RECOMMEND_YES);
        example.setState(GoodsConst.GOODS_STATE_UPPER);
        example.setStoreState(GoodsConst.STORE_STATE_1);
        List<Goods> list = goodsModel.getGoodsList(example, pager);
        List<RecommendGoodsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goods -> {
                vos.add(new RecommendGoodsVO(goods));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("判断入驻店铺发布商品数是否超过等级限制的发布数")
    @GetMapping("enablePublish")
    @ApiResponses(
            @ApiResponse(code = 200, message = "data.isCanPublish:是否可以发布;1-可以发布，0-不能发布")
    )
    public JsonResult enablePublish(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        Integer isCanPublish;

        //获取店铺信息
        Store storeDb = storeModel.getStoreByStoreId(vendor.getStoreId());
        AssertUtil.notNull(storeDb, "未获取到店铺信息");
        //自营店铺不限制
        if (storeDb.getIsOwnStore().equals(StoreConst.IS_OWN_STORE)) {
            isCanPublish = 1;
        } else {
            //获取店铺等级限制的发布商品数
            StoreGrade storeGradeDb = storeGradeModel.getStoreGradeByGradeId(storeDb.getStoreGradeId());
            //获取该店铺已发布的商品数
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.setStoreId(vendor.getStoreId());
            goodsExample.setStateNotEquals(GoodsConst.GOODS_STATE_DELETE);
            List<Goods> goodsList = goodsModel.getGoodsList(goodsExample, null);
            if (goodsList.size() < storeGradeDb.getGoodsLimit()) {
                isCanPublish = 1;
            } else {
                isCanPublish = 0;
            }
        }
        return SldResponse.success(isCanPublish);
    }

}
