package com.slodon.b2b2c.controller.helpdesk.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.helpdesk.example.HelpdeskCommonProblemMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskCommonProblemMsg;
import com.slodon.b2b2c.model.helpdesk.HelpdeskCommonProblemMsgModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.helpdesk.CommonProblemMsgVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-常见问题")
@RestController
@Slf4j
@RequestMapping("v3/helpdesk/seller/problem")
public class SellerCommonProblemMsgController extends BaseController {

    @Resource
    private HelpdeskCommonProblemMsgModel helpdeskCommonProblemMsgModel;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("常见问题列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<CommonProblemMsgVO>> list(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        HelpdeskCommonProblemMsgExample example = new HelpdeskCommonProblemMsgExample();
        example.setStoreId(vendor.getStoreId());
        example.setOrderBy("sort asc, create_time desc");
        List<HelpdeskCommonProblemMsg> autoList = helpdeskCommonProblemMsgModel.getHelpdeskCommonProblemMsgList(example, pager);
        List<CommonProblemMsgVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(autoList)) {
            autoList.forEach(helpdeskCommonProblemMsg -> {
                CommonProblemMsgVO vo = new CommonProblemMsgVO(helpdeskCommonProblemMsg);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增常见问题")
    @VendorLogger(option = "新增常见问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgContent", value = "消息内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "msgReply", value = "回答内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", defaultValue = "1", paramType = "query")
    })
    @PostMapping("add")
    public JsonResult<Integer> add(HttpServletRequest request, String msgContent, String msgReply, Integer sort, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskCommonProblemMsgExample example = new HelpdeskCommonProblemMsgExample();
        example.setStoreId(vendor.getStoreId());
        List<HelpdeskCommonProblemMsg> list = helpdeskCommonProblemMsgModel.getHelpdeskCommonProblemMsgList(example, null);
        AssertUtil.isTrue(list.size() > 20, "最多可设置20个常见问题，请删除不用的常见问题");

        //保存
        HelpdeskCommonProblemMsg insertOne = new HelpdeskCommonProblemMsg();
        insertOne.setStoreId(vendor.getStoreId());
        insertOne.setMsgContent(msgContent);
        insertOne.setMsgReply(msgReply);
        insertOne.setSort(sort);
        insertOne.setIsShow(isShow);
        insertOne.setCreateVendorId(vendor.getVendorId());
        insertOne.setCreateTime(new Date());
        helpdeskCommonProblemMsgModel.saveHelpdeskCommonProblemMsg(insertOne);
        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑常见问题")
    @VendorLogger(option = "编辑常见问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemMsgId", value = "常见问题消息ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "msgContent", value = "消息内容", paramType = "query"),
            @ApiImplicitParam(name = "msgReply", value = "回答内容", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", paramType = "query")
    })
    @PostMapping("edit")
    public JsonResult edit(HttpServletRequest request, Integer problemMsgId, String msgContent, String msgReply, Integer sort, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskCommonProblemMsg msg = helpdeskCommonProblemMsgModel.getHelpdeskCommonProblemMsgByProblemMsgId(problemMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        //编辑
        HelpdeskCommonProblemMsg updateOne = new HelpdeskCommonProblemMsg();
        updateOne.setProblemMsgId(problemMsgId);
        updateOne.setStoreId(vendor.getStoreId());
        updateOne.setMsgContent(msgContent);
        updateOne.setMsgReply(msgReply);
        updateOne.setSort(sort);
        updateOne.setIsShow(isShow);
        helpdeskCommonProblemMsgModel.updateHelpdeskCommonProblemMsg(updateOne);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("是否显示")
    @VendorLogger(option = "是否显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemMsgId", value = "常见问题消息ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", required = true, paramType = "query")
    })
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer problemMsgId, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskCommonProblemMsg msg = helpdeskCommonProblemMsgModel.getHelpdeskCommonProblemMsgByProblemMsgId(problemMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        //编辑
        HelpdeskCommonProblemMsg updateOne = new HelpdeskCommonProblemMsg();
        updateOne.setProblemMsgId(problemMsgId);
        updateOne.setIsShow(isShow);
        helpdeskCommonProblemMsgModel.updateHelpdeskCommonProblemMsg(updateOne);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("删除常见问题")
    @VendorLogger(option = "删除常见问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemMsgId", value = "常见问题消息ID", required = true, paramType = "query")
    })
    @PostMapping("del")
    public JsonResult delAutoMsg(HttpServletRequest request, Integer problemMsgId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskCommonProblemMsg msg = helpdeskCommonProblemMsgModel.getHelpdeskCommonProblemMsgByProblemMsgId(problemMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        Integer count = helpdeskCommonProblemMsgModel.deleteHelpdeskCommonProblemMsg(problemMsgId);

        return SldResponse.success("删除成功");

    }

    @ApiOperation("查看常见问题详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "problemMsgId", value = "常见问题消息ID", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<CommonProblemMsgVO> detail(HttpServletRequest request, Integer problemMsgId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskCommonProblemMsg detail = helpdeskCommonProblemMsgModel.getHelpdeskCommonProblemMsgByProblemMsgId(problemMsgId);
        AssertUtil.notNull(detail, "查询的常见问题信息为空");
        CommonProblemMsgVO vo = new CommonProblemMsgVO(detail);
        return SldResponse.success(vo);
    }

}
