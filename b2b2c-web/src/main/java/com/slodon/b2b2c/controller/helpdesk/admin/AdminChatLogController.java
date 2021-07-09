package com.slodon.b2b2c.controller.helpdesk.admin;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskMsg;
import com.slodon.b2b2c.util.MongoDBUtil;
import com.slodon.b2b2c.vo.helpdesk.HelpdeskMsgLogVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.socket.helpdesk.server.ImSocketServer.MSG_PREFIX;

@Api(tags = "admin-聊天记录管理")
@RestController
@RequestMapping("v3/helpdesk/admin/chat/log")
public class AdminChatLogController extends BaseController {

    @ApiOperation("聊天记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "会员id", paramType = "query"),
            @ApiImplicitParam(name = "storeId", value = "店铺id", paramType = "query"),
            @ApiImplicitParam(name = "vendorId", value = "客服id", paramType = "query"),
            @ApiImplicitParam(name = "msgContent", value = "消息内容", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "msgId", value = "已获取到的最早的一条消息的id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<List<HelpdeskMsgLogVO>> chatLogList(HttpServletRequest request, Integer memberId, Long storeId, Long vendorId,
                                                          String msgContent, Date startTime, Date endTime, Long msgId) {

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<HelpdeskMsg> list = MongoDBUtil.getHelpDeskMsgLogList(MSG_PREFIX, storeId, memberId, vendorId,
                msgContent, startTime, endTime, msgId, pager);
        List<HelpdeskMsgLogVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(helpdeskMsg -> {
                vos.add(new HelpdeskMsgLogVO(helpdeskMsg));
            });
        }
        return SldResponse.success(vos);
    }

}
