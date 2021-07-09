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
import com.slodon.b2b2c.helpdesk.example.HelpdeskAutomaticMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskAutomaticMsg;
import com.slodon.b2b2c.model.helpdesk.HelpdeskAutomaticMsgModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.helpdesk.AutomaticMsgVO;
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

@Api(tags = "seller-自动回复")
@RestController
@Slf4j
@RequestMapping("v3/helpdesk/seller/auto")
public class SellerAutoMsgController extends BaseController {

    @Resource
    private HelpdeskAutomaticMsgModel helpdeskAutomaticMsgModel;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("自动回复列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<AutomaticMsgVO>> list(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        HelpdeskAutomaticMsgExample example = new HelpdeskAutomaticMsgExample();
        example.setStoreId(vendor.getStoreId());
        example.setOrderBy("sort asc, create_time desc");
        List<HelpdeskAutomaticMsg> autoList = helpdeskAutomaticMsgModel.getHelpdeskAutomaticMsgList(example, pager);
        List<AutomaticMsgVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(autoList)) {
            autoList.forEach(helpdeskAutomaticMsg -> {
                AutomaticMsgVO vo = new AutomaticMsgVO(helpdeskAutomaticMsg);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增自动回复")
    @VendorLogger(option = "新增自动回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgContent", value = "消息内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", defaultValue = "1", paramType = "query")
    })
    @PostMapping("add")
    public JsonResult<Integer> add(HttpServletRequest request, String msgContent, Integer sort, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskAutomaticMsgExample example = new HelpdeskAutomaticMsgExample();
        example.setStoreId(vendor.getStoreId());
        List<HelpdeskAutomaticMsg> list = helpdeskAutomaticMsgModel.getHelpdeskAutomaticMsgList(example, null);
        AssertUtil.isTrue(list.size() > 20, "最多可设置20个自动回复，请删除不用的自动回复");
        //保存
        HelpdeskAutomaticMsg insertOne = new HelpdeskAutomaticMsg();
        insertOne.setStoreId(vendor.getStoreId());
        insertOne.setMsgContent(msgContent);
        insertOne.setSort(sort);
        insertOne.setIsShow(isShow);
        insertOne.setCreateVendorId(vendor.getVendorId());
        insertOne.setCreateTime(new Date());
        helpdeskAutomaticMsgModel.saveAutomaticMsg(insertOne);

        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑自动回复")
    @VendorLogger(option = "编辑自动回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "autoMsgId", value = "自动回复消息ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "msgContent", value = "消息内容", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", paramType = "query")
    })
    @PostMapping("edit")
    public JsonResult edit(HttpServletRequest request, Integer autoMsgId, String msgContent, Integer sort, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskAutomaticMsg msg = helpdeskAutomaticMsgModel.getHelpdeskAutomaticMsgByAutoMsgId(autoMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        //编辑
        HelpdeskAutomaticMsg updateOne = new HelpdeskAutomaticMsg();
        updateOne.setAutoMsgId(autoMsgId);
        updateOne.setStoreId(vendor.getStoreId());
        updateOne.setMsgContent(msgContent);
        updateOne.setSort(sort);
        updateOne.setIsShow(isShow);
        helpdeskAutomaticMsgModel.updateAutomaticMsg(updateOne);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("是否显示")
    @VendorLogger(option = "是否显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "autoMsgId", value = "自动回复消息ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", required = true, paramType = "query")
    })
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer autoMsgId, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskAutomaticMsg msg = helpdeskAutomaticMsgModel.getHelpdeskAutomaticMsgByAutoMsgId(autoMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        //编辑
        HelpdeskAutomaticMsg updateOne = new HelpdeskAutomaticMsg();
        updateOne.setAutoMsgId(autoMsgId);
        updateOne.setStoreId(vendor.getStoreId());
        updateOne.setIsShow(isShow);
        helpdeskAutomaticMsgModel.updateAutomaticMsg(updateOne);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("删除自动回复")
    @VendorLogger(option = "删除自动回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "autoMsgId", value = "自动回复消息ID", required = true, paramType = "query")
    })
    @PostMapping("del")
    public JsonResult delAutoMsg(HttpServletRequest request, Integer autoMsgId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskAutomaticMsg msg = helpdeskAutomaticMsgModel.getHelpdeskAutomaticMsgByAutoMsgId(autoMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        Integer count = helpdeskAutomaticMsgModel.deleteHelpdeskAutomaticMsg(autoMsgId);
        return SldResponse.success("删除成功");

    }

    @ApiOperation("查看自动回复详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "autoMsgId", value = "自动回复消息ID", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<AutomaticMsgVO> detail(HttpServletRequest request, Integer autoMsgId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskAutomaticMsg detail = helpdeskAutomaticMsgModel.getHelpdeskAutomaticMsgByAutoMsgId(autoMsgId);
        AssertUtil.notNull(detail, "查询的自动回复信息为空");
        AutomaticMsgVO vo = new AutomaticMsgVO(detail);
        return SldResponse.success(vo);
    }

}
