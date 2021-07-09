package com.slodon.b2b2c.controller.msg.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.member.MemberModel;
import com.slodon.b2b2c.model.msg.MemberReceiveModel;
import com.slodon.b2b2c.model.msg.MsgSendModel;
import com.slodon.b2b2c.model.msg.SystemPushModel;
import com.slodon.b2b2c.msg.example.MemberReceiveExample;
import com.slodon.b2b2c.msg.example.SystemPushExample;
import com.slodon.b2b2c.msg.pojo.MemberReceive;
import com.slodon.b2b2c.msg.pojo.SystemPush;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.msg.MemberReceiveVO;
import com.slodon.b2b2c.vo.msg.SystemPushVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.StringRedisTemplate;
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

@Api(tags = "admin-会员通知")
@RestController
@RequestMapping("v3/msg/admin/msg/push")
public class AdminMsgPushController extends BaseController {

    @Resource
    private SystemPushModel systemPushModel;
    @Resource
    private MemberReceiveModel memberReceiveModel;
    @Resource
    private MemberModel memberModel;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MsgSendModel msgSendModel;

    @ApiOperation("会员通知列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content", value = "消息内容", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "发送开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "发送结束时间", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SystemPushVO>> list(HttpServletRequest request, String content, Date startTime, Date endTime) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SystemPushExample example = new SystemPushExample();
        example.setReceiveType(MsgConst.RECEIVE_TYPE_MEMBER);
        example.setSendWay(MsgConst.SEND_WAY_STATION_LETTER);
        example.setContentLike(content);
        example.setSendTimeAfter(startTime);
        example.setSendTimeBefore(endTime);
        List<SystemPush> list = systemPushModel.getSystemPushList(example, pager);
        List<SystemPushVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(systemPush -> {
                SystemPushVO vo = new SystemPushVO(systemPush);
                MemberReceiveExample receiveExample = new MemberReceiveExample();
                receiveExample.setPushId(systemPush.getPushId());
                receiveExample.setSource(MsgConst.SOURCE_SYSTEM);
                receiveExample.setMsgState(MsgConst.MSG_STATE_HAVE_READ);
                vo.setReadNumber(memberReceiveModel.getMemberReceiveList(receiveExample, null).size());
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("查看会员发送详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pushId", value = "推送id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<PageVO<MemberReceiveVO>> detail(HttpServletRequest request, Integer pushId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        MemberReceiveExample example = new MemberReceiveExample();
        example.setPushId(pushId);
        example.setSource(MsgConst.SOURCE_SYSTEM);
        example.setMsgStateNotEquals(MsgConst.MSG_STATE_DELETE);
        List<MemberReceive> list = memberReceiveModel.getMemberReceiveList(example, pager);
        List<MemberReceiveVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(memberReceive -> {
                vos.add(new MemberReceiveVO(memberReceive));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("发送消息")
    @OperationLogger(option = "发送消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sendWay", value = "发送方式：1--站内信，2--短信", required = true, paramType = "query"),
            @ApiImplicitParam(name = "content", value = "发送内容", required = true, paramType = "query"),
            @ApiImplicitParam(name = "receiveIds", value = "接收人id串，如: 1,2,3,5", required = true, paramType = "query")
    })
    @PostMapping("send")
    public JsonResult send(HttpServletRequest request, Integer sendWay, String content, String receiveIds) {
        //参数校验
        AssertUtil.notEmpty(receiveIds,"接收人id不能为空");
        AssertUtil.notFormatFrontIds(receiveIds,"receiveIds格式错误,请重试");
        Admin admin = UserUtil.getUser(request, Admin.class);

        SystemPush systemPush = new SystemPush();
        systemPush.setContent(content);
        systemPush.setSendTime(new Date());
        systemPush.setSendWay(sendWay);
        systemPush.setIsCheck(MsgConst.IS_OPEN_SWITCH_YES);
        systemPush.setReceiveType(MsgConst.RECEIVE_TYPE_MEMBER);
        systemPush.setReceiveIds(receiveIds);
        systemPush.setCreateId(admin.getAdminId());
        Integer pushId = systemPushModel.saveSystemPush(systemPush);

        String[] split = receiveIds.split(",");
        if (sendWay == MsgConst.SEND_WAY_SMS) {
            for (String str : split) {
                if (StringUtil.isEmpty(str))
                    continue;
                Member member = memberModel.getMemberByMemberId(Integer.parseInt(str));
                if (!StringUtil.isEmpty(member.getMemberMobile())) {
                    String apiKey = stringRedisTemplate.opsForValue().get("notification_sms_key");
                    msgSendModel.sendSMS(member.getMemberMobile(), systemPush.getContent(), apiKey);
                }
            }
//        } else if (sendWay == MsgConst.SEND_WAY_MAIL) {
//            for (String str : split) {
//                if (StringUtil.isEmpty(str))
//                    continue;
//                Member member = memberModel.getMemberByMemberId(Integer.parseInt(str));
//                // 发送邮件
//                if (!StringUtil.isEmpty(member.getMemberEmail())) {
//                    msgSendModel.sendMail(member.getMemberEmail(), systemPush.getDescription(), content);
//                }
//            }
        } else {
            for (String str : split) {
                if (StringUtil.isEmpty(str))
                    continue;
                Member member = memberModel.getMemberByMemberId(Integer.parseInt(str));
                MemberReceive receive = new MemberReceive();
                receive.setTplCode("system_news");
                receive.setMemberId(member.getMemberId());
                receive.setMemberName(member.getMemberName());
                receive.setMemberMobile(member.getMemberMobile());
                receive.setMsgContent(systemPush.getContent());
                receive.setMsgLinkInfo("");
                receive.setMsgSendTime(new Date());
                receive.setMsgState(MsgConst.MSG_STATE_UNREAD);
                receive.setPushId(pushId);
                receive.setSource(MsgConst.SOURCE_SYSTEM);
                memberReceiveModel.saveMemberReceive(receive);
            }
        }
        return SldResponse.success("发送成功");
    }

    @ApiOperation("测试邮件发送")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "toAddress", value = "测试接收的邮件地址", required = true)
    })
    @PostMapping("testEmailSend")
    public JsonResult testEmailSend(HttpServletRequest request, String toAddress) {
        // 发送邮件
        msgSendModel.sendMail(toAddress, "测试邮件", "这是一封来自商联达商城系统演示站测试邮件发送成功");
        return SldResponse.success("测试邮件发送成功");
    }
}
