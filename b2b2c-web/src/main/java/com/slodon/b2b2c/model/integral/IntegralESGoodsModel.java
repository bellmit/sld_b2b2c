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
     * ????????????
     *
     * @param url
     * @param port
     * @param type
     * @return
     */
    @SneakyThrows
    public boolean jobCreateIndexesES(String url, Integer port, String type) {
        //es???????????????????????????
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
                //????????????????????????
                log.error(response.buildFailureMessage());
            }
        }

        if ("job".equals(type)) {
            //??????????????????????????????????????????????????????
            example.setState(null);
            example.setStateNotEquals(GoodsConst.GOODS_STATE_UPPER);
            //???????????????????????????????????????
            List<IntegralGoods> deleteList = integralGoodsReadMapper.listByExample(example);
            if (!CollectionUtils.isEmpty(deleteList)) {
                this.deleteByQuery(url, port, deleteList);
            }
        }
        return true;
    }

    /**
     * ??????SearchIntegralProductVO???????????????????????????
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

            //??????????????????
            StringBuilder labelIds = new StringBuilder();
            //?????????????????????????????????
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
     * ??????es??????
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
                //?????????????????????????????????????????????????????????
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME);
                createIndexRequest.settings(Settings.builder()
                        .put("index.analysis.analyzer.default.type", "ik_max_word")
                        .put("index.analysis.analyzer.default_search.type", "ik_smart"));
                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            }
            BulkRequest bulkRequest = new BulkRequest();
            //????????????
            for (SearchIntegralProductVO g : goodsList) {
                bulkRequest.add(new IndexRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME).id(g.getGoodsId()).source(JSONObject.toJSONString(g), XContentType.JSON));
            }
            response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("??????????????????es????????????", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return response;
    }

    /**
     * ??????es????????????
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
            log.error("??????es????????????", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    //region es????????????

    /**
     * es????????????
     *
     * @param qc
     * @param pager
     * @return
     */
    public List<IntegralGoodsListVO> searchGoodsByES(GoodsSearchConditionDTO qc, PagerInfo pager) {
        List<IntegralGoodsListVO> goodsList = new ArrayList<>();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //??????????????????
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!StringUtil.isEmpty(qc.getLabelId())) {
            //??????
            boolQueryBuilder.must(this.searchIndexAssembling4Label(qc));
        }
        if (!StringUtils.isEmpty(qc.getKeyword())) {
            boolQueryBuilder.must(this.searchIndexAssembling4keyword(qc));
            //????????????????????????
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            HighlightBuilder.Field highlightTitle = new HighlightBuilder.Field(SearchIntegralProductVO.GOODS_NAME_);
            highlightTitle.preTags("<font color=\"red\">");
            highlightTitle.postTags("</font>");
            highlightTitle.highlighterType("unified");
            highlightBuilder.field(highlightTitle);
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        searchSourceBuilder.query(boolQueryBuilder);

        //??????
        if (pager != null) {
            searchSourceBuilder.from(pager.getStart());
            searchSourceBuilder.size(pager.getPageSize());
        }

        int sort = Optional.of(qc).map(GoodsSearchConditionDTO::getSort).orElse(0);
        // es ?????? 0:???????????????1?????????2????????????
        if (sort == 0) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchIntegralProductVO.SALES_NUM_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 1) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchIntegralProductVO.SALES_NUM_).unmappedType("long").order(SortOrder.DESC));
        } else if (sort == 2) {
            searchSourceBuilder.sort(new FieldSortBuilder(SearchIntegralProductVO.STORE_IS_RECOMMEND_).unmappedType("long").order(SortOrder.DESC));
        }

        //????????????
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

                //???????????????
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
            log.error("es????????????:", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("es????????????:", e);
            }
        }
        return goodsList;
    }

    /**
     * ??????es??????
     *
     * @return
     */
    private RestHighLevelClient getESClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(DomainUrlUtil.SLD_ES_URL, DomainUrlUtil.SLD_ES_PORT, "http")));
    }

    /**
     * ??????????????????????????????
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4Label(GoodsSearchConditionDTO qc) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //??????ID
        boolQueryBuilder.must(QueryBuilders.matchQuery(SearchIntegralProductVO.LABEL_IDS_, qc.getLabelId()));

        return boolQueryBuilder;
    }

    /**
     * ?????????????????????????????????
     *
     * @param qc
     * @return
     */
    private BoolQueryBuilder searchIndexAssembling4keyword(GoodsSearchConditionDTO qc) {
        String keyword = qc.getKeyword();
        keyword = StringUtil.stringFilterSpecial(keyword);

        return SearchIntegralProductVO.searchIndexAssembling(keyword);
    }

    //endregion es????????????
}
