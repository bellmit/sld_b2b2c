package com.slodon.b2b2c.seller.pojo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 店铺表
 */
@Data
public class Store implements Serializable {
    private static final long serialVersionUID = -8901149235790938315L;

    @ApiModelProperty("店铺ID，平台统一生成")
    private Long storeId;

    @ApiModelProperty("店铺名称")
    private String storeName;

    @ApiModelProperty("店铺logo")
    private String storeLogo;

    @ApiModelProperty("店铺等级ID")
    private Integer storeGradeId;

    @ApiModelProperty("店铺等级名称")
    private String storeGradeName;

    @ApiModelProperty("店铺seo keyword")
    private String storeSeoKeyword;

    @ApiModelProperty("店铺SEO描述")
    private String storeSeoDesc;

    @ApiModelProperty("店铺评分服务（每天定时任务计算积分）")
    private String serviceScore;

    @ApiModelProperty("店铺评分发货（每天定时任务计算积分）")
    private String deliverScore;

    @ApiModelProperty("店铺评分描述（每天定时任务计算积分）")
    private String descriptionScore;

    @ApiModelProperty("商品数量")
    private Integer goodsNumber;

    @ApiModelProperty("店铺收藏")
    private Integer followNumber;

    @ApiModelProperty("开店时间")
    private Date createTime;

    @ApiModelProperty("店铺到期时间")
    private Date storeExpireTime;

    @ApiModelProperty("店铺总销售金额")
    private BigDecimal storeTotalSale;

    @ApiModelProperty("店铺总订单量")
    private Integer orderTotalCount;

    @ApiModelProperty("店铺完成订单量")
    private Integer orderFinishedCount;

    @ApiModelProperty("店铺状态 1、开启；2、关闭")
    private Integer state;

    @ApiModelProperty("自营店铺1-自营店铺，2-入驻店铺")
    private Integer isOwnStore;

    @ApiModelProperty("店铺分类ID")
    private Integer storeCategoryId;

    @ApiModelProperty("店铺分类名称")
    private String storeCategoryName;

    @ApiModelProperty("店铺主营商品名称，用','分隔，例如'男装,女装,童装'")
    private String mainBusiness;

    @ApiModelProperty("店铺客服电话")
    private String servicePhone;

    @ApiModelProperty("开店时长，1、2、3、4，单位：年")
    private Integer openTime;

    @ApiModelProperty("0-不推荐，1-推荐")
    private String isRecommend;

    @ApiModelProperty("卖家服务，商品详情售后保障处显示")
    private String serviceDetail;

    @ApiModelProperty("pc端店铺横幅")
    private String storeBannerPc;

    @ApiModelProperty("移动端店铺横幅")
    private String storeBannerMobile;

    @ApiModelProperty("店铺背景")
    private String storeBackdrop;

    @ApiModelProperty("省份编码")
    private String provinceCode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("地区编码")
    private String areaCode;

    @ApiModelProperty("省市区组合")
    private String areaInfo;

    @ApiModelProperty("店铺开店详细地址")
    private String address;

    @ApiModelProperty("结算方式：1-按月结算，2-按周结算")
    private Integer billType;

    @ApiModelProperty("店铺结算日期")
    private String billDay;

    @ApiModelProperty("免运费额度，默认为“0”，0表示不设置免运费额度，大于0则表示购买金额达到该额度时免运费")
    private Integer freeFreightLimit;

    @ApiModelProperty("店铺商品总销量")
    private Integer storeSalesVolume;

    @ApiModelProperty("店铺总浏览量")
    private Integer storeLookVolume;
}