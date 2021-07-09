package com.slodon.b2b2c.model.goods;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.StoreConst;
import com.slodon.b2b2c.core.constant.UploadConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.FileReaderUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.seller.StoreGradeReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreInnerLabelReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreReadMapper;
import com.slodon.b2b2c.dao.write.seller.StoreLabelBindGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.seller.StoreWriteMapper;
import com.slodon.b2b2c.goods.dto.GoodsPublishFrontParamDTO;
import com.slodon.b2b2c.goods.dto.GoodsPublishInsertDTO;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.seller.example.StoreLabelBindGoodsExample;
import com.slodon.b2b2c.seller.pojo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.slodon.b2b2c.core.util.FileReaderUtil.readCsv;

/**
 * @program: slodonc
 * @Description 商户管理商品操作
 */
@Component
@Slf4j
public class GoodsSellerModel {

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsExtendModel goodsExtendModel;
    @Resource
    private GoodsPictureModel goodsPictureModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsBindLabelModel goodsBindLabelModel;
    @Resource
    private GoodsBindAttributeValueModel goodsBindAttributeValueModel;
    @Resource
    private GoodsCategoryModel goodsCategoryModel;
    @Resource
    private GoodsBrandModel goodsBrandModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private StoreReadMapper storeReadMapper;
    @Resource
    private StoreGradeReadMapper storeGradeReadMapper;
    @Resource
    private StoreInnerLabelReadMapper storeInnerLabelReadMapper;
    @Resource
    private StoreWriteMapper storeWriteMapper;
    @Resource
    private StoreLabelBindGoodsWriteMapper storeLabelBindGoodsWriteMapper;

    //region 发布商品

    /**
     * 商家发布商品
     * -校验sku是否重复
     * -保存商品信息
     * -保存商品扩展信息
     * -保存货品信息
     * -保存商品属性绑定表（检索属性）
     * -保存商品标签绑定表
     * -保存商品图片表
     * -保存商品绑定店铺内部分类表
     * -增加店铺商品数量(商品状态为上架时操作)
     *
     * @param vendor    店铺管理员
     * @param insertDTO 商品信息
     */
    @Transactional
    public void saveGoods(Vendor vendor, GoodsPublishInsertDTO insertDTO) {
        List<GoodsPublishFrontParamDTO.ProductInfo> productList = insertDTO.getProductList();//货品列表
        //-校验sku是否重复
        this.checkSku(productList);

        //-保存商品信息
        Goods goods = this.insertGoods(vendor, insertDTO);

        //-保存商品扩展信息
        this.insertGoodsExtend(insertDTO, goods, vendor);

        //-保存货品信息
        this.insertProduct(productList, goods);

        //-保存商品属性绑定表（检索属性）
        this.insertGoodsBindAttribute(insertDTO.getAttributeAndParameter(), goods.getGoodsId());

        //-保存商品标签绑定表
        this.insertGoodsBindLabels(insertDTO.getGoodsLabelList(), goods.getGoodsId());

        //-保存商品图片表
        this.insertGoodsPicture(insertDTO.getSpecInfoList(), insertDTO.getImageList(), goods, vendor);

        //-保存商品绑定店铺内部分类表
        this.insertGoodsBindStoreInnerLabels(insertDTO.getStoreInnerLabelList(), goods.getGoodsId(), vendor);

        //-增加店铺商品数量(商品状态为上架时操作)
        this.addStoreGoodsNum(vendor.getStoreId(), goods.getState());
    }

    /**
     * 校验sku是否重复
     *
     * @param productList
     */
    private void checkSku(List<GoodsPublishFrontParamDTO.ProductInfo> productList) {
        Set<String> set = new HashSet<>();//用于校验自身sku是否重复
        productList.forEach(productInfo -> {
            if (!StringUtils.isEmpty(productInfo.getProductCode())) {
                AssertUtil.isTrue(!set.add(productInfo.getProductCode()), "商品货号重复");
                ProductExample example = new ProductExample();
                example.setProductCode(productInfo.getProductCode());
                example.setProductIdNotEquals(((GoodsPublishInsertDTO.ProductInsertInfo) productInfo).getProductId());
                int count = productModel.countByExample(example);
                AssertUtil.isTrue(count > 0, "商品货号[" + productInfo.getProductCode() + "]已存在，请重新输入");
            }
        });
    }

    /**
     * 发布商品-插入商品
     *
     * @param vendor    店铺管理员
     * @param insertDTO 商品信息
     * @return
     */
    private Goods insertGoods(Vendor vendor, GoodsPublishInsertDTO insertDTO) {
        long goodsId = GoodsIdGenerator.goodsIdGenerator();//商品id
        long defaultProductId = GoodsIdGenerator.productIdGenerator();//生成一个默认货品id
        GoodsPublishInsertDTO.ProductInsertInfo defaultProduct = insertDTO.getDefaultProduct();//默认货品

        //查询分类信息
        GoodsCategory goodsCategory3 = goodsCategoryModel.getGoodsCategoryByCategoryId(insertDTO.getCategoryId3());
        AssertUtil.notNull(goodsCategory3, "分类有误");
        GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(goodsCategory3.getPid());
        GoodsCategory goodsCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(goodsCategory2.getPid());

        Goods goods = new Goods();
        goods.setGoodsId(goodsId);
        goods.setGoodsName(insertDTO.getGoodsName());
        goods.setGoodsBrief(insertDTO.getGoodsBrief());
        if (!StringUtil.isNullOrZero(insertDTO.getBrandId())) {
            //查询品牌信息
            GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(insertDTO.getBrandId());
            AssertUtil.notNull(goodsBrand, "品牌有误");
            goods.setBrandId(insertDTO.getBrandId());
            goods.setBrandName(goodsBrand.getBrandName());
        }
        Store storeDb = storeReadMapper.getByPrimaryKey(vendor.getStoreId());
        if (storeDb.getIsOwnStore().equals(StoreConst.NO_OWN_STORE)) {
            //判断发布的商品是否还能被推荐
            if (insertDTO.getStoreIsRecommend() == 1) {
                //查询该店铺已推荐的商品数
                GoodsExample goodsRecommendExample = new GoodsExample();
                goodsRecommendExample.setStoreId(vendor.getStoreId());
                goodsRecommendExample.setStateNotEquals(GoodsConst.GOODS_STATE_DELETE);
                goodsRecommendExample.setStoreIsRecommend(GoodsConst.IS_RECOMMEND_YES);
                List<Goods> goodsRecommends = goodsModel.getGoodsList(goodsRecommendExample, null);
                //获取该店铺推荐限制数
                StoreGrade storeGradeDb = storeGradeReadMapper.getByPrimaryKey(storeDb.getStoreGradeId());
                AssertUtil.isTrue(goodsRecommends.size() >= storeGradeDb.getRecommendLimit(), "店铺已达到推荐数限制，不能设为推荐");
            }
        }
        goods.setCategoryId1(goodsCategory1.getCategoryId());
        goods.setCategoryId2(goodsCategory2.getCategoryId());
        goods.setCategoryId3(goodsCategory3.getCategoryId());
        goods.setCategoryPath(goodsCategory1.getCategoryName() + "->" + goodsCategory2.getCategoryName() + "->" + goodsCategory3.getCategoryName());
        goods.setMarketPrice(defaultProduct.getMarketPrice());
        goods.setGoodsPrice(defaultProduct.getProductPrice());
        goods.setGoodsStock(defaultProduct.getProductStock());
        goods.setVirtualSales(insertDTO.getVirtualSales());
        goods.setIsSpec(CollectionUtils.isEmpty(insertDTO.getSpecInfoList()) ? GoodsConst.IS_SPEC_NO : GoodsConst.IS_SPEC_YES);
        goods.setMainSpecId(insertDTO.getMainSpecId());
        goods.setIsSelf(vendor.getStore().getIsOwnStore());
        goods.setState(this.dealGoodsState(insertDTO.getSellNow()));
        goods.setOnlineTime(goods.getState().equals(GoodsConst.GOODS_STATE_UPPER) ? new Date() : null);
        goods.setStoreId(vendor.getStoreId());
        goods.setStoreName(vendor.getStore().getStoreName());
        goods.setStoreIsRecommend(insertDTO.getStoreIsRecommend());
        goods.setMainImage(defaultProduct.getMainImage());
        goods.setGoodsVideo(insertDTO.getGoodsVideo());
        goods.setBarCode(defaultProduct.getBarCode());
        goods.setServiceLabelIds(insertDTO.getGoodsLabelIds());
        goods.setDefaultProductId(defaultProductId);
        goods.setIsVatInvoice(insertDTO.getIsVatInvoice());
        goods.setCreateTime(new Date());
        goods.setUpdateTime(new Date());
        goodsModel.saveGoods(goods);
        return goods;
    }

    /**
     * 发布商品-插入商品扩展表
     *
     * @param insertDTO
     * @param goods
     * @param vendor
     */
    private void insertGoodsExtend(GoodsPublishInsertDTO insertDTO, Goods goods, Vendor vendor) {
        GoodsExtend goodsExtend = new GoodsExtend();
        goodsExtend.setExtendId(GoodsIdGenerator.goodsExtendIdGenerator());
        goodsExtend.setGoodsId(goods.getGoodsId());
        goodsExtend.setMainSpecId(goods.getMainSpecId());
        if (!CollectionUtils.isEmpty(insertDTO.getSpecInfoList())) {
            goodsExtend.setSpecJson(JSON.toJSONString(insertDTO.getSpecInfoList()));
        }
        if (insertDTO.getAttributeAndParameter() != null) {
            if (!CollectionUtils.isEmpty(insertDTO.getAttributeAndParameter().getAttributeList())) {
                //检索属性
                goodsExtend.setAttributeJson(JSON.toJSONString(insertDTO.getAttributeAndParameter().getAttributeList()));
            }
            if (insertDTO.getAttributeAndParameter().getParameterGroup() != null) {
                //自定义参数
                goodsExtend.setGoodsParameter(JSON.toJSONString(insertDTO.getAttributeAndParameter().getParameterGroup()));
            }
        }
        goodsExtend.setOnlineTime(goods.getOnlineTime());
        goodsExtend.setCreateVendorId(vendor.getVendorId());
        goodsExtend.setCreateTime(new Date());
        goodsExtend.setProvinceCode(insertDTO.getProvinceCode());
        goodsExtend.setCityCode(insertDTO.getCityCode());
        goodsExtend.setFreightId(insertDTO.getFreightId());
        goodsExtend.setFreightFee(insertDTO.getFreightFee());
        goodsExtend.setRelatedTemplateIdTop(insertDTO.getRelatedTemplateIdTop());
        goodsExtend.setRelatedTemplateIdBottom(insertDTO.getRelatedTemplateIdBottom());
        goodsExtend.setGoodsDetails(insertDTO.getGoodsDetails());
        goodsExtendModel.saveGoodsExtend(goodsExtend);
    }


    /**
     * 发布商品-插入货品信息
     *
     * @param productList 货品列表
     * @param goods       商品信息
     */
    private void insertProduct(List<GoodsPublishFrontParamDTO.ProductInfo> productList, Goods goods) {
        productList.forEach(productInfo -> {
            GoodsPublishInsertDTO.ProductInsertInfo productInsertInfo = (GoodsPublishInsertDTO.ProductInsertInfo) productInfo;
            this.doInsertProduct(goods, productInsertInfo);
        });
    }

    /**
     * 执行插入货品操作
     *
     * @param goods
     * @param productInsertInfo
     */
    private void doInsertProduct(Goods goods, GoodsPublishInsertDTO.ProductInsertInfo productInsertInfo) {
        Product product = new Product();
        if (productInsertInfo.getIsDefault() == GoodsConst.PRODUCT_IS_DEFAULT_YES) {
            //默认货品
            product.setProductId(goods.getDefaultProductId());
        } else {
            //非默认货品，生成货品id
            product.setProductId(GoodsIdGenerator.productIdGenerator());
        }
        product.setGoodsId(goods.getGoodsId());
        product.setGoodsName(goods.getGoodsName());
        product.setSpecValues(productInsertInfo.getSpecValues());
        product.setSpecValueIds(productInsertInfo.getSpecValueIds());
        product.setBrandId(goods.getBrandId());
        product.setBrandName(goods.getBrandName());
        product.setStoreId(goods.getStoreId());
        product.setStoreName(goods.getStoreName());
        product.setCategoryId1(goods.getCategoryId1());
        product.setCategoryId2(goods.getCategoryId2());
        product.setCategoryId3(goods.getCategoryId3());
        product.setCategoryPath(goods.getCategoryPath());
        product.setProductPrice(productInsertInfo.getProductPrice());
        product.setMarketPrice(productInsertInfo.getMarketPrice());
        product.setProductStock(productInsertInfo.getProductStock());
        product.setProductStockWarning(productInsertInfo.getProductStockWarning());
        product.setProductCode(productInsertInfo.getProductCode());
        product.setBarCode(productInsertInfo.getBarCode());
        product.setWeight(productInsertInfo.getWeight());
        product.setLength(productInsertInfo.getLength());
        product.setWidth(productInsertInfo.getWidth());
        product.setHeight(productInsertInfo.getHeight());
        product.setMainImage(productInsertInfo.getMainImage());
        product.setState(productInsertInfo.getState());
        product.setIsDefault(productInsertInfo.getIsDefault());
        productModel.saveProduct(product);
    }

    /**
     * 发布商品-保存商品属性绑定表（检索属性）
     *
     * @param attributeAndParameter
     * @param goodsId
     */
    private void insertGoodsBindAttribute(GoodsPublishFrontParamDTO.AttributeAndParameter attributeAndParameter, Long goodsId) {
        if (attributeAndParameter == null) {
            return;
        }
        if (CollectionUtils.isEmpty(attributeAndParameter.getAttributeList())) {
            return;
        }
        attributeAndParameter.getAttributeList().forEach(attribute -> {
            GoodsBindAttributeValue goodsBindAttributeValue = new GoodsBindAttributeValue();
            goodsBindAttributeValue.setGoodsId(goodsId);
            goodsBindAttributeValue.setAttributeId(attribute.getAttributeId());
            goodsBindAttributeValue.setAttributeName(attribute.getAttributeName());
            goodsBindAttributeValue.setAttributeValueId(attribute.getAttributeValueId());
            goodsBindAttributeValue.setAttributeValue(attribute.getAttributeValue());
            goodsBindAttributeValueModel.saveGoodsBindAttributeValue(goodsBindAttributeValue);
        });
    }

    /**
     * 发布商品-保存商品标签绑定表
     *
     * @param labelInfoList
     * @param goodsId
     */
    private void insertGoodsBindLabels(List<GoodsPublishFrontParamDTO.GoodsLabelInfo> labelInfoList, Long goodsId) {
        if (CollectionUtils.isEmpty(labelInfoList)) {
            return;
        }
        labelInfoList.forEach(goodsLabelInfo -> {
            GoodsBindLabel goodsBindLabel = new GoodsBindLabel();
            goodsBindLabel.setGoodsId(goodsId);
            goodsBindLabel.setLabelId(goodsLabelInfo.getGoodsLabelId());
            goodsBindLabel.setLabelName(goodsLabelInfo.getGoodsLabelName());
            goodsBindLabel.setCreateTime(new Date());
            goodsBindLabelModel.saveGoodsBindLabel(goodsBindLabel);
        });
    }

    /**
     * 发布商品-保存商品图片
     *
     * @param specInfoList
     * @param imageInfoList
     * @param goods
     */
    private void insertGoodsPicture(List<GoodsPublishFrontParamDTO.SpecInfo> specInfoList, List<GoodsPublishFrontParamDTO.ImageInfo> imageInfoList, Goods goods, Vendor vendor) {
        if (StringUtil.isNullOrZero(goods.getMainSpecId())) {
            //没有主规格
            imageInfoList.forEach(imageInfo -> {
                this.insertPicture(imageInfo, goods.getGoodsId(), vendor, 0);
            });
        } else {
            //有主规格
            specInfoList.forEach(specInfo -> {
                if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                    specInfo.getSpecValueList().forEach(specValueInfo -> {
                        specValueInfo.getImageList().forEach(imageInfo -> {
                            this.insertPicture(imageInfo, goods.getGoodsId(), vendor, specValueInfo.getSpecValueId());
                        });
                    });
                }
            });
        }
    }

    /**
     * 保存图片
     *
     * @param imageInfo
     * @param goodsId
     * @param vendor
     */
    private void insertPicture(GoodsPublishFrontParamDTO.ImageInfo imageInfo, Long goodsId, Vendor vendor, Integer specValueId) {
        GoodsPicture goodsPicture = new GoodsPicture();
        goodsPicture.setGoodsId(goodsId);
        goodsPicture.setImagePath(imageInfo.getImage());
        goodsPicture.setSort(1);
        goodsPicture.setCreateId(vendor.getVendorId());
        goodsPicture.setCreateTime(new Date());
        goodsPicture.setStoreId(vendor.getStoreId());
        goodsPicture.setIsMain(imageInfo.getIsMain());
        goodsPicture.setSpecValueId(specValueId);
        goodsPictureModel.saveGoodsPicture(goodsPicture);
    }

    /**
     * 发布商品-保存商品绑定店铺内部分类表
     *
     * @param storeInnerLabelList
     * @param goodsId
     * @param vendor
     */
    private void insertGoodsBindStoreInnerLabels(List<GoodsPublishFrontParamDTO.StoreInnerLabelInfo> storeInnerLabelList, Long goodsId, Vendor vendor) {
        if (CollectionUtils.isEmpty(storeInnerLabelList)) {
            return;
        }
        storeInnerLabelList.forEach(storeInnerLabelInfo -> {
            StoreLabelBindGoods storeLabelBindGoods = new StoreLabelBindGoods();
            storeLabelBindGoods.setInnerLabelId(storeInnerLabelInfo.getInnerLabelId());
            storeLabelBindGoods.setInnerLabelName(storeInnerLabelInfo.getInnerLabelName());
            storeLabelBindGoods.setGoodsId(goodsId);
            storeLabelBindGoods.setStoreId(vendor.getStoreId());
            storeLabelBindGoods.setBindTime(new Date());
            storeLabelBindGoods.setBindVendorId(vendor.getVendorId());
            storeLabelBindGoods.setBindVendorName(vendor.getVendorName());
            storeLabelBindGoodsWriteMapper.insert(storeLabelBindGoods);
        });
    }

    /**
     * 增加店铺商品数量(商品状态为上架时操作)
     *
     * @param storeId
     * @param goodsState
     */
    private void addStoreGoodsNum(Long storeId, Integer goodsState) {
        if (!goodsState.equals(GoodsConst.GOODS_STATE_UPPER)) {
            //非上架状态，不处理
            return;
        }
        Store storeDb = storeReadMapper.getByPrimaryKey(storeId);
        Store update = new Store();
        update.setStoreId(storeId);
        update.setGoodsNumber(storeDb.getGoodsNumber() + 1);
        storeWriteMapper.updateByPrimaryKeySelective(update);
    }

    /**
     * 处理商品状态
     *
     * @param sellNow 是否立即售卖，true==是，false==放入仓库
     * @return
     */
    private Integer dealGoodsState(Boolean sellNow) {
        //查询系统配置，是否需要审核，true==需要审核
        boolean needAudit = GoodsConst.GOODS_PUBLISH_NEED_AUDIT_YES.equals(stringRedisTemplate.opsForValue().get("goods_publish_need_audit"));
        if (sellNow) {
            //立即售卖
            return needAudit ? GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT : GoodsConst.GOODS_STATE_UPPER;
        } else {
            //放入仓库
            return needAudit ? GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT : GoodsConst.GOODS_STATE_WAREHOUSE_NO_AUDIT;
        }
    }
    //endregion

    //region 编辑商品

    /**
     * 编辑商品
     * -对比货品列表信息，构造要增、删、改的货品列表
     * -校验sku是否重复
     * -更新商品信息
     * -更新商品扩展信息
     * -更新货品信息
     * -保存新的商品属性绑定表（检索属性）
     * -保存新的商品标签绑定表
     * -保存新的商品图片表
     * -保存商品绑定店铺内部分类表
     * -增加/减少店铺商品数量(商品状态为上架时操作)
     *
     * @param vendor
     * @param insertDTO
     * @param goodsDb
     */
    @Transactional
    public void editGoods(Vendor vendor, GoodsPublishInsertDTO insertDTO, Goods goodsDb) {
        List<GoodsPublishFrontParamDTO.ProductInfo> productList = insertDTO.getProductList();//前端传来的货品列表
        List<GoodsPublishInsertDTO.ProductInsertInfo> addProductList = new ArrayList<>();//要新增的货品
        List<GoodsPublishInsertDTO.ProductInsertInfo> updateProductList = new ArrayList<>();//要删除的货品
        List<Product> deleteProductList = new ArrayList<>();//要删除的货品（伪删除）

        //-对比货品列表信息，构造要增、删、改的货品列表
        this.compareSpecWithDb(goodsDb.getGoodsId(), productList, addProductList, updateProductList, deleteProductList);

        //-校验sku是否重复
        this.checkSku(productList);

        //-更新商品信息
        Goods updateGoods = this.updateGoods(insertDTO, vendor);

        //-更新商品扩展信息
        this.updateGoodsExtend(insertDTO, updateGoods);

        //-更新货品信息
        this.updateProduct(updateGoods, addProductList, updateProductList, deleteProductList);

        //-保存新的商品属性绑定表（检索属性）
        this.updateGoodsBindAttribute(insertDTO.getAttributeAndParameter(), updateGoods.getGoodsId());

        //-保存新的商品标签绑定表
        this.updateGoodsBindLabel(insertDTO.getGoodsLabelList(), updateGoods.getGoodsId());

        //-保存新的商品图片表
        this.updateGoodsPicture(insertDTO.getSpecInfoList(), insertDTO.getImageList(), updateGoods, vendor);

        //-保存商品绑定店铺内部分类表
        this.updateGoodsBindStoreInnerLabels(insertDTO.getStoreInnerLabelList(), updateGoods.getGoodsId(), vendor);

        //-增加/减少店铺商品数量(商品状态为上架时操作)
        this.updateStoreGoodsNum(goodsDb.getState(), updateGoods.getState(), vendor.getStoreId());
    }

    /**
     * 编辑后的货品列表与数据库中的货品列表做规格比对，构造新增、编辑、删除操作的货品列表
     *
     * @param goodsId           商品id
     * @param paramProductList  前端传来的货品列表
     * @param addProductList    最终要新增的货品
     * @param updateProductList 最终要编辑的货品
     * @param deleteProductList 最终要伪删除的货品
     */
    private void compareSpecWithDb(Long goodsId, List<GoodsPublishFrontParamDTO.ProductInfo> paramProductList,
                                   List<GoodsPublishInsertDTO.ProductInsertInfo> addProductList,
                                   List<GoodsPublishInsertDTO.ProductInsertInfo> updateProductList,
                                   List<Product> deleteProductList) {
        //数据库中的货品列表
        ProductExample productExample = new ProductExample();
        productExample.setGoodsId(goodsId);
        List<Product> dbProductList = productModel.getProductList(productExample, null);
        AssertUtil.notEmpty(dbProductList, "商品信息有误");

        //构造新增和更新的货品
        paramProductList.forEach(paramProduct -> {
            GoodsPublishInsertDTO.ProductInsertInfo paramInsertProduct = (GoodsPublishInsertDTO.ProductInsertInfo) paramProduct;
            boolean compared = false;//数据库中是否有此货品
            for (Product dbProduct : dbProductList) {
                if (compareSpec(paramInsertProduct, dbProduct)) {
                    //匹配成功，更新此货品
                    paramInsertProduct.setProductId(dbProduct.getProductId());
                    updateProductList.add(paramInsertProduct);
                    compared = true;
                    break;
                }
            }
            if (!compared) {
                //未匹配成功，添加此货品
                addProductList.add(paramInsertProduct);
            }
        });

        //构造要删除的列表
        dbProductList.forEach(dbProduct -> {
            boolean delete = true;//是否要删除
            for (GoodsPublishInsertDTO.ProductInsertInfo updateProduct : updateProductList) {
                if (dbProduct.getProductId().equals(updateProduct.getProductId())) {
                    //匹配成功，不需要删除
                    delete = false;
                    break;
                }
            }
            if (delete) {
                Product deleteProduct = new Product();
                deleteProduct.setProductId(dbProduct.getProductId());
                deleteProduct.setState(GoodsConst.PRODUCT_STATE_0);//删除状态
                deleteProduct.setIsDefault(GoodsConst.PRODUCT_IS_DEFAULT_NO);//给为非默认
                deleteProduct.setProductCode("");//清除sku code
                deleteProductList.add(deleteProduct);
            }
        });

    }

    /**
     * 前端货品与数据或货品比对
     *
     * @param paramProduct
     * @param dbProduct
     * @return
     */
    private boolean compareSpec(GoodsPublishInsertDTO.ProductInsertInfo paramProduct, Product dbProduct) {
        String s1 = paramProduct.getSpecValueIds();
        String s2 = dbProduct.getSpecValueIds();
        String[] arr1 = s1 == null ? "".split(",") : s1.split(",");//数组元素的格式为  规格id:规格值id
        String[] arr2 = s2 == null ? "".split(",") : s2.split(",");
        Arrays.sort(arr1);
        Arrays.sort(arr2);
        return Arrays.equals(arr1, arr2);
    }

    /**
     * 编辑商品-更新商品信息
     *
     * @param insertDTO
     * @param vendor
     * @return
     */
    private Goods updateGoods(GoodsPublishInsertDTO insertDTO, Vendor vendor) {
        GoodsPublishInsertDTO.ProductInsertInfo defaultProduct = insertDTO.getDefaultProduct();
        //构造默认货品id
        long defaultProductId = StringUtil.isNullOrZero(defaultProduct.getProductId())
                ? GoodsIdGenerator.productIdGenerator() : defaultProduct.getProductId();
        //查询分类信息
        GoodsCategory goodsCategory3 = goodsCategoryModel.getGoodsCategoryByCategoryId(insertDTO.getCategoryId3());
        AssertUtil.notNull(goodsCategory3, "分类有误");
        GoodsCategory goodsCategory2 = goodsCategoryModel.getGoodsCategoryByCategoryId(goodsCategory3.getPid());
        GoodsCategory goodsCategory1 = goodsCategoryModel.getGoodsCategoryByCategoryId(goodsCategory2.getPid());

        Goods goods = new Goods();
        goods.setGoodsId(insertDTO.getGoodsId());
        goods.setGoodsName(insertDTO.getGoodsName());
        goods.setGoodsBrief(StringUtils.isEmpty(insertDTO.getGoodsBrief()) ? "" : insertDTO.getGoodsBrief());//为传商品广告时，要清除原数据
        if (!StringUtil.isNullOrZero(insertDTO.getBrandId())) {
            //查询品牌信息
            GoodsBrand goodsBrand = goodsBrandModel.getGoodsBrandByBrandId(insertDTO.getBrandId());
            AssertUtil.notNull(goodsBrand, "品牌有误");
            goods.setBrandId(insertDTO.getBrandId());
            goods.setBrandName(goodsBrand.getBrandName());
        } else {
            //未选择品牌，要把之前的品牌信息清除
            goods.setBrandId(0);
            goods.setBrandName("");
        }
        Store storeDb = storeReadMapper.getByPrimaryKey(vendor.getStoreId());
        AssertUtil.notNull(storeDb, "未获取到店铺信息");
        if (storeDb.getIsOwnStore().equals(StoreConst.NO_OWN_STORE)) {
            //判断编辑的商品是否还能被推荐
            if (insertDTO.getStoreIsRecommend() == 1) {
                //查询该店铺已推荐的商品数
                GoodsExample goodsRecommendExample = new GoodsExample();
                goodsRecommendExample.setStoreId(vendor.getStoreId());
                goodsRecommendExample.setStateNotEquals(GoodsConst.GOODS_STATE_DELETE);
                goodsRecommendExample.setStoreIsRecommend(GoodsConst.IS_RECOMMEND_YES);
                List<Goods> goodsRecommends = goodsModel.getGoodsList(goodsRecommendExample, null);
                //获取该店铺推荐限制数
                StoreGrade storeGradeDb = storeGradeReadMapper.getByPrimaryKey(storeDb.getStoreGradeId());
                AssertUtil.isTrue(goodsRecommends.size() >= storeGradeDb.getRecommendLimit(), "店铺已达到推荐数限制，不能设为推荐");
            }
        }
        goods.setCategoryId1(goodsCategory1.getCategoryId());
        goods.setCategoryId2(goodsCategory2.getCategoryId());
        goods.setCategoryId3(goodsCategory3.getCategoryId());
        goods.setCategoryPath(goodsCategory1.getCategoryName() + "->" + goodsCategory2.getCategoryName() + "->" + goodsCategory3.getCategoryName());
        goods.setMarketPrice(StringUtil.isNullOrZero(defaultProduct.getMarketPrice()) ? new BigDecimal("0.00") : defaultProduct.getMarketPrice());
        goods.setGoodsStock(defaultProduct.getProductStock());
        goods.setGoodsPrice(defaultProduct.getProductPrice());
        goods.setIsSpec(CollectionUtils.isEmpty(insertDTO.getSpecInfoList()) ? GoodsConst.IS_SPEC_NO : GoodsConst.IS_SPEC_YES);
        goods.setMainSpecId(insertDTO.getMainSpecId());
        goods.setVirtualSales(insertDTO.getVirtualSales());
        goods.setState(this.dealGoodsState(insertDTO.getSellNow()));
        goods.setIsSelf(vendor.getStore().getIsOwnStore());
        goods.setOnlineTime(goods.getState().equals(GoodsConst.GOODS_STATE_UPPER) ? new Date() : null);
        goods.setStoreId(vendor.getStoreId());
        goods.setStoreName(vendor.getStore().getStoreName());
        goods.setStoreIsRecommend(insertDTO.getStoreIsRecommend());
        goods.setMainImage(defaultProduct.getMainImage());
        goods.setGoodsVideo(StringUtils.isEmpty(insertDTO.getGoodsVideo()) ? "" : insertDTO.getGoodsVideo());
        goods.setBarCode(StringUtils.isEmpty(defaultProduct.getBarCode()) ? "" : defaultProduct.getBarCode());
        goods.setServiceLabelIds(StringUtils.isEmpty(insertDTO.getGoodsLabelIds()) ? "" : insertDTO.getGoodsLabelIds());
        goods.setDefaultProductId(defaultProductId);
        goods.setIsVatInvoice(insertDTO.getIsVatInvoice());
        goods.setUpdateTime(new Date());
        goodsModel.updateGoods(goods);
        return goods;
    }

    /**
     * 编辑商品-更新商品扩展表
     *
     * @param insertDTO
     * @param updateGoods
     */
    private void updateGoodsExtend(GoodsPublishInsertDTO insertDTO, Goods updateGoods) {
        GoodsExtend goodsExtend = new GoodsExtend();
        goodsExtend.setMainSpecId(updateGoods.getMainSpecId());
        if (!CollectionUtils.isEmpty(insertDTO.getSpecInfoList())) {
            goodsExtend.setSpecJson(JSON.toJSONString(insertDTO.getSpecInfoList()));
        } else {
            goodsExtend.setSpecJson("");
        }
        if (insertDTO.getAttributeAndParameter() != null) {
            if (!CollectionUtils.isEmpty(insertDTO.getAttributeAndParameter().getAttributeList())) {
                //检索属性
                goodsExtend.setAttributeJson(JSON.toJSONString(insertDTO.getAttributeAndParameter().getAttributeList()));
            } else {
                goodsExtend.setAttributeJson("");//未选择属性时，清除原有属性信息
            }
            if (insertDTO.getAttributeAndParameter().getParameterGroup() != null) {
                //自定义参数
                goodsExtend.setGoodsParameter(JSON.toJSONString(insertDTO.getAttributeAndParameter().getParameterGroup()));
            } else {
                goodsExtend.setGoodsParameter("");
            }
        } else {
            goodsExtend.setAttributeJson("");
            goodsExtend.setGoodsParameter("");
        }
        goodsExtend.setOnlineTime(updateGoods.getOnlineTime());
        goodsExtend.setProvinceCode(insertDTO.getProvinceCode());
        goodsExtend.setCityCode(insertDTO.getCityCode());
        goodsExtend.setFreightId(insertDTO.getFreightId());
        goodsExtend.setFreightFee(insertDTO.getFreightFee());
        goodsExtend.setRelatedTemplateIdTop(StringUtil.isNullOrZero(insertDTO.getRelatedTemplateIdTop()) ? 0 : insertDTO.getRelatedTemplateIdTop());
        goodsExtend.setRelatedTemplateIdBottom(StringUtil.isNullOrZero(insertDTO.getRelatedTemplateIdBottom()) ? 0 : insertDTO.getRelatedTemplateIdBottom());
        goodsExtend.setGoodsDetails(StringUtils.isEmpty(insertDTO.getGoodsDetails()) ? "" : insertDTO.getGoodsDetails());

        GoodsExtendExample extendExample = new GoodsExtendExample();
        extendExample.setGoodsId(updateGoods.getGoodsId());
        Integer count = goodsExtendModel.updateGoodsExtendByExample(goodsExtend, extendExample);
        AssertUtil.isTrue(count == 0, "更新商品扩展信息失败");
    }

    /**
     * 编辑商品-更新货品信息
     *
     * @param updateGoods
     * @param addProductList
     * @param updateProductList
     * @param deleteProductList
     */
    private void updateProduct(Goods updateGoods, List<GoodsPublishInsertDTO.ProductInsertInfo> addProductList,
                               List<GoodsPublishInsertDTO.ProductInsertInfo> updateProductList,
                               List<Product> deleteProductList) {
        //新增
        this.updateProductAdd(updateGoods, addProductList);
        //编辑
        this.updateProductUpdate(updateGoods, updateProductList);
        //删除
        this.updateProductDelete(deleteProductList);
    }

    /**
     * 编辑商品-更新货品信息-新增货品
     *
     * @param updateGoods
     * @param addProductList
     */
    private void updateProductAdd(Goods updateGoods, List<GoodsPublishInsertDTO.ProductInsertInfo> addProductList) {
        if (CollectionUtils.isEmpty(addProductList)) {
            return;
        }
        addProductList.forEach(productInsertInfo -> {
            this.doInsertProduct(updateGoods, productInsertInfo);
        });
    }

    /**
     * 编辑商品-更新货品信息-更新货品
     *
     * @param updateGoods
     * @param updateProductList
     */
    private void updateProductUpdate(Goods updateGoods, List<GoodsPublishInsertDTO.ProductInsertInfo> updateProductList) {
        if (CollectionUtils.isEmpty(updateProductList)) return;
        updateProductList.forEach(productInsertInfo -> {
            Product product = new Product();
            product.setProductId(productInsertInfo.getProductId());
            product.setGoodsName(updateGoods.getGoodsName());
            product.setSpecValues(productInsertInfo.getSpecValues());
            product.setSpecValueIds(productInsertInfo.getSpecValueIds());
            product.setBrandId(StringUtil.isNullOrZero(updateGoods.getBrandId()) ? 0 : updateGoods.getBrandId());
            product.setBrandName(StringUtil.isNullOrZero(updateGoods.getBrandId()) ? "" : updateGoods.getBrandName());
            product.setCategoryId1(updateGoods.getCategoryId1());
            product.setCategoryId2(updateGoods.getCategoryId2());
            product.setCategoryId3(updateGoods.getCategoryId3());
            product.setCategoryPath(updateGoods.getCategoryPath());
            product.setProductPrice(productInsertInfo.getProductPrice());
            product.setMarketPrice(StringUtil.isNullOrZero(productInsertInfo.getMarketPrice()) ? new BigDecimal("0.00") : productInsertInfo.getMarketPrice());
            product.setProductStock(productInsertInfo.getProductStock());
            product.setProductStockWarning(productInsertInfo.getProductStockWarning());
            product.setProductCode(StringUtils.isEmpty(productInsertInfo.getProductCode()) ? "" : productInsertInfo.getProductCode());
            product.setBarCode(StringUtils.isEmpty(productInsertInfo.getBarCode()) ? "" : productInsertInfo.getBarCode());
            product.setWeight(StringUtil.isNullOrZero(productInsertInfo.getWeight()) ? BigDecimal.ZERO : productInsertInfo.getWeight());
            product.setLength(StringUtil.isNullOrZero(productInsertInfo.getLength()) ? BigDecimal.ZERO : productInsertInfo.getLength());
            product.setWidth(StringUtil.isNullOrZero(productInsertInfo.getWidth()) ? BigDecimal.ZERO : productInsertInfo.getWidth());
            product.setHeight(StringUtil.isNullOrZero(productInsertInfo.getHeight()) ? BigDecimal.ZERO : productInsertInfo.getHeight());
            product.setMainImage(productInsertInfo.getMainImage());
            product.setState(productInsertInfo.getState());
            product.setIsDefault(productInsertInfo.getIsDefault());
            productModel.updateProduct(product);
        });
    }

    /**
     * 编辑商品-更新货品信息-删除货品
     *
     * @param deleteProductList
     */
    private void updateProductDelete(List<Product> deleteProductList) {
        if (CollectionUtils.isEmpty(deleteProductList)) return;
        deleteProductList.forEach(product -> {
            productModel.updateProduct(product);
        });
    }

    /**
     * 编辑商品-更新商品属性绑定表
     *
     * @param attributeAndParameter
     * @param goodsId
     */
    private void updateGoodsBindAttribute(GoodsPublishFrontParamDTO.AttributeAndParameter attributeAndParameter, Long goodsId) {
        //删除原有的绑定关系
        goodsBindAttributeValueModel.deleteGoodsBindAttributeValueByGoodsId(goodsId);
        //新增绑定关系
        this.insertGoodsBindAttribute(attributeAndParameter, goodsId);
    }

    /**
     * 编辑商品-更新商品标签绑定表
     *
     * @param goodsLabelInfoList
     * @param goodsId
     */
    private void updateGoodsBindLabel(List<GoodsPublishFrontParamDTO.GoodsLabelInfo> goodsLabelInfoList, Long goodsId) {
        //删除原有的绑定关系
        goodsBindLabelModel.deleteGoodsBindLabelByGoodsId(goodsId);
        //新增绑定关系
        this.insertGoodsBindLabels(goodsLabelInfoList, goodsId);
    }

    /**
     * 编辑商品-更新商品图片
     *
     * @param specInfoList  规格列表
     * @param imageInfoList 图片列表
     * @param goods         商品信息
     * @param vendor        商户管理员
     */
    private void updateGoodsPicture(List<GoodsPublishFrontParamDTO.SpecInfo> specInfoList, List<GoodsPublishFrontParamDTO.ImageInfo> imageInfoList, Goods goods, Vendor vendor) {
        //删除原有图片
        goodsPictureModel.deleteGoodsPictureGoodsId(goods.getGoodsId());
        //新增图片
        this.insertGoodsPicture(specInfoList, imageInfoList, goods, vendor);
    }

    /**
     * 编辑商品-更新商品绑定店铺内部分类
     *
     * @param storeInnerLabelList
     * @param goodsId
     * @param vendor
     */
    private void updateGoodsBindStoreInnerLabels(List<GoodsPublishFrontParamDTO.StoreInnerLabelInfo> storeInnerLabelList, Long goodsId, Vendor vendor) {
        //删除原有绑定关系
        StoreLabelBindGoodsExample example = new StoreLabelBindGoodsExample();
        example.setGoodsId(goodsId);
        storeLabelBindGoodsWriteMapper.deleteByExample(example);

        //新增绑定关系
        this.insertGoodsBindStoreInnerLabels(storeInnerLabelList, goodsId, vendor);
    }

    /**
     * 编辑商品-更新店铺商品数量
     *
     * @param oldGoodsState 原商品状态
     * @param newGoodsState 新商品状态
     * @param storeId       店铺id
     */
    private void updateStoreGoodsNum(Integer oldGoodsState, Integer newGoodsState, Long storeId) {
        Store storeDb = storeReadMapper.getByPrimaryKey(storeId);
        int goodsNum = storeDb.getGoodsNumber();
        if (oldGoodsState.equals(GoodsConst.GOODS_STATE_UPPER)) {
            //原商品的状态为上架，店铺商品数量--
            goodsNum--;
        }
        if (newGoodsState.equals(GoodsConst.GOODS_STATE_UPPER)) {
            //新的商品状态为上架,店铺商品数量++
            goodsNum++;
        }
        if (!storeDb.getGoodsNumber().equals(goodsNum)) {
            //商品数量发生变化才更新
            Store update = new Store();
            update.setStoreId(storeId);
            update.setGoodsNumber(goodsNum);
            storeWriteMapper.updateByPrimaryKeySelective(update);
        }
    }

    //endregion


    /**
     * 商品上架
     *
     * @param goodsIds
     * @return storeId
     */
    public Integer upperShelfGoods(Long storeId, String goodsIds) {
        //商品上架
        //11-放入仓库无需审核 12-放入仓库审核通过 的商品才能上架
        Goods goodsUpdate = new Goods();
        goodsUpdate.setState(GoodsConst.GOODS_STATE_UPPER);
        goodsUpdate.setOnlineTime(new Date());
        goodsUpdate.setUpdateTime(new Date());
        GoodsExample example = new GoodsExample();
        example.setGoodsIdIn(goodsIds);
        example.setStateIn(GoodsConst.GOODS_STATE_WAREHOUSE_NO_AUDIT + "," + GoodsConst.GOODS_STATE_WAREHOUSE_AUDIT_PASS + "," + GoodsConst.GOODS_STATE_LOWER_BY_STORE);
        example.setStoreId(storeId);
        return goodsModel.updateGoodsByExample(goodsUpdate, example);
    }

    /**
     * 商品下架
     *
     * @param goodsIds
     * @return storeId
     */
    public Integer lockupGoods(Long storeId, String goodsIds) {
        int number = 0;
        //商品下架（商户自行下架）
        Goods goodsUpdate = new Goods();
        goodsUpdate.setState(GoodsConst.GOODS_STATE_LOWER_BY_STORE);
        goodsUpdate.setUpdateTime(new Date());
        GoodsExample example = new GoodsExample();
        example.setGoodsIdIn(goodsIds);
        example.setState(GoodsConst.GOODS_STATE_UPPER);
        example.setStoreId(storeId);
        number = goodsModel.updateGoodsByExample(goodsUpdate, example);
        return number;
    }

    /**
     * 根据goodsId更新商品公共信息表（SPU） 修改商品状态为删除
     *
     * @param storeId
     * @param goodsIds
     * @return
     */
    @Transactional
    public Integer deleteGoods(Long storeId, String goodsIds) {
        int number = 0;
        //删除商品
        Goods goodsUpdate = new Goods();
        goodsUpdate.setIsDelete(GoodsConst.GOODS_IS_DELETE_YES);
        goodsUpdate.setState(GoodsConst.GOODS_STATE_DELETE);
        goodsUpdate.setUpdateTime(new Date());
        GoodsExample example = new GoodsExample();
        example.setGoodsIdIn(goodsIds);
//        example.setStateIn(GoodsConst.GOODS_STATE_WAREHOUSE_NO_AUDIT + "," + GoodsConst.GOODS_STATE_LOWER_BY_STORE + "," + GoodsConst.GOODS_STATE_LOWER_BY_SYSTEM);
        example.setStoreId(storeId);
        number = goodsModel.updateGoodsByExample(goodsUpdate, example);
        return number;
    }


    /**
     * 商品csv导入
     *
     * @param vendor
     * @param categoryId3
     * @param filePath
     * @param innerLabelId
     * @return
     */
    @Transactional
    public void goodsCsvImport(Vendor vendor, MultipartFile filePath, Integer categoryId3, Integer innerLabelId) {
        GoodsPublishFrontParamDTO paramDTO = new GoodsPublishFrontParamDTO();
        //店铺分类
        List<GoodsPublishFrontParamDTO.StoreInnerLabelInfo> storeInnerLabelInfoList = new ArrayList<>();
        if (!StringUtil.isNullOrZero(innerLabelId)) {
            StoreInnerLabel storeInnerLabel = storeInnerLabelReadMapper.getByPrimaryKey(innerLabelId);
            AssertUtil.notNull(storeInnerLabel, "店铺内部分类信息为空");
            AssertUtil.notNull(!storeInnerLabel.getStoreId().equals(vendor.getStoreId()), "无权限");
            GoodsPublishFrontParamDTO.StoreInnerLabelInfo storeInnerLabelInfo = new GoodsPublishFrontParamDTO.StoreInnerLabelInfo();
            storeInnerLabelInfo.setInnerLabelId(innerLabelId);
            storeInnerLabelInfo.setInnerLabelName(storeInnerLabel.getInnerLabelName());
            storeInnerLabelInfoList.add(storeInnerLabelInfo);
        }

        String logMsg = "";

        //解析CSV文件
        List<List<String>> dataList = readCsv(filePath);
        //title标题 英文标题
        List<String> englishTitleList = new ArrayList<>();
        //title标题 中文标题
        List<String> chineseTitleList = new ArrayList<>();
        HashMap<String, Object> map = new HashMap<>();
        //数据集合 循环一次为一条数据,对应csv一行
        List<HashMap<String, Object>> contentList = new ArrayList<>();
        //第i行数据
        int i = 0;
        try {
            for (i = 0; i < dataList.size(); i++) {
                paramDTO = new GoodsPublishFrontParamDTO();
                map = new HashMap<>();
                if (i == 0) {
                    //英文标题
                    for (String column : dataList.get(i)) {
                        AssertUtil.notEmpty(column, "csv文件英文标题有空值");
                        englishTitleList.add(column);
                    }
                } else if (i == 1) {
                    //中文标题
                    for (String column : dataList.get(i)) {
                        AssertUtil.notEmpty(column, "csv文件中文标题有空值");
                        chineseTitleList.add(column);
                    }
                } else {
                    //组装数据GoodsPublishFrontParamDTO
                    //1 前端传参
                    //商品分类
                    paramDTO.setCategoryId3(categoryId3);
                    //店铺内部分类
                    paramDTO.setStoreInnerLabelList(storeInnerLabelInfoList);

                    //2 csv文件
                    for (int j = 0; j < englishTitleList.size(); j++) {
                        //定义每一行数据的列值
                        String column = dataList.get(i).get(j);
                        //获取英文标题
                        String title = englishTitleList.get(j);
                        //获取中文标题
                        String chineseTitle = chineseTitleList.get(j);

                        //商品名称为3到50个字符(商品副标题) 必填项
                        if ("goods_name".equalsIgnoreCase(title)) {
                            if (StringUtil.isEmpty(column)) {
                                logMsg = "第" + (i + 2) + "行数据出错," + chineseTitle + "不能为空";
                                log.error(logMsg);
                                AssertUtil.notEmpty(column, logMsg);
                            }
                            paramDTO.setGoodsName(column);
                            continue;
                        }
                        if ("goods_brief".equalsIgnoreCase(title)) {
                            paramDTO.setGoodsBrief(column);
                            continue;
                        }
                        if ("product_price".equalsIgnoreCase(title)) {
                            if (StringUtil.isEmpty(column)) {
                                logMsg = "第" + (i + 2) + "行数据出错," + chineseTitle + "不能为空";
                                log.error(logMsg);
                                AssertUtil.notEmpty(column, logMsg);
                            }
                            paramDTO.setProductPrice(StringUtil.isEmpty(column) ? BigDecimal.ZERO : new BigDecimal(column));
                            continue;
                        }
                        if ("product_stock".equalsIgnoreCase(title)) {
                            if (StringUtil.isEmpty(column)) {
                                logMsg = "第" + (i + 2) + "行数据出错," + chineseTitle + "不能为空";
                                log.error(logMsg);
                                AssertUtil.notEmpty(column, logMsg);
                            }
                            paramDTO.setProductStock(StringUtil.isEmpty(column) ? 0 : Integer.parseInt(column));
                            continue;
                        }
                        if ("product_code".equalsIgnoreCase(title)) {
                            paramDTO.setProductCode(column);
                            continue;
                        }
                        if ("bar_code".equalsIgnoreCase(title)) {
                            paramDTO.setBarCode(column);
                            continue;
                        }
                        if ("goods_details".equalsIgnoreCase(title)) {
                            paramDTO.setGoodsDetails(column);
                            continue;
                        }
                        if ("store_is_recommend".equalsIgnoreCase(title)) {
                            paramDTO.setStoreIsRecommend(StringUtil.isEmpty(column) ? 0 : Integer.parseInt(column));
                            continue;
                        }
                        if ("freight_fee".equalsIgnoreCase(title)) {
                            if (StringUtil.isEmpty(column)) {
                                logMsg = "第" + (i + 2) + "行数据出错," + chineseTitle + "不能为空";
                                log.error(logMsg);
                                AssertUtil.notEmpty(column, logMsg);
                            }
                            paramDTO.setFreightFee(StringUtil.isEmpty(column) ? BigDecimal.ZERO : new BigDecimal(column));
                            continue;
                        }

                        if ("is_vat_invoice".equalsIgnoreCase(title)) {
                            paramDTO.setIsVatInvoice(StringUtil.isEmpty(column) ? 0 : Integer.parseInt(column));
                            continue;
                        }
                        if ("virtual_sales".equalsIgnoreCase(title)) {
                            paramDTO.setVirtualSales(StringUtil.isEmpty(column) ? 0 : Integer.parseInt(column));
                            continue;
                        }
                        if ("sell_now".equalsIgnoreCase(title)) {
                            paramDTO.setSellNow(!StringUtil.isEmpty(column));
                            continue;
                        }
                        if ("market_price".equalsIgnoreCase(title)) {
                            paramDTO.setMarketPrice(StringUtil.isEmpty(column) ? BigDecimal.ZERO : new BigDecimal(column));
                            continue;
                        }
                        if ("product_stock_warning".equalsIgnoreCase(title)) {
                            paramDTO.setProductStockWarning(StringUtil.isEmpty(column) ? 0 : Integer.parseInt(column));
                            continue;
                        }
                        if ("weight".equalsIgnoreCase(title)) {
                            paramDTO.setWeight(StringUtil.isEmpty(column) ? BigDecimal.ZERO : new BigDecimal(column));
                            continue;
                        }
                        if ("length".equalsIgnoreCase(title)) {
                            paramDTO.setLength(StringUtil.isEmpty(column) ? BigDecimal.ZERO : new BigDecimal(column));
                            continue;
                        }
                        if ("width".equalsIgnoreCase(title)) {
                            paramDTO.setWidth(StringUtil.isEmpty(column) ? BigDecimal.ZERO : new BigDecimal(column));
                            continue;
                        }
                        if ("height".equalsIgnoreCase(title)) {
                            paramDTO.setHeight(StringUtil.isEmpty(column) ? BigDecimal.ZERO : new BigDecimal(column));
                            continue;
                        }
                        if ("goods_video".equalsIgnoreCase(title)) {
                            paramDTO.setGoodsVideo(column);
                            continue;
                        }
                        //商品图片列表，无规格或未设置主规格时必传
                        if ("image_list".equalsIgnoreCase(title)) {
                            if (StringUtil.isEmpty(column)) {
                                logMsg = "第" + (i + 2) + "行数据出错," + chineseTitle + "不能为空";
                                log.error(logMsg);
                                AssertUtil.notEmpty(column, logMsg);
                            }
                            dealColumn(paramDTO, column, title);
                            continue;
                        }
                    }
                    //构造入库dto
                    GoodsPublishInsertDTO insertDTO = new GoodsPublishInsertDTO(paramDTO);
                    this.saveGoods(vendor, insertDTO);
                }
            }
        } catch (Exception e) {
            logMsg = "第" + (i + 2) + "行数据导入失败,请重试";
            log.error(logMsg + e);
            throw new MallException(logMsg);
        }
    }

    /**
     * csv导入商品图片列表
     *
     * @param paramDTO
     */
    private void dealColumn(GoodsPublishFrontParamDTO paramDTO, String column, String title) {

        //商品图片列表
        //例:https://jbbcimgdev.slodon.cn/images/seller/goods/0PJAGyUnYbgt5Q6BpKK.gif,https://jbbcimgdev.slodon.cn/images/seller/goods/BYfSNJBE2TNiDR3or8A.jpg
        if ("image_list".equalsIgnoreCase(title)) {
            List<GoodsPublishFrontParamDTO.ImageInfo> imageList = new ArrayList<>();
            List<String> list = Arrays.asList(column.split("&"));
            if (!CollectionUtils.isEmpty(list)) {
                list.forEach(image -> {
                    String newImage = "";
                    //minio图片上传服务器参数
                    //minio地址
                    String minioUrl = stringRedisTemplate.opsForValue().get("minio_url");
                    String minioPort = stringRedisTemplate.opsForValue().get("minio_port");
                    //minio端口
                    AssertUtil.notEmpty(minioPort, "minio端口号未配置");
                    int port = Integer.parseInt(minioPort);
                    String accessKey = stringRedisTemplate.opsForValue().get("minio_access_key");
                    String secretKey = stringRedisTemplate.opsForValue().get("minio_secret_key");

                    String path = UploadConst.PATH_NAME_IMAGE;
                    String bucketName = UploadConst.BUCKET_NAME_IMAGE;
                    String contentType = UploadConst.CONTENT_TYPE_IMAGE;
                    try {
                        //图片上传服务器
                        newImage = FileReaderUtil.uploadFile(image, minioUrl, port, accessKey, secretKey, path, bucketName, contentType);
                    } catch (Exception e) {
                        log.error("图片上传服务器失败");
                        e.printStackTrace();
                    }
                    GoodsPublishFrontParamDTO.ImageInfo imageInfo = new GoodsPublishFrontParamDTO.ImageInfo();
                    imageInfo.setImage(newImage);
                    imageList.add(imageInfo);
                });
            }
            paramDTO.setImageList(imageList);
        }
    }


}