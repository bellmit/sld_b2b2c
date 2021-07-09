package com.slodon.b2b2c.controller.msg.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.msg.StoreReceiveModel;
import com.slodon.b2b2c.model.msg.StoreTplModel;
import com.slodon.b2b2c.model.msg.SystemTplTypeModel;
import com.slodon.b2b2c.msg.example.StoreReceiveExample;
import com.slodon.b2b2c.msg.pojo.StoreReceive;
import com.slodon.b2b2c.msg.pojo.StoreTpl;
import com.slodon.b2b2c.msg.pojo.SystemTplType;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.msg.StoreReceiveMsgVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

@Api(tags = "seller-通知中心")
@RestController
@Slf4j
@RequestMapping("v3/msg/seller/msg")
public class SellerMsgController extends BaseController {

    @Resource
    private StoreReceiveModel storeReceiveModel;
    @Resource
    private StoreTplModel storeTplModel;
    @Resource
    private SystemTplTypeModel systemTplTypeModel;

    @ApiOperation("消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tplType", value = "通知类型，空为全部消息(goods_news:商品消息;order_news:订单消息;after_sale_news:售后消息)", paramType = "query"),
            @ApiImplicitParam(name = "msgState", value = "通知状态：0--未读，1--已读", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<StoreReceiveMsgVO>> list(HttpServletRequest request, String tplType, Integer msgState,
                                                      Date startTime, Date endTime) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        StoreReceiveExample example = new StoreReceiveExample();
        example.setStoreId(vendor.getStoreId());
        example.setVendorId(vendor.getVendorId());
        example.setTplType(tplType);
        example.setMsgState(msgState);
        example.setMsgSendTimeAfter(startTime);
        example.setMsgSendTimeBefore(endTime);
        example.setMsgStateNotEquals(MsgConst.MSG_STATE_DELETE);
        example.setOrderBy("msg_state asc, msg_send_time desc");
        List<StoreReceive> list = storeReceiveModel.getStoreReceiveList(example, pager);
        List<StoreReceiveMsgVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(receive -> {
                StoreReceiveMsgVO vo = new StoreReceiveMsgVO(receive);
                StoreTpl storeTpl = storeTplModel.getStoreTplByTplCode(receive.getTplCode());
                AssertUtil.notNull(storeTpl, "商户消息模板不存在");
                SystemTplType systemTplType = systemTplTypeModel.getSystemTplTypeByTplTypeCode(storeTpl.getTplTypeCode());
                AssertUtil.notNull(systemTplType, "消息模板类型不存在");
                vo.setTplTypeName(systemTplType.getTplName());
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("批量删除消息")
    @VendorLogger(option = "批量删除消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receiveIds", value = "商家消息ID集合，用逗号隔开", required = true)
    })
    @PostMapping("del")
    public JsonResult delMsg(HttpServletRequest request, String receiveIds) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //校验
        AssertUtil.notEmpty(receiveIds, "请选择要删除的消息");
        AssertUtil.notFormatFrontIds(receiveIds,"receiveIds格式错误,请重试");

        storeReceiveModel.batchDeleteStoreReceive(receiveIds,vendor.getStoreId());
        return SldResponse.success("删除成功");
    }

    @ApiOperation("批量标为已读")
    @VendorLogger(option = "批量标为已读")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receiveIds", value = "商家消息ID集合，用逗号隔开", required = true)
    })
    @PostMapping("read")
    public JsonResult read(HttpServletRequest request, String receiveIds) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notEmpty(receiveIds, "请选择要标为已读的消息");
        AssertUtil.notFormatFrontIds(receiveIds,"receiveIds格式错误,请重试");
        storeReceiveModel.batchReadStoreReceive(receiveIds,vendor.getStoreId());
        return SldResponse.success("标为已读成功");
    }
}
