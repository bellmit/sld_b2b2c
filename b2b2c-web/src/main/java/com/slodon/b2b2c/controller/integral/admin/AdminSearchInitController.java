package com.slodon.b2b2c.controller.integral.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.config.DomainUrlUtil;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.TimeUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.model.integral.IntegralESGoodsModel;
import com.slodon.b2b2c.model.system.SettingModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.Setting;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;

@Api(tags = "admin-索引初始化")
@RestController
@RequestMapping("v3/integral/admin/integral/search")
public class AdminSearchInitController {

    @Resource
    private IntegralESGoodsModel integralESGoodsModel;
    @Resource
    private SettingModel settingModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("初始化es索引")
    @OperationLogger(option = "初始化es索引")
    @PostMapping("esInit")
    public JsonResult esInit(HttpServletRequest request) throws IOException {
        Admin adminUser = UserUtil.getUser(request, Admin.class);
        //删除索引
        deleteByQuery();
        //重新把索引生成
        boolean jobResult = integralESGoodsModel.jobCreateIndexesES(DomainUrlUtil.SLD_ES_URL, DomainUrlUtil.SLD_ES_PORT, "init");
        AssertUtil.isTrue(!jobResult, "积分商城初始化es索引失败");

        Setting setting = new Setting();
        setting.setName("integral_es_index_update_time");
        setting.setValue(TimeUtil.getDayAgo(new Date(), 0));
        settingModel.updateSetting(setting);
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
        boolean indexExist = client.indices().exists(new GetIndexRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME), RequestOptions.DEFAULT);
        if (indexExist) {
            //索引存在，删除索引
            DeleteByQueryRequest request = new DeleteByQueryRequest(DomainUrlUtil.INTEGRAL_ES_INDEX_NAME);
            request.setConflicts("proceed");
            request.setQuery(QueryBuilders.matchAllQuery());
            client.deleteByQuery(request, RequestOptions.DEFAULT);
        }
        client.close();
    }
}
