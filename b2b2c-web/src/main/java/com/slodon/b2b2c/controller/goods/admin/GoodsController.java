package com.slodon.b2b2c.controller.goods.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.GoodsConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.dto.GoodsAuditDTO;
import com.slodon.b2b2c.goods.dto.GoodsLookUpDTO;
import com.slodon.b2b2c.goods.example.GoodsExample;
import com.slodon.b2b2c.goods.example.ProductExample;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.goods.pojo.GoodsExtend;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.GoodsExtendModel;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.vo.goods.GoodsVO;
import com.slodon.b2b2c.vo.goods.ProductVO;
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
@RequestMapping("v3/goods/admin/goods")
public class GoodsController extends BaseController {

    @Resource
    private GoodsModel goodsModel;
    @Resource
    private ProductModel productModel;
    @Resource
    private GoodsExtendModel goodsExtendModel;

    @ApiOperation("获取商品列表")
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
    public JsonResult<PageVO<GoodsVO>> getList(HttpServletRequest request, String goodsName, String storeName,
                                               Date startTime, Date endTime, Integer state, Integer auditState) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        GoodsExample example = new GoodsExample();
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
        example.setIsDelete(GoodsConst.GOODS_IS_DELETE_NO);
        List<Goods> goodsList = goodsModel.getGoodsList(example, pager);
        List<GoodsVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            goodsList.forEach(goods -> {
                GoodsExtend goodsExtend = goodsExtendModel.getGoodsExtendByGoodsId(goods.getGoodsId());
                //查询货品列表
                ProductExample productExample = new ProductExample();
                productExample.setGoodsId(goods.getGoodsId());
                productExample.setStateIn(GoodsConst.PRODUCT_STATE_1 + "," + GoodsConst.PRODUCT_STATE_3);
                List<Product> productList = productModel.getProductList(productExample, null);
                AssertUtil.notEmpty(productList, "商品不存在");
                List<ProductVO> productVOS = new ArrayList<>();
                for (Product product : productList) {
                    productVOS.add(new ProductVO(product));
                }
                GoodsVO vo = new GoodsVO(goods, goodsExtend);
                vo.setProductList(productVOS);
                vos.add(vo);
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("商品下架")
    @OperationLogger(option = "商品下架")
    @PostMapping("lockup")
    public JsonResult<Integer> lockup(HttpServletRequest request, GoodsLookUpDTO goodsLookUpDTO) {
        // 验证参数是否为空
        AssertUtil.notNull(goodsLookUpDTO, "下架信息不能为空，请重试！");
        AssertUtil.notEmpty(goodsLookUpDTO.getGoodsIds(), "下架信息不能为空，请重试！");
        AssertUtil.notEmpty(goodsLookUpDTO.getOfflineReason(), "下架原因不能为空，请重试！");
        AssertUtil.notFormatIds(goodsLookUpDTO.getGoodsIds(), "商品id格式错误,请重试");

        String goodsIds = goodsLookUpDTO.getGoodsIds();
        String logMsg = "下架商品ID" + goodsIds;
        int number = goodsModel.goodsLockup(goodsLookUpDTO);
        return SldResponse.success(number + "件商品下架成功", logMsg);
    }

    @ApiOperation("商品审核")
    @OperationLogger(option = "商品审核")
    @PostMapping("Audit")
    public JsonResult<Integer> Audit(HttpServletRequest request, GoodsAuditDTO goodsAuditDTO) {
        // 验证参数是否为空
        AssertUtil.notNull(goodsAuditDTO, "审核信息不能为空，请重试！");
        AssertUtil.notEmpty(goodsAuditDTO.getGoodsIds(), "审核商品不能为空，请重试！");
        AssertUtil.notEmpty(goodsAuditDTO.getState(), "审核状态不能为空，请重试！");
        AssertUtil.notFormatIds(goodsAuditDTO.getGoodsIds(), "商品id格式错误,请重试");

        String logMsg = "审核商品ID" + goodsAuditDTO.getGoodsIds();
        //审核拒绝参数判断
        if (goodsAuditDTO.getState().equals(GoodsConst.GOODS_AUDIT_REJECT)) {
            AssertUtil.notEmpty(goodsAuditDTO.getAuditReason(), "审核拒绝原因不能为空，请重试！");
        }
        goodsModel.Audit(goodsAuditDTO);
        return SldResponse.success(goodsAuditDTO.getState().equals(GoodsConst.GOODS_AUDIT_AGREE) ? "审核通过" : "审核拒绝", logMsg);
    }
}
