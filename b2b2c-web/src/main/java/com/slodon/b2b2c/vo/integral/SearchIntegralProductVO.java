package com.slodon.b2b2c.vo.integral;

import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SearchIntegralProductVO implements Serializable {

    private static final long serialVersionUID = 1093537115349190351L;
    private String goodsId;                                                 //商品ID
    private String goodsName;                                               //商品名称
    private String storeId;                                                 //店铺id
    private String storeName;                                               //店铺名称
    private String content;                                                 //描述相关，需要分词其他不需要分词
    private Long defaultProductId;                                          //默认skuId

    private String goodsImage;                                              //商品图片
    private Integer integralPrice;                                          //积分
    private BigDecimal cashPrice;                                           //现金
    private BigDecimal marketPrice;                                         //市场价
    private Integer goodsStock;                                             //库存

    private Integer salesNum;                                               //销量
    private Integer storeIsRecommend;                                       //商品推荐，0-不推荐；1-推荐（店铺内是否推荐）
    private Integer isSelf;                                                 //是否自营
    private Long onlineTime;                                                //上架时间，毫秒数
    private String labelIds;                                                //标签id集合

    public final static String ID_ = "goodsId";
    public final static String GOODS_NAME_ = "goodsName";
    public final static String STORE_ID_ = "storeId";
    public final static String STORE_NAME_ = "storeName";
    public final static String CONTENT_ = "content";
    public final static String DEFAULT_PRODUCT_ID_ = "defaultProductId";

    public final static String GOODS_IMAGE_ = "goodsImage";
    public final static String INTEGRAL_PRICE_ = "integralPrice";
    public final static String CASH_PRICE_ = "cashPrice";
    public final static String MARKET_PRICE_ = "marketPrice";
    public final static String GOODS_STOCK_ = "goodsStock";

    public final static String SALES_NUM_ = "salesNum";
    public final static String STORE_IS_RECOMMEND_ = "storeIsRecommend";
    public final static String IS_SELF_ = "isSelf";
    public final static String ONLINE_TIME_ = "onlineTime";
    public final static String LABEL_IDS_ = "labelIds";

    /**
     * 搜索条件的拼装
     *
     * @param searchKeyword
     * @return
     */
    public static BoolQueryBuilder searchIndexAssembling(String searchKeyword) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery(SearchIntegralProductVO.CONTENT_, searchKeyword).minimumShouldMatch("-49%"));
        boolQueryBuilder.should(QueryBuilders.matchQuery(SearchIntegralProductVO.GOODS_NAME_, searchKeyword).minimumShouldMatch("-49%"));
        boolQueryBuilder.should(QueryBuilders.matchQuery(SearchIntegralProductVO.STORE_NAME_, searchKeyword).minimumShouldMatch("-49%"));
        return boolQueryBuilder;
    }

}
