package com.slodon.b2b2c.controller.integral.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.integral.example.IntegralGoodsExample;
import com.slodon.b2b2c.integral.example.IntegralProductExample;
import com.slodon.b2b2c.integral.pojo.IntegralGoods;
import com.slodon.b2b2c.integral.pojo.IntegralProduct;
import com.slodon.b2b2c.model.integral.IntegralGoodsModel;
import com.slodon.b2b2c.model.integral.IntegralProductModel;
import com.slodon.b2b2c.system.pojo.Admin;
import com.slodon.b2b2c.vo.integral.IntegralGoodsVO;
import com.slodon.b2b2c.vo.integral.IntegralProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

@Api(tags = "admin-商品管理")
@RestController
@RequestMapping("v3/integral/admin/integral/goods")
public class AdminIntegralGoodsController extends BaseController {

    @Resource
    private IntegralGoodsModel integralGoodsModel;
    @Resource
    private IntegralProductModel integralProductModel;

    @ApiOperation("商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态:2-待审核商品；3-在售商品；4-仓库中商品；6-违规下架商品", paramType = "query"),
            @ApiImplicitParam(name = "auditState", value = "审核状态：2-待审核；4-审核拒绝(待审核商品查询使用)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<IntegralGoodsVO>> list(HttpServletRequest request, String goodsName, String storeName,
                                                    Date startTime, Date endTime, Integer state, Integer auditState) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        IntegralGoodsExample example = new IntegralGoodsExample();
        example.setGoodsNameLike(goodsName);
        example.setStoreNameLike(storeName);
        example.setCreateTimeAfter(startTime);
        example.setCreateTimeBefore(endTime);
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
        List<IntegralGoodsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(goods -> {
                IntegralGoodsVO vo = new IntegralGoodsVO(goods);
                //查询货品列表
                IntegralProductExample productExample = new IntegralProductExample();
                productExample.setGoodsId(goods.getIntegralGoodsId());
                productExample.setState(GoodsConst.PRODUCT_STATE_1);
                List<IntegralProduct> productList = integralProductModel.getIntegralProductList(productExample, null);
                AssertUtil.notEmpty(productList, "积分货品不存在");
                List<IntegralProductVO> productVOS = new ArrayList<>();
                for (IntegralProduct product : productList) {
                    productVOS.add(new IntegralProductVO(product));
                }
                vo.setProductList(productVOS);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("违规下架")
    @OperationLogger(option = "违规下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "integralGoodsIds", value = "商品Id集合，用逗号隔开", required = true),
            @ApiImplicitParam(name = "offlineReason", value = "违规下架原因", required = true),
            @ApiImplicitParam(name = "offlineComment", value = "违规下架备注")
    })
    @PostMapping("lockup")
    public JsonResult<Integer> lockupGoods(HttpServletRequest request, String integralGoodsIds, String offlineReason, String offlineComment) {
        Admin admin = UserUtil.getUser(request, Admin.class);
        // 验证参数是否为空
        AssertUtil.notEmpty(integralGoodsIds, "请选择要下架的数据！");
        AssertUtil.notEmpty(offlineReason, "下架原因不能为空，请重试！");
        AssertUtil.notFormatIds(integralGoodsIds, "商品id格式错误,请重试");

        IntegralGoods goodsUpdate = new IntegralGoods();
        goodsUpdate.setOfflineReason(offlineReason);
        goodsUpdate.setOfflineComment(offlineComment);
        goodsUpdate.setState(GoodsConst.GOODS_STATE_LOWER_BY_SYSTEM);
        goodsUpdate.setUpdateTime(new Date());
        IntegralGoodsExample example = new IntegralGoodsExample();
        example.setIntegralGoodsIdIn(integralGoodsIds);
        int number = integralGoodsModel.updateIntegralGoods(goodsUpdate, example);
        return SldResponse.success(number + "件商品下架成功");
    }

    @ApiOperation("商品审核")
    @OperationLogger(option = "商品审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "integralGoodsIds", value = "商品Id集合，用逗号隔开", required = true),
            @ApiImplicitParam(name = "state", value = "审核拒绝或通过,0-拒绝,1-通过", required = true),
            @ApiImplicitParam(name = "auditReason", value = "审核拒绝原因,当审核拒绝时必填"),
            @ApiImplicitParam(name = "auditComment", value = "审核拒绝备注")
    })
    @PostMapping("audit")
    public JsonResult<Integer> audit(HttpServletRequest request, String integralGoodsIds, String state, String auditReason, String auditComment) {
        Admin admin = UserUtil.getUser(request, Admin.class);
        // 验证参数是否为空
        AssertUtil.notEmpty(integralGoodsIds, "审核商品不能为空，请重试！");
        AssertUtil.notEmpty(state, "审核状态不能为空，请重试！");
        AssertUtil.notFormatIds(integralGoodsIds, "商品id格式错误,请重试");

        int number = 0;
        IntegralGoodsExample example = new IntegralGoodsExample();
        example.setIntegralGoodsIdIn(integralGoodsIds);
        IntegralGoods goodsUpdate = new IntegralGoods();
        //审核拒绝时 保存拒绝原因 备注
        if (state.equals(GoodsConst.GOODS_AUDIT_REJECT)) {
            AssertUtil.notEmpty(auditReason, "审核拒绝原因不能为空，请重试！");
            //批量保存审核拒绝时 保存拒绝原因 备注
            goodsUpdate.setAuditReason(auditReason);
            goodsUpdate.setAuditComment(auditComment);
            goodsUpdate.setUpdateTime(new Date());
            // 放入仓库待审核和立即上架待审核的商品 状态 改为审核拒绝
            goodsUpdate.setState(GoodsConst.GOODS_STATE_REJECT);
            goodsUpdate.setOnlineTime(null);
            example.setStateIn(GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT + "," + GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT);
            number = integralGoodsModel.updateIntegralGoods(goodsUpdate, example);
        } else { //审核通过处理
            // 放入仓库待审核的商品 状态 改为放入仓库审核通过
            goodsUpdate.setState(GoodsConst.GOODS_STATE_WAREHOUSE_AUDIT_PASS);
            example.setState(GoodsConst.GOODS_STATE_WAREHOUSE_TO_AUDIT);
            goodsUpdate.setOnlineTime(new Date());
            number = integralGoodsModel.updateIntegralGoods(goodsUpdate, example);

            // 立即上架待审核的商品 状态 改为GOODS_STATE_UPPER
            goodsUpdate.setState(GoodsConst.GOODS_STATE_UPPER);
            example.setState(GoodsConst.GOODS_STATE_SELL_NOW_TO_AUDIT);
            goodsUpdate.setOnlineTime(new Date());
            goodsUpdate.setOnlineTime(new Date());
            number += integralGoodsModel.updateIntegralGoods(goodsUpdate, example);
        }
        return SldResponse.success("操作成功");
    }
}
