package com.slodon.b2b2c.controller.cms.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.cms.dto.FriendLinkAddDTO;
import com.slodon.b2b2c.cms.dto.FriendLinkUpdateDTO;
import com.slodon.b2b2c.cms.example.FriendLinkExample;
import com.slodon.b2b2c.cms.pojo.FriendLink;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.cms.FriendLinkModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.cms.FriendLinkVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lxk
 */
@Api(tags = "admin-合作伙伴管理")
@RestController
@Slf4j
@RequestMapping("v3/cms/admin/friendLink")
public class AdminFriendLinkController {

    @Resource
    private FriendLinkModel friendLinkModel;

    @ApiOperation("合作伙伴列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "linkName", value = "合作伙伴名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<FriendLinkVO>> getList(HttpServletRequest request, @RequestParam(value = "linkName", required = false) String linkName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        FriendLinkExample example = new FriendLinkExample();
        example.setLinkNameLike(linkName);
        List<FriendLink> friendLinkList = friendLinkModel.getFriendLinkList(example, pager);
        ArrayList<FriendLinkVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(friendLinkList)) {
            for (FriendLink friendLink : friendLinkList) {
                FriendLinkVO vo = new FriendLinkVO(friendLink);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("合作伙伴详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "linkId", value = "链接id", required = true)
    })
    @GetMapping("detail")
    public JsonResult<FriendLink> getDetail(HttpServletRequest request, @RequestParam("linkId") Integer linkId) {
        return SldResponse.success(friendLinkModel.getFriendLinkByLinkId(linkId));
    }

    @ApiOperation("新增合作伙伴")
    @OperationLogger(option = "新增合作伙伴")
    @PostMapping("add")
    public JsonResult<Integer> addFriendLink(HttpServletRequest request, FriendLinkAddDTO friendLinkAddDTO) {
        String logMsg = "链接名称" + friendLinkAddDTO.getLinkName();

        Admin admin = UserUtil.getUser(request, Admin.class);
        friendLinkModel.saveFriendLink(friendLinkAddDTO, admin);
        return SldResponse.success("保存成功", logMsg);
    }

    @ApiOperation("编辑合作伙伴")
    @OperationLogger(option = "编辑合作伙伴")
    @PostMapping("edit")
    public JsonResult<Integer> editFriendLink(HttpServletRequest request, FriendLinkUpdateDTO friendLinkUpdateDTO) throws Exception {
        String logMsg = "链接id" + friendLinkUpdateDTO.getLinkId();

        Admin admin = UserUtil.getUser(request, Admin.class);

        friendLinkModel.updateFriendLink(friendLinkUpdateDTO, admin);
        return SldResponse.success("修改成功", logMsg);
    }

    @ApiOperation("删除合作伙伴")
    @OperationLogger(option = "删除合作伙伴")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "linkIds", value = "链接ID集合,用逗号隔开", required = true)
    })
    @PostMapping("del")
    public JsonResult<Integer> deleteFriendLink(HttpServletRequest request, String linkIds) {
        //参数校验
        AssertUtil.notEmpty(linkIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(linkIds, "linkIds格式错误,请重试");

        String logMsg = "链接ID集合" + linkIds;
        friendLinkModel.batchDeleteFriendLink(linkIds);
        return SldResponse.success("删除成功", logMsg);
    }

}
