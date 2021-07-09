package com.slodon.b2b2c.goods.example;

import com.slodon.b2b2c.core.response.PagerInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品公共信息表（SPU）example
 */
@Data
public class GoodsExample implements Serializable {
    private static final long serialVersionUID = 1640657789224788735L;

    /**
     * 用于编辑时的重复判断
     */
    private Long goodsIdNotEquals;

    /**
     * 用于批量操作
     */
    private String goodsIdIn;

    /**
     * 用于批量操作
     */
    private String goodsIdNotIn;

    /**
     * 自增物理主键
     */
    private Long id;

    /**
     * 商品ID
     */
    private Long goodsId;

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
     * 商品关键字，用于检索商品，用逗号分隔
     */
    private String keyword;

    /**
     * 商品关键字，用于检索商品，用逗号分隔,用于模糊查询
     */
    private String keywordLike;

    /**
     * 品牌ID
     */
    private Integer brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 品牌名称,用于模糊查询
     */
    private String brandNameLike;

    /**
     * 1级分类ID
     */
    private Integer categoryId1;

    /**
     * 2级分类ID
     */
    private Integer categoryId2;

    /**
     * 3级分类ID
     */
    private Integer categoryId3;

    /**
     * 商品所属分类路径，(例如：分类1/分类2/分类3，前后都无斜杠)
     */
    private String categoryPath;

    /**
     * 市场价
     */
    private BigDecimal marketPrice;

    /**
     * 商城价销售价格
     */
    private BigDecimal goodsPrice;

    /**
     * 活动价格，发布单减活动时，设置活动价
     */
    private BigDecimal promotionPrice;

    /**
     * 活动类型
     */
    private Integer promotionType;

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
     * 0-没有启用规格；1-启用规格
     */
    private Integer isSpec;

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
     * 评价总数
     */
    private Integer commentNumber;

    /**
     * 是否是虚拟商品：1-实物商品；2-虚拟商品
     */
    private Integer isVirtualGoods;

    /**
     * 商品主图路径，每个SPU一张主图
     */
    private String mainImage;

    /**
     * 商品视频
     */
    private String goodsVideo;

    /**
     * 商品条形码（标准的商品条形码）
     */
    private String barCode;

    /**
     * 商品服务标签；用英文逗号分隔（仅作展示使用）
     */
    private String serviceLabelIds;

    /**
     * 是否违规下架(0-否，1-是)
     */
    private Integer isOffline;

    /**
     * 商品是否被删除：0-否，1-是；商品伪删除
     */
    private Integer isDelete;

    /**
     * 商品是否被锁定：0-否，1-是；商品参与活动后被锁定，锁定期间不能编辑，活动结束后解除锁定
     */
    private Integer isLock;

    /**
     * 默认货品id
     */
    private Long defaultProductId;

    /**
     * 是否可以开具增值税发票0-不可以；1-可以
     */
    private Integer isVatInvoice;

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
     * 排序条件，条件之间用逗号隔开，如果不传则按照goodsId倒序排列
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
     * 用于判断商品上新时使用
     */
    private String lastNew;

    /**
     * 货品货号：即SKU ID（店铺内唯一）
     */
    private String productCodeLike;

    /**
     * 商品条形码（标准的商品条形码）
     */
    private String barCodeLike;

    /**
     * 上架时间,用于模糊查询
     */
    private String onlineTimeLike;

    /**
     * 上架时间大于
     */
    private String onlineTimeLikeAfter;

    /**
     * 查询销量大于0
     */
    private String querySales;
}