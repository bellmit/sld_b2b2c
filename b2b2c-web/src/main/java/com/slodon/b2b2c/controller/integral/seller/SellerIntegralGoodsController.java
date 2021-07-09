package com.slodon.b2b2c.controller.integral.seller;

import com.slodon.b2b2c.aop.VendorLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.IntegralConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.integral.dto.GoodsImportFrontParamDTO;
import com.slodon.b2b2c.integral.dto.GoodsPublishFrontParamDTO;
import com.slodon.b2b2c.integral.dto.GoodsPublishInsertDTO;
import com.slodon.b2b2c.integral.example.*;
import com.slodon.b2b2c.integral.pojo.*;
import com.slodon.b2b2c.model.integral.*;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.integral.IntegralGoodsDetailVO;
import com.slodon.b2b2c.vo.integral.IntegralGoodsLabelVO;
import com.slodon.b2b2c.vo.integral.IntegralGoodsVO;
import com.slodon.b2b2c.vo.integral.IntegralProductVO;
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

@Api(tags = "seller-商品管理")
@RestController
@RequestMapping("v3/integral/seller/integral/goods")
public class SellerIntegralGoodsController extends BaseController {

    @Resource
    private IntegralGoodsModel integralGoodsModel;
    @Resource
    private IntegralProductModel integralProductModel;
    @Resource
    private IntegralGoodsBindLabelModel integralGoodsBindLabelModel;
    @Resource
    private IntegralGoodsPictureModel integralGoodsPictureModel;
    @Resource
    private IntegralGoodsLabelModel integralGoodsLabelModel;
    @Resource
    private RabbitTemplate rabbitTemplate;

    @ApiOperation("商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态:2-待审核商品；3-在售商品；4-仓库中商品；6-违规下架商品", paramType = "query"),
            @ApiImplicitParam(name = "auditState", value = "审核状态：2-待审核；4-审核拒绝(待审核商品查询使用)", paramType = "query"),
            @ApiImplicitParam(name = "goodsCode", value = "商品货号", paramType = "query"),
            @ApiImplicitParam(name = "barCode", value = "商品条形码", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<IntegralGoodsVO>> list(HttpServletRequest request, String goodsName, Date startTime, Date endTime,
                                                    Integer state, Integer auditState, String goodsCode, String barCode) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralGoodsExample example = new IntegralGoodsExample();
        example.setStoreId(vendor.getStoreId());
        example.setGoodsNameLike(goodsName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);
        example.setProductCodeLike(goodsCode);
        example.setBarCodeLike(barCode);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == GoodsConst.GOODS_STATE_2) {
                if (!StringUtil.isNullOrZero(auditState)) {
                    if (auditState == GoodsConst.GOODS_STATE_2) {
                        example.setStateIn(GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT + ", " + GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT);
                    } else {
                        example.setState(GoodsConst.GOODS_STATE_REJECT);
                    }
                } else {
                    example.setStateIn(GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT + ", " + GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT + ", " + GoodsConst.GOODS_STATE_REJECT);
                }
            } else if (state == GoodsConst.GOODS_STATE_4) {
                example.setStateIn(GoodsConst.GOODS_STATE_WAREHOUSE_NO_AUDIT + ", " + GoodsConst.GOODS_STATE_WAREHOUSE_AUDIT_PASS + ", " + GoodsConst.GOODS_STATE_LOWER_BY_STORE);
            } else {
                example.setState(state);
            }
        } else {
            //默认显示在售商品
            example.setState(GoodsConst.GOODS_STATE_UPPER);
        }
        example.setStateNotEquals(GoodsConst.GOODS_STATE_DELETE);
        List<IntegralGoods> list = integralGoodsModel.getIntegralGoodsList(example, pager);
        //查找goods对应的goodsExtend 构造VO
        List<IntegralGoodsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goods -> {
                //是否达到预警值，true==是
                boolean warning = false;
                IntegralGoodsVO vo = new IntegralGoodsVO(goods);
                //查询货品列表
                IntegralProductExample productExample = new IntegralProductExample();
                productExample.setGoodsId(goods.getIntegralGoodsId());
                productExample.setState(GoodsConst.PRODUCT_STATE_1);
                List<IntegralProduct> productList = integralProductModel.getIntegralProductList(productExample, null);
                AssertUtil.notEmpty(productList, "商品不存在");
                List<IntegralProductVO> productVOS = new ArrayList<>();
                for (IntegralProduct product : productList) {
                    productVOS.add(new IntegralProductVO(product));
                    if (product.getProductStock().compareTo(product.getProductStockWarning()) <= 0) {
                        warning = true;
                    }
                }
                vo.setProductList(productVOS);
                vo.setWarning(warning);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("商品详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "integralGoodsId", value = "商品Id", paramType = "query", required = true),
    })
    @GetMapping("detail")
    public JsonResult<IntegralGoodsDetailVO> detail(HttpServletRequest request, Long integralGoodsId) {
        IntegralGoods goods = integralGoodsModel.getIntegralGoodsByIntegralGoodsId(integralGoodsId);
        AssertUtil.isTrue(goods == null, "商品不存在，请重试！");

        //商品绑定标签
        IntegralGoodsBindLabelExample goodsBindLabelExample = new IntegralGoodsBindLabelExample();
        goodsBindLabelExample.setGoodsId(integralGoodsId);
        goodsBindLabelExample.setOrderBy("bind_id asc");
        List<IntegralGoodsBindLabel> goodsBindLabelList = integralGoodsBindLabelModel.getIntegralGoodsBindLabelList(goodsBindLabelExample, null);

        //正常和禁用的货品
        IntegralProductExample productExample = new IntegralProductExample();
        productExample.setGoodsId(integralGoodsId);
        productExample.setStateIn(GoodsConst.PRODUCT_STATE_1 + "," + GoodsConst.PRODUCT_STATE_2);
        productExample.setOrderBy("integral_product_id asc");
        List<IntegralProduct> productList = integralProductModel.getIntegralProductList(productExample, null);

        //查询商品图片
        IntegralGoodsPictureExample goodsPictureExample = new IntegralGoodsPictureExample();
        goodsPictureExample.setGoodsId(integralGoodsId);
        goodsPictureExample.setOrderBy("picture_id asc");
        List<IntegralGoodsPicture> goodsPictureList = integralGoodsPictureModel.getIntegralGoodsPictureList(goodsPictureExample, null);

        IntegralGoodsDetailVO detailVO = new IntegralGoodsDetailVO(goods, goodsBindLabelList, goodsPictureList, productList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("发布商品")
    @VendorLogger(option = "发布商品")
    @PostMapping("add")
    public JsonResult<Integer> addGoods(HttpServletRequest request, @RequestBody GoodsPublishFrontParamDTO paramDTO) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        //参数校验
        this.publishCheck(paramDTO);
        //构造入库dto
        GoodsPublishInsertDTO insertDTO = new GoodsPublishInsertDTO(paramDTO);

        integralGoodsModel.saveIntegralGoods(vendor, insertDTO);
        return SldResponse.success("发布商品成功");
    }

    @ApiOperation("编辑商品")
    @VendorLogger(option = "编辑商品")
    @PostMapping("edit")
    public JsonResult editGoods(HttpServletRequest request, @RequestBody GoodsPublishFrontParamDTO paramDTO) {
        AssertUtil.notNullOrZero(paramDTO.getIntegralGoodsId(), "请选择要编辑的商品");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //查询数据库中的商品
        IntegralGoods goodsDb = integralGoodsModel.getIntegralGoodsByIntegralGoodsId(paramDTO.getIntegralGoodsId());
        AssertUtil.notNull(goodsDb, "商品不存在");

        this.publishCheck(paramDTO);
        //构造入库dto
        GoodsPublishInsertDTO insertDTO = new GoodsPublishInsertDTO(paramDTO);
        integralGoodsModel.updateIntegralGoods(vendor, insertDTO, goodsDb);
        return SldResponse.success("编辑商品成功");
    }

    /**
     * 发布编辑商品参数校验
     *
     * @param paramDTO
     */
    private void publishCheck(GoodsPublishFrontParamDTO paramDTO) {
        AssertUtil.notEmpty(paramDTO.getGoodsName(), "请填写商品名称");
        AssertUtil.notEmpty(paramDTO.getLabelIds(), "请选择积分商品标签");

        //多规格校验
        if (!CollectionUtils.isEmpty(paramDTO.getSpecInfoList())) {
            paramDTO.getSpecInfoList().forEach(specInfo -> {
                AssertUtil.notEmpty(specInfo.getSpecValueList(), "规格：" + specInfo.getSpecName() + "未添加规格值");
                if (specInfo.getIsMainSpec() == GoodsConst.IS_MAIN_SPEC_YES) {
                    //主规格，规格值图片必传
                    specInfo.getSpecValueList().forEach(specValueInfo -> {
                        AssertUtil.notEmpty(specValueInfo.getImageList(), "请上传主规格图片");
                    });
                }
            });
        }
    }

    @ApiOperation("商品上架")
    @VendorLogger(option = "商品上架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "integralGoodsIds", value = "商品Id集合，用逗号隔开", required = true, paramType = "query")
    })
    @PostMapping("upperShelf")
    public JsonResult<Integer> upperShelfGoods(HttpServletRequest request, String integralGoodsIds) {
        // 验证参数是否为空
        AssertUtil.notEmpty(integralGoodsIds, "上架商品ID不能为空,请重试");
        AssertUtil.notFormatFrontIds(integralGoodsIds, "integralGoodsIds格式错误,请重试");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        int number = integralGoodsModel.upperShelfGoods(vendor.getStoreId(), integralGoodsIds);
        return SldResponse.success(number + "件商品上架成功");
    }

    @ApiOperation("商品下架")
    @VendorLogger(option = "商品下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "integralGoodsIds", value = "商品Id集合，用逗号隔开", required = true, paramType = "query")
    })
    @PostMapping("lockup")
    public JsonResult<Integer> lockupGoods(HttpServletRequest request, String integralGoodsIds) {
        // 验证参数是否为空
        AssertUtil.notEmpty(integralGoodsIds, "下架商品ID不能为空,请重试");
        AssertUtil.notFormatFrontIds(integralGoodsIds, "integralGoodsIds格式错误,请重试");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        int number = integralGoodsModel.lockupGoods(vendor.getStoreId(), integralGoodsIds);
        return SldResponse.success(number + "件商品下架成功");
    }

    @ApiOperation("商品删除")
    @VendorLogger(option = "商品删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "integralGoodsIds", value = "商品Id集合，用逗号隔开", required = true, paramType = "query")
    })
    @PostMapping("deleteGoods")
    public JsonResult<Integer> deleteGoods(HttpServletRequest request, String integralGoodsIds) {
        // 验证参数是否为空
        AssertUtil.notEmpty(integralGoodsIds, "删除商品ID不能为空,请重试");
        AssertUtil.notFormatFrontIds(integralGoodsIds, "integralGoodsIds格式错误,请重试");

        Vendor vendor = UserUtil.getUser(request, Vendor.class);
        int number = integralGoodsModel.deleteGoods(vendor.getStoreId(), integralGoodsIds);
        return SldResponse.success(number + "件商品删除成功");
    }

    @ApiOperation("从商城导入商品")
    @VendorLogger(option = "从商城导入商品")
    @PostMapping("importGoods")
    public JsonResult<Integer> importGoods(HttpServletRequest request, @RequestBody List<GoodsImportFrontParamDTO> paramDTOList) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        integralGoodsModel.importIntegralGoods(vendor, paramDTOList);
        return SldResponse.success("导入成功");
    }

    @ApiOperation("标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("labelList")
    public JsonResult<PageVO<IntegralGoodsLabelVO>> labelList(HttpServletRequest request) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralGoodsLabelExample example = new IntegralGoodsLabelExample();
        example.setParentLabelId(0);
        example.setState(IntegralConst.STATE_1);
        example.setOrderBy("sort asc, create_time desc");
        List<IntegralGoodsLabel> list = integralGoodsLabelModel.getIntegralGoodsLabelList(example, pager);
        List<IntegralGoodsLabelVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goodsLabel -> {
                IntegralGoodsLabelVO vo = new IntegralGoodsLabelVO(goodsLabel);
                //查询是否有二级标签
                example.setParentLabelId(goodsLabel.getLabelId());
                List<IntegralGoodsLabel> childrenList = integralGoodsLabelModel.getIntegralGoodsLabelList(example, null);
                if (CollectionUtils.isEmpty(childrenList)) {
                    vo.setChildren(null);
                } else {
                    List<IntegralGoodsLabelVO> children = new ArrayList<>();
                    childrenList.forEach(label -> {
                        IntegralGoodsLabelVO labelVO = new IntegralGoodsLabelVO(label);
                        labelVO.setChildren(null);
                        children.add(labelVO);
                    });
                    vo.setChildren(children);
                }
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
