package com.slodon.b2b2c.controller.seller.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.seller.StoreApplyModel;
import com.slodon.b2b2c.model.seller.StoreGradeModel;
import com.slodon.b2b2c.model.seller.StoreRenewModel;
import com.slodon.b2b2c.seller.dto.StoreGradeAddDTO;
import com.slodon.b2b2c.seller.dto.StoreGradeUpdateDTO;
import com.slodon.b2b2c.seller.example.StoreApplyExample;
import com.slodon.b2b2c.seller.example.StoreGradeExample;
import com.slodon.b2b2c.seller.example.StoreRenewExample;
import com.slodon.b2b2c.seller.pojo.StoreApply;
import com.slodon.b2b2c.seller.pojo.StoreGrade;
import com.slodon.b2b2c.seller.pojo.StoreRenew;
import com.slodon.b2b2c.vo.seller.StoreGradeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "admin-店铺等级")
@RestController
@RequestMapping("v3/seller/admin/storeGrade")
public class AdminStoreGradeController {

    @Resource
    private StoreGradeModel storeGradeModel;
    @Resource
    private StoreApplyModel storeApplyModel;
    @Resource
    private StoreRenewModel storeRenewModel;

    @ApiOperation("店铺等级列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gradeName", value = "等级名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreGradeVO>> getList(HttpServletRequest request,
                                                    @RequestParam(value = "gradeName", required = false) String gradeName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        StoreGradeExample example = new StoreGradeExample();
        example.setGradeNameLike(gradeName);
        List<StoreGrade> storeGradeList = storeGradeModel.getStoreGradeList(example, pager);
        List<StoreGradeVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(storeGradeList)) {
            for (StoreGrade storeGrade : storeGradeList) {
                StoreGradeVO vo = new StoreGradeVO(storeGrade);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增店铺等级")
    @OperationLogger(option = "新增店铺等级")
    @PostMapping("add")
    public JsonResult<Integer> addStoreGrade(HttpServletRequest request, StoreGradeAddDTO storeGradeAddDTO) throws Exception {
        String logMsg = "等级名称" + storeGradeAddDTO.getGradeName();
        storeGradeModel.saveStoreGrade(storeGradeAddDTO);
        return SldResponse.success("添加成功", logMsg);
    }

    @ApiOperation("编辑店铺等级")
    @OperationLogger(option = "编辑店铺等级")
    @PostMapping("edit")
    public JsonResult editStoreGrade(HttpServletRequest request, StoreGradeUpdateDTO storeGradeUpdateDTO) throws Exception {
        String logMsg = "等级ID" + storeGradeUpdateDTO.getGradeId();

        StoreApplyExample example = new StoreApplyExample();
        example.setStoreGradeId(storeGradeUpdateDTO.getGradeId());
        List<StoreApply> storeApplyList = storeApplyModel.getStoreApplyList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(storeApplyList), "使用中的等级不能编辑");

        storeGradeModel.updateStoreGrade(storeGradeUpdateDTO);
        return SldResponse.success("修改成功", logMsg);
    }

    @ApiOperation("删除店铺等级")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "gradeId", value = "等级id", required = true)
    })
    @OperationLogger(option = "删除店铺等级")
    @PostMapping("del")
    public JsonResult delStoreGrade(HttpServletRequest request, @RequestParam("gradeId") Integer gradeId) {
        String logMsg = "等级ID" + gradeId;
        AssertUtil.notNullOrZero(gradeId, "请选择要删除的等级");
        StoreApplyExample example = new StoreApplyExample();
        example.setStoreGradeId(gradeId);
        List<StoreApply> storeApplyList = storeApplyModel.getStoreApplyList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(storeApplyList), "使用中的等级不能删除");

        StoreRenewExample storeRenewExample = new StoreRenewExample();
        storeRenewExample.setGradeId(gradeId);
        List<StoreRenew> storeRenewList = storeRenewModel.getStoreRenewList(storeRenewExample, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(storeRenewList), "使用中的等级不能删除");

        storeGradeModel.deleteStoreGrade(gradeId);
        return SldResponse.success("删除成功", logMsg);
    }

}
