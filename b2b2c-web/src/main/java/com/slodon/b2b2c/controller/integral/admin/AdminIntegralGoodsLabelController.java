package com.slodon.b2b2c.controller.integral.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.integral.example.IntegralGoodsLabelExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsLabel;
import com.slodon.b2b2c.model.integral.IntegralGoodsLabelModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.integral.IntegralGoodsLabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "admin-标签管理")
@RestController
@RequestMapping("v3/integral/admin/integral/goodsLabel")
public class AdminIntegralGoodsLabelController extends BaseController {

    @Resource
    private IntegralGoodsLabelModel integralGoodsLabelModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", required = true, defaultValue = "0", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<IntegralGoodsLabelVO>> list(HttpServletRequest request, @RequestParam(value = "labelId", defaultValue = "0") Integer labelId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralGoodsLabelExample example = new IntegralGoodsLabelExample();
        example.setParentLabelId(labelId);
        List<IntegralGoodsLabel> list = integralGoodsLabelModel.getIntegralGoodsLabelList(example, pager);
        List<IntegralGoodsLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsLabel -> {
                IntegralGoodsLabelVO vo = new IntegralGoodsLabelVO(goodsLabel);
                //查询是否有二级标签
                example.setParentLabelId(goodsLabel.getLabelId());
                vo.setChildren(integralGoodsLabelModel.getIntegralGoodsLabelCount(example) > 0 ? new ArrayList<>() : null);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取商品标签树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parentLabelId", value = "上级标签id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "grade", value = "查询层数", paramType = "query"),
    })
    @GetMapping("getLabelTree")
    public JsonResult<List<IntegralGoodsLabelVO>> getLabelTree(HttpServletRequest request, Integer parentLabelId, Integer grade) {
        return SldResponse.success(this.getGoodsLabelTree(parentLabelId, grade));
    }

    /**
     * 获取商品标签树
     *
     * @param parentLabelId 上级标签id
     * @param grade         获取级别，比如 pid=0,pid=0,grade=2时，获取1、2级分类；pid=0,grade=1时，获取1级分类；
     * @return
     */
    private List<IntegralGoodsLabelVO> getGoodsLabelTree(Integer parentLabelId, Integer grade) {
        if (grade == 0) {
            return null;
        }
        IntegralGoodsLabelExample example = new IntegralGoodsLabelExample();
        example.setParentLabelId(parentLabelId);
        List<IntegralGoodsLabel> list = integralGoodsLabelModel.getIntegralGoodsLabelList(example, null);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<IntegralGoodsLabelVO> vos = new ArrayList<>();
        list.forEach(goodsLabel -> {
            IntegralGoodsLabelVO vo = new IntegralGoodsLabelVO(goodsLabel);
            vo.setChildren(getGoodsLabelTree(goodsLabel.getLabelId(), grade - 1));
            vos.add(vo);
        });
        return vos;
    }

    @ApiOperation("新增标签")
    @OperationLogger(option = "新增标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelName", value = "标签名称", required = true),
            @ApiImplicitParam(name = "parentLabelId", value = "上级标签id", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true),
            @ApiImplicitParam(name = "image", value = "二级标签图片")
    })
    @PostMapping("add")
    public JsonResult addGoodsLabel(HttpServletRequest request, String labelName, Integer sort, String image,
                                    @RequestParam(value = "parentLabelId", required = false, defaultValue = "0") Integer parentLabelId) {
        AssertUtil.sortCheck(sort);

        Admin admin = UserUtil.getUser(request, Admin.class);

        IntegralGoodsLabel label = new IntegralGoodsLabel();
        label.setLabelName(labelName);
        label.setSort(sort);
        label.setParentLabelId(parentLabelId);
        label.setImage(image);
        label.setCreateAdminId(admin.getAdminId());
        label.setCreateTime(new Date());
        integralGoodsLabelModel.saveIntegralGoodsLabel(label);
        return SldResponse.success("新增成功");
    }

    @ApiOperation("编辑标签")
    @OperationLogger(option = "编辑标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", required = true),
            @ApiImplicitParam(name = "labelName", value = "标签名称", required = true),
            @ApiImplicitParam(name = "parentLabelId", value = "上级标签id", defaultValue = "0"),
            @ApiImplicitParam(name = "sort", value = "排序", required = true),
            @ApiImplicitParam(name = "image", value = "二级标签图片")
    })
    @PostMapping("update")
    public JsonResult updateGoodsLabel(HttpServletRequest request, Integer labelId, String labelName, Integer sort, String image,
                                       @RequestParam(value = "parentLabelId", required = false, defaultValue = "0") Integer parentLabelId) {
        AssertUtil.sortCheck(sort);

        Admin admin = UserUtil.getUser(request, Admin.class);

        IntegralGoodsLabel label = new IntegralGoodsLabel();
        label.setLabelId(labelId);
        label.setLabelName(labelName);
        label.setSort(sort);
        label.setParentLabelId(parentLabelId);
        label.setImage(image);
        label.setUpdateAdminId(admin.getAdminId());
        label.setUpdateTime(new Date());
        integralGoodsLabelModel.updateIntegralGoodsLabel(label);
        return SldResponse.success("编辑成功");
    }

    @ApiOperation("是否显示")
    @OperationLogger(option = "是否显示")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", required = true),
            @ApiImplicitParam(name = "state", value = "标签状态：0、不显示；1、显示", required = true)
    })
    @PostMapping("isShow")
    public JsonResult isShow(HttpServletRequest request, Integer labelId, Integer state) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        IntegralGoodsLabel label = new IntegralGoodsLabel();
        label.setLabelId(labelId);
        label.setState(state);
        integralGoodsLabelModel.updateIntegralGoodsLabel(label);
        return SldResponse.success("修改成功");
    }

    @ApiOperation("设置广告")
    @OperationLogger(option = "设置广告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", required = true),
            @ApiImplicitParam(name = "data", value = "一级分类广告图", required = true)
    })
    @PostMapping("setAdv")
    public JsonResult setAdv(HttpServletRequest request, Integer labelId, String data) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        IntegralGoodsLabel label = new IntegralGoodsLabel();
        label.setLabelId(labelId);
        label.setData(data);
        integralGoodsLabelModel.updateIntegralGoodsLabel(label);
        return SldResponse.success("设置成功");
    }

    @ApiOperation("删除标签")
    @OperationLogger(option = "删除标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签id", required = true)
    })
    @PostMapping("del")
    public JsonResult delGoodsLabel(HttpServletRequest request, Integer labelId) {
        Admin admin = UserUtil.getUser(request, Admin.class);

        integralGoodsLabelModel.deleteIntegralGoodsLabel(labelId);
        return SldResponse.success("删除成功");
    }
}
