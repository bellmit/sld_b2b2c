package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.PromotionConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Goods;
import com.slodon.b2b2c.model.goods.GoodsModel;
import com.slodon.b2b2c.model.promotion.CouponModel;
import com.slodon.b2b2c.model.promotion.FullNldRuleModel;
import com.slodon.b2b2c.model.promotion.FullNumLadderDiscountModel;
import com.slodon.b2b2c.promotion.example.FullNldRuleExample;
import com.slodon.b2b2c.promotion.example.FullNumLadderDiscountExample;
import com.slodon.b2b2c.promotion.pojo.Coupon;
import com.slodon.b2b2c.promotion.pojo.FullNldRule;
import com.slodon.b2b2c.promotion.pojo.FullNumLadderDiscount;
import com.slodon.b2b2c.vo.promotion.FullCouponVO;
import com.slodon.b2b2c.vo.promotion.FullDiscountVO;
import com.slodon.b2b2c.vo.promotion.FullGiftVO;
import com.slodon.b2b2c.vo.promotion.FullNldDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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

@Api(tags = "admin-满N件折扣")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/fullNld")
public class AdminFullNldController extends BaseController {

    @Resource
    private FullNumLadderDiscountModel fullNumLadderDiscountModel;
    @Resource
    private FullNldRuleModel fullNldRuleModel;
    @Resource
    private CouponModel couponModel;
    @Resource
    private GoodsModel goodsModel;

    @ApiOperation("阶梯满件折扣列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "fullName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "活动状态( 1-已创建，2-已发布，3-进行中，4-已结束  5-已失效)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<FullDiscountVO>> list(HttpServletRequest request, String storeName, String fullName,
                                                   Date startTime, Date endTime, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        FullNumLadderDiscountExample example = new FullNumLadderDiscountExample();
        example.setStoreNameLike(storeName);
        example.setFullNameLike(fullName);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == PromotionConst.STATE_RELEASE_2) {
                example.setState(PromotionConst.STATE_RELEASE_2);
                example.setStartTimeAfter(new Date());
            } else if (state == PromotionConst.STATE_ON_GOING_3) {
                example.setState(PromotionConst.STATE_RELEASE_2);
                example.setStartTimeBefore(new Date());
                example.setEndTimeAfter(new Date());
            } else if (state == PromotionConst.STATE_COMPLETE_4) {
                example.setState(PromotionConst.STATE_RELEASE_2);
                example.setEndTimeBefore(new Date());
            } else {
                example.setState(state);
            }
        }
        example.setStateNotEquals(PromotionConst.STATE_DELETED_6);
        List<FullNumLadderDiscount> list = fullNumLadderDiscountModel.getFullNumLadderDiscountList(example, pager);
        List<FullDiscountVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(discount -> {
                vos.add(new FullDiscountVO(discount));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("阶梯满件折扣详情")
    @GetMapping("detail")
    public JsonResult<FullNldDetailVO> detail(HttpServletRequest request, Integer fullId) {
        AssertUtil.notNullOrZero(fullId, "阶梯满件折扣活动id不能为空");

        FullNumLadderDiscount fullNumLadderDiscount = fullNumLadderDiscountModel.getFullNumLadderDiscountByFullId(fullId);

        FullNldDetailVO detailVO = new FullNldDetailVO(fullNumLadderDiscount);
        //查询阶梯满件折扣规则
        FullNldRuleExample example = new FullNldRuleExample();
        example.setFullId(fullId);
        List<FullNldRule> list = fullNldRuleModel.getFullNldRuleList(example, null);
        AssertUtil.notEmpty(list, "获取阶梯满件折扣规则信息为空，请重试");

        List<FullNldDetailVO.FullNldRuleVO> ruleList = new ArrayList<>();
        for (FullNldRule rule : list) {
            FullNldDetailVO.FullNldRuleVO ruleVO = new FullNldDetailVO.FullNldRuleVO(rule);
            if (!StringUtil.isEmpty(rule.getSendCouponIds())) {
                String sendCouponIds = rule.getSendCouponIds();
                String[] couponIds = sendCouponIds.split(",");
                List<FullCouponVO> couponVOS = new ArrayList<>();
                for (String str : couponIds) {
                    if (StringUtil.isEmpty(str)) continue;
                    Coupon coupon = couponModel.getCouponByCouponId(Integer.parseInt(str));
                    if (coupon == null) {
                        continue;
                    }
                    couponVOS.add(new FullCouponVO(coupon));
                }
                ruleVO.setCouponList(couponVOS);
            }
            if (!StringUtil.isEmpty(rule.getSendGoodsIds())) {
                String sendGoodsIds = rule.getSendGoodsIds();
                String[] goodsIds = sendGoodsIds.split(",");
                List<FullGiftVO> giftVOS = new ArrayList<>();
                for (String str : goodsIds) {
                    if (StringUtil.isEmpty(str)) continue;
                    Goods goods = goodsModel.getGoodsByGoodsId(Long.parseLong(str));
                    AssertUtil.notNull(goods, "获取商品信息为空");

                    giftVOS.add(new FullGiftVO(goods));
                }
                ruleVO.setGiftList(giftVOS);
            }
            ruleList.add(ruleVO);
        }
        detailVO.setRuleList(ruleList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("删除阶梯满件折扣")
    @OperationLogger(option = "删除阶梯满件折扣")
    @PostMapping("del")
    public JsonResult delFullNld(HttpServletRequest request, Integer fullId) {
        fullNumLadderDiscountModel.deleteFullNumLadderDiscount(fullId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("失效阶梯满件折扣")
    @OperationLogger(option = "失效阶梯满件折扣")
    @PostMapping("invalid")
    public JsonResult invalidFullNld(HttpServletRequest request, Integer fullId) {
        FullNumLadderDiscount fullNumLadderDiscount = new FullNumLadderDiscount();
        fullNumLadderDiscount.setFullId(fullId);
        fullNumLadderDiscount.setState(PromotionConst.STATE_EXPIRED_5);
        fullNumLadderDiscountModel.updateFullNumLadderDiscount(fullNumLadderDiscount);
        return SldResponse.success("失效成功");
    }

}
