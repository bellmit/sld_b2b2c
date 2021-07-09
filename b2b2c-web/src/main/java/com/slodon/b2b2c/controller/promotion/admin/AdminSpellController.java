package com.slodon.b2b2c.controller.promotion.admin;

import com.slodon.b2b2c.aop.OperationLogger;
import com.slodon.b2b2c.core.constant.SpellConst;
import com.slodon.b2b2c.core.controller.BaseController;
import com.slodon.b2b2c.core.response.JsonResult;
import com.slodon.b2b2c.core.response.PageVO;
import com.slodon.b2b2c.core.response.PagerInfo;
import com.slodon.b2b2c.core.response.SldResponse;
import com.slodon.b2b2c.core.util.AssertUtil;
import com.slodon.b2b2c.core.util.StringUtil;
import com.slodon.b2b2c.core.util.WebUtil;
import com.slodon.b2b2c.goods.pojo.Product;
import com.slodon.b2b2c.model.goods.ProductModel;
import com.slodon.b2b2c.model.promotion.SpellGoodsModel;
import com.slodon.b2b2c.model.promotion.SpellModel;
import com.slodon.b2b2c.model.promotion.SpellTeamMemberModel;
import com.slodon.b2b2c.model.promotion.SpellTeamModel;
import com.slodon.b2b2c.promotion.example.SpellExample;
import com.slodon.b2b2c.promotion.example.SpellGoodsExample;
import com.slodon.b2b2c.promotion.example.SpellTeamExample;
import com.slodon.b2b2c.promotion.example.SpellTeamMemberExample;
import com.slodon.b2b2c.promotion.pojo.Spell;
import com.slodon.b2b2c.promotion.pojo.SpellGoods;
import com.slodon.b2b2c.promotion.pojo.SpellTeam;
import com.slodon.b2b2c.promotion.pojo.SpellTeamMember;
import com.slodon.b2b2c.vo.promotion.*;
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

@Api(tags = "admin-拼团")
@RestController
@Slf4j
@RequestMapping("v3/promotion/admin/spell")
public class AdminSpellController extends BaseController {

    @Resource
    private SpellModel spellModel;
    @Resource
    private SpellGoodsModel spellGoodsModel;
    @Resource
    private SpellTeamModel spellTeamModel;
    @Resource
    private SpellTeamMemberModel spellTeamMemberModel;
    @Resource
    private ProductModel productModel;

    @ApiOperation("拼团列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "storeName", value = "店铺名称", paramType = "query"),
            @ApiImplicitParam(name = "spellName", value = "活动名称", paramType = "query"),
            @ApiImplicitParam(name = "startTime", value = "活动开始时间", paramType = "query"),
            @ApiImplicitParam(name = "endTime", value = "活动结束时间", paramType = "query"),
            @ApiImplicitParam(name = "state", value = "状态(1-待发布；2-未开始；3-进行中；4-已失效；5-已结束)", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("list")
    public JsonResult<PageVO<SpellVO>> list(HttpServletRequest request, String storeName, String spellName,
                                            Date startTime, Date endTime, Integer state) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellExample example = new SpellExample();
        example.setStoreNameLike(storeName);
        example.setSpellNameLike(spellName);
        example.setStartTimeBefore(endTime);
        example.setEndTimeAfter(startTime);
        if (!StringUtil.isNullOrZero(state)) {
            if (state == SpellConst.STATE_1) {
                example.setState(SpellConst.ACTIVITY_STATE_1);
            } else if (state == SpellConst.STATE_2) {
                example.setState(SpellConst.ACTIVITY_STATE_2);
                example.setStartTimeAfter(new Date());
            } else if (state == SpellConst.STATE_3) {
                example.setState(SpellConst.ACTIVITY_STATE_2);
                example.setStartTimeBefore(new Date());
                example.setEndTimeAfter(new Date());
            } else if (state == SpellConst.STATE_4) {
                example.setState(SpellConst.ACTIVITY_STATE_3);
            } else if (state == SpellConst.STATE_5) {
                example.setState(SpellConst.ACTIVITY_STATE_2);
                example.setEndTimeBefore(new Date());
            }
        }
        example.setStateNotEquals(SpellConst.ACTIVITY_STATE_4);
        List<Spell> list = spellModel.getSpellList(example, pager);
        List<SpellVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(spell -> {
                vos.add(new SpellVO(spell));
            });
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }

    @ApiOperation("拼团详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query")
    })
    @GetMapping("detail")
    public JsonResult<SpellDetailVO> detail(HttpServletRequest request, Integer spellId) {
        AssertUtil.notNullOrZero(spellId, "拼团活动id不能为空");

        Spell spell = spellModel.getSpellBySpellId(spellId);
        AssertUtil.notNull(spell, "获取拼团活动信息为空，请重试！");
        SpellDetailVO detailVO = new SpellDetailVO(spell);

        //查询拼团活动商品信息
        String fields = "goods_id, goods_name, goods_image";
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(spellId);
        example.setGroupBy("goods_id, goods_name, goods_image");
        example.setOrderBy("goods_id desc");
        List<SpellGoods> list = spellGoodsModel.getSpellGoodsListFieldsByExample(fields, example);
        List<SpellDetailVO.GoodsVO> goodsList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (SpellGoods spellGoods : list) {
                SpellDetailVO.GoodsVO goodsVO = new SpellDetailVO.GoodsVO(spellGoods);

                SpellGoodsExample goodsExample = new SpellGoodsExample();
                goodsExample.setSpellId(spellId);
                goodsExample.setGoodsId(spellGoods.getGoodsId());
                List<SpellGoods> spellGoodsList = spellGoodsModel.getSpellGoodsList(goodsExample, null);
                List<SpellDetailVO.GoodsVO.SpellProductVO> productList = new ArrayList<>();
                for (SpellGoods goods : spellGoodsList) {
                    SpellDetailVO.GoodsVO.SpellProductVO productVO = new SpellDetailVO.GoodsVO.SpellProductVO(goods);
                    //查询sku库存
                    Product product = productModel.getProductByProductId(goods.getProductId());
                    AssertUtil.notNull(product, "获取货品品信息为空，请重试");
                    productVO.setStock(product.getProductStock());
                    productList.add(productVO);
                }
                goodsVO.setProductList(productList);

                goodsList.add(goodsVO);
            }
        }
        detailVO.setGoodsList(goodsList);
        return SldResponse.success(detailVO);
    }

    @ApiOperation("删除拼团")
    @OperationLogger(option = "删除拼团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query")
    })
    @PostMapping("del")
    public JsonResult delSpell(HttpServletRequest request, Integer spellId) {
        AssertUtil.notNullOrZero(spellId, "请选择要删除的数据");
        spellModel.deleteSpell(spellId);
        return SldResponse.success("删除成功");
    }

    @ApiOperation("失效拼团")
    @OperationLogger(option = "失效拼团")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query")
    })
    @PostMapping("invalid")
    public JsonResult invalidSpell(HttpServletRequest request, Integer spellId) {
        AssertUtil.notNullOrZero(spellId, "拼团活动id不能为空");
        Spell spell = new Spell();
        spell.setSpellId(spellId);
        spell.setState(SpellConst.ACTIVITY_STATE_3);
        spellModel.updateSpell(spell);
        return SldResponse.success("失效成功");
    }

    @ApiOperation("查看商品列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsName", value = "商品名称", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("goodList")
    public JsonResult<PageVO<SpellGoodsListVO>> goodList(HttpServletRequest request, Integer spellId, String goodsName) {
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        SpellGoodsExample example = new SpellGoodsExample();
        example.setSpellId(spellId);
        example.setGoodsNameLike(goodsName);
        List<SpellGoodsListVO> list = spellGoodsModel.getSpellGoodsList2(example, pager);
        return SldResponse.success(new PageVO<>(list, pager));
    }

    @ApiOperation("查看团队列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "spellId", value = "拼团活动id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "state", value = "拼团状态,1-进行中；2-成功；3-失败", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "分页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "current", value = "当前页面位置", defaultValue = "1", paramType = "query")
    })
    @GetMapping("teamList")
    public JsonResult<PageVO<SpellTeamVO>> teamList(HttpServletRequest request, Integer spellId, Long goodsId, Integer state) {
        AssertUtil.notNullOrZero(spellId, "拼团活动id不能为空");
        AssertUtil.notNullOrZero(goodsId, "商品id不能为空");
        PagerInfo pager = WebUtil.handlerPagerInfo(request);
        //查询拼团活动团队表
        SpellTeamExample example = new SpellTeamExample();
        example.setSpellId(spellId);
        example.setGoodsId(goodsId);
        example.setState(state);
        example.setLeaderPayState(SpellConst.PAY_STATE_1);
        List<SpellTeam> list = spellTeamModel.getSpellTeamList(example, pager);
        List<SpellTeamVO> vos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            for (SpellTeam team : list) {
                SpellTeamVO teamVO = new SpellTeamVO(team);
                //赋值拼团成员列表
                SpellTeamMemberExample memberExample = new SpellTeamMemberExample();
                memberExample.setSpellTeamId(team.getSpellTeamId());
                memberExample.setPayState(SpellConst.PAY_STATE_1);
                memberExample.setOrderBy("participate_time asc");
                List<SpellTeamMember> memberList = spellTeamMemberModel.getSpellTeamMemberList(memberExample, null);
                List<SpellTeamMemberVO> memberVOS = new ArrayList<>();
                for (SpellTeamMember member : memberList) {
                    memberVOS.add(new SpellTeamMemberVO(member));
                }
                teamVO.setMemberList(memberVOS);
                vos.add(teamVO);
            }
        }
        return SldResponse.success(new PageVO<>(vos, pager));
    }
}
