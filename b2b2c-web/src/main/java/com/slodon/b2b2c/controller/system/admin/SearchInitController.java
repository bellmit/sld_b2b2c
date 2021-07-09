package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.model.goods.ESGoodsModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@Api(tags = "admin-索引初始化")
@RestController
@RequestMapping("v3/system/admin/search")
public class SearchInitController {

    @Resource
    private ESGoodsModel esGoodsModel;

    @ApiOperation("初始化es索引")
    @OperationLogger(option = "初始化es索引")
    @PostMapping("esInit")
    public JsonResult esInit() throws IOException {
        //删除索引
        deleteByQuery();
        //重新把索引生成
        esGoodsModel.jobCreateIndexesES(DomainUrlUtil.SLD_ES_URL, DomainUrlUtil.SLD_ES_PORT, "init");

        return SldResponse.success("初始化成功");
    }

    private static RestHighLevelClient getESClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(DomainUrlUtil.SLD_ES_URL, DomainUrlUtil.SLD_ES_PORT, "http")));
    }

    /**
     * 删除索引
     */
    private static void deleteByQuery() throws IOException {
        RestHighLevelClient client = getESClient();
        boolean indexExist = client.indices().exists(new GetIndexRequest(DomainUrlUtil.ES_INDEX_NAME), RequestOptions.DEFAULT);
        if (indexExist) {
            //索引存在，删除索引
            DeleteByQueryRequest request = new DeleteByQueryRequest(DomainUrlUtil.ES_INDEX_NAME);
            request.setConflicts("proceed");
            request.setQuery(QueryBuilders.matchAllQuery());
            client.deleteByQuery(request, RequestOptions.DEFAULT);
        }
        client.close();
    }
}
