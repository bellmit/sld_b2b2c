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
import com.slodon.b2b2c.helpdesk.example.HelpdeskQuickMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskQuickMsg;
import com.slodon.b2b2c.model.helpdesk.HelpdeskQuickMsgModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.helpdesk.QuickMsgVO;
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

@Api(tags = "seller-快捷回复")
@RestController
@Slf4j
@RequestMapping("v3/helpdesk/seller/quick")
public class SellerQuickMsgController extends BaseController {

    @Resource
    private HelpdeskQuickMsgModel helpdeskQuickMsgModel;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("快捷回复列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<QuickMsgVO>> list(HttpServletRequest request) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        HelpdeskQuickMsgExample example = new HelpdeskQuickMsgExample();
        example.setStoreId(vendor.getStoreId());
        example.setOrderBy("sort asc, create_time desc");
        List<HelpdeskQuickMsg> autoList = helpdeskQuickMsgModel.getHelpdeskQuickMsgList(example, pager);
        List<QuickMsgVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(autoList)) {
            autoList.forEach(helpdeskQuickMsg -> {
                QuickMsgVO vo = new QuickMsgVO(helpdeskQuickMsg);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增快捷回复")
    @VendorLogger(option = "新增快捷回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgContent", value = "消息内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", defaultValue = "1", paramType = "query")
    })
    @PostMapping("add")
    public JsonResult<Integer> add(HttpServletRequest request, String msgContent, Integer sort, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskQuickMsgExample example = new HelpdeskQuickMsgExample();
        example.setStoreId(vendor.getStoreId());
        List<HelpdeskQuickMsg> list = helpdeskQuickMsgModel.getHelpdeskQuickMsgList(example, null);
        AssertUtil.isTrue(list.size() > 20, "最多可设置20个快捷回复，请删除不用的快捷回复");
        //保存
        HelpdeskQuickMsg insertOne = new HelpdeskQuickMsg();
        insertOne.setStoreId(vendor.getStoreId());
        insertOne.setMsgContent(msgContent);
        insertOne.setSort(sort);
        insertOne.setIsShow(isShow);
        insertOne.setCreateTime(new Date());
        insertOne.setCreateVendorId(vendor.getVendorId());
        helpdeskQuickMsgModel.saveHelpdeskQuickMsg(insertOne);

        return SldResponse.success("添加成功");
    }

    @ApiOperation("编辑快捷回复")
    @VendorLogger(option = "编辑快捷回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quickMsgId", value = "快捷回复消息ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "msgContent", value = "消息内容", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "排序", paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", paramType = "query")
    })
    @PostMapping("edit")
    public JsonResult edit(HttpServletRequest request, Integer quickMsgId, String msgContent, Integer sort, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskQuickMsg msg = helpdeskQuickMsgModel.getHelpdeskQuickMsgByQuickMsgId(quickMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        //编辑
        HelpdeskQuickMsg updateOne = new HelpdeskQuickMsg();
        updateOne.setQuickMsgId(quickMsgId);
        updateOne.setMsgContent(msgContent);
        updateOne.setSort(sort);
        updateOne.setIsShow(isShow);
        helpdeskQuickMsgModel.updateHelpdeskQuickMsg(updateOne);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("是否显示")
    @VendorLogger(option = "是否显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quickMsgId", value = "快捷回复消息ID", required = true, paramType = "query"),
            @ApiImplicitParam(name = "isShow", value = "是否显示 0-不显示 1-显示", required = true, paramType = "query")
    })
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer quickMsgId, Integer isShow) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskQuickMsg msg = helpdeskQuickMsgModel.getHelpdeskQuickMsgByQuickMsgId(quickMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        //编辑
        HelpdeskQuickMsg updateOne = new HelpdeskQuickMsg();
        updateOne.setQuickMsgId(quickMsgId);
        updateOne.setIsShow(isShow);
        helpdeskQuickMsgModel.updateHelpdeskQuickMsg(updateOne);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("删除快捷回复")
    @VendorLogger(option = "删除快捷回复")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quickMsgId", value = "快捷回复消息ID", required = true, paramType = "query")
    })
    @PostMapping("del")
    public JsonResult delAutoMsg(HttpServletRequest request, Integer quickMsgId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskQuickMsg msg = helpdeskQuickMsgModel.getHelpdeskQuickMsgByQuickMsgId(quickMsgId);
        AssertUtil.isTrue(!msg.getStoreId().equals(vendor.getStoreId()), "无操作权限");
        Integer count = helpdeskQuickMsgModel.deleteHelpdeskQuickMsg(quickMsgId);

        return SldResponse.success("删除成功");

    }

    @ApiOperation("查看快捷回复详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "quickMsgId", value = "快捷回复消息ID", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<QuickMsgVO> detail(HttpServletRequest request, Integer quickMsgId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        HelpdeskQuickMsg detail = helpdeskQuickMsgModel.getHelpdeskQuickMsgByQuickMsgId(quickMsgId);
        AssertUtil.notNull(detail, "查询的快捷回复信息为空");
        QuickMsgVO vo = new QuickMsgVO(detail);
        return SldResponse.success(vo);
    }

}
