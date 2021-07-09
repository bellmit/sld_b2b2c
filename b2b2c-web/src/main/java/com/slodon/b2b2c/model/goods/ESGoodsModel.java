package com.slodon.b2b2c.model.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.GoodsCategoryConst;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.ImageSizeEnum;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.exception.MallException;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.goods.GoodsBindAttributeValueReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsCategoryReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsPromotionReadMapper;
import com.slodon.b2b2c.dao.read.goods.GoodsReadMapper;
import com.slodon.b2b2c.dao.read.member.MemberFollowProductReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreInnerLabelReadMapper;
import com.slodon.b2b2c.dao.read.seller.StoreLabelBindGoodsReadMapper;
import com.slodon.b2b2c.goods.dto.SearchConditionDTO;
import com.slodon.b2b2c.goods.example.GoodsBindAttributeValueExample;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.GoodsPromotionExample;
import com.slodon.b2b2c.goods.pojo.*;
import com.slodon.b2b2c.member.example.MemberFollowProductExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberFollowProduct;
import com.slodon.b2b2c.model.promotion.PromotionCommonModel;
import com.slodon.b2b2c.model.system.SettingModel;
import com.slodon.b2b2c.seller.example.StoreLabelBindGoodsExample;
import com.slodon.b2b2c.seller.pojo.StoreInnerLabel;
import com.slodon.b2b2c.seller.pojo.StoreLabelBindGoods;
import com.slodon.b2b2c.system.pojo.Setting;
import com.slodon.b2b2c.vo.goods.GoodsListVO;
import com.slodon.b2b2c.vo.goods.SearchProductVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.alibaba.fastjson.serializer.SerializerFeature.MapSortField;
import static com.alibaba.fastjson.serializer.SerializerFeature.SortField;

@Component
@Slf4j
public class ESGoodsModel {

    @Resource
    private GoodsReadMapper goodsReadMapper;
    @Resource
    private GoodsCategoryReadMapper goodsCategoryReadMapper;
    @Resource
    private GoodsPromotionReadMapper goodsPromotionReadMapper;
    @Resource
    private GoodsBindAttributeValueReadMapper goodsBindAttributeValueReadMapper;

    @Resource
    private StoreInnerLabelReadMapper storeInnerLabelReadMapper;
    @Resource
    private StoreLabelBindGoodsReadMapper storeLabelBindGoodsReadMapper;
    @Resource
    private MemberFollowProductReadMapper memberFollowProductReadMapper;
    @Resource
    private GoodsExtendModel goodsExtendModel;
    @Resource
    private PromotionCommonModel promotionCommonModel;
    @Resource
    private SettingModel settingModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建索引
     *
     * @param url
     * @param port
     * @param type job-定时更新，其他-初始化索引
     * @return
     */
    @SneakyThrows
    public boolean jobCreateIndexesES(String url, Integer port, String type) {
        //es索引上次更新的时间
        String esIndexUpdateTime = stringRedisTemplate.opsForValue().get("es_index_update_time");

        GoodsExample example = new GoodsExample();
        example.setState(GoodsConst.GOODS_STATE_UPPER);
        if ("job".equals(type)) {
            example.setUpdateTimeAfter(TimeUtil.strToDate(esIndexUpdateTime));
        }
        List<Goods> addList = goodsReadMapper.listByExample(example);

        if (!CollectionUtils.isEmpty(addList)) {
            List<SearchProductVO> addGoodsList = this.getSearchProductVOs(addList);
            BulkResponse response = this.createIndex(url, port, addGoodsList);
            if (response.hasFailures()) {
                //索引插入数据失败
                log.error(response.buildFailureMessage());
            }
        }

        if ("job".equals(type)) {
            //下架状态的商品，执行删除索引记录操作
            example.setState(null);
            example.setStateNotEquals(GoodsConst.GOODS_STATE_UPPER);
            //需要删除索引记录的商品列表
            List<Goods> deleteList = goodsReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(deleteList)) {
                this.deleteByQuery(url, port, deleteList);
            }
        }

        //更新es时间
        String updateTime = TimeUtil.getDateTimeString(new Date());
        Setting setting = new Setting();
        setting.setName("es_index_update_time");
        setting.setValue(updateTime);
        settingModel.updateSetting(setting);
        //更新redis里的配置
        stringRedisTemplate.opsForValue().set("es_index_update_time", updateTime);
        return true;
    }

    /**
     * 组装SearchProductVO对象，用来创建索引
     *
     * @param goodsList
     * @return
     */
    private List<SearchProductVO> getSearchProductVOs(List<Goods> goodsList) {
        List<SearchProductVO> searchProductVOs = new ArrayList<>();
        SearchProductVO searchProductVO;
        for (Goods goods : goodsList) {
            searchProductVO = new SearchProductVO();
            searchProductVO.setGoodsId(goods.getGoodsId().toString());
            searchProductVO.setGoodsName(goods.getGoodsName());
            searchProductVO.setBrandId(StringUtil.isNullOrZero(goods.getBrandId()) ? "" : goods.getBrandId().toString());
            searchProductVO.setBrandName(goods.getBrandName());
            searchProductVO.setStoreId(goods.getStoreId().toString());
            searchProductVO.setStoreName(goods.getStoreName());
            searchProductVO.setCategoryId1(goods.getCategoryId1().toString());
            searchProductVO.setCategoryId2(goods.getCategoryId2().toString());
            searchProductVO.setCategoryId3(goods.getCategoryId3().toString());

            GoodsCategory goodsCategory = goodsCategoryReadMapper.getByPrimaryKey(goods.getCategoryId3());
            searchProductVO.setCategoryName(goodsCategory.getCategoryName());
            searchProductVO.setContent(goods.getGoodsBrief());
            searchProductVO.setDefaultProductId(goods.getDefaultProductId());

            searchProductVO.setGoodsImage(goods.getMainImage());
            searchProductVO.setGoodsPrice(goods.getGoodsPrice());
            searchProductVO.setMarketPrice(goods.getMarketPrice());
            searchProductVO.setGoodsStock(goods.getGoodsStock());
            searchProductVO.setCommentsNumber(goods.getCommentNumber());

            //商品的系统属性
            GoodsBindAttributeValueExample valueExample = new GoodsBindAttributeValueExample();
            valueExample.setGoodsId(goods.getGoodsId());
            List<GoodsBindAttributeValue> bindAttributeValueList = goodsBindAttributeValueReadMapper.listByExample(valueExample);
            if (!CollectionUtils.isEmpty(bindAttributeValueList)) {
                StringBuffer attributeInfo = new StringBuffer();
                bindAttributeValueList.forEach(bindAttributeValue -> {
                    attributeInfo.append(",").append(bindAttributeValue.getAttributeName()).append("_").append(bindAttributeValue.getAttributeValue());
                });
                searchProductVO.setAttributeInfo(attributeInfo.toString().substring(1));
            }

            searchProductVO.setSalesNum(goods.getVirtualSales() + goods.getActualSales());
            //商品扩展信息
            GoodsExtend goodsExtend = goodsExtendModel.getGoodsExtendByGoodsId(goods.getGoodsId());
            searchProductVO.setClickNum(goodsExtend.getClickNumber());
            searchProductVO.setFollowNum(goodsExtend.getFollowNumber());
            searchProductVO.setStoreIsRecommend(goods.getStoreIsRecommend());
            searchProductVO.setIsSelf(goods.getIsSelf());


            //查询店铺分类绑定商品表
            StoreLabelBindGoodsExample bindGoodsExample = new StoreLabelBindGoodsExample();
            bindGoodsExample.setGoodsId(goods.getGoodsId());
            List<StoreLabelBindGoods> labelBindGoodsList = storeLabelBindGoodsReadMapper.listByExample(bindGoodsExample);
            if (!CollectionUtils.isEmpty(labelBindGoodsList)) {
                //商家内部一级分类ID
                StringBuilder storeInnerLabelId1 = new StringBuilder();
                //商家内部二级分类ID
                StringBuilder storeInnerLabelId2 = new StringBuilder();
                for (StoreLabelBindGoods labelBindGoods : labelBindGoodsList) {
                    StoreInnerLabel storeInnerLabel = storeInnerLabelReadMapper.getByPrimaryKey(labelBindGoods.getInnerLabelId());
                    if (storeInnerLabel.getParentInnerLabelId() == 0) {
                        //商品绑定的是一级内部分类
                        storeInnerLabelId1.append(" ").append(storeInnerLabel.getInnerLabelId());
                    } else {
                        //商品绑定的是二级内部分类
                        storeInnerLabelId2.append(" ").append(storeInnerLabel.getInnerLabelId());
                        storeInnerLabelId1.append(" ").append(storeInnerLabel.getParentInnerLabelId());
                    }
                }
                searchProductVO.setStoreInnerLabelId1(storeInnerLabelId1.toString().substring(1));
                if (!StringUtils.isEmpty(storeInnerLabelId2.toString())) {
                    searchProductVO.setStoreInnerLabelId2(storeInnerLabelId2.toString().substring(1));
                }
            }
            searchProductVO.setOnlineTime(goods.getOnlineTime().getTime());

            //商品参与的活动
            GoodsPromotionExample promotionExample = new GoodsPromotionExample();
            promotionExample.setIsEffective(PromotionConst.IS_EFFECTIVE_YES);
            promotionExample.setStartTimeBefore(new Date());
            promotionExample.setEndTimeAfter(new Date());
            //一级活动，绑定商品
            promotionExample.setBindType(PromotionConst.BIND_TYPE_1);
            promotionExample.setGoodsId(goods.getGoodsId());
            List<GoodsPromotion> list = goodsPromotionReadMapper.listByExample(promotionExample);
            //二级活动，绑定店铺
            promotionExample.setGoodsId(null);
            promotionExample.setStoreId(goods.getStoreId());
            promotionExample.setBindType(PromotionConst.BIND_TYPE_2);
            list.addAll(goodsPromotionReadMapper.listByExample(promotionExample));
            //三级活动，绑定三级分类
            promotionExample.setStoreId(null);
            promotionExample.setGoodsCategoryId3(goods.getCategoryId3());
            promotionExample.setBindType(PromotionConst.BIND_TYPE_3);
            list.addAll(goodsPromotionReadMapper.listByExample(promotionExample));
            List<GoodsListVO.PromotionVO> vos = new ArrayList<>();
            //根据活动名称过滤重复
            Set<String> set = new HashSet<>();
            if (!CollectionUtils.isEmpty(list)) {
                for (GoodsPromotion goodsActivity : list) {
                    String promotionName = promotionCommonModel.getPromotionName(goodsActivity.getPromotionType());
                    if (!set.add(promotionName)) {
                        continue;
                    }
                    GoodsListVO.PromotionVO promotionVO = new GoodsListVO.PromotionVO();
                    promotionVO.setGoodsPromotionId(goodsActivity.getGoodsPromotionId());
                    promotionVO.setPromotionId(goodsActivity.getPromotionId());
                    promotionVO.setPromotionName(promotionName);
                    promotionVO.setPromotionType(goodsActivity.getPromotionType());
                    promotionVO.setPromotionGrade(goodsActivity.getPromotionGrade());
                    vos.add(promotionVO);
                    //如果是商品活动，查询活动价格
                    if (goodsActivity.getBindType() == PromotionConst.BIND_TYPE_1) {
                        BigDecimal promotionPrice = promotionCommonModel.getPromotionPrice(goodsActivity.getPromotionType(), goodsActivity.getPromotionId(), goodsActivity.getProductId());
                        if (promotionPrice.compareTo(BigDecimal.ZERO) > 0) {
                            searchProductVO.setGoodsPrice(promotionPrice);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(vos)) {
                searchProductVO.setActivityInfo(JSON.toJSONString(vos, SortField, MapSortField));
            }
            searchProductVOs.add(searchProductVO);
        }
        return searchProductVOs;
    }

    /**
     * 创建es索引
     *
     * @param url
     * @param port
     * @param goodsList
     * @throws IOException
     */
    private BulkResponse createIndex(String url, Integer port, List<SearchProductVO> goodsList) throws IOException {
        RestHighLevelClient client = null;
        BulkResponse response = null;
        try {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(url, port, "http")));
            boolean indexExist = client.indices().exists(new GetIndexRequest(DomainUrlUtil.ES_INDEX_NAME), RequestOptions.DEFAULT);
            if (!indexExist) {
                //索引不存在，创建索引，设置索引分词格式
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(DomainUrlUtil.ES_INDEX_NAME);
                createIndexRequest.settings(Settings.builder()
                        .put("index.analysis.analyzer.default.type", "ik_max_word")
                        .put("index.analysis.analyzer.default_search.type", "ik_smart"));
                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            }
            BulkRequest bulkRequest = new BulkRequest();
            //插入数据
            for (SearchProductVO g : goodsList) {
                bulkRequest.add(new IndexRequest(DomainUrlUtil.ES_INDEX_NAME).id(g.getGoodsId()).source(JSONObject.toJSONString(g), XContentType.JSON));
            }
            response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("创建es索引异常", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return response;
    }

    /**
     * 删除es索引记录
     *
     * @param url
     * @param port
     * @param goodsList
     * @throws IOException
     */
    private void deleteByQuery(String url, Integer port, List<Goods> goodsList) throws IOException {
        RestHighLevelClient client = null;
        try {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(url, port, "http")));
            BulkRequest bulkRequest = new BulkRequest();
            for (Goods g : goodsList) {
                bulkRequest.add(new DeleteRequest(DomainUrlUtil.ES_INDEX_NAME, g.getGoodsId().toString()));
            }
            client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("删除es索引异常", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    //region es搜索商品

    /**
     * es搜索商品
     *
     * @param qc
     * @param pager
     * @return
     */
    public List<GoodsListVO> searchGoodsByES(SearchConditionDTO qc, PagerInfo pager, Member member) {
        List<GoodsListVO> goodsList = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索条件构造
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtil.isEmpty(qc.getGoodsIds())) {
            //商品id搜索
            boolQueryBuilder.must(this.searchIndexAssembling4goods(qc));
        }
        if (!StringUtil.isEmpty(qc.getCategoryIds())) {
            //分类id搜索
            boolQueryBuilder.must(this.searchIndexAssembling4cate(qc));
        }
        if (!StringUtils.isEmpty(qc.getAttributeInfo())) {
            //检索属性搜索
            boolQueryBuilder.must(this.searchIndexAssembling4Attribute(qc));
        }
        if (!StringUtil.isEmpty(qc.getBrandIds())) {
            //品牌
            boolQueryBuilder.must(this.searchIndexAssembling4brand(qc));
        }
        if (!StringUtil.isNullOrZero(qc.getStoreId())) {
            //店铺
            boolQueryBuilder.must(this.searchIndexAssembling4Store(qc));
        }
        if (!StringUtil.isNullOrZero(qc.getStoreInnerLabelId())) {
            //店铺内部分类
            boolQueryBuilder.must(this.searchIndexAssembling4StoreInnerLabel(qc));
        }
        if (!StringUtils.isEmpty(qc.getKeyword())) {
            boolQueryBuilder.must(this.searchIndexAssembling4keyword(qc));
            //搜索结果高亮设置
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field(SearchProductVO.GOODS_NAME_);
            highlightTitle.preTags("<font color=\"red\">");
            highlightTitle.postTags("</font>");
            highlightTitle.highlighterType("unified");
            highlightBuilder.field(highlightTitle);
            searchSourceBuilder.highlighter(highlightBuilder);
        }
        if (qc.getLowPrice() != null) {
            //价格最小值
            boolQueryBuilder.must(QueryBuilders.rangeQuery(SearchProductVO.GOODS_PRICE_).gte(qc.getLowPrice()));
        }
        if (qc.getHighPrice() != null) {
            //价格最大值
            boolQueryBuilder.must(QueryBuilders.rangeQuery(SearchProductVO.GOODS_PRICE_).lte(qc.getHighPrice()));
        }

        if (Optional.of(qc).map(SearchConditionDTO::getIsSelf).orElse(0) == 1) {
            //是否自营
            boolQueryBuilder.must(QueryBuilders.matchQuery(SearchProductVO.IS_SELF_, qc.getIsSelf().toString()));
        }
        if (Optional.of(qc).map(SearchConditionDTO::getStore).orElse(0) == 1) {
            //是否有货
            boolQueryBuilder.must(QueryBuilders.rangeQuery(SearchProductVO.GOODS_STOCK_).gte(1));
        }

        searchSourceBuilder.query(boolQueryBuilder);
        //分页
        if (pager != null) {
            searchSourceBuilder.from(pager.getStart());
            searchSourceBuilder.size(pager.getPageSize());
        }

        int sort = Optional.of(qc).map(SearchConditionDTO::getSort).orElse(0);
        // es 排序 0:默认排序；1销量；2评论；3价格从低到高；4、价格从高到低；5、人气从高到低；6、收藏数从高到低；7、店铺推荐
        if (sort == 0) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchProductVO.SALES_NUM_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 1) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchProductVO.SALES_NUM_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 2) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchProductVO.COMMENTS_NUMBER_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 3) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchProductVO.GOODS_PRICE_).unmappedType("long").order(SortOrder.ASC));
        } else if (sort == 4) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchProductVO.GOODS_PRICE_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 5) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchProductVO.CLICK_NUM_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 6) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchProductVO.FOLLOW_NUM_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 7) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchProductVO.STORE_IS_RECOMMEND_).unmappedType("long").order(SortOrder.DESC));
        }

        //查询商品
        RestHighLevelClient client = getESClient();
        try {
            SearchRequest searchRequest = new SearchRequest(DomainUrlUtil.ES_INDEX_NAME);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            if (pager != null) {
                pager.setRowsCount((int) hits.getTotalHits().value);
            }
            for (SearchHit hit : hits.getHits()) {
                JSONObject jsonObject = JSONObject.parseObject(hit.getSourceAsString());

                //取高亮结果
                Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
                HighlightField highlightField = highlightFieldMap.get(SearchProductVO.GOODS_NAME_);
                String goodsName = !StringUtils.isEmpty(highlightField) && !StringUtils.isEmpty(highlightField.fragments())
                        ? highlightField.fragments()[0].string()
                        : jsonObject.getString(SearchProductVO.GOODS_NAME_);
                Long goodsId = Long.valueOf(jsonObject.getString(SearchProductVO.ID_));
                Long storeId = Long.valueOf(jsonObject.getString(SearchProductVO.STORE_ID_));
                Integer categoryId3 = new Integer(jsonObject.getString(SearchProductVO.CATEGORY_ID3_));
                Integer categoryId2 = new Integer(jsonObject.getString(SearchProductVO.CATEGORY_ID2_));
                Integer categoryId1 = new Integer(jsonObject.getString(SearchProductVO.CATEGORY_ID1_));
                Integer commentsNumber = new Integer(jsonObject.getString(SearchProductVO.COMMENTS_NUMBER_));
                GoodsListVO vo = new GoodsListVO();
                vo.setGoodsId(goodsId);
                vo.setGoodsName(goodsName);
                vo.setGoodsImage(FileUrlUtil.getFileUrl(jsonObject.getString(SearchProductVO.GOODS_IMAGE_), ImageSizeEnum._300x300));
                vo.setGoodsPrice(new BigDecimal(jsonObject.getString(SearchProductVO.GOODS_PRICE_)));
                vo.setMarketPrice(jsonObject.getString(SearchProductVO.MARKET_PRICE_) == null ? null : new BigDecimal(jsonObject.getString(SearchProductVO.MARKET_PRICE_)));
                vo.setGoodsBrief(jsonObject.getString(SearchProductVO.CONTENT_));
                vo.setCommentsNumber(commentsNumber);
                vo.setDefaultProductId(jsonObject.getLong(SearchProductVO.DEFAULT_PRODUCT_ID_));
                vo.setCategoryId3(categoryId3);
                vo.setCategoryId2(categoryId2);
                vo.setCategoryId1(categoryId1);
                vo.setStoreId(storeId);
                vo.setStoreName(jsonObject.getString(SearchProductVO.STORE_NAME_));
                if (!StringUtil.isEmpty(jsonObject.getString(SearchProductVO.STORE_INNER_LABEL_ID2_))) {
                    for (String storeInnerLabelId : jsonObject.getString(SearchProductVO.STORE_INNER_LABEL_ID2_).split(" ")) {
                        vo.getStoreCategoryId().add(Integer.valueOf(storeInnerLabelId));
                    }
                }
                vo.setSaleNum(new Integer(jsonObject.getString(SearchProductVO.SALES_NUM_)));
                vo.setIsOwnShop(new Integer(jsonObject.getString(SearchProductVO.IS_SELF_)));
                //活动信息
                String activityInfo = jsonObject.getString(SearchProductVO.ACTIVITY_INFO_);
                if (!StringUtil.isEmpty(activityInfo)) {
                    List<GoodsListVO.PromotionVO> activityList = JSONArray.parseArray(activityInfo, GoodsListVO.PromotionVO.class);
                    vo.setActivityList(activityList);
                }
                //是否收藏商品
                vo.setIsFollowGoods(false);
                if (member != null && !StringUtil.isNullOrZero(member.getMemberId())) {
                    MemberFollowProductExample followProductExample = new MemberFollowProductExample();
                    followProductExample.setGoodsId(goodsId);
                    followProductExample.setMemberId(member.getMemberId());
                    List<MemberFollowProduct> followGoodsList = memberFollowProductReadMapper.listByExample(followProductExample);
                    if (!CollectionUtils.isEmpty(followGoodsList)) {
                        vo.setIsFollowGoods(true);
                    }
                }
                goodsList.add(vo);
            }
        } catch (Exception e) {
            log.error("es连接异常:", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("es连接异常:", e);
            }
        }
        return goodsList;
    }

    /**
     * 创建es连接
     *
     * @return
     */
    private RestHighLevelClient getESClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(DomainUrlUtil.SLD_ES_URL, DomainUrlUtil.SLD_ES_PORT, "http")));
    }

    /**
     * 商品id的搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4goods(SearchConditionDTO qc) {
        //商品ID
        String[] strings = qc.getGoodsIds().split(",");
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        for (String goodsId : strings) {
            queryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.ID_, goodsId));
        }
        return queryBuilder;
    }

    /**
     * 商品分类页的搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4cate(SearchConditionDTO qc) {
        //商品ID
        String[] strings = qc.getCategoryIds().split(",");
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        for (String categoryId : strings) {
            //查询分类
            GoodsCategory goodsCategory = goodsCategoryReadMapper.getByPrimaryKey(categoryId);
            AssertUtil.notNull(goodsCategory, "分类信息有误");

            //根据分类等级构造查询条件
            switch (goodsCategory.getGrade()) {
                case GoodsCategoryConst.CATEGORY_GRADE_1://一级分类
                    queryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.CATEGORY_ID1_, categoryId));
                    break;
                case GoodsCategoryConst.CATEGORY_GRADE_2://二级分类
                    queryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.CATEGORY_ID2_, categoryId));
                    break;
                case GoodsCategoryConst.CATEGORY_GRADE_3://三级分类
                    queryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.CATEGORY_ID3_, categoryId));
                    break;
                default:
                    throw new MallException("分类信息有误");
            }
        }
        return queryBuilder;
    }

    /**
     * 商品检索属性搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4Attribute(SearchConditionDTO qc) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //查询条件中包含属性信息
        //属性名称_属性值 的数组
        String[] attributes = qc.getAttributeInfo().split(",");
        for (String attribute : attributes) {
            //完全匹配
            boolQueryBuilder.should(QueryBuilders.matchPhraseQuery(SearchProductVO.ATTRIBUTE_INFO_, attribute));

        }
        return boolQueryBuilder;
    }

    /**
     * 品牌页的搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4brand(SearchConditionDTO qc) {
        // 品牌ID
        String[] strings = qc.getBrandIds().split(",");
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        for (String brandIdStr : strings) {
            queryBuilder.should(QueryBuilders.matchQuery(SearchProductVO.BRAND_ID_, brandIdStr));
        }

        return queryBuilder;
    }

    /**
     * 店铺的搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4Store(SearchConditionDTO qc) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 店铺ID
        boolQueryBuilder.must(QueryBuilders.matchQuery(SearchProductVO.STORE_ID_, qc.getStoreId().toString()));

        return boolQueryBuilder;
    }

    /**
     * 店铺内部分类搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4StoreInnerLabel(SearchConditionDTO qc) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 店铺分类
        // 店铺分类ID
        StoreInnerLabel storeInnerLabel = storeInnerLabelReadMapper.getByPrimaryKey(qc.getStoreInnerLabelId());
        AssertUtil.notNull(storeInnerLabel, "店铺内部分类不存在");
        if (storeInnerLabel.getParentInnerLabelId() == 0) {
            //一级分类
            boolQueryBuilder.must(QueryBuilders.matchQuery(SearchProductVO.STORE_INNER_LABEL_ID1_, qc.getStoreInnerLabelId().toString()));
        } else {
            //二级分类
            boolQueryBuilder.must(QueryBuilders.matchQuery(SearchProductVO.STORE_INNER_LABEL_ID2_, qc.getStoreInnerLabelId().toString()));
        }
        return boolQueryBuilder;
    }

    /**
     * 搜索页的搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4keyword(SearchConditionDTO qc) {
        String keyword = qc.getKeyword();
        keyword = StringUtil.stringFilterSpecial(keyword);

        return SearchProductVO.searchIndexAssembling(keyword);
    }

    //endregion es搜索商品
}
