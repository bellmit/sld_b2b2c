package com.slodon.b2b2c.model.integral;

import com.alibaba.fastjson.JSONObject;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.util.FileUrlUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsBindLabelReadMapper;
import com.slodon.b2b2c.dao.read.integral.IntegralGoodsReadMapper;
import com.slodon.b2b2c.integral.dto.GoodsSearchConditionDTO;
import com.slodon.b2b2c.integral.example.IntegralGoodsBindLabelExample;
import com.slodon.b2b2c.integral.example.IntegralGoodsExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoods;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsBindLabel;
import com.slodon.b2b2c.vo.integral.IntegralGoodsListVO;
import com.slodon.b2b2c.vo.integral.SearchIntegralProductVO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class IntegralESGoodsModel {

    @Resource
    private IntegralGoodsReadMapper integralGoodsReadMapper;
    @Resource
    private IntegralGoodsBindLabelReadMapper integralGoodsBindLabelReadMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 创建索引
     *
     * @param url
     * @param port
     * @param type
     * @return
     */
    @SneakyThrows
    public boolean jobCreateIndexesES(String url, Integer port, String type) {
        //es索引上次更新的时间
        String esIndexUpdateTime = stringRedisTemplate.opsForValue().get("integral_es_index_update_time");

        IntegralGoodsExample example = new IntegralGoodsExample();
        example.setState(GoodsConst.GOODS_STATE_UPPER);
        if ("job".equals(type)) {
            example.setUpdateTimeAfter(TimeUtil.strToDate(esIndexUpdateTime));
        }
        List<IntegralGoods> addList = integralGoodsReadMapper.listByExample(example);

        if (!CollectionUtils.isEmpty(addList)) {
            List<SearchIntegralProductVO> addGoodsList = this.getSearchIntegralProductVOs(addList);
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
            List<IntegralGoods> deleteList = integralGoodsReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(deleteList)) {
                this.deleteByQuery(url, port, deleteList);
            }
        }
        return true;
    }

    /**
     * 组装SearchIntegralProductVO对象，用来创建索引
     *
     * @param goodsList
     * @return
     */
    private List<SearchIntegralProductVO> getSearchIntegralProductVOs(List<IntegralGoods> goodsList) {
        List<SearchIntegralProductVO> SearchIntegralProductVOs = new ArrayList<>();
        SearchIntegralProductVO SearchIntegralProductVO;
        for (IntegralGoods goods : goodsList) {
            SearchIntegralProductVO = new SearchIntegralProductVO();
            SearchIntegralProductVO.setGoodsId(goods.getIntegralGoodsId().toString());
            SearchIntegralProductVO.setGoodsName(goods.getGoodsName());
            SearchIntegralProductVO.setStoreId(goods.getStoreId().toString());
            SearchIntegralProductVO.setStoreName(goods.getStoreName());
            SearchIntegralProductVO.setContent(goods.getGoodsBrief());
            SearchIntegralProductVO.setDefaultProductId(goods.getDefaultProductId());

            SearchIntegralProductVO.setGoodsImage(goods.getMainImage());
            SearchIntegralProductVO.setIntegralPrice(goods.getIntegralPrice());
            SearchIntegralProductVO.setCashPrice(goods.getCashPrice());
            SearchIntegralProductVO.setMarketPrice(goods.getMarketPrice());
            SearchIntegralProductVO.setGoodsStock(goods.getGoodsStock());

            SearchIntegralProductVO.setSalesNum(goods.getVirtualSales() + goods.getActualSales());
            SearchIntegralProductVO.setStoreIsRecommend(goods.getStoreIsRecommend());
            SearchIntegralProductVO.setIsSelf(goods.getIsSelf());
            SearchIntegralProductVO.setOnlineTime(goods.getOnlineTime().getTime());

            //积分商品标签
            StringBuilder labelIds = new StringBuilder();
            //查询积分商品绑定标签表
            IntegralGoodsBindLabelExample bindLabelExample = new IntegralGoodsBindLabelExample();
            bindLabelExample.setGoodsId(goods.getIntegralGoodsId());
            List<IntegralGoodsBindLabel> goodsBindLabelList = integralGoodsBindLabelReadMapper.listByExample(bindLabelExample);
            if (!CollectionUtils.isEmpty(goodsBindLabelList)) {
                for (IntegralGoodsBindLabel label : goodsBindLabelList) {
                    labelIds.append(label.getLabelId1()).append(" ").append(label.getLabelId2()).append(" ");
                }
            }
            SearchIntegralProductVO.setLabelIds(labelIds.toString());
            SearchIntegralProductVOs.add(SearchIntegralProductVO);
        }
        return SearchIntegralProductVOs;
    }

    /**
     * 创建es索引
     *
     * @param url
     * @param port
     * @param goodsList
     * @throws IOException
     */
    private BulkResponse createIndex(String url, Integer port, List<SearchIntegralProductVO> goodsList) throws IOException {
        RestHighLevelClient client = null;
        BulkResponse response = null;
        try {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(url, port, "http")));
            boolean indexExist = client.indices().exists(new GetIndexRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME), RequestOptions.DEFAULT);
            if (!indexExist) {
                //索引不存在，创建索引，设置索引分词格式
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME);
                createIndexRequest.settings(Settings.builder()
                        .put("index.analysis.analyzer.default.type", "ik_max_word")
                        .put("index.analysis.analyzer.default_search.type", "ik_smart"));
                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            }
            BulkRequest bulkRequest = new BulkRequest();
            //插入数据
            for (SearchIntegralProductVO g : goodsList) {
                bulkRequest.add(new IndexRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME).id(g.getGoodsId()).source(JSONObject.toJSONString(g), XContentType.JSON));
            }
            response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("创建积分商城es索引异常", e);
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
    private void deleteByQuery(String url, Integer port, List<IntegralGoods> goodsList) throws IOException {
        RestHighLevelClient client = null;
        try {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(url, port, "http")));
            BulkRequest bulkRequest = new BulkRequest();
            for (IntegralGoods g : goodsList) {
                bulkRequest.add(new DeleteRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME, g.getIntegralGoodsId().toString()));
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
    public List<IntegralGoodsListVO> searchGoodsByES(GoodsSearchConditionDTO qc, PagerInfo pager) {
        List<IntegralGoodsListVO> goodsList = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //搜索条件构造
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtil.isEmpty(qc.getLabelId())) {
            //标签
            boolQueryBuilder.must(this.searchIndexAssembling4Label(qc));
        }
        if (!StringUtils.isEmpty(qc.getKeyword())) {
            boolQueryBuilder.must(this.searchIndexAssembling4keyword(qc));
            //搜索结果高亮设置
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field(SearchIntegralProductVO.GOODS_NAME_);
            highlightTitle.preTags("<font color=\"red\">");
            highlightTitle.postTags("</font>");
            highlightTitle.highlighterType("unified");
            highlightBuilder.field(highlightTitle);
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        //分页
        if (pager != null) {
            searchSourceBuilder.from(pager.getStart());
            searchSourceBuilder.size(pager.getPageSize());
        }

        int sort = Optional.of(qc).map(GoodsSearchConditionDTO::getSort).orElse(0);
        // es 排序 0:默认排序；1销量；2店铺推荐
        if (sort == 0) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchIntegralProductVO.SALES_NUM_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 1) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchIntegralProductVO.SALES_NUM_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 2) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchIntegralProductVO.STORE_IS_RECOMMEND_).unmappedType("long").order(SortOrder.DESC));
        }

        //查询商品
        RestHighLevelClient client = getESClient();
        try {
            SearchRequest searchRequest = new SearchRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME);
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
                HighlightField highlightField = highlightFieldMap.get(SearchIntegralProductVO.GOODS_NAME_);
                String goodsName = !StringUtils.isEmpty(highlightField) && !StringUtils.isEmpty(highlightField.fragments())
                        ? highlightField.fragments()[0].string()
                        : jsonObject.getString(SearchIntegralProductVO.GOODS_NAME_);
                Long goodsId = Long.valueOf(jsonObject.getString(SearchIntegralProductVO.ID_));
                Long storeId = Long.valueOf(jsonObject.getString(SearchIntegralProductVO.STORE_ID_));
                IntegralGoodsListVO vo = new IntegralGoodsListVO();
                vo.setIntegralGoodsId(goodsId);
                vo.setGoodsName(goodsName);
                vo.setGoodsImage(FileUrlUtil.getFileUrl(jsonObject.getString(SearchIntegralProductVO.GOODS_IMAGE_), null));
                vo.setIntegralPrice(new Integer(jsonObject.getString(SearchIntegralProductVO.INTEGRAL_PRICE_)));
                vo.setCashPrice(new BigDecimal(jsonObject.getString(SearchIntegralProductVO.CASH_PRICE_)));
                vo.setMarketPrice(jsonObject.getString(SearchIntegralProductVO.MARKET_PRICE_) == null ? null : new BigDecimal(jsonObject.getString(SearchIntegralProductVO.MARKET_PRICE_)));
                vo.setGoodsBrief(jsonObject.getString(SearchIntegralProductVO.CONTENT_));
                vo.setDefaultProductId(jsonObject.getLong(SearchIntegralProductVO.DEFAULT_PRODUCT_ID_));
                vo.setStoreId(storeId);
                vo.setStoreName(jsonObject.getString(SearchIntegralProductVO.STORE_NAME_));
                vo.setSaleNum(new Integer(jsonObject.getString(SearchIntegralProductVO.SALES_NUM_)));
                vo.setIsOwnShop(new Integer(jsonObject.getString(SearchIntegralProductVO.IS_SELF_)));
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
     * 标签的搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4Label(GoodsSearchConditionDTO qc) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //标签ID
        boolQueryBuilder.must(QueryBuilders.matchQuery(SearchIntegralProductVO.LABEL_IDS_, qc.getLabelId()));

        return boolQueryBuilder;
    }

    /**
     * 搜索页的搜索条件的拼装
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4keyword(GoodsSearchConditionDTO qc) {
        String keyword = qc.getKeyword();
        keyword = StringUtil.stringFilterSpecial(keyword);

        return SearchIntegralProductVO.searchIndexAssembling(keyword);
    }

    //endregion es搜索商品
}
