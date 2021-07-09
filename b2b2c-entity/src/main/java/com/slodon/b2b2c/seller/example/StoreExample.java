package com.slodon.b2b2c.seller.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class StoreExample implements Serializable {
    private static final long serialVersionUID = 1166108227252621115L;

    /**
     * 用于编辑时的重复判断
     */
    private Long storeIdNotEquals;

    /**
     * 用于批量操作
     */
    private String storeIdIn;

    /**
     * 店铺ID，平台统一生成
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 店铺名称,用于模糊查询
     */
    private String storeNameLike;

    /**
     * 店铺logo
     */
    private String storeLogo;

    /**
     * 店铺等级ID
     */
    private Integer storeGradeId;

    /**
     * 店铺等级名称
     */
    private String storeGradeName;

    /**
     * 店铺等级名称,用于模糊查询
     */
    private String storeGradeNameLike;

    /**
     * 店铺seo keyword
     */
    private String storeSeoKeyword;

    /**
     * 店铺SEO描述
     */
    private String storeSeoDesc;

    /**
     * 店铺评分服务（每天定时任务计算积分）
     */
    private String serviceScore;

    /**
     * 店铺评分发货（每天定时任务计算积分）
     */
    private String deliverScore;

    /**
     * 店铺评分描述（每天定时任务计算积分）
     */
    private String descriptionScore;

    /**
     * 商品数量
     */
    private Integer goodsNumber;

    /**
     * 商品数量不为空
     */
    private String goodsNumberNotNull;

    /**
     * 商品数量,用于模糊查询
     */
    private String goodsNumberLike;

    /**
     * 店铺收藏
     */
    private Integer followNumber;

    /**
     * 大于等于开始时间
     */
    private Date createTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date createTimeBefore;

    /**
     * 大于等于开始时间
     */
    private Date storeExpireTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date storeExpireTimeBefore;

    /**
     * 店铺总销售金额
     */
    private BigDecimal storeTotalSale;

    /**
     * 店铺总订单量
     */
    private Integer orderTotalCount;

    /**
     * 店铺完成订单量
     */
    private Integer orderFinishedCount;

    /**
     * 店铺状态 1、开启；2、关闭
     */
    private Integer state;

    /**
     * 店铺状态 1、开启；2、关闭；3、删除，用于展示
     */
    private Integer stateNotEquals;

    /**
     * 自营店铺1-自营店铺，2-入驻店铺
     */
    private Integer isOwnStore;

    /**
     * 店铺分类ID
     */
    private Integer storeCategoryId;

    /**
     * 店铺分类名称
     */
    private String storeCategoryName;

    /**
     * 店铺分类名称,用于模糊查询
     */
    private String storeCategoryNameLike;

    /**
     * 店铺主营商品名称，用','分隔，例如'男装,女装,童装'
     */
    private String mainBusiness;

    /**
     * 店铺客服电话
     */
    private String servicePhone;

    /**
     * 开店时长
     */
    private Integer openTime;

    /**
     * 0-不推荐，1-推荐
     */
    private String isRecommend;

    /**
     * 卖家服务，商品详情售后保障处显示
     */
    private String serviceDetail;

    /**
     * pc端店铺横幅
     */
    private String storeBannerPc;

    /**
     * 移动端店铺横幅
     */
    private String storeBannerMobile;

    /**
     * 店铺背景
     */
    private String storeBackdrop;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 地区编码
     */
    private String areaCode;

    /**
     * 省市区组合
     */
    private String areaInfo;

    /**
     * 店铺开店详细地址
     */
    private String address;

    /**
     * 结算方式：1-按月结算，2-按周结算
     */
    private Integer billType;

    /**
     * 店铺结算日期
     */
    private String billDay;

    /**
     * 免运费额度，默认为“0”，0表示不设置免运费额度，大于0则表示购买金额达到该额度时免运费
     */
    private Integer freeFreightLimit;

    /**
     * 店铺商品总销量
     */
    private Integer storeSalesVolume;

    /**
     * 店铺总浏览量
     */
    private Integer storeLookVolume;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照storeId倒序排列
     */
    private String orderBy;

    /**
     * 分组条件
     */
    private String groupBy;

    /**
     * 分页信息
     */
    private PagerInfo pager;

    /**
     * 店主账号
     */
    private String vendorNameLike;
}