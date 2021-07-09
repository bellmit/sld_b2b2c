package com.slodon.b2b2c.controller.helpdesk.seller;

import com.slodon.b2b2c.core.constant.HelpdeskConst;
import com.slodon.b2b2c.core.constant.VendorConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.helpdesk.dto.VendorContactDTO;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskMsg;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.util.MongoDBUtil;
import com.slodon.b2b2c.vo.helpdesk.HelpdeskMsgLogVO;
import com.slodon.b2b2c.vo.helpdesk.HelpdeskMsgVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.slodon.b2b2c.socket.helpdesk.server.ImSocketServer.MSG_PREFIX;
import static com.slodon.b2b2c.socket.helpdesk.server.ImSocketServer.VENDOR_CONTACT_PREFIX;

@Api(tags = "seller-聊天管理")
@RestController
@RequestMapping("v3/helpdesk/seller/chat")
public class SellerChatController extends BaseController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    @ApiOperation("获取聊天列表会员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgId", value = "已获取到的最早的一条消息的id", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "vendorId", value = "客服vendorId", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<List<HelpdeskMsgVO>> chatList(HttpServletRequest request,
                                                    @RequestParam(value = "msgId", required = false, defaultValue = Long.MAX_VALUE + "") Long msgId,
                                                    String memberName,Integer vendorId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        if (!StringUtil.isNullOrZero(vendorId)){
            //查看其他客服聊天记录，只有超级管理员有权限
            AssertUtil.isTrue(!VendorConst.IS_STORE_ADMIN_1.equals(vendor.getIsStoreAdmin()),"未授权");
        }
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<VendorContactDTO> contactList = MongoDBUtil.getVendorContactList(VENDOR_CONTACT_PREFIX, memberName,
                StringUtil.isNullOrZero(vendorId) ? vendor.getVendorId() : vendorId, msgId, pager);
        List<HelpdeskMsgVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(contactList)) {
            return SldResponse.success(result);
        }
        //店铺id
        Long storeId = vendor.getStoreId();
        contactList.forEach(vendorContact -> {
            //会员id
            Integer memberId = vendorContact.getMemberId();
            //先从MongoDB中获取
            HelpdeskMsg helpdeskMsg = MongoDBUtil.getChatDetail(MSG_PREFIX, vendorContact.getMsgId());
            if (helpdeskMsg != null) {
                if (StringUtils.isEmpty(helpdeskMsg.getMemberAvatar())) {
                    helpdeskMsg.setVendorAvatar(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
                }
                //未读数量
                Long unreadNum = MongoDBUtil.getUnReadNum(MSG_PREFIX, storeId, memberId, HelpdeskConst.ROLE_TYPE_MEMBER);
                helpdeskMsg.setReceiveMsgNumber(StringUtil.isNullOrZero(unreadNum) ? 0 : unreadNum.intValue());
                result.add(new HelpdeskMsgVO(helpdeskMsg));
            }
        });
        return SldResponse.success(result);
    }

    @ApiOperation("获取聊天记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "会员id", paramType = "query"),
            @ApiImplicitParam(name = "vendorId", value = "客服id", paramType = "query"),
            @ApiImplicitParam(name = "msgContent", value = "消息内容", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "msgId", value = "已获取到的最早的一条消息的id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("logList")
    public JsonResult<PageVO<HelpdeskMsgLogVO>> chatLogList(HttpServletRequest request, Integer memberId, Long vendorId,
                                                            String msgContent, Date startTime, Date endTime, Long msgId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<HelpdeskMsg> list = MongoDBUtil.getHelpDeskMsgLogList(MSG_PREFIX, vendor.getStoreId(), memberId, vendorId,
                msgContent, startTime, endTime, msgId, pager);
        List<HelpdeskMsgLogVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(helpdeskMsg -> {
                vos.add(0, new HelpdeskMsgLogVO(helpdeskMsg));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取聊天记录,返回比msgId小的10条记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId", value = "会员id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "msgId", value = "已获取到的最早的一条消息的id", paramType = "query"),
    })
    @PostMapping("msgLog")
    public JsonResult<List<HelpdeskMsgVO>> msgLog(HttpServletRequest request,
                                                  @RequestParam(value = "memberId") Integer memberId,
                                                  @RequestParam(value = "msgId", required = false, defaultValue = Long.MAX_VALUE + "") Long msgId) {
        int pageSize = 10;//每次获取消息条数
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //先从MongoDB中获取
        List<HelpdeskMsg> msgList = MongoDBUtil.getHelpDeskMsgList(MSG_PREFIX, vendor.getStoreId(), memberId, msgId, pageSize);
        List<HelpdeskMsgVO> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(msgList)) {
            for (HelpdeskMsg helpdeskMsg : msgList) {
                if (StringUtils.isEmpty(helpdeskMsg.getMemberAvatar())) {
                    helpdeskMsg.setMemberAvatar(stringRedisTemplate.opsForValue().get("default_image_user_portrait"));
                }
                result.add(0, new HelpdeskMsgVO(helpdeskMsg));
            }
        }
        return SldResponse.success(result);
    }

}
