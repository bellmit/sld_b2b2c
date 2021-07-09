package com.slodon.b2b2c.vo.goods;

import lombok.Data;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SearchProductVO implements Serializable {

    private static final long serialVersionUID = 4869522769556435643L;
    private String goodsId;                                     //商品ID
    private String goodsName;                                   //商品名称
    private String brandId;                                     //品牌id
    private String brandName;                                   //品牌名称
    private String storeId;                                     //店铺id
    private String storeName;                                   //店铺名称
    private String CategoryId1;                                 //一级分类id
    private String CategoryId2;                                 //二级分类id
    private String CategoryId3;                                 //三级分类id
    private String CategoryName;                                //分类名称
    private String content;                                     //描述相关，需要分词其他不需要分词
    private Long defaultProductId;                              //默认skuId

    private String goodsImage;                                  //商品图片
    private BigDecimal goodsPrice;                              //商品价格
    private BigDecimal marketPrice;                             //市场价
    private Integer goodsStock;                                 //库存
    private Integer commentsNumber;                             //评论数

    private String attributeInfo;                               //属性信息，格式为：属性1名称_属性值名称,属性2名称_属性值名称
    private Integer salesNum;                                   //销量
    private Integer clickNum;                                   //浏览量（人气）
    private Integer followNum;                                  //被收藏数量
    private Integer storeIsRecommend;                           //商品推荐，0-不推荐；1-推荐（店铺内是否推荐）
    private Integer isSelf;                                     //是否自营
    private String storeInnerLabelId1;                          //商家内部一级分类ID
    private String storeInnerLabelId2;                          //商家内部二级分类ID
    private Long onlineTime;                                    //上架时间，毫秒数
    private String activityInfo;                                //活动信息

    public final static String ID_ = "goodsId";
    public final static String GOODS_NAME_ = "goodsName";
    public final static String BRAND_ID_ = "brandId";
    public final static String BRAND_NAME_ = "brandName";
    public final static String STORE_ID_ = "storeId";
    public final static String STORE_NAME_ = "storeName";
    public final static String CATEGORY_ID1_ = "categoryId1";
    public final static String CATEGORY_ID2_ = "categoryId2";
    public final static String CATEGORY_ID3_ = "categoryId3";
    public final static String CATEGORY_NAME_ = "categoryName";
    public final static String CONTENT_ = "content";
    public final static String DEFAULT_PRODUCT_ID_ = "defaultProductId";

    public final static String GOODS_IMAGE_ = "goodsImage";
    public final static String GOODS_PRICE_ = "goodsPrice";
    public final static String MARKET_PRICE_ = "marketPrice";
    public final static String GOODS_STOCK_ = "goodsStock";
    public final static String COMMENTS_NUMBER_ = "commentsNumber";

    public final static String ATTRIBUTE_INFO_ = "attributeInfo";
    public final static String SALES_NUM_ = "salesNum";
    public final static String CLICK_NUM_ = "clickNum";
    public final static String FOLLOW_NUM_ = "followNum";
    public final static String STORE_IS_RECOMMEND_ = "storeIsRecommend";
    public final static String IS_SELF_ = "isSelf";
    public final static String STORE_INNER_LABEL_ID1_ = "storeInnerLabelId1";
    public final static String STORE_INNER_LABEL_ID2_ = "storeInnerLabelId2";
    public final static String ONLINE_TIME_ = "onlineTime";
    public final static String ACTIVITY_INFO_ = "activityInfo";

    /**
     * 搜索条件的拼装
     *
     * @param searchKeyword
     * @return
     */
    public static BoolQueryBuilder searchIndexAssembling(String searchKeyword) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.CONTENT_, searchKeyword).minimumShouldMatch("-49%"));
        boolQueryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.GOODS_NAME_, searchKeyword).minimumShouldMatch("-49%"));
        boolQueryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.BRAND_NAME_, searchKeyword).minimumShouldMatch("-49%"));
        boolQueryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.STORE_NAME_, searchKeyword).minimumShouldMatch("-49%"));
        boolQueryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.CATEGORY_NAME_, searchKeyword).minimumShouldMatch("-49%"));
        return boolQueryBuilder;
    }

}
