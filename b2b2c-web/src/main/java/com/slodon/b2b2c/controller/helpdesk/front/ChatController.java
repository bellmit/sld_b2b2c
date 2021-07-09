package com.slodon.b2b2c.controller.helpdesk.front;

import com.slodon.b2b2c.core.constant.HelpdeskConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.helpdesk.dto.MemberContactDTO;
import com.slodon.b2b2c.helpdesk.example.HelpdeskCommonProblemMsgExample;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskCommonProblemMsg;
import com.slodon.b2b2c.helpdesk.pojo.HelpdeskMsg;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.helpdesk.HelpdeskCommonProblemMsgModel;
import com.slodon.b2b2c.util.MongoDBUtil;
import com.slodon.b2b2c.vo.helpdesk.CommonProblemMsgVO;
import com.slodon.b2b2c.vo.helpdesk.HelpdeskMsgVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.slodon.b2b2c.socket.helpdesk.server.ImSocketServer.MEMBER_CONTACT_PREFIX;
import static com.slodon.b2b2c.socket.helpdesk.server.ImSocketServer.MSG_PREFIX;

@Slf4j
@Api(tags = "front-聊天管理")
@RestController
@RequestMapping("v3/helpdesk/front/chat")
public class ChatController extends BaseController {

    @Resource
    private HelpdeskCommonProblemMsgModel helpdeskCommonProblemMsgModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisTemplate<String, Object> objectRedisTemplate;

    @ApiOperation("获取聊天列表店铺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgId", value = "已获取到的最早的一条消息的id", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("storeList")
    public JsonResult<List<HelpdeskMsgVO>> storeList(HttpServletRequest request,
                                                     String storeName,
                                                     @RequestParam(value = "msgId", required = false, defaultValue = Long.MAX_VALUE + "") Long msgId) {
        Member member = UserUtil.getUser(request, Member.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<MemberContactDTO> contactList = MongoDBUtil.getMemberContactList(MEMBER_CONTACT_PREFIX, member.getMemberId(), msgId,storeName, pager);
        log.info("获取会员最近联系人列表_" + member.getMemberId() + "==============" + contactList);
        List<HelpdeskMsgVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(contactList)) {
            return SldResponse.success(result);
        }
        //会员id
        Integer memberId = member.getMemberId();
        contactList.forEach(memberContact -> {
            //店铺id
            Long storeId = memberContact.getStoreId();
            //先从MongoDB中获取
            HelpdeskMsg helpdeskMsg = MongoDBUtil.getChatDetail(MSG_PREFIX, memberContact.getMsgId());
            if (helpdeskMsg != null) {
                log.info("聊天记录_" + memberContact.getMsgId() + "==============" + helpdeskMsg);
                if (StringUtils.isEmpty(helpdeskMsg.getMemberAvatar())) {
                    helpdeskMsg.setVendorAvatar(stringRedisTemplate.opsForValue().get("default_image_store_logo"));
                }
                //未读数量
                Long unreadNum = MongoDBUtil.getUnReadNum(MSG_PREFIX, storeId, memberId, HelpdeskConst.ROLE_TYPE_VENDOR);
                helpdeskMsg.setReceiveMsgNumber(StringUtil.isNullOrZero(unreadNum) ? 0 : unreadNum.intValue());
                result.add(new HelpdeskMsgVO(helpdeskMsg));
            }
        });
        return SldResponse.success(result);
    }

    @ApiOperation("获取聊天记录,返回比msgId小的10条记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "msgId", value = "已获取到的最早的一条消息的id", paramType = "query"),
    })
    @PostMapping("msgLog")
    public JsonResult<List<HelpdeskMsgVO>> msgLog(HttpServletRequest request,
                                                  @RequestParam(value = "storeId") Long storeId,
                                                  @RequestParam(value = "msgId", required = false, defaultValue = Long.MAX_VALUE + "") Long msgId) {
        int pageSize = 10;//每次获取消息条数
        Member member = UserUtil.getUser(request, Member.class);
        //先从MongoDB中获取
        List<HelpdeskMsg> msgList = MongoDBUtil.getHelpDeskMsgList(MSG_PREFIX, storeId, member.getMemberId(), msgId, pageSize);
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

    @ApiOperation("常见问题列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeId", value = "店铺id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("problemList")
    public JsonResult<PageVO<CommonProblemMsgVO>> problemList(HttpServletRequest request, Long storeId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        HelpdeskCommonProblemMsgExample example = new HelpdeskCommonProblemMsgExample();
        example.setStoreId(storeId);
        example.setIsShow(HelpdeskConst.IS_SHOW_YES);
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

}
