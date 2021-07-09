package com.slodon.b2b2c.model.promotion;


import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.constant.RedisConst;
import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.promotion.*;
import com.slodon.b2b2c.dao.write.promotion.SeckillStageProductWriteMapper;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsPromotion;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.GoodsPromotionModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.seller.StoreModel;
import com.slodon.b2b2c.promotion.dto.JoinSeckillDTO;
import com.slodon.b2b2c.promotion.dto.SeckillGoodsAuditDTO;
import com.slodon.b2b2c.promotion.example.SeckillStageExample;
import com.slodon.b2b2c.promotion.example.SeckillStageProductBindMemberExample;
import com.slodon.b2b2c.promotion.example.SeckillStageProductExample;
import com.slodon.b2b2c.promotion.pojo.*;
import com.slodon.b2b2c.seller.pojo.Store;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.SeckillStageGoodsVO;
import com.slodon.b2b2c.vo.promotion.SeckillStageProductVO;
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
 * 秒杀商品表model
 */
@Component
@Slf4j
public class SeckillStageProductModel {

    @Resource
    private SeckillStageProductReadMapper seckillStageProductReadMapper;
    @Resource
    private SeckillStageProductBindMemberReadMapper seckillStageProductBindMemberReadMapper;
    @Resource
    private SeckillStageProductWriteMapper seckillStageProductWriteMapper;
    @Resource
    private SeckillReadMapper seckillReadMapper;
    @Resource
    private SeckillStageReadMapper seckillStageReadMapper;
    @Resource
    private SeckillLabelReadMapper seckillLabelReadMapper;

    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsModel goodsModel;
    @Resource
    private StoreModel storeModel;
    @Resource
    private GoodsPromotionModel goodsPromotionModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增秒杀商品表
     *
     * @param seckillStageProduct
     * @return
     */
    public Integer saveSeckillStageProduct(SeckillStageProduct seckillStageProduct) {
        int count = seckillStageProductWriteMapper.insert(seckillStageProduct);
        if (count == 0) {
            throw new MallException("添加秒杀商品表失败，请重试");
        }
        return count;
    }

    /**
     * 新增秒杀商品
     *
     * @param joinSeckillDTO
     * @param vendor
     * @return
     */
    @Transactional
    public Integer saveSeckillStageProductByDto(JoinSeckillDTO joinSeckillDTO, Vendor vendor) {

        //获取店铺信息
        Store storeDb = storeModel.getStoreByStoreId(vendor.getStoreId());

        int count = 0;
        for (JoinSeckillDTO.GoodsInfo goodsInfo : joinSeckillDTO.getGoodsInfoList()) {
            //获取商品信息
            Goods goodsDb = goodsModel.getGoodsByGoodsId(goodsInfo.getGoodsId());

            for (JoinSeckillDTO.ProductInfo productInfo : goodsInfo.getProductInfoList()) {
                //获取货品信息
                Product productDb = productModel.getProductByProductId(productInfo.getProductId());

                //新增秒杀商品表
                SeckillStageProduct seckillStageProduct = new SeckillStageProduct();
                //根据场次id获取场次信息
                SeckillStage seckillStage = seckillStageReadMapper.getByPrimaryKey(joinSeckillDTO.getStageId());
                seckillStageProduct.setStartTime(seckillStage.getStartTime());
                if (!StringUtils.isEmpty(joinSeckillDTO.getEndTime())) {
                    seckillStageProduct.setEndTime(joinSeckillDTO.getEndTime());
                } else {
                    seckillStageProduct.setEndTime(seckillStage.getEndTime());
                }
                seckillStageProduct.setProductId(productDb.getProductId());
                seckillStageProduct.setGoodsId(goodsDb.getGoodsId());
                seckillStageProduct.setGoodsName(goodsDb.getGoodsName());
                seckillStageProduct.setMainImage(goodsDb.getMainImage());
                seckillStageProduct.setStoreId(storeDb.getStoreId());
                seckillStageProduct.setStoreName(storeDb.getStoreName());
                seckillStageProduct.setProductPrice(productDb.getProductPrice());
                seckillStageProduct.setSeckillPrice(productInfo.getSeckillPrice());
                seckillStageProduct.setSeckillStock(productInfo.getSeckillStock());
                seckillStageProduct.setUpperLimit(productInfo.getUpperLimit());
                seckillStageProduct.setSeckillId(joinSeckillDTO.getSeckillId());
                //获取活动名称
                Seckill seckill = seckillReadMapper.getByPrimaryKey(joinSeckillDTO.getSeckillId());
                seckillStageProduct.setSeckillName(seckill.getSeckillName());
                seckillStageProduct.setStageId(joinSeckillDTO.getStageId());
                //获取场次名称
                seckillStageProduct.setStageName(seckillStage.getStageName());
                seckillStageProduct.setLabelId(joinSeckillDTO.getLabelId());
                //获取活动标签名称
                SeckillLabel seckillLabel = seckillLabelReadMapper.getByPrimaryKey(joinSeckillDTO.getLabelId());
                seckillStageProduct.setLabelName(seckillLabel.getLabelName());
                seckillStageProduct.setProductStock(productDb.getProductStock());

                //若审核开关关闭，则状态为审核通过
                if (stringRedisTemplate.opsForValue().get("seckill_is_audit_enable").equals(SeckillConst.SECKILL_AUDIT_SWITCH_CLOSE)) {
                    seckillStageProduct.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
                    //审核开关关闭时参加活动时将商品存入活动表
                    GoodsPromotion insertGoodsPromotion = new GoodsPromotion();
                    insertGoodsPromotion.setPromotionId(seckillStageProduct.getSeckillId());
                    insertGoodsPromotion.setStoreId(seckillStageProduct.getStoreId());
                    insertGoodsPromotion.setStoreName(seckillStageProduct.getStoreName());
                    insertGoodsPromotion.setBindTime(new Date());
                    insertGoodsPromotion.setGoodsId(seckillStageProduct.getGoodsId());
                    insertGoodsPromotion.setProductId(seckillStageProduct.getProductId());
                    insertGoodsPromotion.setPromotionGrade(PromotionConst.PROMOTION_GRADE_1);
                    insertGoodsPromotion.setPromotionType(PromotionConst.PROMOTION_TYPE_104);
                    insertGoodsPromotion.setStartTime(seckillStageProduct.getStartTime());
                    insertGoodsPromotion.setEndTime(seckillStageProduct.getEndTime());
                    insertGoodsPromotion.setDescription("秒杀商品活动");
                    insertGoodsPromotion.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
                    insertGoodsPromotion.setBindType(PromotionConst.BIND_TYPE_1);
                    count = goodsPromotionModel.saveGoodsPromotion(insertGoodsPromotion);
                    AssertUtil.isTrue(count == 0, "记录秒杀活动信息失败，请重试");
                }

                count = seckillStageProductWriteMapper.insert(seckillStageProduct);
                if (count == 0) {
                    throw new MallException("添加秒杀商品表失败，请重试");
                }
                //修改商品为锁定状态
                goodsDb.setIsLock(GoodsConst.IS_LOCK_YES);
                count = goodsModel.updateGoods(goodsDb);
                if (count == 0) {
                    throw new MallException("修改商品表失败，请重试");
                }
                //修改货品为锁定状态
                productDb.setState(GoodsConst.PRODUCT_STATE_3);
                count = productModel.updateProduct(productDb);
                if (count == 0) {
                    throw new MallException("修改货品表失败，请重试");
                }

                //参与就存入redis货品限购数量和秒杀库存
                stringRedisTemplate.opsForValue().set(RedisConst.REDIS_SECKILL_PRODUCT_BUY_LIMIT_PREFIX + productInfo.getProductId(), String.valueOf(productInfo.getUpperLimit()));
                stringRedisTemplate.opsForValue().set(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + productInfo.getProductId(), String.valueOf(productInfo.getSeckillStock()));
            }
        }
        return count;
    }

    /**
     * 根据stageProductId删除秒杀商品表
     *
     * @param stageProductId stageProductId
     * @return
     */
    public Integer deleteSeckillStageProduct(Integer stageProductId) {
        if (StringUtils.isEmpty(stageProductId)) {
            throw new MallException("请选择要删除的数据");
        }
        int count = seckillStageProductWriteMapper.deleteByPrimaryKey(stageProductId);
        if (count == 0) {
            log.error("根据stageProductId：" + stageProductId + "删除秒杀商品表失败");
            throw new MallException("删除秒杀商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据条件删除秒杀商品表
     *
     * @param example
     * @return
     */
    @Transactional
    public Integer deleteSeckillStageProductByExample(SeckillStageProductExample example) {
        int count = 0;
        count = seckillStageProductWriteMapper.deleteByExample(example);
        if (count == 0) {
            throw new MallException("删除秒杀商品表失败,请重试");
        }
        //解除商品锁定
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.setGoodsIdIn(String.valueOf(example.getGoodsId()));
        goodsExample.setStoreId(example.getStoreId());
        Goods goodsDb = goodsModel.getGoodsList(goodsExample, null).get(0);

        AssertUtil.notNull(goodsDb, "商品信息为空");
        Goods goodsUpdate = new Goods();
        goodsUpdate.setGoodsId(goodsDb.getGoodsId());
        goodsUpdate.setIsLock(GoodsConst.IS_LOCK_NO);
        count = goodsModel.updateGoods(goodsUpdate);
        if (count == 0) {
            throw new MallException("修改商品表失败,请重试");
        }
        //解除货品锁定
        ProductExample productExample = new ProductExample();
        productExample.setGoodsId(goodsDb.getGoodsId());
        productExample.setStoreId(goodsDb.getStoreId());
        List<Product> productList = productModel.getProductList(productExample, null);
        if (!CollectionUtils.isEmpty(productList)) {
            for (Product product : productList) {
                Product productUpdate = new Product();
                productUpdate.setProductId(product.getProductId());
                productUpdate.setState(GoodsConst.PRODUCT_STATE_1);
                count = productModel.updateProduct(productUpdate);
                if (count == 0) {
                    throw new MallException("修改货品表失败,请重试");
                }
            }
        }
        return count;
    }

    /**
     * 根据stageProductId更新秒杀商品表
     *
     * @param seckillStageProduct
     * @return
     */
    public Integer updateSeckillStageProduct(SeckillStageProduct seckillStageProduct) {
        if (StringUtils.isEmpty(seckillStageProduct.getStageProductId())) {
            throw new MallException("请选择要修改的数据");
        }
        int count = seckillStageProductWriteMapper.updateByPrimaryKeySelective(seckillStageProduct);
        if (count == 0) {
            log.error("根据stageProductId：" + seckillStageProduct.getStageProductId() + "更新秒杀商品表失败");
            throw new MallException("更新秒杀商品表失败,请重试");
        }
        return count;
    }

    /**
     * 根据stageProductId获取秒杀商品表详情
     *
     * @param stageProductId stageProductId
     * @return
     */
    public SeckillStageProduct getSeckillStageProductByStageProductId(Integer stageProductId) {
        return seckillStageProductReadMapper.getByPrimaryKey(stageProductId);
    }

    /**
     * 根据条件获取秒杀商品表列表
     *
     * @param fields  查询字段，逗号分隔
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillStageProduct> getSeckillStageProductFieldList(String fields, SeckillStageProductExample example, PagerInfo pager) {
        List<SeckillStageProduct> seckillStageProductList;
        if (pager != null) {
            pager.setRowsCount(seckillStageProductReadMapper.countByGoodsIdExample(fields, example));
            seckillStageProductList = seckillStageProductReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        } else {
            seckillStageProductList = seckillStageProductReadMapper.listFieldsByExample(fields, example);
        }
        return seckillStageProductList;
    }

    /**
     * 根据条件获取秒杀商品表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillStageProduct> getSeckillStageProductList(SeckillStageProductExample example, PagerInfo pager) {
        List<SeckillStageProduct> seckillStageProductList;
        if (pager != null) {
            pager.setRowsCount(seckillStageProductReadMapper.countByExample(example));
            seckillStageProductList = seckillStageProductReadMapper.listPageByExample(example, pager.getStart(), pager.getPageSize());
        } else {
            seckillStageProductList = seckillStageProductReadMapper.listByExample(example);
        }
        return seckillStageProductList;
    }

    /**
     * 根据goodsId分组获取秒杀商品表列表
     *
     * @param example 查询条件信息
     * @param pager   分页信息
     * @return
     */
    public List<SeckillStageProduct> getSeckillStageProducts(String fields, SeckillStageProductExample example, PagerInfo pager) {
        List<SeckillStageProduct> seckillStageProductList;
        if (pager != null) {
            pager.setRowsCount(seckillStageProductReadMapper.countByGoodsIdExample(fields, example));
            seckillStageProductList = seckillStageProductReadMapper.listFieldsPageByExample(fields, example, pager.getStart(), pager.getPageSize());
        } else {
            seckillStageProductList = seckillStageProductReadMapper.listFieldsByExample(fields, example);
        }
        return seckillStageProductList;
    }

    /**
     * 秒杀查看待审核 拒绝商品列表
     *
     * @param seckillId   活动id
     * @param storeName   店铺名称
     * @param goodsName   商品名称
     * @param verifyState 审核状态
     * @param pager       分页信息
     * @return
     */
    public List<SeckillStageGoodsVO> getAuditList(Integer seckillId, String storeName, String goodsName, Integer verifyState, PagerInfo pager) {
        List<SeckillStageGoodsVO> list = new ArrayList<>();

        //根据goods_id分组
        String fields = "goods_id,stage_id,main_image,goods_name,store_name,label_name,stage_name,verify_state,remark";
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setSeckillId(seckillId);
        example.setStoreNameLike(storeName);
        example.setGoodsNameLike(goodsName);
        example.setVerifyState(verifyState);
        example.setVerifyStateNotEquals(SeckillConst.SECKILL_AUDIT_STATE_2);    //审核列表不展示审核通过的
        example.setGroupBy(fields);
        example.setOrderBy("stage_id desc");
        List<SeckillStageProduct> goodsList = getSeckillStageProductFieldList(fields, example, pager);
        if (!CollectionUtils.isEmpty(goodsList)) {
            for (SeckillStageProduct seckillStageProduct : goodsList) {
                list.add(new SeckillStageGoodsVO(seckillStageProduct));
            }
        }
        return list;
    }

    /**
     * 秒杀查看商品列表
     *
     * @param storeName 店铺名称
     * @param goodsName 商品名称
     * @param stageId   场次ID
     * @param pager     分页信息
     * @return
     */
    public List<SeckillStageGoodsVO> getStageGoodsList(String storeName, String goodsName, Integer stageId, PagerInfo pager) {
        List<SeckillStageGoodsVO> list = new ArrayList<>();
        //根据goods_id分组
        String fields = "goods_id,stage_id,main_image,goods_name,store_name,label_name,stage_name,verify_state,remark";
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setStageId(stageId);
        example.setStoreNameLike(storeName);
        example.setGoodsNameLike(goodsName);
        example.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
        example.setGroupBy(fields);
        example.setOrderBy("goods_id desc");
        List<SeckillStageProduct> goodsList = getSeckillStageProductFieldList(fields, example, pager);
        if (!CollectionUtils.isEmpty(goodsList)) {
            for (SeckillStageProduct seckillStageProduct : goodsList) {
                list.add(new SeckillStageGoodsVO(seckillStageProduct));
            }
        }
        return list;
    }

    /**
     * 秒杀场次单一商品下货品列表
     *
     * @param goodsId 商品ID
     * @param stageId 场次ID
     * @return
     */
    public List<SeckillStageProductVO> getStageProductList(Long goodsId, Integer stageId, String goodsName, String storeName) {
        List<SeckillStageProductVO> list = new ArrayList<>();
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setStageId(stageId);
        example.setGoodsId(goodsId);
        example.setGoodsNameLike(goodsName);
        example.setStoreNameLike(storeName);
        List<SeckillStageProduct> productList = getSeckillStageProductList(example, null);

        if (!CollectionUtils.isEmpty(productList)) {
            for (SeckillStageProduct seckillStageProduct : productList) {
                Product product = productModel.getProductByProductId(seckillStageProduct.getProductId());
                AssertUtil.notNull(product, "获取货品品信息为空，请重试");
                SeckillStageProductVO vo = new SeckillStageProductVO(seckillStageProduct);
                vo.setSpecValues(product.getSpecValues());
                vo.setProductStock(product.getProductStock());
                list.add(vo);
            }
        }
        return list;
    }

    public Integer getCountByStageID(Integer StageID) {
        String fields = "goods_id";
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setStageId(StageID);
        example.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
        return seckillStageProductReadMapper.countByGoodsIdExample(fields, example);
    }

    /**
     * 商品审核
     *
     * @param goodsAuditDTO
     * @return
     */
    @Transactional
    public Integer Audit(SeckillGoodsAuditDTO goodsAuditDTO) {
        int number = 0;
        SeckillStageProductExample seckillStageProductExample = new SeckillStageProductExample();
        seckillStageProductExample.setGoodsIdIn(goodsAuditDTO.getGoodsIds());
        seckillStageProductExample.setStageIdIn(goodsAuditDTO.getStageIds());
        seckillStageProductExample.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_1);
        List<SeckillStageProduct> seckillStageProductList = seckillStageProductReadMapper.listByExample(seckillStageProductExample);
        AssertUtil.notNull(seckillStageProductList, "未查询到秒杀货品信息失败,请重试");
        SeckillStageProduct seckillStageProductUpdate = new SeckillStageProduct();
        //审核拒绝时 保存拒绝原因 备注
        if (goodsAuditDTO.getState().equals(SeckillConst.SECKILL_GOODS_AUDIT_REJECT)) {
            //批量保存审核拒绝时 保存拒绝原因 并将货品状态改为正常
            seckillStageProductUpdate.setRemark(goodsAuditDTO.getAuditReason());
            seckillStageProductUpdate.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_3);
            if (!CollectionUtils.isEmpty(seckillStageProductList)) {
                StringBuilder productIds = new StringBuilder();
                StringBuilder goodsIds = new StringBuilder();
                List<String> productLimitList = new ArrayList<>();
                List<String> productStockList = new ArrayList<>();
                seckillStageProductList.forEach(seckillStageProduct -> {
                    productIds.append(",").append(seckillStageProduct.getProductId());
                    goodsIds.append(",").append(seckillStageProduct.getGoodsId());

                    productLimitList.add(RedisConst.REDIS_SECKILL_PRODUCT_BUY_LIMIT_PREFIX + seckillStageProduct.getProductId());
                    productStockList.add(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + seckillStageProduct.getProductId());
                });

                //秒杀结束后 将货品表状态改为正常
                Product updateProduct = new Product();
                updateProduct.setState(GoodsConst.PRODUCT_STATE_1);//正常状态
                ProductExample example = new ProductExample();
                example.setProductIdIn(productIds.substring(1));
                productModel.updateProductByExample(updateProduct, example);
                //秒杀结束后 将商品品表状态改为非锁定
                Goods updateGoods = new Goods();
                updateGoods.setIsLock(GoodsConst.IS_LOCK_NO);//正常状态
                GoodsExample goodsExample = new GoodsExample();
                goodsExample.setGoodsIdIn(goodsIds.substring(1));
                goodsModel.updateGoodsByExample(updateGoods, goodsExample);

                //删除redis存放的货品限购数量以及库存
                stringRedisTemplate.delete(productLimitList);
                stringRedisTemplate.delete(productStockList);
            }

        } else { //审核通过处理
            seckillStageProductUpdate.setRemark("");
            seckillStageProductUpdate.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
            //保存活动货品信息到goods_promotion表
            if (!CollectionUtils.isEmpty(seckillStageProductList)) {
                seckillStageProductList.forEach(seckillStageProduct -> {
                    GoodsPromotion insertGoodsPromotion = new GoodsPromotion();
                    insertGoodsPromotion.setPromotionId(seckillStageProduct.getSeckillId());
                    insertGoodsPromotion.setStoreId(seckillStageProduct.getStoreId());
                    insertGoodsPromotion.setStoreName(seckillStageProduct.getStoreName());
                    insertGoodsPromotion.setBindTime(new Date());
                    insertGoodsPromotion.setGoodsId(seckillStageProduct.getGoodsId());
                    insertGoodsPromotion.setProductId(seckillStageProduct.getProductId());
                    insertGoodsPromotion.setPromotionGrade(PromotionConst.PROMOTION_GRADE_1);
                    insertGoodsPromotion.setPromotionType(PromotionConst.PROMOTION_TYPE_104);
                    insertGoodsPromotion.setStartTime(seckillStageProduct.getStartTime());
                    insertGoodsPromotion.setEndTime(seckillStageProduct.getEndTime());
                    insertGoodsPromotion.setDescription("秒杀商品活动");
                    insertGoodsPromotion.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
                    insertGoodsPromotion.setBindType(PromotionConst.BIND_TYPE_1);
                    Integer count = goodsPromotionModel.saveGoodsPromotion(insertGoodsPromotion);
                    AssertUtil.isTrue(count == 0, "记录秒杀活动信息失败，请重试");
                });
            }
        }
        number = seckillStageProductWriteMapper.updateByExampleSelective(seckillStageProductUpdate, seckillStageProductExample);
        return number;
    }


    /**
     * 秒杀场次开始后将秒杀货品和限购数量放入redis
     *
     * @return
     */
    public boolean jobSaveSeckillStageProduct() {
        //查询当前开始的秒杀场次
        SeckillStageExample example = new SeckillStageExample();

        //去当前时间最近的下一个整分钟 10：56：09 时取11：07：00
        Calendar ca = Calendar.getInstance();
        ca.add(Calendar.MINUTE, 1);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        System.out.println("获取小于等于: " + ca.getTime() + "的秒杀场次");
        example.setStartTimeBefore(ca.getTime());
        example.setEndTimeAfter(ca.getTime());
        List<SeckillStage> list = seckillStageReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(list)) {
            log.info("未查询到当前时间:" + ca.getTime() + "的场次");
            return false;
        }

        //同一时间所有场次IDs
        StringBuilder stageIds = new StringBuilder();
        list.forEach(seckillStage -> {
            log.info("查询到当前时间:" + ca.getTime() + "的场次" + seckillStage.getStageName());
            stageIds.append(",").append(seckillStage.getStageId());
        });

        //查询当前时间下所有场次待审核的货品
        SeckillStageProductExample productExample = new SeckillStageProductExample();
        productExample.setStageIdIn(stageIds.substring(1));
        productExample.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_1);
        List<SeckillStageProduct> seckillStageProducts = seckillStageProductReadMapper.listByExample(productExample);
        if (!CollectionUtils.isEmpty(seckillStageProducts)) {
            StringBuilder productIds = new StringBuilder();
            StringBuilder goodsIds = new StringBuilder();
            List<String> productLimitList = new ArrayList<>();
            List<String> productStockList = new ArrayList<>();
            for (SeckillStageProduct seckillStageProduct : seckillStageProducts) {
                //待审核状态改为审核拒绝
                SeckillStageProduct seckillStageProductUpdate = new SeckillStageProduct();
                seckillStageProductUpdate.setStageProductId(seckillStageProduct.getStageProductId());
                seckillStageProductUpdate.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_3);
                seckillStageProductUpdate.setRemark("系统自动拒绝");
                seckillStageProductWriteMapper.updateByPrimaryKeySelective(seckillStageProductUpdate);

                productIds.append(",").append(seckillStageProduct.getProductId());
                goodsIds.append(",").append(seckillStageProduct.getGoodsId());

                productLimitList.add(RedisConst.REDIS_SECKILL_PRODUCT_BUY_LIMIT_PREFIX + seckillStageProduct.getProductId());
                productStockList.add(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + seckillStageProduct.getProductId());
            }
            //审核拒绝后 将货品表状态改为正常
            Product updateProduct = new Product();
            updateProduct.setState(GoodsConst.PRODUCT_STATE_1);//正常状态
            ProductExample productExample1 = new ProductExample();
            productExample1.setProductIdIn(productIds.substring(1));
            productModel.updateProductByExample(updateProduct, productExample1);

            //审核拒绝后 将商品表状态改为未锁定
            Goods updateGoods = new Goods();
            updateGoods.setIsLock(GoodsConst.IS_LOCK_NO);//正常状态
            GoodsExample goodsExample = new GoodsExample();
            goodsExample.setGoodsIdIn(goodsIds.substring(1));
            goodsModel.updateGoodsByExample(updateGoods, goodsExample);

            //删除redis存放的货品限购数量以及库存
            stringRedisTemplate.delete(productLimitList);
            stringRedisTemplate.delete(productStockList);
        }

        //查询当前时间下所有场次的审核通过的货品
        SeckillStageProductExample seckillStageProductExample = new SeckillStageProductExample();
        seckillStageProductExample.setStageIdIn(stageIds.substring(1));
        seckillStageProductExample.setVerifyState(SeckillConst.SECKILL_STATE_2);
        List<SeckillStageProduct> seckillStageProductList = seckillStageProductReadMapper.listByExample(seckillStageProductExample);

        if (!CollectionUtils.isEmpty(seckillStageProductList)) {
            log.info("秒杀定时任务查询到商品列表" + seckillStageProductList.size());
            StringBuilder productIds = new StringBuilder();
            seckillStageProductList.forEach(seckillStageProduct -> {
                log.info("秒杀定时任务查询到商品" + seckillStageProduct.getProductId());
                productIds.append(seckillStageProduct.getProductId());
                productIds.append(",");

                //秒杀价格保存到货品表
                Product product = new Product();
                product.setProductId(seckillStageProduct.getProductId());
                product.setActivityPrice(seckillStageProduct.getSeckillPrice());
                productModel.updateProduct(product);

                //查询是否商品默认货品，如果是活动开始修改商品价格，es同步更新为活动价格
                Goods goodsDb = goodsModel.getGoodsByGoodsId(seckillStageProduct.getGoodsId());
                AssertUtil.notNull(goodsDb, "商品不存在");
                if (goodsDb.getDefaultProductId().equals(seckillStageProduct.getProductId())) {
                    Goods goods = new Goods();
                    goods.setGoodsId(seckillStageProduct.getGoodsId());
                    goods.setGoodsPrice(seckillStageProduct.getSeckillPrice());
                    goods.setUpdateTime(new Date());
                    goodsModel.updateGoods(goods);
                }
            });
            SeckillStageProductExample stageProductExample = new SeckillStageProductExample();
            stageProductExample.setProductIdIn(productIds.substring(0, productIds.length() - 1));
            stageProductExample.setState(SeckillConst.SECKILL_STATE_1);
            log.info("设置秒杀场次开始货品" + productIds);
            SeckillStageProduct updateSeckillStageProduct = new SeckillStageProduct();
            updateSeckillStageProduct.setState(SeckillConst.SECKILL_STATE_2);
            seckillStageProductWriteMapper.updateByExampleSelective(updateSeckillStageProduct, stageProductExample);
        }
        return true;
    }

    /**
     * 秒杀场次结束后将秒杀货品的库存从redis放回商品表,并将场次货品表中的状态设为已结束
     *
     * @return
     */
    public boolean jobRecoverSeckillProduct() {
        //查询当前进行中 且时间到了结束时间的货品
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setEndTimeBefore(new Date());
        example.setState(SeckillConst.SECKILL_STATE_2);
        List<SeckillStageProduct> seckillStageProductList = seckillStageProductReadMapper.listByExample(example);
        if (CollectionUtils.isEmpty(seckillStageProductList)) {
            log.info("未查询到当前时间:" + new Date() + "结束秒杀的货品");
            //没有就正常返回不处理
            return true;
        }

        Set<String> redisDeleteSet = new HashSet<>();

        seckillStageProductList.forEach(seckillStageProduct -> {
            log.info("查询到当前时间结束秒杀的货品:" + seckillStageProduct.getGoodsName());

            //秒杀结束后 将商品品表状态改为非锁定
            Goods updateGoods = new Goods();
            updateGoods.setGoodsId(seckillStageProduct.getGoodsId());
            updateGoods.setIsLock(GoodsConst.IS_LOCK_NO);//正常状态
            //查询是否商品默认货品，如果是活动结束修改商品价格，es同步更新为默认货品价格
            Goods goodsDb = goodsModel.getGoodsByGoodsId(seckillStageProduct.getGoodsId());
            AssertUtil.notNull(goodsDb, "商品不存在");
            if (goodsDb.getDefaultProductId().equals(seckillStageProduct.getProductId())) {
                //查询默认货品信息
                Product product = productModel.getProductByProductId(goodsDb.getDefaultProductId());
                AssertUtil.notNull(product, "默认货品不存在");
                updateGoods.setGoodsPrice(product.getProductPrice());
            }
            updateGoods.setUpdateTime(new Date());
            goodsModel.updateGoods(updateGoods);

            //秒杀商品结束后 将货品表状态改为正常
            Product updateProduct = new Product();
            updateProduct.setProductId(seckillStageProduct.getProductId());
            updateProduct.setState(GoodsConst.PRODUCT_STATE_1);//正常状态
            updateProduct.setActivityPrice(BigDecimal.ZERO);//活动结束活动价格变为0
            productModel.updateProduct(updateProduct);

            //秒杀商品结束后 将秒杀场次绑定货品表状态改为结束
            SeckillStageProduct updateSeckillStageProduct = new SeckillStageProduct();
            updateSeckillStageProduct.setStageProductId(seckillStageProduct.getStageProductId());
            updateSeckillStageProduct.setState(SeckillConst.SECKILL_STATE_3);
            seckillStageProductWriteMapper.updateByPrimaryKeySelective(updateSeckillStageProduct);

            //删除redis中的秒杀信息
            redisDeleteSet.add(RedisConst.REDIS_SECKILL_PRODUCT_BUY_LIMIT_PREFIX + seckillStageProduct.getProductId());
            redisDeleteSet.add(RedisConst.REDIS_SECKILL_PRODUCT_STOCK_PREFIX + seckillStageProduct.getProductId());
        });

        stringRedisTemplate.delete(redisDeleteSet);
        return true;
    }

    /**
     * 秒杀商品设置提醒成功后，距离秒杀场次开始时间3分钟时，向会员发送站内信及APP消息推送
     *
     * @return
     */
    @Transactional
    public List<SeckillStageProductBindMember> jobSendSeckillStageProductNotice() {
        List<SeckillStageProductBindMember> bindMemberList = new ArrayList<>();
        //查询1小时后的秒杀场次
        SeckillStageExample notStartStageExample = new SeckillStageExample();
        notStartStageExample.setStartTimeAfter(new Date());
        Date date = TimeUtil.getHourAgoDate(new Date(), 1);
        notStartStageExample.setStartTimeBefore(date);
        notStartStageExample.setOrderBy("start_time ASC");
        List<SeckillStage> notStartList = seckillStageReadMapper.listByExample(notStartStageExample);
        if (!CollectionUtils.isEmpty(notStartList)) {
            notStartList.forEach(SeckillStage -> {
                //查询该场次下审核通过的货品
                SeckillStageProductExample seckillStageProductExample = new SeckillStageProductExample();
                seckillStageProductExample.setStageId(SeckillStage.getStageId());
                seckillStageProductExample.setVerifyState(SeckillConst.SECKILL_AUDIT_STATE_2);
                List<SeckillStageProduct> seckillStageProductList = seckillStageProductReadMapper.listByExample(seckillStageProductExample);
                if (!CollectionUtils.isEmpty(seckillStageProductList)) {
                    seckillStageProductList.forEach(seckillStageProduct -> {
                        SeckillStageProductBindMemberExample productBindMemberExample = new SeckillStageProductBindMemberExample();
                        productBindMemberExample.setStageProductId(seckillStageProduct.getStageProductId());
                        List<SeckillStageProductBindMember> productBindMemberList = seckillStageProductBindMemberReadMapper.listByExample(productBindMemberExample);
                        if (!CollectionUtils.isEmpty(productBindMemberList)) {
                            bindMemberList.add(productBindMemberList.get(0));
                        }
                    });
                }
            });
        }

        return bindMemberList;
    }
}