package com.slodon.b2b2c.integral.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 积分商品表（SPU）example
 */
@Data
public class IntegralGoodsExample implements Serializable {
    private static final long serialVersionUID = 6308753485003922394L;
    /**
     * 用于编辑时的重复判断
     */
    private Long integralGoodsIdNotEquals;

    /**
     * 用于批量操作
     */
    private String integralGoodsIdIn;

    /**
     * 自增物理主键
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long integralGoodsId;

    /**
     * 商品名称为3到50个字符(商品副标题)
     */
    private String goodsName;

    /**
     * 商品名称为3到50个字符(商品副标题),用于模糊查询
     */
    private String goodsNameLike;

    /**
     * 商品副标题，长度建议140个字符内
     */
    private String goodsBrief;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 价格组合的最多使用积分(基础积分，可以减少积分增加现金），不能为零
     */
    private Integer integralPrice;

    /**
     * 价格组合最少使用现金(基础现金，可以增加现金，减少积分）为零则只显示积分
     */
    private BigDecimal cashPrice;

    /**
     * 商品库存
     */
    private Integer goodsStock;

    /**
     * 虚拟销量
     */
    private Integer virtualSales;

    /**
     * 实际销量
     */
    private Integer actualSales;

    /**
     * 商品主规格；0-无主规格，其他id为对应的主规格ID，主规格值切换商品主图会切换
     */
    private Integer mainSpecId;

    /**
     * 1-自营；2-商家
     */
    private Integer isSelf;

    /**
     * 状态：11-放入仓库无需审核；12-放入仓库审核通过；20-立即上架待审核；21-放入仓库待审核；3-上架（a. 审核通过上架，b. 不需要平台审核，商户创建商品后点击上架操作）；4-审核驳回(平台驳回）；5-商品下架（商户自行下架）；6-违规下架（平台违规下架操作）；7-已删除（状态1、5、6可以删除后进入此状态）
     */
    private Integer state;

    /**
     * stateIn，用于批量操作
     */
    private String stateIn;

    /**
     * stateNotIn，用于批量操作
     */
    private String stateNotIn;

    /**
     * stateNotEquals，用于批量操作
     */
    private Integer stateNotEquals;

    /**
     * 0-不推荐；1-推荐（平台是否推荐）
     */
    private Integer isRecommend;

    /**
     * 大于等于开始时间
     */
    private Date onlineTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date onlineTimeBefore;

    /**
     * 商家ID
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
     * 商品推荐，0-不推荐；1-推荐（店铺内是否推荐）
     */
    private Integer storeIsRecommend;

    /**
     * 店铺状态：1-店铺正常；0-店铺关闭 默认状态为1
     */
    private Integer storeState;

    /**
     * storeStateIn，用于批量操作
     */
    private String storeStateIn;

    /**
     * storeStateNotIn，用于批量操作
     */
    private String storeStateNotIn;

    /**
     * storeStateNotEquals，用于批量操作
     */
    private Integer storeStateNotEquals;

    /**
     * 商品主图路径，每个SPU一张主图
     */
    private String mainImage;

    /**
     * 商品视频
     */
    private String goodsVideo;

    /**
     * 默认货品id
     */
    private Long defaultProductId;

    /**
     * 是否可以开具增值税发票0-不可以；1-可以
     */
    private Integer isVatInvoice;

    /**
     * 省份编码
     */
    private String provinceCode;

    /**
     * 城市编码
     */
    private String cityCode;

    /**
     * 创建人
     */
    private Long createVendorId;

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
    private Date updateTimeAfter;

    /**
     * 小于等于结束时间
     */
    private Date updateTimeBefore;

    /**
     * 审核失败原因
     */
    private String auditReason;

    /**
     * 审核失败原因,用于模糊查询
     */
    private String auditReasonLike;

    /**
     * 商品审核失败附加备注信息
     */
    private String auditComment;

    /**
     * 违规下架原因
     */
    private String offlineReason;

    /**
     * 违规下架原因,用于模糊查询
     */
    private String offlineReasonLike;

    /**
     * 违规下架备注信息
     */
    private String offlineComment;

    /**
     * 商品规格json（商品发布使用的规格及规格值信息）
     */
    private String specJson;

    /**
     * 商品详情信息
     */
    private String goodsDetails;

    /**
     * 排序条件，条件之间用逗号隔开，如果不传则按照integralGoodsId倒序排列
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
     * 标签id
     */
    private Integer labelId;

    /**
     * 货品货号：即SKU ID（店铺内唯一）
     */
    private String productCodeLike;

    /**
     * 商品条形码（标准的商品条形码）
     */
    private String barCodeLike;
}