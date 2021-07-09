package com.slodon.b2b2c.controller.cms.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.cms.dto.InformationAddDTO;
import com.slodon.b2b2c.cms.dto.InformationUpdateDTO;
import com.slodon.b2b2c.cms.example.InformationExample;
import com.slodon.b2b2c.model.cms.InformationModel;
import com.slodon.b2b2c.cms.pojo.Information;
import com.slodon.b2b2c.vo.cms.InformationVO;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.system.pojo.Admin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxk
 */
@Api(tags = "admin-资讯管理")
@RestController
@Slf4j
@RequestMapping("v3/cms/admin/information")
public class AdminInformationController {

    @Resource
    private InformationModel informationModel;

    @ApiOperation("资讯列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "资讯标题", paramType = "query"),
            @ApiImplicitParam(name = "cateName", value = "分类名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<InformationVO>> getList(HttpServletRequest request, @RequestParam(value = "title", required = false) String title, @RequestParam(value = "cateName", required = false) String cateName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        InformationExample informationExample = new InformationExample();
        informationExample.setTitleLike(title);
        informationExample.setCateNameLike(cateName);
        informationExample.setPager(pager);
        List<Information> informationList = informationModel.getInformationList(informationExample, pager);
        List<InformationVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationList)) {
            for (Information information : informationList) {
                InformationVO vo = new InformationVO(information);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, informationExample.getPager()));
    }

    @ApiOperation("发布资讯")
    @OperationLogger(option = "发布资讯")
    @PostMapping("add")
    public JsonResult<Integer> addInformation(HttpServletRequest request, InformationAddDTO informationAddDTO) throws Exception {
        String logMsg = "资讯标题" + informationAddDTO.getTitle();
        Admin admin = UserUtil.getUser(request, Admin.class);

        AssertUtil.notNull(informationAddDTO.getContent(), "资讯内容不能为空");
        String description = informationAddDTO.getContent().replaceAll(System.getProperty("line.separator"), "");
        informationAddDTO.setContent(description);
        informationModel.saveInformation(informationAddDTO, admin);
        return SldResponse.success("发布成功", logMsg);
    }

    @ApiOperation(value = "编辑资讯", notes = "可用于编辑、修改阅读量、是否推荐、是否显示")
    @OperationLogger(option = "编辑资讯")
    @PostMapping("edit")
    public JsonResult editInformation(HttpServletRequest request, InformationUpdateDTO informationUpdateDTO) throws Exception {
        String logMsg = "资讯id" + informationUpdateDTO.getInformationId();

        Admin admin = UserUtil.getUser(request, Admin.class);

        informationModel.updateInformation(informationUpdateDTO, admin);
        return SldResponse.success("修改成功", logMsg);
    }

    @ApiOperation("删除资讯")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "informationId", value = "资讯id", required = true)
    })
    @OperationLogger(option = "删除资讯")
    @PostMapping("del")
    public JsonResult delInformation(HttpServletRequest request, @RequestParam("informationId") Integer informationId) {
        String logMsg = "资讯id" + informationId;
        informationModel.deleteInformation(informationId);
        return SldResponse.success("删除成功", logMsg);
    }

    @ApiOperation("复制资讯")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "informationId", value = "资讯id", required = true)
    })
    @OperationLogger(option = "复制资讯")
    @PostMapping("copyLink")
    public JsonResult copyLink(HttpServletRequest request, @RequestParam("informationId") Integer informationId) throws Exception {
        String logMsg = "资讯id" + informationId;

        Admin admin = UserUtil.getUser(request, Admin.class);

        AssertUtil.notNullOrZero(informationId, "请选择要复制的资讯id");
        //根据资讯id获取到资讯信息
        Information informationDb = informationModel.getInformationByInformationId(informationId);
        AssertUtil.notNull(informationDb, "资讯不存在");
        InformationAddDTO informationAddDTO = new InformationAddDTO();
        informationAddDTO.setCateId(informationDb.getCateId());
        informationAddDTO.setTitle("copy-" + informationDb.getTitle());
        informationAddDTO.setContent(informationDb.getContent());
        informationAddDTO.setCoverImage(informationDb.getCoverImage());
        informationModel.saveInformation(informationAddDTO, admin);
        return SldResponse.success("复制成功", logMsg);
    }

}
