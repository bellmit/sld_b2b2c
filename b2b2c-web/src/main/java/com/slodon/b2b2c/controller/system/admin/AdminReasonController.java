package com.slodon.b2b2c.controller.system.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.AdminConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.model.system.ReasonModel;
import com.slodon.b2b2c.system.example.ReasonExample;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.system.pojo.Reason;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-原因管理")
@RestController
@RequestMapping("v3/system/admin/reason")
public class AdminReasonController extends BaseController {

    @Resource
    private ReasonModel reasonModel;

    @ApiOperation("原因列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "原因类型：101-违规下架；102-商品审核拒绝；103-入驻审核拒绝；104-会员取消订单；105-仅退款-未收货；106-仅退款-已收货；107-退款退货原因；108-商户取消订单", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示：1-显示，0-不显示", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "理由", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<Reason>> list(HttpServletRequest request, Integer type, String content, Integer isShow) {
        ReasonExample example = new ReasonExample();
        example.setType(type);
        example.setContentLike(content);
        example.setIsShow(isShow);
        List<Reason> list = reasonModel.getReasonList(example, null);
        return SldResponse.success(new PageVO<>(list, null));
    }

    @ApiOperation("新增原因")
    @OperationLogger(option = "新增原因")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "原因类型：101-违规下架；102-商品审核拒绝；103-入驻审核拒绝；104-会员取消订单；105-仅退款-未收货；106-仅退款-已收货；107-退款退货原因；108-商户取消订单", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "理由", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示：1-显示，0-不显示", required = true, paramType = "query")
    })
    @PostMapping("add")
    public JsonResult addReason(HttpServletRequest request, Integer type, String content, Integer sort, Integer isShow) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        //原因不可重复
        ReasonExample example = new ReasonExample();
        example.setType(type);
        example.setContent(content);
        List<Reason> list = reasonModel.getReasonList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "理由不可重复");
        example.setContent(null);
        AssertUtil.isTrue(reasonModel.getReasonCount(example) >= 20, "最多可设置20个理由，请删除不用的理由");

        Reason reason = new Reason();
        reason.setType(type);
        reason.setContent(content);
        reason.setSort(sort);
        reason.setIsShow(isShow);
        reason.setIsInner(AdminConst.IS_INNER_NO);
        reason.setCreateAdminId(admin.getAdminId());
        reason.setCreateTime(new Date());
        reasonModel.saveReason(reason);
        return SldResponse.success("新增成功");
    }

    @ApiOperation("编辑原因")
    @OperationLogger(option = "编辑原因")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reasonId", value = "原因id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "理由", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示：1-显示，0-不显示", required = true, paramType = "query")
    })
    @PostMapping("update")
    public JsonResult updateReason(HttpServletRequest request, Integer reasonId, String content, Integer sort, Integer isShow) {
        //查询原因信息
        Reason reasonDb = reasonModel.getReasonByReasonId(reasonId);
        AssertUtil.notNull(reasonDb, "原因不存在");

        //原因不可重复
        ReasonExample example = new ReasonExample();
        example.setType(reasonDb.getType());
        example.setContent(content);
        example.setReasonIdNotEquals(reasonId);
        List<Reason> list = reasonModel.getReasonList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "理由不可重复");

        Reason reason = new Reason();
        reason.setReasonId(reasonId);
        reason.setContent(content);
        reason.setSort(sort);
        reason.setIsShow(isShow);
        reasonModel.updateReason(reason);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("删除原因")
    @OperationLogger(option = "删除原因")
    @PostMapping("del")
    public JsonResult delReason(HttpServletRequest request, Integer reasonId) {
        reasonModel.deleteReason(reasonId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("是否显示原因")
    @OperationLogger(option = "是否显示原因")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "reasonId", value = "原因id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示：1-显示，0-不显示", required = true, paramType = "query")
    })
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer reasonId, Integer isShow) {
        Reason reason = new Reason();
        reason.setReasonId(reasonId);
        reason.setIsShow(isShow);
        reasonModel.updateReason(reason);
        return SldResponse.success("修改成功");
    }
}
