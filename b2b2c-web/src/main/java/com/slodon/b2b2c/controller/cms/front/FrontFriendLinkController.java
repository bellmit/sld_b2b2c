package com.slodon.b2b2c.controller.cms.front;

import com.slodon.b2b2c.cms.example.FriendLinkExample;
import com.slodon.b2b2c.cms.pojo.FriendLink;
import com.slodon.b2b2c.core.constant.FriendLinkConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.model.cms.FriendLinkModel;
import com.slodon.b2b2c.vo.cms.FriendLinkVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "front-合作伙伴")
@RestController
@RequestMapping("v3/cms/front/friendLink")
public class FrontFriendLinkController {

    @Resource
    private FriendLinkModel friendLinkModel;

    @ApiOperation("合作伙伴列表")
    @GetMapping("list")
    public JsonResult<List<FriendLinkVO>> getList(HttpServletRequest request) {
        FriendLinkExample example = new FriendLinkExample();
        example.setState(FriendLinkConst.STATE_YES);
        example.setOrderBy("sort asc, create_time desc");
        List<FriendLink> friendLinkList = friendLinkModel.getFriendLinkList(example, null);
        List<FriendLinkVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(friendLinkList)) {
            for (FriendLink friendLink : friendLinkList) {
                vos.add(new FriendLinkVO(friendLink));
            }
        }
        return SldResponse.success(vos);
    }

}
