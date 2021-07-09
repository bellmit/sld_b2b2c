package com.slodon.b2b2c.controller.member.front;

import com.slodon.b2b2c.core.constant.MemberConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.member.dto.MemberAddressAddDTO;
import com.slodon.b2b2c.member.dto.MemberAddressUpdateDTO;
import com.slodon.b2b2c.member.example.MemberAddressExample;
import com.slodon.b2b2c.member.pojo.Member;
import com.slodon.b2b2c.member.pojo.MemberAddress;
import com.slodon.b2b2c.model.member.MemberAddressModel;
import com.slodon.b2b2c.vo.member.AddressDetailVO;
import com.slodon.b2b2c.vo.member.MemberAddressVO;
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
import java.util.List;

@Api(tags = "front-我的地址")
@Slf4j
@RestController
@RequestMapping("v3/member/front/memberAddress")
public class MemberAddressController extends BaseController {

    @Resource
    private MemberAddressModel memberAddressModel;

    @ApiOperation("新增收货地址")
    @PostMapping("add")
    public JsonResult addMemberAddress(HttpServletRequest request, MemberAddressAddDTO memberAddressAddDTO) throws Exception {
        Member member = UserUtil.getUser(request, Member.class);
        Integer addressId = memberAddressModel.saveMemberAddress(memberAddressAddDTO, member.getMemberId());
        return SldResponse.success("新增成功",addressId);
    }

    @ApiOperation("删除收货地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressIds", value = "地址id集合,用逗号隔开", paramType = "query", required = true)
    })
    @PostMapping("del")
    public JsonResult deleteMemberAddress(HttpServletRequest request, String addressIds) {
        //参数校验
        AssertUtil.notEmpty(addressIds,"请选择要删除的数据");
        AssertUtil.notFormatFrontIds(addressIds,"addressIds格式错误,请重试");

        //删除
        Member member = UserUtil.getUser(request, Member.class);
        memberAddressModel.batchDeleteMemberAddress(addressIds, member.getMemberId());
        return SldResponse.success("删除成功");
    }

    @ApiOperation("编辑收货地址")
    @PostMapping("edit")
    public JsonResult editMemberAddress(HttpServletRequest request, MemberAddressUpdateDTO memberAddressUpdateDTO) throws Exception {
        //校验
        MemberAddress detail = memberAddressModel.getMemberAddressByAddressId(memberAddressUpdateDTO.getAddressId());
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.isTrue(!member.getMemberId().equals(detail.getMemberId()), "无权限");

        memberAddressModel.updateMemberAddress(memberAddressUpdateDTO, member.getMemberId());
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("收货地址列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<MemberAddressVO>> getList(HttpServletRequest request) {
        Member member = UserUtil.getUser(request, Member.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        MemberAddressExample example = new MemberAddressExample();
        example.setMemberId(member.getMemberId());
        example.setPager(pager);
        List<MemberAddress> list = memberAddressModel.getMemberAddressList(example, pager);
        ArrayList<MemberAddressVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(memberAddress -> {
                vos.add(new MemberAddressVO(memberAddress));
            });
        }
        return SldResponse.success(new PageVO<>(vos, example.getPager()));
    }

    @ApiOperation("设置默认")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "地址id", required = true),
            @ApiImplicitParam(name = "isDefault", value = "是否默认地址：1-默认地址，0-非默认地址", required = true)
    })
    @PostMapping("changeDefaultAddress")
    public JsonResult changeDefaultAddress(HttpServletRequest request, Integer addressId, Integer isDefault) {
        //校验
        MemberAddress detail = memberAddressModel.getMemberAddressByAddressId(addressId);
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.isTrue(!member.getMemberId().equals(detail.getMemberId()), "无权限");

        memberAddressModel.changeDefaultAddress(addressId, isDefault, member.getMemberId());
        return SldResponse.success(isDefault == MemberConst.IS_DEFAULT_1 ? "设置默认成功" : "设置非默认成功");
    }

    @ApiOperation("地址详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addressId", value = "地址id", paramType = "query", required = true)
    })
    @GetMapping("detail")
    public JsonResult<AddressDetailVO> getMemberAddress(HttpServletRequest request, Integer addressId) {
        //校验
        MemberAddress detail = memberAddressModel.getMemberAddressByAddressId(addressId);
        Member member = UserUtil.getUser(request, Member.class);
        AssertUtil.isTrue(!member.getMemberId().equals(detail.getMemberId()), "无权限");

        AddressDetailVO vo = new AddressDetailVO(detail);
        return SldResponse.success(vo);
    }

}
