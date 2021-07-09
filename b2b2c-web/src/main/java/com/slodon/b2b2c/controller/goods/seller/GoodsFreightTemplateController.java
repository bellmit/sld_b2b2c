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
import com.slodon.b2b2c.goods.dto.GoodsFreightTemplateDTO;
import com.slodon.b2b2c.goods.example.GoodsFreightExtendExample;
import com.slodon.b2b2c.goods.example.GoodsFreightTemplateExample;
import com.slodon.b2b2c.goods.pojo.GoodsFreightTemplate;
import com.slodon.b2b2c.model.goods.GoodsFreightExtendModel;
import com.slodon.b2b2c.model.goods.GoodsFreightTemplateModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.goods.FreightTemplateListVO;
import com.slodon.b2b2c.vo.goods.GoodsFreightTemplateVO;
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

@Api(tags = "seller物流运费模板")
@RestController
@Slf4j
@RequestMapping("v3/goods/seller/goodsFreightTemplate")
public class GoodsFreightTemplateController extends BaseController {

    @Resource
    private GoodsFreightTemplateModel goodsFreightTemplateModel;
    @Resource
    private GoodsFreightExtendModel goodsFreightExtendModel;

    @ApiOperation("获取运费模板列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "templateName", value = "模板名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<GoodsFreightTemplateVO>> getList(HttpServletRequest request,
                                                              @RequestParam(value = "templateName", required = false) String templateName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        GoodsFreightTemplateExample goodsFreightTemplateExample = new GoodsFreightTemplateExample();
        goodsFreightTemplateExample.setStoreId(vendor.getStoreId());
        goodsFreightTemplateExample.setTemplateNameLike(templateName);
        List<GoodsFreightTemplate> goodsFreightTemplateList = goodsFreightTemplateModel.getGoodsFreightTemplateList(goodsFreightTemplateExample, pager);
        List<GoodsFreightTemplateVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsFreightTemplateList)) {
            for (GoodsFreightTemplate goodsFreightTemplate : goodsFreightTemplateList) {
                GoodsFreightExtendExample extendExample = new GoodsFreightExtendExample();
                extendExample.setFreightTemplateId(goodsFreightTemplate.getFreightTemplateId());
                goodsFreightTemplate.setFreightExtendList(goodsFreightExtendModel.getGoodsFreightExtendList(extendExample, null));
                GoodsFreightTemplateVO vo = new GoodsFreightTemplateVO(goodsFreightTemplate);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }


    @ApiOperation("新增运费模板")
    @VendorLogger(option = "新增运费模板")
    @PostMapping("add")
    public JsonResult<Integer> addGoodsFreightTemplate(HttpServletRequest request, GoodsFreightTemplateDTO goodsFreightTemplateDTO) {
        String logMsg = "运费模板名称：" + goodsFreightTemplateDTO.getTemplateName();
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.notEmpty(goodsFreightTemplateDTO.getTemplateName(), "运费模板名称不能为空");

        goodsFreightTemplateModel.saveFreightTemplate(goodsFreightTemplateDTO, vendor);
        return SldResponse.success("添加成功", logMsg);
    }

    @ApiOperation("获取运费模板详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "freightTemplateId", value = "运费模板ID", required = true)
    })
    @PostMapping("detail")
    public JsonResult<GoodsFreightTemplateVO> getDetail(HttpServletRequest request, Integer freightTemplateId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        GoodsFreightTemplate freightTemplate = goodsFreightTemplateModel.getGoodsFreightTemplateByFreightTemplateId(freightTemplateId);
        AssertUtil.isTrue(!freightTemplate.getStoreId().equals(vendor.getStoreId()), "您无权限操作");
        GoodsFreightTemplateVO vo = new GoodsFreightTemplateVO(freightTemplate);
        return SldResponse.success(vo);
    }

    @ApiOperation("修改运费模板")
    @VendorLogger(option = "修改运费模板")
    @PostMapping("update")
    public JsonResult updateGoodsFreightTemplate(HttpServletRequest request, GoodsFreightTemplateDTO goodsFreightTemplateDTO) {
        String logMsg = "运费模板id：" + goodsFreightTemplateDTO.getFreightTemplateId();
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        AssertUtil.notNullOrZero(goodsFreightTemplateDTO.getFreightTemplateId(), "运费模板id不能为空");
        GoodsFreightTemplate freightTemplate = goodsFreightTemplateModel.getGoodsFreightTemplateByFreightTemplateId(goodsFreightTemplateDTO.getFreightTemplateId());
        AssertUtil.isTrue(!freightTemplate.getStoreId().equals(vendor.getStoreId()), "您无权限操作");
        //判断模板名称是否重复
        GoodsFreightTemplateExample example = new GoodsFreightTemplateExample();
        example.setStoreId(vendor.getStoreId());
        example.setTemplateName(goodsFreightTemplateDTO.getTemplateName());
        example.setFreightTemplateIdNotEquals(goodsFreightTemplateDTO.getFreightTemplateId());
        List<GoodsFreightTemplate> list = goodsFreightTemplateModel.getGoodsFreightTemplateList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "模板名称已存在，请重新填写");

        goodsFreightTemplateModel.updateFreightTemplate(goodsFreightTemplateDTO);
        return SldResponse.success("修改成功", logMsg);
    }

    @ApiOperation("删除运费模板")
    @VendorLogger(option = "删除运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "freightTemplateId", value = "运费模板id", required = true)
    })
    @PostMapping("delete")
    public JsonResult deleteGoodsFreightTemplate(HttpServletRequest request, Integer freightTemplateId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.notNullOrZero(freightTemplateId, "运费模板id不能为空");
        GoodsFreightTemplate freightTemplate = goodsFreightTemplateModel.getGoodsFreightTemplateByFreightTemplateId(freightTemplateId);
        AssertUtil.isTrue(!freightTemplate.getStoreId().equals(vendor.getStoreId()), "您无权限操作");

        goodsFreightTemplateModel.deleteGoodsFreightTemplate(freightTemplateId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("根据商品模板类型获取模板接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chargeType", value = "计费方式：1-按件，2-按重量，3-按体积", required = true)
    })
    @PostMapping("getByType")
    public JsonResult<PageVO<FreightTemplateListVO>> getGoodsFreightTemplatesByType(HttpServletRequest request,
                                                                                    @RequestParam("chargeType") String chargeType) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        AssertUtil.notEmpty(chargeType, "模板类型不能为空");

        GoodsFreightTemplateExample goodsFreightTemplateExample = new GoodsFreightTemplateExample();
        goodsFreightTemplateExample.setChargeType(chargeType);
        goodsFreightTemplateExample.setPager(pager);
        List<GoodsFreightTemplate> goodsFreightTemplates = goodsFreightTemplateModel.getGoodsFreightTemplatesByType(goodsFreightTemplateExample, pager);
        List<FreightTemplateListVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsFreightTemplates)) {
            for (GoodsFreightTemplate goodsFreightTemplate : goodsFreightTemplates) {
                FreightTemplateListVO vo = new FreightTemplateListVO(goodsFreightTemplate);
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, goodsFreightTemplateExample.getPager()));
    }

    @ApiOperation("删除运费模板详情接口")
    @VendorLogger(option = "删除运费模板详情接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "freightExtendId", value = "运费模板扩展ID", required = true)
    })
    @PostMapping("delFreightExtend")
    public JsonResult deleteGoodsFreightExtend(HttpServletRequest request, Integer freightExtendId) {
        AssertUtil.notNullOrZero(freightExtendId, "运费模板扩展id不能为空");
        goodsFreightExtendModel.deleteGoodsFreightExtend(freightExtendId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("复制运费模板接口")
    @VendorLogger(option = "复制运费模板接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "freightTemplateId", value = "运费模板id", required = true)
    })
    @PostMapping("copy")
    public JsonResult copyGoodsFreightTemplate(HttpServletRequest request, Integer freightTemplateId) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        String logMsg = "运费模板id" + freightTemplateId;
        AssertUtil.notNullOrZero(freightTemplateId, "运费模板id不能为空");
        GoodsFreightTemplate freightTemplate = goodsFreightTemplateModel.getGoodsFreightTemplateByFreightTemplateId(freightTemplateId);
        AssertUtil.isTrue(!freightTemplate.getStoreId().equals(vendor.getStoreId()), "您无权限操作");

        goodsFreightTemplateModel.copyGoodsFreightTemplate(freightTemplateId);
        return SldResponse.success("复制成功");
    }

}
