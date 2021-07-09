package com.slodon.b2b2c.controller.promotion.admin;


import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.constant.SeckillConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.promotion.SeckillStageProductModel;
import com.slodon.b2b2c.promotion.dto.SeckillGoodsAuditDTO;
import com.slodon.b2b2c.promotion.example.SeckillStageProductExample;
import com.slodon.b2b2c.vo.promotion.SeckillStageGoodsVO;
import com.slodon.b2b2c.vo.promotion.SeckillStageProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "admin-秒杀活动商品管理")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/seckillGoods")
public class AdminSeckillGoodsController extends BaseController {

    @Resource
    private SeckillStageProductModel seckillStageProductModel;

    @ApiOperation("获取审核商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "seckillId", value = "活动id", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "verifyState", value = "审核状态 1待审核 3拒绝", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("AuditList")
    public JsonResult<PageVO<SeckillStageGoodsVO>> getAuditList(HttpServletRequest request, Integer seckillId,
                                                                String storeName, String goodsName, Integer verifyState) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<SeckillStageGoodsVO> list = seckillStageProductModel.getAuditList(seckillId, storeName, goodsName, verifyState, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("获取商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "stageId", value = "场次id", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SeckillStageGoodsVO>> getList(HttpServletRequest request, String storeName, String goodsName,
                                                           Integer stageId) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<SeckillStageGoodsVO> list = seckillStageProductModel.getStageGoodsList(storeName, goodsName, stageId, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("查看场次商品的货品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stageId", value = "场次id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
    })
    @GetMapping("productList")
    public JsonResult<PageVO<SeckillStageProductVO>> productList(HttpServletRequest request, Long goodsId, Integer stageId, String storeName, String goodsName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        List<SeckillStageProductVO> list = seckillStageProductModel.getStageProductList(goodsId, stageId, goodsName, storeName);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("商品审核")
    @OperationLogger(option = "商品审核")
    @PostMapping("Audit")
    public JsonResult<Integer> Audit(HttpServletRequest request, SeckillGoodsAuditDTO goodsAuditDTO) {
        // 验证参数是否为空
        AssertUtil.notNull(goodsAuditDTO, "审核信息不能为空，请重试！");
        AssertUtil.notEmpty(goodsAuditDTO.getGoodsIds(), "审核商品不能为空，请重试！");
        AssertUtil.notEmpty(goodsAuditDTO.getStageIds(), "审核商品场次不能为空，请重试！");
        AssertUtil.notNull(goodsAuditDTO.getState(), "审核状态不能为空，请重试！");
        AssertUtil.isTrue(goodsAuditDTO.getState() != SeckillConst.SECKILL_GOODS_AUDIT_AGREE && goodsAuditDTO.getState() != SeckillConst.SECKILL_GOODS_AUDIT_REJECT, "审核状态参数不能为空，请重试！");
        AssertUtil.notFormatIds(goodsAuditDTO.getGoodsIds(), "商品id格式错误,请重试");

        String logMsg = "审核商品ID" + goodsAuditDTO.getGoodsIds();
        //审核拒绝参数判断
        if (goodsAuditDTO.getState().equals(GoodsConst.GOODS_AUDIT_REJECT)) {
            AssertUtil.notEmpty(goodsAuditDTO.getAuditReason(), "审核拒绝原因不能为空，请重试！");
        }
        seckillStageProductModel.Audit(goodsAuditDTO);
        return SldResponse.success("审核成功");
    }

    @ApiOperation("删除秒杀商品")
    @OperationLogger(option = "删除秒杀商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "stageId", value = "场次id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, paramType = "query")
    })
    @PostMapping("del")
    public JsonResult del(HttpServletRequest request, Integer stageId, Long goodsId) {
        SeckillStageProductExample example = new SeckillStageProductExample();
        example.setGoodsId(goodsId);
        example.setStageId(stageId);
        seckillStageProductModel.deleteSeckillStageProductByExample(example);
        return SldResponse.success("删除成功");
    }
}
