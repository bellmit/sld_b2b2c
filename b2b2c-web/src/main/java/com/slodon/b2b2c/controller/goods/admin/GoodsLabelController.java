package com.slodon.b2b2c.controller.goods.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.GoodsLabelAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsLabelEditDTO;
import com.slodon.b2b2c.goods.example.GoodsLabelExample;
import com.slodon.b2b2c.goods.pojo.GoodsLabel;
import com.slodon.b2b2c.model.goods.GoodsLabelModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.goods.GoodsLabelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "admin-商品标签")
@RestController
@RequestMapping("v3/goods/admin/goodsLabel")
public class GoodsLabelController extends BaseController {

    @Resource
    private GoodsLabelModel goodsLabelModel;

    @ApiOperation("商品标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelName", value = "标签名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsLabelVO>> getList(HttpServletRequest request, String labelName) {
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据标签名称查询商品标签集合
        GoodsLabelExample example = new GoodsLabelExample();
        example.setLabelNameLike(labelName);
        List<GoodsLabel> list = goodsLabelModel.getGoodsLabelList(example, pager);

        ArrayList<GoodsLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsLabel -> {
                GoodsLabelVO vo = new GoodsLabelVO(goodsLabel);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("获取商品标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "商品标签id", required = true)
    })
    @GetMapping("goodsLabel")
    public JsonResult<GoodsLabel> getGoodsLabel(HttpServletRequest request, @RequestParam("labelId") Integer labelId) {
        return SldResponse.success(goodsLabelModel.getGoodsLabelByLabelId(labelId));
    }

    @ApiOperation("删除商品标签")
    @OperationLogger(option = "删除商品标签")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelIds", value = "商品标签id集合,用逗号隔开", required = true)
    })
    @PostMapping("del")
    public JsonResult delGoodsLabel(HttpServletRequest request, String labelIds) {
        //参数校验
        AssertUtil.notEmpty(labelIds, "请选择要删除的数据");
        AssertUtil.notFormatFrontIds(labelIds, "labelIds格式错误,请重试");

        goodsLabelModel.batchDeleteGoodsLabel(labelIds);
        return SldResponse.success("删除成功", "商品标签ids:" + labelIds);
    }

    @ApiOperation("新增商品标签")
    @OperationLogger(option = "新增商品标签")
    @PostMapping("add")
    public JsonResult addGoodsLabel(HttpServletRequest request, GoodsLabelAddDTO goodsLabelAddDTO) throws Exception {
        AssertUtil.sortCheck(goodsLabelAddDTO.getSort());

        Admin admin = UserUtil.getUser(request, Admin.class);
        //验证参数是否为空
        AssertUtil.notEmpty(goodsLabelAddDTO.getLabelName(), "商品标签名称不能为空,请重试!");

        goodsLabelModel.saveGoodsLabel(goodsLabelAddDTO, admin.getAdminId());
        return SldResponse.success("保存成功", "商品标签名称:" + goodsLabelAddDTO.getLabelName());
    }

    @ApiOperation("编辑商品标签")
    @OperationLogger(option = "编辑商品标签")
    @PostMapping("edit")
    public JsonResult editGoodsLabel(HttpServletRequest request, GoodsLabelEditDTO goodsLabelEditDTO) throws Exception {
        AssertUtil.sortCheck(goodsLabelEditDTO.getSort());

        AssertUtil.notNullOrZero(goodsLabelEditDTO.getLabelId(), "请选择要修改的数据,请重试!");
        AssertUtil.notEmpty(goodsLabelEditDTO.getLabelName(), "商品标签名称不能为空,请重试!");

        goodsLabelModel.updateGoodsLabel(goodsLabelEditDTO);
        return SldResponse.success("修改成功", "商品标签名称:" + goodsLabelEditDTO.getLabelName());
    }
}
