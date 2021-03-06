package com.slodon.b2b2c.controller.promotion.admin;

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

@Api(tags = "admin-??????????????????")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/spell/order")
public class AdminSpellOrderController extends BaseController {

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

    @ApiOperation("??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "????????????id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "orderSn", value = "?????????", paramType = "query"),
            @ApiImplicitParam(name = "memberName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "????????????", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "?????????(1-????????????2-?????????3-?????????", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "????????????", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "??????????????????", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SpellOrderVO>> list(HttpServletRequest request, Integer spellId, String orderSn, String memberName,
                                                 String goodsName, Date startTime, Date endTime, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellTeamExample example = new SpellTeamExample();
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
                //??????????????????
                SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
                teamMemberExample.setSpellTeamId(team.getSpellTeamId());
                teamMemberExample.setOrderSnLike(example.getOrderSnLike());
                teamMemberExample.setMemberNameLike(example.getMemberNameLike());
                List<SpellTeamMember> memberList = spellTeamMemberModel.getSpellTeamMemberList(teamMemberExample, null);
                List<SpellMemberOrderVO> voList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(memberList)) {
                    for (SpellTeamMember member : memberList) {
                        //??????????????????
                        Order order = orderModel.getOrderByOrderSn(member.getOrderSn());
                        AssertUtil.notNull(order, "????????????");
                        // ?????????????????????????????????????????????
                        OrderProductExample productExample = new OrderProductExample();
                        productExample.setOrderSn(member.getOrderSn());
                        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(productExample, null);
                        AssertUtil.notEmpty(orderProductList, "????????????????????????????????????????????????");
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

    @ApiOperation("????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderSn", value = "?????????", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<SpellOrderDetailVO> detail(HttpServletRequest request, String orderSn) {
        //??????????????????
        Order order = orderModel.getOrderByOrderSn(orderSn);
        AssertUtil.notNull(order, "??????????????????");

        //????????????????????????
        OrderProductExample example = new OrderProductExample();
        example.setOrderSn(orderSn);
        List<OrderProduct> orderProductList = orderProductModel.getOrderProductList(example, null);
        AssertUtil.notEmpty(orderProductList, "??????????????????????????????????????????");
        OrderProduct orderProduct = orderProductList.get(0);

        //????????????????????????
        OrderExtendExample extendExample = new OrderExtendExample();
        extendExample.setOrderSn(orderSn);
        List<OrderExtend> orderExtendList = orderExtendModel.getOrderExtendList(extendExample, null);
        AssertUtil.notEmpty(orderExtendList, "??????????????????????????????????????????");
        OrderExtend orderExtend = orderExtendList.get(0);

        //????????????????????????
        SpellTeamMemberExample teamMemberExample = new SpellTeamMemberExample();
        teamMemberExample.setSpellTeamId(orderProduct.getSpellTeamId());
        teamMemberExample.setOrderSn(orderSn);
        List<SpellTeamMember> memberList = spellTeamMemberModel.getSpellTeamMemberList(teamMemberExample, null);
        AssertUtil.notEmpty(memberList, "??????????????????????????????");
        SpellTeamMember teamMember = memberList.get(0);

        //????????????????????????
        SpellGoodsExample spellGoodsExample = new SpellGoodsExample();
        spellGoodsExample.setSpellId(teamMember.getSpellId());
        spellGoodsExample.setGoodsId(orderProduct.getGoodsId());
        spellGoodsExample.setProductId(orderProduct.getProductId());
        List<SpellGoods> spellGoodsList = spellGoodsModel.getSpellGoodsList(spellGoodsExample, null);
        AssertUtil.notEmpty(spellGoodsList, "??????????????????????????????????????????");
        SpellGoods spellGoods = spellGoodsList.get(0);

        SpellOrderDetailVO orderVO = new SpellOrderDetailVO(order, orderProduct, orderExtend, spellGoods);
        return SldResponse.success(orderVO);
    }
}
