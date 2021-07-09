package com.slodon.b2b2c.controller.cms.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.cms.dto.InformationCategoryAddDTO;
import com.slodon.b2b2c.cms.dto.InformationCategoryUpdateDTO;
import com.slodon.b2b2c.cms.example.InformationCategoryExample;
import com.slodon.b2b2c.cms.pojo.InformationCategory;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.cms.InformationCategoryModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.cms.InformationCategoryVO;
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
@Api(tags = "admin-资讯分类管理")
@RestController
@Slf4j
@RequestMapping("v3/cms/admin/informationCategory")
public class AdminInformationCategoryController {

    @Resource
    private InformationCategoryModel informationCategoryModel;

    @ApiOperation("资讯分类列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cateName", value = "资讯分类名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<InformationCategoryVO>> getList(HttpServletRequest request, @RequestParam(value = "cateName", required = false) String cateName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        InformationCategoryExample example = new InformationCategoryExample();
        example.setCateNameLike(cateName);
        List<InformationCategory> informationCategoryList = informationCategoryModel.getInformationCategoryList(example, pager);
        List<InformationCategoryVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(informationCategoryList)) {
            for (InformationCategory informationCategory : informationCategoryList) {
                InformationCategoryVO vo = new InformationCategoryVO(informationCategory);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增资讯分类")
    @OperationLogger(option = "新增资讯分类")
    @PostMapping("add")
    public JsonResult<Integer> addInformationCategory(HttpServletRequest request, InformationCategoryAddDTO informationCategoryAddDTO) {
        String logMsg = "资讯分类名称" + informationCategoryAddDTO.getCateName();

        Admin admin = UserUtil.getUser(request, Admin.class);

        informationCategoryModel.saveInformationCategory(informationCategoryAddDTO, admin);
        return SldResponse.success("保存成功", logMsg);
    }

    @ApiOperation(value = "编辑资讯分类", notes = "用于编辑或是否显示资讯分类")
    @OperationLogger(option = "编辑资讯分类")
    @PostMapping("edit")
    public JsonResult editInformationCategory(HttpServletRequest request, InformationCategoryUpdateDTO informationCategoryUpdateDTO) throws Exception {
        String logMsg = "资讯分类id" + informationCategoryUpdateDTO.getCateId();
        Admin admin = UserUtil.getUser(request, Admin.class);

        informationCategoryModel.updateInformationCategory(informationCategoryUpdateDTO, admin);
        return SldResponse.success("修改成功", logMsg);

    }

    @ApiOperation("删除资讯分类")
    @OperationLogger(option = "删除资讯分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cateIds", value = "资讯分类ID集合，用逗号分隔", required = true)
    })
    @PostMapping("del")
    public JsonResult delInformationCategory(HttpServletRequest request, String cateIds) {
        //参数校验
        AssertUtil.notEmpty(cateIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(cateIds, "cateIds格式错误,请重试");

        String logMsg = "资讯分类ID集合" + cateIds;
        informationCategoryModel.batchDeleteInformation(cateIds);
        return SldResponse.success("删除成功", logMsg);
    }

}
