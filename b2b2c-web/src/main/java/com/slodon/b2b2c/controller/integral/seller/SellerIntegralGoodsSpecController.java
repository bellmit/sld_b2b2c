package com.slodon.b2b2c.controller.integral.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.integral.example.IntegralGoodsSpecExample;
import com.slodon.b2b2c.integral.example.IntegralGoodsSpecValueExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsSpec;
import com.slodon.b2b2c.integral.pojo.IntegralGoodsSpecValue;
import com.slodon.b2b2c.model.integral.IntegralGoodsSpecModel;
import com.slodon.b2b2c.model.integral.IntegralGoodsSpecValueModel;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.integral.IntegralGoodsSpecVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

@Api(tags = "seller-规格管理")
@RestController
@RequestMapping("v3/integral/seller/integral/goodsSpec")
public class SellerIntegralGoodsSpecController {

    @Resource
    private IntegralGoodsSpecModel integralGoodsSpecModel;
    @Resource
    private IntegralGoodsSpecValueModel integralGoodsSpecValueModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("规格列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specName", value = "规格名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<IntegralGoodsSpecVO>> getList(HttpServletRequest request, String specName) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralGoodsSpecExample example = new IntegralGoodsSpecExample();
        example.setSpecNameLike(specName);
        example.setStoreId(vendor.getStoreId());
        List<IntegralGoodsSpec> goodsSpecList = integralGoodsSpecModel.getIntegralGoodsSpecList(example, pager);
        List<IntegralGoodsSpecVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsSpecList)) {
            goodsSpecList.forEach(spec -> {
                IntegralGoodsSpecValueExample specValueExample = new IntegralGoodsSpecValueExample();
                specValueExample.setSpecId(spec.getSpecId());
                List<IntegralGoodsSpecValue> goodsSpecValueList = integralGoodsSpecValueModel.getIntegralGoodsSpecValueList(specValueExample, null);
                vos.add(new IntegralGoodsSpecVO(spec, goodsSpecValueList));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("新增规格")
    @VendorLogger(option = "新增规格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specName", value = "规格名称", required = true)
    })
    @PostMapping("add")
    public JsonResult<Integer> addSpec(HttpServletRequest request, String specName) {
        // 验证参数是否为空
        AssertUtil.notEmpty(specName, "规格名称不能为空，请重试！");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //查重
        IntegralGoodsSpecExample goodsSpecExample = new IntegralGoodsSpecExample();
        goodsSpecExample.setStoreId(vendor.getStoreId());
        goodsSpecExample.setSpecName(specName);
        List<IntegralGoodsSpec> list = integralGoodsSpecModel.getIntegralGoodsSpecList(goodsSpecExample, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "规格名称已存在，请重新填写");

        IntegralGoodsSpec goodsSpec = new IntegralGoodsSpec();
        goodsSpec.setSpecName(specName);
        goodsSpec.setSpecType(GoodsConst.SPEC_TYPE_1);
        goodsSpec.setStoreId(vendor.getStoreId());
        goodsSpec.setCreateId(vendor.getVendorId().intValue());
        goodsSpec.setCreateName(vendor.getVendorName());
        goodsSpec.setCreateTime(new Date());
        goodsSpec.setUpdateId(vendor.getVendorId().intValue());
        goodsSpec.setUpdateName(vendor.getVendorName());
        goodsSpec.setUpdateTime(new Date());
        goodsSpec.setState(1);
        goodsSpec.setSort(1);
        Integer specId = integralGoodsSpecModel.saveIntegralGoodsSpec(goodsSpec);
        return SldResponse.success("添加成功", specId);
    }

    @ApiOperation("新增规格值")
    @VendorLogger(option = "新增规格值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "specId", value = "规格Id", required = true),
            @ApiImplicitParam(name = "specValue", value = "规格值", required = true),
    })
    @PostMapping("addSpecValue")
    public JsonResult<Integer> addSpecValue(HttpServletRequest request, Integer specId, String specValue) {
        // 验证参数是否为空
        AssertUtil.notNullOrZero(specId, "规则Id不能为空，请重试！");
        AssertUtil.notEmpty(specValue, "规则值不能为空，请重试！");

        IntegralGoodsSpec goodsSpec = integralGoodsSpecModel.getIntegralGoodsSpecBySpecId(specId);
        AssertUtil.notNull(goodsSpec, "未查询到对应规格，请重试！");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        AssertUtil.isTrue(!vendor.getStoreId().equals(goodsSpec.getStoreId()), "没有权限操作");

        //判断规则值是否重复
        IntegralGoodsSpecValueExample example = new IntegralGoodsSpecValueExample();
        example.setSpecId(specId);
        example.setSpecValue(specValue);
        List<IntegralGoodsSpecValue> list = integralGoodsSpecValueModel.getIntegralGoodsSpecValueList(example, null);
        AssertUtil.isTrue(!CollectionUtils.isEmpty(list), "规则值已存在，请重新填写");

        //插入规格值表
        IntegralGoodsSpecValue goodsSpecValue = new IntegralGoodsSpecValue();
        goodsSpecValue.setSpecId(specId);
        goodsSpecValue.setSpecValue(specValue);
        goodsSpecValue.setStoreId(vendor.getStoreId());
        goodsSpecValue.setCreateId(vendor.getVendorId().intValue());
        goodsSpecValue.setCreateTime(new Date());
        Integer specValueId = integralGoodsSpecValueModel.saveIntegralGoodsSpecValue(goodsSpecValue);
        return SldResponse.success("添加成功", specValueId);
    }

}
