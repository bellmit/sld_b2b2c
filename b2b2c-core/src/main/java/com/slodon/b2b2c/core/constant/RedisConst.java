package com.slodon.b2b2c.core.constant;

/**
 * redis缓存相关常量
 */
public class RedisConst {

    /**
     * redis中的 key值
     */
    public final static String REGION = "region";//地址

    /**
     * 取缓存的条件：
     * 1.redis可用，并且有相关的缓存数据
     * 2.save_time >= update_time
     * 数据库中修改或新增数据时，更新update_time
     * 更新redis缓存数据时，更新save_time
     */
    public final static String EXPRESS_COMPANY = "express_company";//快递公司
    public final static String EXPRESS_COMPANY_SAVE_TIME = "express_company_save_time"; //redis中快递公司数据保存时间
    public final static String EXPRESS_COMPANY_UPDATE_TIME = "express_company_update_time";//数据库中快递公司数据修改时间（新增数据或修改数据）

    public final static String GOODS_CATEGORY = "goods_category";//商品分类
    public final static String GOODS_CATEGORY_SAVE_TIME = "goods_category_SAVE_TIME"; //redis中商品分类数据保存时间
    public final static String GOODS_CATEGORY_UPDATE_TIME = "goods_category_UPDATE_TIME";//数据库中商品分类数据修改时间（新增数据或修改数据）

    public final static String PC_GOODS_CATEGORY = "goods_category";//PC商品分类
    public final static String PC_GOODS_CATEGORY_SAVE_TIME = "goods_category_SAVE_TIME"; //redis中PC商品分类数据保存时间
    public final static String PC_GOODS_CATEGORY_UPDATE_TIME = "goods_category_UPDATE_TIME";//数据库中PC商品分类数据修改时间（新增数据或修改数据）

    public final static String STORE_CATEGORY = "store_category";//店铺分类
    public final static String STORE_CATEGORY_SAVE_TIME = "store_category_SAVE_TIME"; //redis中店铺分类数据保存时间
    public final static String STORE_CATEGORY_UPDATE_TIME = "store_category_UPDATE_TIME";//数据库中店铺分类数据修改时间（新增数据或修改数据）

    public final static String STORE_GRADE = "store_grade";//店铺等级
    public final static String STORE_GRADE_SAVE_TIME = "store_grade_SAVE_TIME"; //redis中店铺等级数据保存时间
    public final static String STORE_GRADE_UPDATE_TIME = "store_grade_UPDATE_TIME";//数据库中店铺等级数据修改时间（新增数据或修改数据）

    /**
     * reids中保存的预售商品已购买数量的key前缀,key=前缀+goodsId+memberId
     */
    public static final String PRE_SELL_PURCHASED_NUM_PREFIX = "pre_sell_purchased_num_";

    /**
     * reids中保存的阶梯团商品已购买数量的key前缀,key=前缀+goodsId+memberId
     */
    public static final String LADDER_GROUP_PURCHASED_NUM_PREFIX = "ladder_group_purchased_num_";

    /**
     * reids中保存的秒杀商品库存的key前缀,key=前缀+productId
     */
    public static final String REDIS_SECKILL_PRODUCT_STOCK_PREFIX = "seckill_stage_product_stock_";
    /**
     * reids中保存的秒杀商品限购的key前缀,key=前缀+productId
     */
    public static final String REDIS_SECKILL_PRODUCT_BUY_LIMIT_PREFIX = "seckill_stage_product_upper_limit_";
    /**
     * reids中保存的秒杀商品用户已购买数量的key前缀,key=前缀+productId+_+memberId
     */
    public static final String REDIS_SECKILL_MEMBER_BUY_NUM_PREFIX = "seckill_member_buy_num_";

    /**
     * reids中保存的拼团商品已购买数量的key前缀,key=前缀+goodsId+memberId
     */
    public static final String SPELL_PURCHASED_NUM_PREFIX = "spell_purchased_num_";

    /**
     * reids中保存的直播rtmp的key前缀,key=前缀+liveId
     */
    public static final String LIVE_RTMP_PREFIX = "live_rtmp_";

    /**
     * reids中保存的直播m3u8的key前缀,key=前缀+liveId
     */
    public static final String LIVE_M3U8_PREFIX = "live_m3u8_";

}
