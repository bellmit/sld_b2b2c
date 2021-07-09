package com.slodon.b2b2c.controller.cms.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.model.cms.InformationIndexModel;
import com.slodon.b2b2c.cms.pojo.InformationIndex;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author lxk
 */
@Api(tags = "admin-资讯首页")
@RestController
@Slf4j
@RequestMapping("v3/cms/admin/informationIndex")
public class AdminInformationIndexController {

    @Resource
    private InformationIndexModel informationIndexModel;

    @ApiOperation("编辑资讯首页")
    @OperationLogger(option = "编辑资讯首页")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "data", required = true, value = "首页数据，格式为：" +
                    "{\n" +
                    "    \"banner\": [\n" +
                    "        {\n" +
                    "            \"image_path\": \"/1.jpg\",\n" +
                    "            \"link_url\": \"/1.html\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"image_path\": \"/1.jpg\",\n" +
                    "            \"link_url\": \"/1.html\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"adv\": [\n" +
                    "        {\n" +
                    "            \"image_path\": \"/1.jpg\",\n" +
                    "            \"description\": \"111\",\n" +
                    "            \"link_url\": \"/1.html\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"image_path\": \"/1.jpg\",\n" +
                    "            \"description\": \"222\",\n" +
                    "            \"link_url\": \"/1.html\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"image_path\": \"/1.jpg\",\n" +
                    "            \"description\": \"333\",\n" +
                    "            \"link_url\": \"/1.html\"\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}")
    })
    @PostMapping("update")
    public JsonResult updateIndex(HttpServletRequest request, String data) {
        InformationIndex informationIndex = new InformationIndex();
        informationIndex.setIndexId(1);
        informationIndex.setData(data);
        return SldResponse.success(informationIndexModel.updateInformationIndex(informationIndex));
    }

    @ApiOperation("查看资讯首页详情")
    @OperationLogger(option = "查看资讯首页详情")
    @GetMapping("detail")
    public JsonResult<InformationIndex> getDetail(HttpServletRequest request) {
        return SldResponse.success(informationIndexModel.getInformationIndexByIndexId(1));
    }

}
