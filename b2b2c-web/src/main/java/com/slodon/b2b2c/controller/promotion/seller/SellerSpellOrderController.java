package com.slodon.b2b2c.controller.promotion.seller;

import com.slodon.b2b2c.business.example.OrderExtendExample;
import com.slodon.b2b2c.business.example.OrderProductExample;
import com.slodon.b2b2c.business.pojo.Order;
import com.slodon.b2b2c.business.pojo.OrderExtend;
import com.slodon.b2b2c.business.pojo.OrderProduct;
import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.UserUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.model.business.OrderExtendModel;
import com.slodon.b2b2c.model.business.OrderModel;
import com.slodon.b2b2c.model.business.OrderProductModel;
import com.slodon.b2b2c.model.promotion.SpellGoodsModel;
import com.slodon.b2b2c.model.promotion.SpellTeamMemberModel;
import com.slodon.b2b2c.model.promotion.SpellTeamModel;
import com.slodon.b2b2c.promotion.example.SpellGoodsExample;
import com.slodon.b2b2c.promotion.example.SpellTeamExample;
import com.slodon.b2b2c.promotion.example.SpellTeamMemberExample;
import com.slodon.b2b2c.promotion.pojo.SpellGoods;
import com.slodon.b2b2c.promotion.pojo.SpellTeam;
import com.slodon.b2b2c.promotion.pojo.SpellTeamMember;
import com.slodon.b2b2c.seller.pojo.Vendor;
import com.slodon.b2b2c.vo.promotion.SpellMemberOrderVO;
import com.slodon.b2b2c.vo.promotion.SpellOrderDetailVO;
import com.slodon.b2b2c.vo.promotion.SpellOrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Api(tags = "seller-拼团活动订单")
@RestController
@Slf4j
@RequestMapping("v3/promotion/seller/spell/order")
public class SellerSpellOrderController extends BaseController {

    @Resource
    private SpellTeamModel spellTeamModel;
    @Resource
    private SpellTeamMemberModel spellTeamMemberModel;
    @Resource
    private SpellGoodsModel spellGoodsModel;
    @Resource
    private OrderModel orderModel;
    @Resource
    private OrderExtendModel orderExtendModel;
    @Resource
    private OrderProductModel orderProductModel;

    @ApiOperation("拼团订单列表(全部活动)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellName", value = "拼团活动名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "下单开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", paramType = "query"),
            @ApiImplicitParam(name = "spellStartTime", value = "拼团开始时间", paramType = "query"),
            @ApiImplicitParam(name = "spellEndTime", value = "拼团结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "团状态(1-拼团中；2-成功；3-失败）", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("spellList")
    public JsonResult<PageVO<SpellOrderVO>> spellList(HttpServletRequest request, String spellName, String orderSn, String memberName,
                                                      String goodsName, Date startTime, Date endTime, Integer state,
                                                      Date spellStartTime, Date spellEndTime) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellTeamExample example = new SpellTeamExample();
        example.setStoreId(vendor.getStoreId());
        example.setSpellNameLike(spellName);
        example.setGoodsNameLike(goodsName);
        example.setState(state);
        example.setOrderSnLike(orderSn);
        example.setMemberNameLike(memberName);
        example.setOrderStartTime(startTime);
        example.setOrderEndTime(endTime);
        example.setCreateTimeAfter(spellStartTime);
        example.setCreateTimeBefore(spellEndTime);
        example.setLeaderPayState(SpellConst.PAY_STATE_1);
        List<SpellTeam> list = spellTeamModel.getSpellTeamList(example, pager);
        List<SpellOrderVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (SpellTeam team : list) {
                SpellOrderVO vo = new SpellOrderVO(team);
                //查询团队成员
                SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
                teamMemberExample.setSpellTeamId(team.getSpellTeamId());
                teamMemberExample.setOrderSnLike(example.getOrderSnLike());
                teamMemberExample.setMemberNameLike(example.getMemberNameLike());
                List<SpellTeamMember> memberList = spellTeamMemberModel.getSpellTeamMemberList(teamMemberExample, null);
                List<SpellMemberOrderVO> voList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(memberList)) {
                    for (SpellTeamMember member : memberList) {
                        //查询订单信息
                        Order order = orderModel.getOrderByOrderSn(member.getOrderSn());
                        AssertUtil.notNull(order, "无此订单");
                        // 拼团订单一个订单号对应一个货品
                        OrderProductExample productExample = new OrderProductExample();
                        productExample.setOrderSn(member.getOrderSn());
                        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(productExample, null);
                        AssertUtil.notEmpty(orderProductList, "获取订单货品明细信息为空，请重试");
                        SpellMemberOrderVO memberOrderVO = new SpellMemberOrderVO(member, orderProductList.get(0));
                        memberOrderVO.setOrderState(order.getOrderState());
                        memberOrderVO.setOrderStateValue(SpellMemberOrderVO.dealOrderStateValue(order.getOrderState()));
                        voList.add(memberOrderVO);
                    }
                    vo.setMemberList(voList);
                }
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("拼团活动订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "orderSn", value = "订单号", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "会员名称", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "下单开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "下单结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "团状态(1-拼团中；2-成功；3-失败）", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SpellOrderVO>> list(HttpServletRequest request, Integer spellId, String orderSn, String memberName,
                                                 String goodsName, Date startTime, Date endTime, Integer state) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellTeamExample example = new SpellTeamExample();
        example.setStoreId(vendor.getStoreId());
        example.setSpellId(spellId);
        example.setGoodsNameLike(goodsName);
        example.setState(state);
        example.setOrderSnLike(orderSn);
        example.setMemberNameLike(memberName);
        example.setOrderStartTime(startTime);
        example.setOrderEndTime(endTime);
        example.setLeaderPayState(SpellConst.PAY_STATE_1);
        List<SpellTeam> list = spellTeamModel.getSpellTeamList(example, pager);
        List<SpellOrderVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (SpellTeam team : list) {
                SpellOrderVO vo = new SpellOrderVO(team);
                //查询团队成员
                SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
                teamMemberExample.setSpellTeamId(team.getSpellTeamId());
                teamMemberExample.setOrderSnLike(example.getOrderSnLike());
                teamMemberExample.setMemberNameLike(example.getMemberNameLike());
                List<SpellTeamMember> memberList = spellTeamMemberModel.getSpellTeamMemberList(teamMemberExample, null);
                List<SpellMemberOrderVO> voList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(memberList)) {
                    for (SpellTeamMember member : memberList) {
                        //查询订单信息
                        Order order = orderModel.getOrderByOrderSn(member.getOrderSn());
                        AssertUtil.notNull(order, "无此订单");
                        // 拼团订单一个订单号对应一个货品
                        OrderProductExample productExample = new OrderProductExample();
                        productExample.setOrderSn(member.getOrderSn());
                        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(productExample, null);
                        AssertUtil.notEmpty(orderProductList, "获取订单货品明细信息为空，请重试");
                        SpellMemberOrderVO memberOrderVO = new SpellMemberOrderVO(member, orderProductList.get(0));
                        memberOrderVO.setOrderState(order.getOrderState());
                        memberOrderVO.setOrderStateValue(SpellMemberOrderVO.dealOrderStateValue(order.getOrderState()));
                        voList.add(memberOrderVO);
                    }
                    vo.setMemberList(voList);
                }
                vos.add(vo);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("订单详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "订单号", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<SpellOrderDetailVO> detail(HttpServletRequest request, String orderSn) {
        Vendor vendor = UserUtil.getUser(request, Vendor.class);

        //查询订单信息
        Order order = orderModel.getOrderByOrderSn(orderSn);
        AssertUtil.notNull(order, "无此订单信息");
        AssertUtil.isTrue(!order.getStoreId().equals(vendor.getStoreId()), "无权限");

        //查询订单货品信息
        OrderProductExample example = new OrderProductExample();
        example.setOrderSn(orderSn);
        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(example, null);
        AssertUtil.notEmpty(orderProductList, "获取订单货品信息为空，请重试");
        OrderProduct orderProduct = orderProductList.get(0);

        //查询订单扩展信息
        OrderExtendExample extendExample = new OrderExtendExample();
        extendExample.setOrderSn(orderSn);
        List<OrderExtend> orderExtendList = orderExtendModel.getOrderExtendList(extendExample, null);
        AssertUtil.notEmpty(orderExtendList, "获取订单扩展信息为空，请重试");
        OrderExtend orderExtend = orderExtendList.get(0);

        //查询团队成员信息
        SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
        teamMemberExample.setSpellTeamId(orderProduct.getSpellTeamId());
        teamMemberExample.setOrderSn(orderSn);
        List<SpellTeamMember> memberList = spellTeamMemberModel.getSpellTeamMemberList(teamMemberExample, null);
        AssertUtil.notEmpty(memberList, "获取拼团成员信息为空");
        SpellTeamMember teamMember = memberList.get(0);

        //查询拼团商品信息
        SpellGoodsExample spellGoodsExample = new SpellGoodsExample();
        spellGoodsExample.setSpellId(teamMember.getSpellId());
        spellGoodsExample.setGoodsId(orderProduct.getGoodsId());
        spellGoodsExample.setProductId(orderProduct.getProductId());
        List<SpellGoods> spellGoodsList = spellGoodsModel.getSpellGoodsList(spellGoodsExample, null);
        AssertUtil.notEmpty(spellGoodsList, "获取订单货品扩展信息为空，请重试");
        SpellGoods spellGoods = spellGoodsList.get(0);

        SpellOrderDetailVO orderVO = new SpellOrderDetailVO(order, orderProduct, orderExtend, spellGoods);
        return SldResponse.success(orderVO);
    }
}
