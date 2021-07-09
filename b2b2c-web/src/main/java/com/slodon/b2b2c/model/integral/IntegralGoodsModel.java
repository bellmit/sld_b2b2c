package com.slodon.b2b2c.model.integral;

import com.alibaba.fastjson.JSON;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.IntegralConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.uid.GoodsIdGenerator;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsLabelReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralProductReadMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsBindLabelWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsPictureWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralGoodsWriteMapper;
import com.slodon.b2b2c.dao.write.integral.IntegralProductWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsExtendExample;
import com.slodon.b2b2c.goods.example.GoodsPictureExample;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.goods.pojo.GoodsPicture;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.integral.dto.GoodsImportFrontParamDTO;
import com.slodon.b2b2c.integral.dto.GoodsPublishFrontParamDTO;
import com.slodon.b2b2c.integral.dto.GoodsPublishInsertDTO;
import com.slodon.b2b2c.integral.example.IntegralGoodsBindLabelExample;
import com.slodon.b2b2c.integral.example.IntegralGoodsExample;
import com.slodon.b2b2c.integral.example.IntegralGoodsPictureExample;
import com.slodon.b2b2c.integral.example.IntegralProductExample;
import com.slodon.b2b2c.integral.pojo.*;
import com.slodon.b2b2c.model.goods.GoodsExtendModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPictureModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 积分商品表（SPU）model
 */
@Component
@Slf4j
public class IntegralGoodsModel {

    @Resource
    private IntegralGoodsReadMapper integralGoodsReadMapper;
    @Resource
    private IntegralGoodsWriteMapper integralGoodsWriteMapper;
    @Resource
    private IntegralProductReadMapper integralProductReadMapper;
    @Resource
    private IntegralProductWriteMapper integralProductWriteMapper;
    @Resource
    private IntegralGoodsLabelReadMapper integralGoodsLabelReadMapper;
    @Resource
    private IntegralGoodsBindLabelWriteMapper integralGoodsBindLabelWriteMapper;
    @Resource
    private IntegralGoodsPictureWriteMapper integralGoodsPictureWriteMapper;
    @Resource
    private IntegralGoodsSpecValueModel integralGoodsSpecValueModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private GoodsExtendModel goodsExtendModel;
    @Resource
    private GoodsPictureModel goodsPictureModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //region 发布商品

    /**
     * 商家发布商品
     * -校验sku是否重复
     * -保存商品信息
     * -保存货品信息
     * -保存商品标签绑定表
     * -保存商品图片表
     *
     * @param vendor    店铺管理员
     * @param insertDTO 商品信息
     */
    @Transactional
    public void saveIntegralGoods(Vendor vendor, GoodsPublishInsertDTO insertDTO) {
        List<GoodsPublishFrontParamDTO.IntegralProductInfo> productList = insertDTO.getProductList();//货品列表
        //-校验sku是否重复
        this.checkSku(productList);

        //-保存商品信息
        IntegralGoods goods = this.insertGoods(vendor, insertDTO);

        //-保存货品信息
        this.insertProduct(productList, goods);

        //-保存商品标签绑定表
        this.insertGoodsBindLabels(insertDTO.getLabelIds(), goods.getIntegralGoodsId());

        //-保存商品图片表
        this.insertGoodsPicture(insertDTO.getSpecInfoList(), insertDTO.getImageList(), goods, vendor);

    }

    /**
     * 校验sku是否重复
     *
     * @param productList
     */
    private void checkSku(List<GoodsPublishFrontParamDTO.IntegralProductInfo> productList) {
        Set<String> set = new HashSet<>();//用于校验自身sku是否重复
        productList.forEach(productInfo -> {
            if (!StringUtils.isEmpty(productInfo.getProductCode())) {
                AssertUtil.isTrue(!set.add(productInfo.getProductCode()), "sku:" + productInfo.getProductCode() + "重复");
                IntegralProductExample example = new IntegralProductExample();
                example.setProductCode(productInfo.getProductCode());
                example.setIntegralProductIdNotEquals(((GoodsPublishInsertDTO.ProductInsertInfo) productInfo).getIntegralProductId());
                int count = integralProductReadMapper.countByExample(example);
                AssertUtil.isTrue(count > 0, "sku[" + productInfo.getProductCode() + "]已存在，请重新输入");
            }
            //积分换算比例
            String integralScale = stringRedisTemplate.opsForValue().get("integral_conversion_ratio");
            //校验积分价格是否合法
            if (!StringUtil.isNullOrZero(productInfo.getIntegralPrice())) {
                AssertUtil.notEmpty(integralScale, "积分转换金额失败，请联系系统管理员！");
                AssertUtil.isTrue(Integer.parseInt(integralScale) > productInfo.getIntegralPrice(), "积分必须大于等于换算比例" + integralScale + ",并且是整数倍");
                AssertUtil.isTrue(productInfo.getIntegralPrice() % Integer.parseInt(integralScale) != 0, "积分必须是换算比例" + integralScale + "的整数倍");
            } else {
                AssertUtil.isTrue(StringUtil.isNullOrZero(productInfo.getCashPrice()), "积分价格和现金价格至少有一项大于0，若设置积分价格，则必须满足大于等于换算比例" + integralScale + ",并且是整数倍");
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
    private IntegralGoods insertGoods(Vendor vendor, GoodsPublishInsertDTO insertDTO) {
        long goodsId = GoodsIdGenerator.goodsIdGenerator();//商品id
        long defaultProductId = GoodsIdGenerator.productIdGenerator();//生成一个默认货品id
        GoodsPublishInsertDTO.ProductInsertInfo defaultProduct = insertDTO.getDefaultProduct();//默认货品

        IntegralGoods goods = new IntegralGoods();
        goods.setIntegralGoodsId(goodsId);
        goods.setGoodsName(insertDTO.getGoodsName());
        goods.setGoodsBrief(insertDTO.getGoodsBrief());
        goods.setMarketPrice(defaultProduct.getMarketPrice());
        goods.setIntegralPrice(defaultProduct.getIntegralPrice());
        goods.setCashPrice(defaultProduct.getCashPrice());
        goods.setGoodsStock(defaultProduct.getProductStock());
        goods.setActualSales(insertDTO.getVirtualSales());
        goods.setMainSpecId(insertDTO.getMainSpecId());
        goods.setIsSelf(vendor.getStore().getIsOwnStore());
        goods.setState(this.dealGoodsState(insertDTO.getSellNow()));
        goods.setOnlineTime(goods.getState().equals(GoodsConst.GOODS_STATE_UPPER) ? new Date() : null);
        goods.setStoreId(vendor.getStoreId());
        goods.setStoreName(vendor.getStore().getStoreName());
        goods.setStoreIsRecommend(insertDTO.getStoreIsRecommend());
        goods.setMainImage(defaultProduct.getMainImage());
        goods.setGoodsVideo(insertDTO.getGoodsVideo());
        goods.setDefaultProductId(defaultProductId);
        goods.setIsVatInvoice(insertDTO.getIsVatInvoice());
        goods.setCreateVendorId(vendor.getVendorId());
        goods.setCreateTime(new Date());
        goods.setUpdateTime(new Date());
        if (!CollectionUtils.isEmpty(insertDTO.getSpecInfoList())) {
            goods.setSpecJson(JSON.toJSONString(insertDTO.getSpecInfoList()));
        }
        goods.setGoodsDetails(insertDTO.getGoodsDetails());
        int count = integralGoodsWriteMapper.insert(goods);
        AssertUtil.isTrue(count == 0, "保存积分商品信息失败");
        return goods;
    }

    /**
     * 发布商品-插入货品信息
     *
     * @param productList 货品列表
     * @param goods       商品信息
     */
    private void insertProduct(List<GoodsPublishFrontParamDTO.IntegralProductInfo> productList, IntegralGoods goods) {
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
    private void doInsertProduct(IntegralGoods goods, GoodsPublishInsertDTO.ProductInsertInfo productInsertInfo) {
        IntegralProduct product = new IntegralProduct();
        if (productInsertInfo.getIsDefault() == GoodsConst.PRODUCT_IS_DEFAULT_YES) {
            //默认货品
            product.setIntegralProductId(goods.getDefaultProductId());
        } else {
            //非默认货品，生成货品id
            product.setIntegralProductId(GoodsIdGenerator.productIdGenerator());
        }
        product.setGoodsId(goods.getIntegralGoodsId());
        product.setGoodsName(goods.getGoodsName());
        product.setSpecValues(productInsertInfo.getSpecValues());
        product.setSpecValueIds(productInsertInfo.getSpecValueIds());
        product.setStoreId(goods.getStoreId());
        product.setStoreName(goods.getStoreName());
        product.setMarketPrice(productInsertInfo.getMarketPrice());
        product.setIntegralPrice(productInsertInfo.getIntegralPrice());
        product.setCashPrice(productInsertInfo.getCashPrice());
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
        int count = integralProductWriteMapper.insert(product);
        AssertUtil.isTrue(count == 0, "保存积分货品信息失败");
    }

    /**
     * 发布商品-保存商品标签绑定表
     *
     * @param labelIds
     * @param goodsId
     */
    private void insertGoodsBindLabels(String labelIds, Long goodsId) {
        String[] labelIdArray = labelIds.split(",");
        for (String labelId : labelIdArray) {
            if (StringUtil.isEmpty(labelId)) {
                continue;
            }
            IntegralGoodsLabel goodsLabel = integralGoodsLabelReadMapper.getByPrimaryKey(labelId);
            AssertUtil.notNull(goodsId, "积分商品标签不存在");
            IntegralGoodsBindLabel goodsBindLabel = new IntegralGoodsBindLabel();
            goodsBindLabel.setGoodsId(goodsId);
            goodsBindLabel.setLabelId1(goodsLabel.getParentLabelId());
            goodsBindLabel.setLabelId2(goodsLabel.getLabelId());
            goodsBindLabel.setGrade(goodsLabel.getGrade());
            if (goodsLabel.getGrade() == IntegralConst.GRADE_1) {
                goodsBindLabel.setLabelPath(goodsLabel.getLabelName());
            } else {
                IntegralGoodsLabel parentGoodsLabel = integralGoodsLabelReadMapper.getByPrimaryKey(goodsLabel.getParentLabelId());
                AssertUtil.notNull(parentGoodsLabel, "积分商品标签不存在");
                goodsBindLabel.setLabelPath(parentGoodsLabel.getLabelName() + " > " + goodsLabel.getLabelName());
            }
            int count = integralGoodsBindLabelWriteMapper.insert(goodsBindLabel);
            AssertUtil.isTrue(count == 0, "保存商品绑定标签信息失败");
        }
    }

    /**
     * 发布商品-保存商品图片
     *
     * @param specInfoList
     * @param imageInfoList
     * @param goods
     */
    private void insertGoodsPicture(List<GoodsPublishFrontParamDTO.SpecInfo> specInfoList, List<GoodsPublishFrontParamDTO.ImageInfo> imageInfoList,
                                    IntegralGoods goods, Vendor vendor) {
        if (StringUtil.isNullOrZero(goods.getMainSpecId())) {
            //没有主规格
            imageInfoList.forEach(imageInfo -> {
                this.insertPicture(imageInfo, goods.getIntegralGoodsId(), vendor, 0);
            });
        } else {
            //有主规格
            specInfoList.forEach(specInfo -> {
                if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                    specInfo.getSpecValueList().forEach(specValueInfo -> {
                        specValueInfo.getImageList().forEach(imageInfo -> {
                            this.insertPicture(imageInfo, goods.getIntegralGoodsId(), vendor, specValueInfo.getSpecValueId());
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
        IntegralGoodsPicture goodsPicture = new IntegralGoodsPicture();
        goodsPicture.setGoodsId(goodsId);
        goodsPicture.setImagePath(imageInfo.getImage());
        goodsPicture.setSort(1);
        goodsPicture.setCreateId(vendor.getVendorId());
        goodsPicture.setCreateTime(new Date());
        goodsPicture.setStoreId(vendor.getStoreId());
        goodsPicture.setIsMain(imageInfo.getIsMain());
        goodsPicture.setSpecValueId(specValueId);
        int count = integralGoodsPictureWriteMapper.insert(goodsPicture);
        AssertUtil.isTrue(count == 0, "保存商品对应图片信息失败");
    }

    /**
     * 处理商品状态
     *
     * @param sellNow 是否立即售卖，true==是，false==放入仓库
     * @return
     */
    private Integer dealGoodsState(Boolean sellNow) {
        //查询系统配置，是否需要审核，true==需要审核
        boolean needAudit = GoodsConst.GOODS_PUBLISH_NEED_AUDIT_YES.equals(stringRedisTemplate.opsForValue().get("integral_audit_is_enable"));
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
     * -更新货品信息
     * -保存新的商品标签绑定表
     * -保存新的商品图片表
     *
     * @param vendor
     * @param insertDTO
     * @param goodsDb
     */
    @Transactional
    public void updateIntegralGoods(Vendor vendor, GoodsPublishInsertDTO insertDTO, IntegralGoods goodsDb) {
        List<GoodsPublishFrontParamDTO.IntegralProductInfo> productList = insertDTO.getProductList();//前端传来的货品列表
        List<GoodsPublishInsertDTO.ProductInsertInfo> addProductList = new ArrayList<>();//要新增的货品
        List<GoodsPublishInsertDTO.ProductInsertInfo> updateProductList = new ArrayList<>();//要删除的货品
        List<IntegralProduct> deleteProductList = new ArrayList<>();//要删除的货品（伪删除）

        //-对比货品列表信息，构造要增、删、改的货品列表
        this.compareSpecWithDb(goodsDb.getIntegralGoodsId(), productList, addProductList, updateProductList, deleteProductList);

        //-校验sku是否重复
        this.checkSku(productList);

        //-更新商品信息
        IntegralGoods updateGoods = this.updateGoods(insertDTO, vendor);

        //-更新货品信息
        this.updateProduct(updateGoods, addProductList, updateProductList, deleteProductList);

        //-保存新的商品标签绑定表
        this.updateGoodsBindLabel(insertDTO.getLabelIds(), updateGoods.getIntegralGoodsId());

        //-保存新的商品图片表
        this.updateGoodsPicture(insertDTO.getSpecInfoList(), insertDTO.getImageList(), updateGoods, vendor);

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
    private void compareSpecWithDb(Long goodsId, List<GoodsPublishFrontParamDTO.IntegralProductInfo> paramProductList,
                                   List<GoodsPublishInsertDTO.ProductInsertInfo> addProductList,
                                   List<GoodsPublishInsertDTO.ProductInsertInfo> updateProductList,
                                   List<IntegralProduct> deleteProductList) {
        //数据库中的货品列表
        IntegralProductExample productExample = new IntegralProductExample();
        productExample.setGoodsId(goodsId);
        List<IntegralProduct> dbProductList = integralProductReadMapper.listByExample(productExample);
        AssertUtil.notEmpty(dbProductList, "商品信息有误");

        //构造新增和更新的货品
        paramProductList.forEach(paramProduct -> {
            GoodsPublishInsertDTO.ProductInsertInfo paramInsertProduct = (GoodsPublishInsertDTO.ProductInsertInfo) paramProduct;
            boolean compared = false;//数据库中是否有此货品
            for (IntegralProduct dbProduct : dbProductList) {
                if (compareSpec(paramInsertProduct, dbProduct)) {
                    //匹配成功，更新此货品
                    paramInsertProduct.setIntegralProductId(dbProduct.getIntegralProductId());
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
                if (dbProduct.getIntegralProductId().equals(updateProduct.getIntegralProductId())) {
                    //匹配成功，不需要删除
                    delete = false;
                    break;
                }
            }
            if (delete) {
                IntegralProduct deleteProduct = new IntegralProduct();
                deleteProduct.setIntegralProductId(dbProduct.getIntegralProductId());
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
    private boolean compareSpec(GoodsPublishInsertDTO.ProductInsertInfo paramProduct, IntegralProduct dbProduct) {
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
    private IntegralGoods updateGoods(GoodsPublishInsertDTO insertDTO, Vendor vendor) {
        GoodsPublishInsertDTO.ProductInsertInfo defaultProduct = insertDTO.getDefaultProduct();
        //构造默认货品id
        long defaultProductId = StringUtil.isNullOrZero(defaultProduct.getIntegralProductId())
                ? GoodsIdGenerator.productIdGenerator() : defaultProduct.getIntegralProductId();

        IntegralGoods goods = new IntegralGoods();
        goods.setIntegralGoodsId(insertDTO.getIntegralGoodsId());
        goods.setGoodsName(insertDTO.getGoodsName());
        goods.setGoodsBrief(StringUtils.isEmpty(insertDTO.getGoodsBrief()) ? "" : insertDTO.getGoodsBrief());//为传商品广告时，要清除原数据
        goods.setMarketPrice(StringUtil.isNullOrZero(defaultProduct.getMarketPrice()) ? new BigDecimal("0.00") : defaultProduct.getMarketPrice());
        goods.setGoodsStock(defaultProduct.getProductStock());
        goods.setIntegralPrice(defaultProduct.getIntegralPrice());
        goods.setCashPrice(defaultProduct.getCashPrice());
        goods.setMainSpecId(insertDTO.getMainSpecId());
        goods.setState(this.dealGoodsState(insertDTO.getSellNow()));
        goods.setIsSelf(vendor.getStore().getIsOwnStore());
        goods.setOnlineTime(goods.getState().equals(GoodsConst.GOODS_STATE_UPPER) ? new Date() : null);
        goods.setStoreId(vendor.getStoreId());
        goods.setStoreName(vendor.getStore().getStoreName());
        goods.setStoreIsRecommend(insertDTO.getStoreIsRecommend());
        goods.setMainImage(defaultProduct.getMainImage());
        goods.setGoodsVideo(StringUtils.isEmpty(insertDTO.getGoodsVideo()) ? "" : insertDTO.getGoodsVideo());
        goods.setDefaultProductId(defaultProductId);
        goods.setIsVatInvoice(insertDTO.getIsVatInvoice());
        int count = integralGoodsWriteMapper.updateByPrimaryKeySelective(goods);
        AssertUtil.isTrue(count == 0, "修改商品信息失败");
        return goods;
    }


    /**
     * 编辑商品-更新货品信息
     *
     * @param updateGoods
     * @param addProductList
     * @param updateProductList
     * @param deleteProductList
     */
    private void updateProduct(IntegralGoods updateGoods, List<GoodsPublishInsertDTO.ProductInsertInfo> addProductList,
                               List<GoodsPublishInsertDTO.ProductInsertInfo> updateProductList,
                               List<IntegralProduct> deleteProductList) {
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
    private void updateProductAdd(IntegralGoods updateGoods, List<GoodsPublishInsertDTO.ProductInsertInfo> addProductList) {
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
    private void updateProductUpdate(IntegralGoods updateGoods, List<GoodsPublishInsertDTO.ProductInsertInfo> updateProductList) {
        if (CollectionUtils.isEmpty(updateProductList)) return;
        updateProductList.forEach(productInsertInfo -> {
            IntegralProduct product = new IntegralProduct();
            product.setIntegralProductId(productInsertInfo.getIntegralProductId());
            product.setGoodsName(updateGoods.getGoodsName());
            product.setSpecValues(productInsertInfo.getSpecValues());
            product.setSpecValueIds(productInsertInfo.getSpecValueIds());
            product.setIntegralPrice(productInsertInfo.getIntegralPrice());
            product.setCashPrice(productInsertInfo.getCashPrice());
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
            int count = integralProductWriteMapper.updateByPrimaryKeySelective(product);
            AssertUtil.isTrue(count == 0, "修改货品信息失败");
        });
    }

    /**
     * 编辑商品-更新货品信息-删除货品
     *
     * @param deleteProductList
     */
    private void updateProductDelete(List<IntegralProduct> deleteProductList) {
        if (CollectionUtils.isEmpty(deleteProductList)) return;
        deleteProductList.forEach(product -> {
            integralProductWriteMapper.updateByPrimaryKeySelective(product);
        });
    }

    /**
     * 编辑商品-更新商品标签绑定表
     *
     * @param labelIds
     * @param goodsId
     */
    private void updateGoodsBindLabel(String labelIds, Long goodsId) {
        //删除原有的绑定关系
        IntegralGoodsBindLabelExample example = new IntegralGoodsBindLabelExample();
        example.setGoodsId(goodsId);
        integralGoodsBindLabelWriteMapper.deleteByExample(example);
        //新增绑定关系
        this.insertGoodsBindLabels(labelIds, goodsId);
    }

    /**
     * 编辑商品-更新商品图片
     *
     * @param specInfoList  规格列表
     * @param imageInfoList 图片列表
     * @param goods         商品信息
     * @param vendor        商户管理员
     */
    private void updateGoodsPicture(List<GoodsPublishFrontParamDTO.SpecInfo> specInfoList, List<GoodsPublishFrontParamDTO.ImageInfo> imageInfoList,
                                    IntegralGoods goods, Vendor vendor) {
        //删除原有图片
        IntegralGoodsPictureExample example = new IntegralGoodsPictureExample();
        example.setGoodsId(goods.getIntegralGoodsId());
        integralGoodsPictureWriteMapper.deleteByExample(example);
        //新增图片
        this.insertGoodsPicture(specInfoList, imageInfoList, goods, vendor);
    }

    //endregion

    /**
     * 商品上架
     *
     * @param goodsIds
     * @return storeId
     */
    public Integer upperShelfGoods(Long storeId, String goodsIds) {
        //11-放入仓库无需审核 12-放入仓库审核通过 的商品才能上架
        IntegralGoods goodsUpdate = new IntegralGoods();
        goodsUpdate.setState(GoodsConst.GOODS_STATE_UPPER);
        goodsUpdate.setOnlineTime(new Date());
        goodsUpdate.setUpdateTime(new Date());
        IntegralGoodsExample example = new IntegralGoodsExample();
        example.setIntegralGoodsIdIn(goodsIds);
        example.setStateIn(GoodsConst.GOODS_STATE_WAREHOUSE_NO_AUDIT + "," + GoodsConst.GOODS_STATE_WAREHOUSE_AUDIT_PASS + "," + GoodsConst.GOODS_STATE_LOWER_BY_STORE);
        example.setStoreId(storeId);
        int number = integralGoodsWriteMapper.updateByExampleSelective(goodsUpdate, example);
        AssertUtil.notNullOrZero(number, "商品上架失败");
        return number;
    }

    /**
     * 商品下架（商户自行下架）
     *
     * @param goodsIds
     * @return storeId
     */
    public Integer lockupGoods(Long storeId, String goodsIds) {
        IntegralGoods goodsUpdate = new IntegralGoods();
        goodsUpdate.setState(GoodsConst.GOODS_STATE_LOWER_BY_STORE);
        goodsUpdate.setOnlineTime(null);
        goodsUpdate.setUpdateTime(new Date());
        IntegralGoodsExample example = new IntegralGoodsExample();
        example.setIntegralGoodsIdIn(goodsIds);
        example.setState(GoodsConst.GOODS_STATE_UPPER);
        example.setStoreId(storeId);
        int number = integralGoodsWriteMapper.updateByExampleSelective(goodsUpdate, example);
        AssertUtil.notNullOrZero(number, "商品下架失败");
        return number;
    }

    /**
     * 根据goodsIds更新商品信息,修改商品状态为删除
     *
     * @param storeId
     * @param goodsIds
     * @return
     */
    public Integer deleteGoods(Long storeId, String goodsIds) {
        IntegralGoods goodsUpdate = new IntegralGoods();
        goodsUpdate.setState(GoodsConst.GOODS_STATE_DELETE);
        goodsUpdate.setOnlineTime(null);
        goodsUpdate.setUpdateTime(new Date());
        IntegralGoodsExample example = new IntegralGoodsExample();
        example.setIntegralGoodsIdIn(goodsIds);
        example.setStoreId(storeId);
        int number = integralGoodsWriteMapper.updateByExampleSelective(goodsUpdate, example);
        AssertUtil.notNullOrZero(number, "删除商品失败");
        //删除商品对应的标签绑定信息
        IntegralGoodsBindLabelExample labelExample = new IntegralGoodsBindLabelExample();
        labelExample.setGoodsIdIn(goodsIds);
        integralGoodsBindLabelWriteMapper.deleteByExample(labelExample);
        return number;
    }

    /**
     * 商家导入商品
     * -对比货品列表信息，构造未参与的货品列表
     * -保存商品信息
     * -保存货品信息
     * -保存商品标签绑定表
     * -保存商品图片表
     *
     * @param vendor       店铺管理员
     * @param paramDTOList 商品信息
     */
    @Transactional
    public void importIntegralGoods(Vendor vendor, List<GoodsImportFrontParamDTO> paramDTOList) {
        if (CollectionUtils.isEmpty(paramDTOList)) {
            return;
        }
        for (GoodsImportFrontParamDTO paramDTO : paramDTOList) {
            List<Product> noHaveProductList = new ArrayList<>();//未参与的货品

            //-对比货品列表信息，构造未参与的货品列表
            this.compareProductWithDb(paramDTO.getGoodsId(), paramDTO.getProductList(), noHaveProductList);

            //-保存商品信息
            IntegralGoods goods = this.importGoods(vendor, paramDTO);

            //-保存货品信息
            this.importProduct(paramDTO.getProductList(), goods, noHaveProductList);

            //-保存商品标签绑定表
            this.insertGoodsBindLabels(paramDTO.getLabelIds(), goods.getIntegralGoodsId());

            //-保存商品图片表
            this.importPicture(paramDTO.getGoodsId(), goods.getIntegralGoodsId(), vendor);
        }
    }

    /**
     * 参与的货品列表与数据库中的货品列表比对，构造未参与的货品列表
     *
     * @param goodsId           商品id
     * @param paramProductList  前端传来的货品列表
     * @param noHaveProductList 未参与的货品
     */
    private void compareProductWithDb(Long goodsId, List<GoodsImportFrontParamDTO.ProductInfo> paramProductList, List<Product> noHaveProductList) {
        //根据商品id查询所有的货品
        ProductExample example = new ProductExample();
        example.setGoodsId(goodsId);
        List<Product> dbProductList = productModel.getProductList(example,null);
        AssertUtil.notEmpty(dbProductList, "商城的货品信息不存在");

        //构造要参与的列表
        dbProductList.forEach(dbProduct -> {
            boolean isHave = true;//是否要参与
            for (GoodsImportFrontParamDTO.ProductInfo productInfo : paramProductList) {
                if (dbProduct.getProductId().equals(productInfo.getProductId())) {
                    //匹配成功，不需要参与
                    isHave = false;
                    break;
                }
            }
            if (isHave) {
                noHaveProductList.add(dbProduct);
            }
        });
    }

    /**
     * 导入商品
     *
     * @param vendor   店铺管理员
     * @param paramDTO 商品信息
     * @return
     */
    private IntegralGoods importGoods(Vendor vendor, GoodsImportFrontParamDTO paramDTO) {
        long goodsId = GoodsIdGenerator.goodsIdGenerator();//商品id
        long defaultProductId = GoodsIdGenerator.productIdGenerator();//生成一个默认货品id
        GoodsImportFrontParamDTO.ProductInfo defaultProduct = paramDTO.getProductList().get(0);//第一个为默认货品
        //查询商城的商品信息
        Goods goodsDb = goodsModel.getGoodsByGoodsId(paramDTO.getGoodsId());
        AssertUtil.notNull(goodsDb, "商城的商品信息不存在");
        //查询商城的商品扩展信息
        GoodsExtendExample example = new GoodsExtendExample();
        example.setGoodsId(paramDTO.getGoodsId());
        List<GoodsExtend> goodsExtendList = goodsExtendModel.getGoodsExtendList(example,null);
        AssertUtil.notEmpty(goodsExtendList, "商城的商品扩展信息不存在");
        GoodsExtend goodsExtend = goodsExtendList.get(0);

        IntegralGoods goods = new IntegralGoods();
        goods.setIntegralGoodsId(goodsId);
        goods.setGoodsName(goodsDb.getGoodsName());
        goods.setGoodsBrief(goodsDb.getGoodsBrief());
        goods.setMarketPrice(goodsDb.getMarketPrice());
        goods.setIntegralPrice(defaultProduct.getIntegralPrice());
        goods.setCashPrice(defaultProduct.getCashPrice());
        goods.setGoodsStock(defaultProduct.getProductStock());
        goods.setActualSales(goodsDb.getVirtualSales());
        goods.setMainSpecId(goodsDb.getMainSpecId());
        goods.setIsSelf(vendor.getStore().getIsOwnStore());
        goods.setState(this.dealGoodsState(paramDTO.getSellNow()));
        goods.setOnlineTime(goods.getState().equals(GoodsConst.GOODS_STATE_UPPER) ? new Date() : null);
        goods.setStoreId(vendor.getStoreId());
        goods.setStoreName(vendor.getStore().getStoreName());
        goods.setStoreIsRecommend(goodsDb.getStoreIsRecommend());
        goods.setMainImage(goodsDb.getMainImage());
        goods.setGoodsVideo(goodsDb.getGoodsVideo());
        goods.setDefaultProductId(defaultProductId);
        goods.setIsVatInvoice(goodsDb.getIsVatInvoice());
        goods.setCreateVendorId(vendor.getVendorId());
        goods.setCreateTime(new Date());
        goods.setUpdateTime(new Date());
        if (!StringUtil.isEmpty(goodsExtend.getSpecJson())) {
            goods.setSpecJson(integralGoodsSpecValueModel.dealSpecJson(goodsExtend.getSpecJson(), vendor));
        }
        goods.setGoodsDetails(goodsExtend.getGoodsDetails());
        int count = integralGoodsWriteMapper.insert(goods);
        AssertUtil.isTrue(count == 0, "保存积分商品信息失败");
        return goods;
    }

    /**
     * 导入货品信息
     *
     * @param productInfoList
     * @param goods
     */
    private void importProduct(List<GoodsImportFrontParamDTO.ProductInfo> productInfoList, IntegralGoods goods, List<Product> noHaveProductList) {
        //是否有默认货品id
        boolean isHasDefaultProductId = false;
        for (GoodsImportFrontParamDTO.ProductInfo productInfo : productInfoList) {
            //查询商城的货品信息
            Product productDb = productModel.getProductByProductId(productInfo.getProductId());
            AssertUtil.notNull(productDb, "商城的货品信息不存在");

            IntegralProduct product = new IntegralProduct();
            if (!isHasDefaultProductId) {
                //默认货品
                product.setIntegralProductId(goods.getDefaultProductId());
                isHasDefaultProductId = true;
                product.setIsDefault(GoodsConst.PRODUCT_IS_DEFAULT_YES);
            } else {
                //非默认货品，生成货品id
                product.setIntegralProductId(GoodsIdGenerator.productIdGenerator());
                product.setIsDefault(GoodsConst.PRODUCT_IS_DEFAULT_NO);
            }
            product.setGoodsId(goods.getIntegralGoodsId());
            product.setGoodsName(goods.getGoodsName());
            product.setSpecValues(productDb.getSpecValues());
            product.setSpecValueIds(integralGoodsSpecValueModel.dealSpecValueIds(goods.getSpecJson(), productDb.getSpecValues(), goods.getStoreId()));
            product.setStoreId(goods.getStoreId());
            product.setStoreName(goods.getStoreName());
            product.setMarketPrice(productDb.getMarketPrice());
            product.setIntegralPrice(productInfo.getIntegralPrice());
            product.setCashPrice(productInfo.getCashPrice());
            product.setProductStock(productInfo.getProductStock());
            product.setProductStockWarning(productInfo.getProductStockWarning());
            product.setProductCode(productInfo.getProductCode());
            product.setBarCode(productInfo.getBarCode());
            product.setWeight(productInfo.getWeight());
            product.setLength(productInfo.getLength());
            product.setWidth(productInfo.getWidth());
            product.setHeight(productInfo.getHeight());
            product.setMainImage(productDb.getMainImage());
            product.setState(GoodsConst.PRODUCT_STATE_1);
            int count = integralProductWriteMapper.insert(product);
            AssertUtil.isTrue(count == 0, "保存积分货品信息失败");
        }
        //导入未参与的货品
        if (!CollectionUtils.isEmpty(noHaveProductList)) {
            noHaveProductList.forEach(product -> {
                IntegralProduct noHaveProduct = new IntegralProduct();
                noHaveProduct.setIntegralProductId(GoodsIdGenerator.productIdGenerator());
                noHaveProduct.setIsDefault(GoodsConst.PRODUCT_IS_DEFAULT_NO);
                noHaveProduct.setGoodsId(goods.getIntegralGoodsId());
                noHaveProduct.setGoodsName(goods.getGoodsName());
                noHaveProduct.setSpecValues(product.getSpecValues());
                noHaveProduct.setSpecValueIds(integralGoodsSpecValueModel.dealSpecValueIds(goods.getSpecJson(), product.getSpecValues(), goods.getStoreId()));
                noHaveProduct.setStoreId(goods.getStoreId());
                noHaveProduct.setStoreName(goods.getStoreName());
                noHaveProduct.setMarketPrice(product.getMarketPrice());
                noHaveProduct.setIntegralPrice(0);
                noHaveProduct.setCashPrice(product.getProductPrice());
                noHaveProduct.setProductStock(product.getProductStock());
                noHaveProduct.setProductStockWarning(product.getProductStockWarning());
                noHaveProduct.setProductCode(product.getProductCode());
                noHaveProduct.setBarCode(product.getBarCode());
                noHaveProduct.setWeight(product.getWeight());
                noHaveProduct.setLength(product.getLength());
                noHaveProduct.setWidth(product.getWidth());
                noHaveProduct.setHeight(product.getHeight());
                noHaveProduct.setMainImage(product.getMainImage());
                noHaveProduct.setState(GoodsConst.PRODUCT_STATE_2);
                int count = integralProductWriteMapper.insert(noHaveProduct);
                AssertUtil.isTrue(count == 0, "保存积分货品信息失败");
            });
        }
    }

    /**
     * 导入图片
     *
     * @param goodsId
     * @param integralGoodsId
     * @param vendor
     */
    private void importPicture(Long goodsId, Long integralGoodsId, Vendor vendor) {
        //查询商城的商品图片
        GoodsPictureExample example = new GoodsPictureExample();
        example.setGoodsId(goodsId);
        List<GoodsPicture> list = goodsPictureModel.getGoodsPictureList(example,null);
        AssertUtil.notEmpty(list, "商城的商品图片不存在");
        list.forEach(goodsPicture -> {
            IntegralGoodsPicture integralGoodsPicture = new IntegralGoodsPicture();
            integralGoodsPicture.setGoodsId(integralGoodsId);
            integralGoodsPicture.setImagePath(goodsPicture.getImagePath());
            integralGoodsPicture.setSort(goodsPicture.getSort());
            integralGoodsPicture.setCreateId(vendor.getVendorId());
            integralGoodsPicture.setCreateTime(new Date());
            integralGoodsPicture.setStoreId(vendor.getStoreId());
            integralGoodsPicture.setIsMain(goodsPicture.getIsMain());
            integralGoodsPicture.setSpecValueId(goodsPicture.getSpecValueId());
            int count = integralGoodsPictureWriteMapper.insert(integralGoodsPicture);
            AssertUtil.isTrue(count == 0, "保存商品对应图片信息失败");
        });
    }

    /**
     * 根据integralGoodsId删除积分商品表（SPU）
     *
     * @param integralGoodsId integralGoodsId
     * @return
     */
    public Integer deleteIntegralGoods(Long integralGoodsId) {
        if (StringUtils.isEmpty(integralGoodsId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = integralGoodsWriteMapper.deleteByPrimaryKey(integralGoodsId);
        if (count == 0) {
            log.error("根据integralGoodsId：" + integralGoodsId + "删除积分商品表（SPU）失败");
            throw new MallException("删除积分商品表（SPU）失败,请重试");
        }
        return count;
    }

    /**
     * 根据integralGoodsId更新积分商品表（SPU）
     *
     * @param integralGoods
     * @return
     */
    public Integer updateIntegralGoods(IntegralGoods integralGoods) {
        if (StringUtils.isEmpty(integralGoods.getIntegralGoodsId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = integralGoodsWriteMapper.updateByPrimaryKeySelective(integralGoods);
        if (count == 0) {
            log.error("根据integralGoodsId：" + integralGoods.getIntegralGoodsId() + "更新积分商品表（SPU）失败");
            throw new MallException("更新积分商品表（SPU）失败,请重试");
        }
        return count;
    }

    /**
     * 根据查询条件信息更新积分商品表（SPU）
     *
     * @param integralGoods
     * @return
     */
    public Integer updateIntegralGoods(IntegralGoods integralGoods, IntegralGoodsExample example) {
        return integralGoodsWriteMapper.updateByExampleSelective(integralGoods, example);
    }

    /**
     * 根据integralGoodsId获取积分商品表（SPU）详情
     *
     * @param integralGoodsId integralGoodsId
     * @return
     */
    public IntegralGoods getIntegralGoodsByIntegralGoodsId(Long integralGoodsId) {
        return integralGoodsReadMapper.getByPrimaryKey(integralGoodsId);
    }

    /**
     * 根据条件获取积分商品表（SPU）列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<IntegralGoods> getIntegralGoodsList(IntegralGoodsExample example, PagerInfo pager) {
        List<IntegralGoods> integralGoodsList;
        if (pager != null) {
            pager.setRowsCount(integralGoodsReadMapper.countByExample(example));
            integralGoodsList = integralGoodsReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            integralGoodsList = integralGoodsReadMapper.listByExample(example);
        }
        return integralGoodsList;
    }
}