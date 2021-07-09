package com.slodon.b2b2c.controller.goods.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.GoodsRelatedTemplateAddDTO;
import com.slodon.b2b2c.goods.dto.GoodsRelatedTemplateUpdateDTO;
import com.slodon.b2b2c.goods.example.GoodsRelatedTemplateExample;
import com.slodon.b2b2c.goods.pojo.GoodsRelatedTemplate;
import com.slodon.b2b2c.model.goods.GoodsRelatedTemplateModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.GoodsRelatedTemplateVO;
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

import static com.slodon.b2b2c.core.constant.GoodsConst.TEMPLATE_CONTENT_1;
import static com.slodon.b2b2c.core.constant.GoodsConst.TEMPLATE_CONTENT_2;

@Api(tags = "seller关联版式")
@RestController
@RequestMapping("v3/goods/seller/goodsRelatedTemplate")
public class GoodsRelatedTemplateController extends BaseController {

    @Resource
    private GoodsRelatedTemplateModel goodsRelatedTemplateModel;

    @ApiOperation("新增关联版式")
    @VendorLogger(option = "新增关联版式")
    @PostMapping("add")
    public JsonResult<Integer> addGoodsRelatedTemplate(HttpServletRequest request, GoodsRelatedTemplateAddDTO goodsRelatedTemplateAddDTO) throws Exception {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //验证参数
        AssertUtil.isTrue(goodsRelatedTemplateAddDTO == null,"关联模板不能为空,请重试!");
        AssertUtil.notEmpty(goodsRelatedTemplateAddDTO.getTemplateName(),"关联模板名称不能为空,请重试!");
        AssertUtil.notNullOrZero(goodsRelatedTemplateAddDTO.getTemplatePosition(),"关联模板位置不能为空,请重试!");
        AssertUtil.notEmpty(goodsRelatedTemplateAddDTO.getTemplateContent(),"关联模板内容不能为空,请重试!");

        goodsRelatedTemplateModel.saveGoodsRelatedTemplate(goodsRelatedTemplateAddDTO, vendor.getStoreId());
        return SldResponse.success("添加成功", goodsRelatedTemplateAddDTO.getTemplateName());
    }

    @ApiOperation("删除关联版式")
    @VendorLogger(option = "删除关联版式")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateIds", value = "关联版式id集合,用逗号隔开", required = true)
    })
    @PostMapping("delete")
    public JsonResult deleteGoodsRelatedTemplate(HttpServletRequest request, String templateIds) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //参数校验
        AssertUtil.notEmpty(templateIds,"请选择要删除的数据");
        AssertUtil.notFormatFrontIds(templateIds,"templateIds格式错误,请重试");

        goodsRelatedTemplateModel.deleteGoodsRelatedTemplate(templateIds,vendor.getStoreId());
        return SldResponse.success("删除成功");
    }

    @ApiOperation("编辑关联版式")
    @VendorLogger(option = "编辑关联版式")
    @PostMapping("edit")
    public JsonResult editGoodsRelatedTemplate(HttpServletRequest request, GoodsRelatedTemplateUpdateDTO goodsRelatedTemplateUpdateDTO) throws Exception {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //参数校验
        AssertUtil.notNullOrZero(goodsRelatedTemplateUpdateDTO.getTemplateId(),"请选择要删除的数据");

        goodsRelatedTemplateModel.updateGoodsRelatedTemplate(goodsRelatedTemplateUpdateDTO,vendor.getStoreId());
        return SldResponse.success("编辑成功", goodsRelatedTemplateUpdateDTO.getTemplateName());
    }

    @ApiOperation("关联版式列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateName", value = "版式名称", paramType = "query"),
            @ApiImplicitParam(name = "templatePosition", value = "版式位置", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsRelatedTemplateVO>> getList(HttpServletRequest request,
                                                              @RequestParam(value = "templateName", required = false) String templateName,
                                                              @RequestParam(value = "templatePosition", required = false) Integer templatePosition) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //分页设置
        PagerInfo pager = WebUtil.handlerPagerInfo(request);

        //根据条件查询集合
        GoodsRelatedTemplateExample example = new GoodsRelatedTemplateExample();
        example.setStoreId(vendor.getStoreId());
        example.setTemplateNameLike(templateName);
        if (templatePosition != null && (templatePosition == TEMPLATE_CONTENT_1 || templatePosition == TEMPLATE_CONTENT_2)) {
            example.setTemplatePosition(templatePosition);
        }
        List<GoodsRelatedTemplate> list = goodsRelatedTemplateModel.getGoodsRelatedTemplateList(example, pager);

        //响应
        List<GoodsRelatedTemplateVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsRelatedTemplate -> {
                GoodsRelatedTemplateVO vo = new GoodsRelatedTemplateVO(goodsRelatedTemplate);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("关联版式详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateId", value = "模板id", paramType = "query"),
    })
    @GetMapping("details")
    public JsonResult<GoodsRelatedTemplateVO> getDetails(HttpServletRequest request,
                                                         @RequestParam(value = "templateId", required = false) Integer templateId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(templateId,"模板id不能为空和0,请重试");
        GoodsRelatedTemplate goodsRelatedTemplate = goodsRelatedTemplateModel.getGoodsRelatedTemplateByTemplateId(templateId);

        AssertUtil.notNull(goodsRelatedTemplate, "关联版式id:"+templateId+"详情为空!");
        AssertUtil.isTrue(!goodsRelatedTemplate.getStoreId().equals(vendor.getStoreId()), "无操作权限，请重试！");
        return SldResponse.success(new GoodsRelatedTemplateVO(goodsRelatedTemplate));
    }
}
