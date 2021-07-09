package com.slodon.b2b2c.controller.msg.front;

import com.slodon.b2b2c.core.constant.MsgConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.model.msg.MemberReceiveModel;
import com.slodon.b2b2c.model.msg.SystemTplTypeModel;
import com.slodon.b2b2c.msg.example.MemberReceiveExample;
import com.slodon.b2b2c.msg.example.SystemTplTypeExample;
import com.slodon.b2b2c.msg.pojo.MemberReceive;
import com.slodon.b2b2c.msg.pojo.SystemTplType;
import com.slodon.b2b2c.vo.msg.MessageVO;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "front-消息通知")
@RestController
@RequestMapping("v3/msg/front/msg")
public class FrontMsgController extends BaseController {

    @Resource
    private SystemTplTypeModel systemTplTypeModel;
    @Resource
    private MemberReceiveModel memberReceiveModel;

    @ApiOperation("获取各类型未读消息数")
    @GetMapping("msgListNum")
    public JsonResult<List<MessageVO>> msgListNum(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);

        SystemTplTypeExample example = new SystemTplTypeExample();
        example.setTplSource(MsgConst.TPL_SOURCE_MEMBER);
        List<SystemTplType> list = systemTplTypeModel.getSystemTplTypeList(example, null);
        if (CollectionUtils.isEmpty(list)) {
            return SldResponse.fail("请配置消息模板分类-内置表");
        }
        List<MessageVO> vos = new ArrayList<>();
        for (SystemTplType type : list) {
            MessageVO vo = new MessageVO();
            vo.setTplTypeCode(type.getTplTypeCode());
            vo.setMsgName(type.getTplName());
            MemberReceiveExample receiveExample = new MemberReceiveExample();
            receiveExample.setMemberId(member.getMemberId());
            receiveExample.setTplTypeCode(type.getTplTypeCode());
            receiveExample.setMsgState(MsgConst.MSG_STATE_UNREAD);
            vo.setMsgNum(memberReceiveModel.getMemberReceiveCount(receiveExample));
            vos.add(vo);
        }
        return SldResponse.success(vos);
    }

    @ApiOperation("消息列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tplType", value = "消息类型,空为全部消息(订单消息:order_news,系统消息:system_news,售后消息:after_sale_news,资产消息:assets_news,预约提醒:appointment_news)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("msgList")
    public JsonResult<PageVO<MemberReceive>> msgList(HttpServletRequest request, String tplType) {
        Member member = UserUtil.getUser(request, Member.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        MemberReceiveExample example = new MemberReceiveExample();
        example.setMemberId(member.getMemberId());
        example.setTplTypeCode(tplType);
        example.setMsgStateNotEquals(MsgConst.MSG_STATE_DELETE);
        example.setOrderBy("msg_state asc, msg_send_time desc");
        List<MemberReceive> list = memberReceiveModel.getMemberReceiveList(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("删除消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receiveIds", value = "消息ID集合，用逗号隔开", required = true)
    })
    @PostMapping("del")
    public JsonResult delMsg(HttpServletRequest request, String receiveIds) {
        Member member = UserUtil.getUser(request, Member.class);
        //校验
        AssertUtil.notEmpty(receiveIds, "receiveIds不能为空，请重试！");
        AssertUtil.notFormatFrontIds(receiveIds, "receiveIds格式错误,请重试");

        MemberReceiveExample example = new MemberReceiveExample();
        example.setMemberId(member.getMemberId());
        example.setReceiveIdIn(receiveIds);
        MemberReceive memberReceive = new MemberReceive();
        memberReceive.setMsgState(MsgConst.MSG_STATE_DELETE);
        memberReceive.setMsgOperateTime(new Date());
        memberReceiveModel.updateMemberReceiveByExample(memberReceive, example);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("标为已读")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receiveIds", value = "消息ID集合，用逗号隔开", required = true)
    })
    @PostMapping("read")
    public JsonResult read(HttpServletRequest request, String receiveIds) {
        Member member = UserUtil.getUser(request, Member.class);
        MemberReceiveExample example = new MemberReceiveExample();
        example.setMemberId(member.getMemberId());
        example.setReceiveIdIn(receiveIds);
        MemberReceive memberReceive = new MemberReceive();
        memberReceive.setMsgState(MsgConst.MSG_STATE_HAVE_READ);
        memberReceive.setMsgOperateTime(new Date());
        memberReceiveModel.updateMemberReceiveByExample(memberReceive, example);
        return SldResponse.success("标为已读成功");
    }

}
