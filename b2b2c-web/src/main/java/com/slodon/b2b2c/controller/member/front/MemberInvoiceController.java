package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.dto.MemberInvoiceAddDTO;
import com.slodon.b2b2c.member.dto.MemberInvoiceUpdateDTO;
import com.slodon.b2b2c.member.example.MemberInvoiceExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberInvoice;
import com.slodon.b2b2c.model.member.MemberInvoiceModel;
import com.slodon.b2b2c.vo.member.InvoiceVO;
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
import java.util.List;

@Api(tags = "front-发票")
@RestController
@RequestMapping("v3/member/front/memberInvoice")
public class MemberInvoiceController extends BaseController {

    @Resource
    private MemberInvoiceModel memberInvoiceModel;

    @ApiOperation("新增发票")
    @PostMapping("add")
    public JsonResult addMemberInvoice(HttpServletRequest request, MemberInvoiceAddDTO memberInvoiceAddDTO) throws Exception {
        Member member = UserUtil.getUser(request, Member.class);
        Integer invoiceId = memberInvoiceModel.saveMemberInvoice(memberInvoiceAddDTO, member.getMemberId());
        return SldResponse.success("新增成功", invoiceId);
    }

    @ApiOperation("删除发票")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "invoiceIds", value = "发票id集合,用逗号隔开", paramType = "query", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteMemberInvoice(HttpServletRequest request, String invoiceIds) {
        //参数校验
        AssertUtil.notEmpty(invoiceIds,"请选择要删除的数据");
        AssertUtil.notFormatFrontIds(invoiceIds,"invoiceIds格式错误,请重试");

        //删除
        Member member = UserUtil.getUser(request, Member.class);
        memberInvoiceModel.batchDeleteMemberInvoice(invoiceIds, member.getMemberId());
        return SldResponse.success("删除成功");
    }

    @ApiOperation("编辑发票")
    @PostMapping("edit")
    public JsonResult editMemberInvoice(HttpServletRequest request, MemberInvoiceUpdateDTO memberInvoiceUpdateDTO) throws Exception {
        //校验
        MemberInvoice detail = memberInvoiceModel.getMemberInvoiceByInvoiceId(memberInvoiceUpdateDTO.getInvoiceId());
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.isTrue(!member.getMemberId().equals(detail.getMemberId()), "无权限");

        memberInvoiceModel.updateMemberInvoice(memberInvoiceUpdateDTO, member.getMemberId());
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("发票列表")
    @GetMapping("list")
    public JsonResult<PageVO<InvoiceVO>> getList(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        MemberInvoiceExample example = new MemberInvoiceExample();
        example.setMemberId(member.getMemberId());
        example.setPager(pager);

        List<MemberInvoice> list = memberInvoiceModel.getMemberInvoiceList(example, pager);
        ArrayList<InvoiceVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(memberInvoice -> {
                InvoiceVO vo = new InvoiceVO(memberInvoice);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, example.getPager()));
    }

    @ApiOperation("更改默认发票")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "invoiceId", value = "发票id", required = true),
            @ApiImplicitParam(name = "isDefault", value = "是否默认发票：true-默认发票，false-非默认发票", required = true)
    })
    @PostMapping("changeDefaultInvoice")
    public JsonResult changeDefaultInvoice(HttpServletRequest request,
                                           Integer invoiceId,
                                           Boolean isDefault) {

        //校验
        MemberInvoice detail = memberInvoiceModel.getMemberInvoiceByInvoiceId(invoiceId);
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.isTrue(!member.getMemberId().equals(detail.getMemberId()), "无权限");

        memberInvoiceModel.changeDefaultInvoice(invoiceId, isDefault, member.getMemberId());
        return SldResponse.success("更改默认发票成功");
    }

}
